package com.example.ticktapp.firebase

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.text.format.DateFormat
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class FirebaseChatUtils {
    /**
     * Checks if the Internet connection is available.
     *
     * @return Returns true if the Internet connection is available. False otherwise.
     */
  /*  fun isInternetAvailable(ctx: Context): Boolean {
        val connectivityManager =
            ctx.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var networkInfo: NetworkInfo? = null
        if (connectivityManager != null) {
            networkInfo = connectivityManager.activeNetworkInfo
        }
        return networkInfo != null && networkInfo.isConnected
    }*/

    /**
     * Shows the message passed in the parameter in the Toast.
     *
     * @param msg Message to be show in the toast.
     */
    fun showToast(ctx: Context?, msg: CharSequence?) {
        if (toast != null) {
            toast!!.cancel()
        }
        toast = Toast.makeText(ctx, msg, Toast.LENGTH_SHORT)
        toast!!.show()
    }

    /**
     * method used to set image using glide
     *
     * @param mContext
     * @param imageUrl
     * @param imageView
     * @param radius
     * @param placeholder
     * @param isCircular
     */
    fun setImageOnView(
        mContext: Context,
        imageUrl: Any?,
        imageView: ImageView,
        radius: Float,
        placeholder: Int,
        progressBar: ProgressBar?,
        isCircular: Boolean
    ) {
     /*   Glide.with(mContext).load(imageUrl)
            .placeholder(placeholder).error(placeholder)
            .centerCrop().into(object : BitmapImageViewTarget(imageView) {
                override fun setResource(resource: Bitmap?) {
                    if (progressBar != null) {
                        progressBar.visibility = View.GONE
                    }
                    val circularBitmapDrawable: RoundedBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(mContext.resources, resource)
                    if (isCircular) circularBitmapDrawable.setCircular(true) else circularBitmapDrawable.setCornerRadius(
                        radius
                    )
                    imageView.setImageDrawable(circularBitmapDrawable)
                }

                fun onLoadFailed(e: Exception?, errorDrawable: Drawable?) {
                    if (progressBar != null) {
                        progressBar.visibility = View.GONE
                    }
                }
            })*/
    }

    /**
     * method used to set image using glide
     *
     * @param mContext
     * @param imageUrl
     * @param imageView
     * @param placeholder
     */
    fun setImageOnView(
        mContext: Context,
        imageUrl: Any,
        imageView: ImageView,
        placeholder: Int,
        progressBar: ProgressBar?
    ) {
       /* Glide.with(mContext).load(imageUrl.toString())
            .placeholder(placeholder)
            .error(placeholder).centerCrop().into(object : BitmapImageViewTarget(imageView) {
                override fun setResource(resource: Bitmap?) {
                    if (progressBar != null) {
                        progressBar.visibility = View.GONE
                    }
                    val circularBitmapDrawable: RoundedBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(mContext.resources, resource)
                    circularBitmapDrawable.setCornerRadius(10)
                    imageView.setImageDrawable(circularBitmapDrawable)
                }

                fun onLoadFailed(e: Exception?, errorDrawable: Drawable?) {
                    if (progressBar != null) {
                        progressBar.visibility = View.GONE
                    }
                }
            })*/
    }

    /**
     * method used to set image using glide
     *
     * @param mContext
     * @param imageUrl
     * @param imageView
     * @param radius
     * @param placeholder
     * @param isCircular
     */
    fun setMessageImageOnView(
        mContext: Context,
        imageUrl: Any?,
        imageView: ImageView,
        radius: Float,
        placeholder: Drawable?,
        progressBar: ProgressBar?,
        isCircular: Boolean
    ) {
      /*  Glide.with(mContext).load(imageUrl).placeholder(placeholder).error(placeholder)
            .centerCrop().into(object : BitmapImageViewTarget(imageView) {
                override fun setResource(resource: Bitmap?) {
                    if (progressBar != null) {
                        progressBar.visibility = View.GONE
                    }
                    val circularBitmapDrawable: RoundedBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(mContext.resources, resource)
                    if (isCircular) circularBitmapDrawable.setCircular(true) else circularBitmapDrawable.setCornerRadius(
                        radius
                    )
                    imageView.setImageDrawable(circularBitmapDrawable)
                }

                fun onLoadFailed(e: Exception?, errorDrawable: Drawable?) {
                    if (progressBar != null) {
                        progressBar.visibility = View.GONE
                    }
                }
            })*/
    }

    /**
     * get time difference
     *
     * @param timeStamp
     * @return
     */
    fun getDateDifference(timeStamp: String): Int {
        val timeInMillis = timeStamp.toLong()
        var time = 0
        try {
            val date = DateFormat.format("MM/dd/yyyy", timeInMillis) as String
            val currentDate = DateFormat.format("MM/dd/yyyy", System.currentTimeMillis()) as String
            val date1: Date
            val date2: Date
            val dates = SimpleDateFormat("MM/dd/yyyy")

            //Setting dates
            date1 = dates.parse(date)
            date2 = dates.parse(currentDate)

            //Comparing dates
            val difference = Math.abs(date1.time - date2.time)
            val differenceDates = difference / (24 * 60 * 60 * 1000)
            time = differenceDates.toInt()
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return time
    }

    /**
     * convert Time
     *
     * @param timeStamp
     * @return
     */
    fun getDate(timeStamp: String): String {
        val timeInMillis = timeStamp.toLong()
        return DateFormat.format("dd/MM/yyyy", timeInMillis) as String
    }

    /**
     * Method to get Image path from uri
     *
     * @param mContext
     * @param mImageUri
     * @return
     */
    fun getImagePathFromUri(mContext: Context, mImageUri: Uri): String? {
        val filePath = arrayOf(MediaStore.Images.Media.DATA)
        val c = mContext.contentResolver.query(mImageUri, filePath, null, null, null)
        val picturePath: String?
        if (c != null) {
            c.moveToFirst()
            val columnIndex = c.getColumnIndex(filePath[0])
            picturePath = c.getString(columnIndex)
            c.close()
        } else {
            picturePath = mImageUri.path
        }
        return picturePath
    }

    /**
     * Method to get image uri from bitmap
     *
     * @param mContext
     * @param inImage
     * @return
     */
    fun getImageUri(mContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(mContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    /**
     * Method to get image uri from bitmap
     *
     * @param bitmapImage
     * @return
     */
    fun compressBitmap(bitmapImage: Bitmap): Bitmap {
        val bytearrayoutputstream = ByteArrayOutputStream()
        bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, bytearrayoutputstream)
        val BYTE = bytearrayoutputstream.toByteArray()
        return BitmapFactory.decodeByteArray(BYTE, 0, BYTE.size)
    }

    /**
     * Method to get time from timestamp
     *
     * @param timeStamp
     * @return
     */
    fun getTimeFromTimeStamp(timeStamp: String): String {
        val day = getDateDifference(timeStamp)
        return if (day == 0) {
            DateFormat.format("hh:mm a", timeStamp.toLong()).toString()
        } else if (day == 1) {
            "Yesterday"
        } else {
            getDate(timeStamp)
        }
    }

    fun getStaticMapImage(latitude: Double, longitude: Double): String {
        return "https://maps.googleapis.com/maps/api/staticmap?center=$latitude,$longitude&zoom=12&size=200x180&markers=color:red%7Clabel:S%7C$latitude,$longitude&markers=size:tiny%7Ccolor:green%7CDelta+Junction,AK&markers=size:mid%7Ccolor:0xFFFF00%7Clabel:C%7CTok,AK&key=AIzaSyAZxKx6JwRrhBJFJy0Wp_V9ASrG-wGV_xo"
    }

    /**
     * method to change milliseconds to timer
     *
     * @param milliseconds
     * @return
     */
    fun milliSecondsToTimer(milliseconds: Long): String {
        var finalTimerString = ""
        var secondsString = ""
        var minutesString = ""

        // Convert total duration into time
        val hours = (milliseconds / (1000 * 60 * 60)).toInt()
        val minutes = (milliseconds % (1000 * 60 * 60)).toInt() / (1000 * 60)
        val seconds = (milliseconds % (1000 * 60 * 60) % (1000 * 60) / 1000).toInt()
        // Add hours if there
        if (hours > 0) {
            finalTimerString = "$hours:"
        }

        // Prepending 0 to seconds if it is one digit
        secondsString = if (seconds < 10) {
            "0$seconds"
        } else {
            "" + seconds
        }
        minutesString = if (minutes < 10) {
            "0$minutes"
        } else {
            "" + minutes
        }
        finalTimerString = "$finalTimerString$minutesString:$secondsString"

        // return timer string
        return finalTimerString
    }

    /**
     * Function to get Progress percentage
     *
     * @param currentDuration
     * @param totalDuration
     */
    fun getProgressPercentage(currentDuration: Long, totalDuration: Long): Int {
        var percentage = 0.toDouble()
        val currentSeconds: Long = ((currentDuration / 1000) as Int).toLong()
        val totalSeconds: Long = ((totalDuration / 1000) as Int).toLong()

        // calculating percentage
        percentage = currentSeconds.toDouble() / totalSeconds * 100

        // return percentage
        return percentage.toInt()
    }

    /**
     * method to get audio file path
     *
     * @return
     */
    val audioFilePath: String
        get() {
            val file = File(Environment.getExternalStorageDirectory().toString() + "/Chat/Audios")
            if (!file.exists()) {
                file.mkdirs()
            }
            return file.absolutePath + "/AUDIO_" + System.currentTimeMillis() + ".aac"
        }

    /**
     * method to get the file type
     *
     * @param messageType
     * @return
     */
    fun getFileType(messageType: String): Int {
        if (messageType == FirebaseConstants.IMAGE) return FirebaseConstants.FILE_IMAGE else if (messageType == FirebaseConstants.VIDEO) return FirebaseConstants.FILE_VIDEO
        return -1
    }

    companion object {
        private var mFirebaseChatUtils: FirebaseChatUtils? = null
        private var toast: Toast? = null

        /*
     * Method to get instance of class
     *
     * @return
     */
        val instance: FirebaseChatUtils?
            get() = if (mFirebaseChatUtils == null) {
                mFirebaseChatUtils = FirebaseChatUtils()
                mFirebaseChatUtils
            } else {
                mFirebaseChatUtils
            }
    }
}