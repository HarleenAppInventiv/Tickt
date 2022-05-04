package com.example.ticktapp.util

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Level.INFO

object ImageUtils {

    const val REQ_CODE_CAMERA_PICTURE = 1
    const val REQ_CODE_GALLERY_PICTURE = 2
    private val TAG = ImageUtils::class.java.simpleName
    private lateinit var imageFile: File

    fun displayImagePicker(parentContext: Any) {
        var context: Context? = null
        if (parentContext is Fragment) {
            context = parentContext.context
        } else if (parentContext is Activity)
            context = parentContext

        if (context != null) {
            val pickerItems = arrayOf(
                /*context.getString(R.string.camera),
                context.getString(R.string.choose_gallery),*/
                context.getString(android.R.string.cancel)
            )

            val builder = AlertDialog.Builder(context)
            /*builder.setTitle(context.getString(R.string.select_your_choice))*/
            builder.setItems(pickerItems) { dialog, which ->
                when (which) {
                    0 -> openCamera(parentContext)
                    1 -> openGallery(parentContext)
                }
                dialog.dismiss()
            }
            val alertDialog = builder.create()
            alertDialog.show()
        }
    }


    /**
     * Open the device camera using the ACTION_IMAGE_CAPTURE intent
     *
     * @param uiReference Reference of the current ui.
     * Use either android.support.v7.app.AppCompatActivity or android.support
     * .v4.app.Fragment
     * //     * @param imageFile   Destination image file
     */
    fun openCamera(uiReference: Any) {
        var context: Context? = null
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            if (uiReference is Fragment)
                context = uiReference.context
            else if (uiReference is AppCompatActivity)
                context = uiReference

            if (context != null) {

                if (context.externalCacheDir != null)
                    imageFile = createImageFile(context.externalCacheDir!!.path)!!
                // Put the uri of the image file as intent extra

                val imageUri = FileProvider.getUriForFile(
                    context,
                    "com.example.ticktapp.provider",
                    imageFile
                )
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)

                // Get a list of all the camera apps
                val resolvedIntentActivities = context.packageManager
                    .queryIntentActivities(
                        cameraIntent, PackageManager
                            .MATCH_DEFAULT_ONLY
                    )

                // Grant uri read/write permissions to the camera apps
                for (resolvedIntentInfo in resolvedIntentActivities) {
                    val packageName = resolvedIntentInfo.activityInfo.packageName

                    context.grantUriPermission(
                        packageName, imageUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                }

                if (uiReference is Fragment)
                    uiReference.startActivityForResult(
                        cameraIntent,
                        REQ_CODE_CAMERA_PICTURE
                    )
                else
                    (uiReference as AppCompatActivity).startActivityForResult(
                        cameraIntent, REQ_CODE_CAMERA_PICTURE
                    )
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun openGallery(uiReference: Any) {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        if (uiReference is AppCompatActivity) uiReference.startActivityForResult(
            galleryIntent, REQ_CODE_GALLERY_PICTURE
        )
        else if (uiReference is Fragment) uiReference.startActivityForResult(
            galleryIntent, REQ_CODE_GALLERY_PICTURE
        )
    }

    fun getImagePathFromGallery(context: Context, imageUri: Uri): File? {
        var imagePath: String? = null
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(
            imageUri,
            filePathColumn, null, null, null
        )
        if (cursor != null) {
            cursor.moveToFirst()
            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
            imagePath = cursor.getString(columnIndex)
            cursor.close()
        }
        return File(imagePath!!)
    }


    private fun saveImageToExternalStorage(context: Context, finalBitmap: Bitmap?): File? {
        val file2: File
        val mediaStorageDir = File(context.externalCacheDir!!.path)
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null
            }
        }
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        file2 = File(
            mediaStorageDir.path + File.separator
                    + "IMG_" + timeStamp + ".jpg"
        )

        try {
            val out = FileOutputStream(file2)
            finalBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()
            return file2
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return file2
    }

    private fun getFile(imgPath: String?): Bitmap? {
        var bMapRotate: Bitmap? = null
        try {

            if (imgPath != null) {
                val exif = ExifInterface(imgPath)

                val mOrientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 1
                )

                val options = BitmapFactory.Options()
                options.inJustDecodeBounds = true
                BitmapFactory.decodeFile(imgPath, options)
                options.inSampleSize = calculateInSampleSize(options)
                options.inJustDecodeBounds = false

                bMapRotate = BitmapFactory.decodeFile(imgPath, options)
                when (mOrientation) {
                    6 -> {
                        val matrix = Matrix()
                        matrix.postRotate(90f)
                        bMapRotate = Bitmap.createBitmap(
                            bMapRotate, 0, 0,
                            bMapRotate.width, bMapRotate.height,
                            matrix, true
                        )
                    }
                    8 -> {
                        val matrix = Matrix()
                        matrix.postRotate(270f)
                        bMapRotate = Bitmap.createBitmap(
                            bMapRotate, 0, 0,
                            bMapRotate.width, bMapRotate.height,
                            matrix, true
                        )
                    }
                    3 -> {
                        val matrix = Matrix()
                        matrix.postRotate(180f)
                        bMapRotate = Bitmap.createBitmap(
                            bMapRotate, 0, 0,
                            bMapRotate.width, bMapRotate.height,
                            matrix, true
                        )
                    }
                }
            }
        } catch (e: OutOfMemoryError) {
            bMapRotate = null
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: Exception) {
            bMapRotate = null
            e.printStackTrace()
        }
        return bMapRotate
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options
    ): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > 400 || width > 400) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            while (halfHeight / inSampleSize > 400 && halfWidth / inSampleSize > 400) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    @Throws(IOException::class)
    private fun createImageFile(directory: String): File? {
        var imageFile: File? = null
        if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
            val storageDir = File(directory)
            if (!storageDir.mkdirs()) {
                if (!storageDir.exists()) {
                    return null
                }
            }
            val imageFileName = "JPEG_" + System.currentTimeMillis() + "_"

            imageFile = File.createTempFile(imageFileName, ".jpg", storageDir)
        }
        return imageFile
    }

    fun getFile(context: Context) = saveImageToExternalStorage(context, getFile(imageFile.path))

    fun getPathFromUri(context: Context, uri: Uri): String? {
        // DocumentProvider
        if ("content".equals(uri.scheme, ignoreCase = true)) {

            // Return the remote address
            return if (isGooglePhotosUri(uri)) uri.lastPathSegment else
                getDataColumn(context, uri)

        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        // File
        // MediaStore (and general)

        return null
    }

    private fun getDataColumn(
        context: Context, uri: Uri
    ): String? {

        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)

        try {
            cursor = context.contentResolver.query(uri, projection, null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }
}