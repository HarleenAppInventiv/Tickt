package com.dreamg.videoTrimmer

import android.app.Activity
import android.content.ContentValues
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.format.DateUtils
import android.util.Log
import com.abedelazizshe.lightcompressorlibrary.CompressionListener
import com.abedelazizshe.lightcompressorlibrary.VideoCompressor
import com.abedelazizshe.lightcompressorlibrary.VideoQuality
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

/**
 * Created by Shweta 9/10/20
 * Prismetric Technology
 */

public class LightVideoCompression(
    var activity: Activity,
    var path: String,
    var destPath: File,
    var listener: CompressListener
) {
    init {
        start()
    }

    fun start() {
        //   val desFile = returnVideoFileName()

        destPath?.let {
            var time = 0L
            VideoCompressor.start(
                path,
                destPath.path,
                object : CompressionListener {
                    override fun onProgress(percent: Float) {
                        //Update UI
//                            if (percent <= 100 && percent.toInt() % 5 == 0)
                        /*runOnUiThread {
                                progress.text = "${percent.toLong()}%"
                                progressBar.progress = percent.toInt()
                            }*/
                    }

                    override fun onStart() {
                        time = System.currentTimeMillis()
                        /*progress.visibility = View.VISIBLE
                            progressBar.visibility = View.VISIBLE
                            originalSize.text =
                                    "Original size: ${getFileSize(File(path).length())}"
                            progress.text = ""
                            progressBar.progress = 0*/
                    }

                    override fun onSuccess() {
                        val newSizeValue = destPath.length()
                        Log.e("Trim", "Size after compression: ${getFileSize(newSizeValue)}")
                        time = System.currentTimeMillis() - time
                        Log.e("Trim", "Duration: ${DateUtils.formatElapsedTime(time / 1000)}")

                        path = destPath.path
                        listener?.onCompressedVideo(path)
                    }

                    override fun onFailure(failureMessage: String) {
                        listener?.onCompressFail()
                        Log.wtf("failureMessage", failureMessage)
                    }

                    override fun onCancelled() {
                        Log.wtf("TAG", "compression has been cancelled")
                        // make UI changes, cleanup, etc
                    }
                },
                VideoQuality.MEDIUM,
                isMinBitRateEnabled = true,
                keepOriginalResolution = false,
            )
        }
    }

    private fun saveVideoFile(filePath: String?): File? {
        filePath?.let {
            val videoFile = File(filePath)
            val videoFileName = "${System.currentTimeMillis()}_${videoFile.name}"
            val folderName = Environment.DIRECTORY_MOVIES
            if (Build.VERSION.SDK_INT >= 29) {

                val values = ContentValues().apply {

                    put(
                        MediaStore.Images.Media.DISPLAY_NAME,
                        videoFileName
                    )
                    put(MediaStore.Images.Media.MIME_TYPE, "video/mp4")
                    put(MediaStore.Images.Media.RELATIVE_PATH, folderName)
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }

                val collection =
                    MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

                val fileUri = activity.applicationContext.contentResolver.insert(collection, values)

                fileUri?.let {
                    activity.application.contentResolver.openFileDescriptor(fileUri, "w")
                        .use { descriptor ->
                            descriptor?.let {
                                FileOutputStream(descriptor.fileDescriptor).use { out ->
                                    FileInputStream(videoFile).use { inputStream ->
                                        val buf = ByteArray(4096)
                                        while (true) {
                                            val sz = inputStream.read(buf)
                                            if (sz <= 0) break
                                            out.write(buf, 0, sz)
                                        }
                                    }
                                }
                            }
                        }

                    values.clear()
                    values.put(MediaStore.Video.Media.IS_PENDING, 0)
                    activity.applicationContext.contentResolver.update(fileUri, values, null, null)

                    return File(getMediaPath(activity.applicationContext, fileUri))
                }
            } else {
                val downloadsPath =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val desFile = File(downloadsPath, videoFileName)

                if (desFile.exists())
                    desFile.delete()

                try {
                    desFile.createNewFile()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                return desFile
            }
        }
        return null
    }

    public interface CompressListener {
        fun onCompressedVideo(destPath: String)
        fun onCompressFail()
    }
}