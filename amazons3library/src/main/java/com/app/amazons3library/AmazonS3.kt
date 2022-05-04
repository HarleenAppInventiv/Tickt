package com.app.amazons3library

import android.content.Context
import com.amazonaws.AmazonServiceException
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.amazonaws.services.s3.model.DeleteObjectRequest
import com.amazonaws.services.s3.model.DeleteObjectsRequest
import com.amazonaws.services.s3.model.DeleteObjectsRequest.KeyVersion
import com.amazonaws.services.s3.model.MultiObjectDeleteException
import com.app.amazons3library.imageutils.ImageCompressor.Companion.getDefault
import com.app.amazons3library.interfaces.AmazonCallback
import com.app.amazons3library.model.MediaBean
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.File
import java.io.FileFilter
import java.util.*

class AmazonS3 {
    private lateinit var mContext: Context
    private lateinit var amazonCallback: AmazonCallback
    private var mTransferUtility: TransferUtility? = null
    /*
     *  Initialize activity instance
     */
    fun setContext(context: Context) {
        mContext = context
    }

    /*
    *  Initialize AmazonS3 callback
    */
    fun setCallback(amazonCallback: AmazonCallback) {
        this.amazonCallback = amazonCallback
    }

    /*
    * Upload video, image and other type file on amazon s3
    * */
    fun upload(mediaBean: MediaBean) {
        val file = File(mediaBean.mediaPath!!)
        if (file.exists()) {
            mTransferUtility =
                AmazonUtils.getTransferUtility(mContext)
            if (ImageFileFilter().accept(file)) {
                getDefault(mContext)
                        .compressToFileAsObservable(file)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ file -> uploadFileOnAmazon(mediaBean, file) }) { }
            } else uploadFileOnAmazon(mediaBean, file)
        } else amazonCallback.uploadFailed(mediaBean)
    }

    fun addDataInBean(
        id: Int,
        name: String,
        path: String?
    ): MediaBean {
        val bean = MediaBean()
        bean.id = id
        bean.name = name
        bean.mediaPath = path
        return bean
    }

    /**
     * Method to delete the object
     *
     * @param context         context
     * @param fileS3ObjectKey file object to be deleted
     */
    fun deleteFileFromS3(context: Context, fileS3ObjectKey: String, versionId: String) {
        try {
            val s3client =
                AmazonUtils.getS3Client(context)
            s3client?.deleteObject(DeleteObjectRequest(AmazonS3Constants.BUCKET, versionId + "_" + fileS3ObjectKey))
        } catch (e: AmazonServiceException) {
            e.printStackTrace()
        }
    }

    /**
     * Method to delete multiple amazon s3 file objects
     *
     * @param context            context
     * @param objectKeyNamesList list of object key names to be deleted from the bucket
     */
    fun deleteMultipleFilesFromS3(context: Context, objectKeyNamesList: List<String?>) {
        try {
            val multiObjectDeleteRequest = DeleteObjectsRequest(AmazonS3Constants.BUCKET)
            val s3client =
                AmazonUtils.getS3Client(context)
            val keys: MutableList<KeyVersion> = ArrayList()
            for (objectKeyName in objectKeyNamesList) {
                keys.add(KeyVersion(objectKeyName))
            }
            multiObjectDeleteRequest.keys = keys
            val delObjRes = s3client?.deleteObjects(multiObjectDeleteRequest)
            System.out.format("Successfully deleted all the %s items.\n",
                    delObjRes?.deletedObjects?.size)
        } catch (e: MultiObjectDeleteException) {
            for (deleteError in e.errors) { // Process exception.
                e.printStackTrace()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Method to upload image on amazon.
     *
     * @param mediaBean
     * @param file
     */
    private fun uploadFileOnAmazon(mediaBean: MediaBean, file: File) {
        val observer: TransferObserver = mTransferUtility!!.upload(AmazonS3Constants.BUCKET, file.name, file, CannedAccessControlList.PublicRead)
        observer.setTransferListener(UploadListener(mediaBean))
        mediaBean.mObserver = observer
    }

    fun cancelUploadToS3(mediaBean: MediaBean) {
        mTransferUtility!!.cancel(mediaBean.id)
    }

    fun pauseUploadToS3(mediaBean: MediaBean) {
        mTransferUtility!!.pause(mediaBean.id)
    }

    fun resumeUploadToS3(mediaBean: MediaBean) {
        mTransferUtility!!.resume(mediaBean.id)
    }

    private inner class UploadListener(private val mediaBean: MediaBean) : TransferListener {
        // Simply updates the UI list when notified.
        override fun onError(id: Int, e: Exception) {
            mediaBean.isSuccess = "0"
            mediaBean.id = id
            amazonCallback.uploadError(e, mediaBean)
        }

        override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
            val progress = (bytesCurrent.toDouble() * 100 / bytesTotal).toInt()
            mediaBean.id = id
            mediaBean.progress = progress
            amazonCallback.uploadProgress(mediaBean)
        }

        override fun onStateChanged(id: Int, newState: TransferState) {
            mediaBean.id = id
            if (newState == TransferState.COMPLETED) {
                mediaBean.isSuccess = "1"
                val url = AmazonS3Constants.AMAZON_SERVER_URL + mediaBean.mObserver?.key
                mediaBean.serverUrl = url
                amazonCallback.uploadSuccess(mediaBean)
            } else if (newState == TransferState.FAILED) {
                mediaBean.isSuccess = "0"
                amazonCallback.uploadFailed(mediaBean)
            }
        }

    }

    /*
    * Check file is image or not
    * */
    inner class ImageFileFilter : FileFilter {
        private val okFileExtensions = arrayOf("jpg", "png", "gif", "jpeg")
        override fun accept(file: File): Boolean {
            for (extension in okFileExtensions) {
                if (file.name.toLowerCase().endsWith(extension)) {
                    return true
                }
            }
            return false
        }
    }
}