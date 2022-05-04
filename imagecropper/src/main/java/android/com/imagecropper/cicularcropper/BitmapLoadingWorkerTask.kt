package android.com.imagecropper.cicularcropper

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask

import java.lang.ref.WeakReference

internal class BitmapLoadingWorkerTask (cropImageView: CropImageView,
                                        val uri: Uri) : AsyncTask<Void, Void, BitmapLoadingWorkerTask.Result>() {
    private val mCropImageViewReference: WeakReference<CropImageView> = WeakReference(cropImageView)
    private val mContext: Context
    private val mWidth: Int
    private val mHeight: Int

    init {
        mContext = cropImageView.context
        val metrics = cropImageView.resources.displayMetrics
        val densityAdj = (if (metrics.density > 1) 1f / metrics.density else 1f).toDouble()
        mWidth = (metrics.widthPixels * densityAdj).toInt()
        mHeight = (metrics.heightPixels * densityAdj).toInt()
    }

    /**
     * Decode image in background.
     *
     * @param params ignored
     * @return the decoded bitmap data
     */
    override fun doInBackground(vararg params: Void): Result? {
        try {
            if (!isCancelled) {
                val decodeResult = BitmapUtils.decodeSampledBitmap(mContext, uri, mWidth, mHeight)
                if (!isCancelled) {
                    val rotateResult = BitmapUtils.rotateBitmapByExif(decodeResult.bitmap!!, mContext, uri)

                    return Result(uri, rotateResult.bitmap, decodeResult.sampleSize, rotateResult.degrees)
                }
            }
            return null
        } catch (e: Exception) {
            return Result(uri, e)
        }

    }

    /**
     * Once complete, see if ImageView is still around and set bitmap.
     *
     * @param result the result of bitmap loading
     */
    override fun onPostExecute(result: Result?) {
        if (result != null) {
            var completeCalled = false
            if (!isCancelled) {
                val cropImageView = mCropImageViewReference.get()
                if (cropImageView != null) {
                    completeCalled = true
                    cropImageView.onSetImageUriAsyncComplete(result)
                }
            }
            if (!completeCalled && result.bitmap != null) {
                // fast release of unused bitmap
                result.bitmap.recycle()
            }
        }
    }

    /**
     * The result of BitmapLoadingWorkerTask async loading.
     */
    class Result {
        val uri: Uri
        val bitmap: Bitmap?
        val loadSampleSize: Int
        val degreesRotated: Int
        val error: Exception?

        internal constructor(uri: Uri, bitmap: Bitmap, loadSampleSize: Int, degreesRotated: Int) {
            this.uri = uri
            this.bitmap = bitmap
            this.loadSampleSize = loadSampleSize
            this.degreesRotated = degreesRotated
            this.error = null
        }

        internal constructor(uri: Uri, error: Exception) {
            this.uri = uri
            this.bitmap = null
            this.loadSampleSize = 0
            this.degreesRotated = 0
            this.error = error
        }
    }
}
