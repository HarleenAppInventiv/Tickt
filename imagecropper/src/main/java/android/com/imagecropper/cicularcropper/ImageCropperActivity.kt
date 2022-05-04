// "Therefore those skilled at the unorthodox
// are infinite as heaven and earth,
// inexhaustible as the great rivers.
// When they come to an end,
// they begin again,
// like the days and months;
// they die and are reborn,
// like the four seasons."
//
// - Sun Tsu,
// "The Art of War"

package android.com.imagecropper.cicularcropper


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.PorterDuff
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.com.imagecropper.R
import android.content.Context
import android.widget.*
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.TextViewCompat
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.net.URL


/**
 * Built-in activity for image cropping.<br></br>
 * Use [ImageCropper.activity] to create a builder to start this activity.
 */
class ImageCropperActivity : AppCompatActivity(), CropImageView.OnSetImageUriCompleteListener,
    CropImageView.OnCropImageCompleteListener {

    private var btnCancel: AppCompatTextView? = null
    private var btnDone: AppCompatTextView? = null
    private var llCropperTool: RelativeLayout? = null
    private val PICK_IMAGE = 100
    private var mpackageManager: PackageManager? = null

    /**
     * The crop image view library widget used in the activity
     */
    private var mCropImageView: CropImageView? = null

    /**
     * Persist URI image to crop URI if specific permissions are required
     */
    private var mCropImageUri: Uri? = null

    /**
     * the options that were set for the crop image
     */
    private var mOptions: ImageCropperOptions? = null

    /**
     * Get Android uri to save the cropped image into.<br></br>
     * Use the given in options or create a temp file.
     */
    protected val outputUri: Uri?
            get() {
            var outputUri: Uri? = mOptions!!.outputUri
            if (outputUri == null || outputUri == Uri.EMPTY) {
                try {
                    val ext = if (mOptions!!.outputCompressFormat == Bitmap.CompressFormat.JPEG)
                        ".jpg"
                    else if (mOptions!!.outputCompressFormat == Bitmap.CompressFormat.PNG) ".png" else ".webp"
                    outputUri = Uri.fromFile(File.createTempFile("cropped", ext, cacheDir))
                } catch (e: IOException) {
                    throw RuntimeException("Failed to create temp file for output image", e)
                }

            }
            return outputUri
        }

    @SuppressLint("NewApi")
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.crop_image_activity)

        mCropImageView = findViewById<View>(R.id.cropImageView) as CropImageView
        btnCancel = findViewById<View>(R.id.btn_cancel) as AppCompatTextView
        btnDone = findViewById<View>(R.id.btn_done) as AppCompatTextView
        llCropperTool = findViewById<View>(R.id.tool_crop_image) as RelativeLayout

        btnCancel!!.setOnClickListener { onBackPressed() }
        btnDone!!.setOnClickListener { cropImage() }


        val intent = intent
        mCropImageUri = intent.getParcelableExtra(ImageCropper.CROP_IMAGE_EXTRA_SOURCE)
        mOptions = intent.getParcelableExtra(ImageCropper.CROP_IMAGE_EXTRA_OPTIONS)

        btnCancel!!.setBackgroundColor(mOptions!!.toolbarColor)
        btnDone!!.setBackgroundColor(mOptions!!.toolbarColor)


        if (savedInstanceState == null) {
            if (mCropImageUri == null || mCropImageUri == Uri.EMPTY) {
                if (ImageCropper.isExplicitCameraPermissionRequired(this)) {
                    // request permissions and handle the result in onRequestPermissionsResult()
                    requestPermissions(
                        arrayOf(Manifest.permission.CAMERA),
                        ImageCropper.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE
                    )
                } else {
                    ImageCropper.startPickImageActivity(this)
                }
            } else if (ImageCropper.isReadExternalStoragePermissionsRequired(
                    this,
                    mCropImageUri!!
                )
            ) {
                // request permissions and handle the result in onRequestPermissionsResult()
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    ImageCropper.PICK_IMAGE_PERMISSIONS_REQUEST_CODE
                )
            } else {
                // no permissions required or already grunted, can start crop image activity
                mCropImageView!!.setImageUriAsync(mCropImageUri)
            }
        }

        val actionBar = supportActionBar
        if (actionBar != null) {
            val title = if (!mOptions!!.activityTitle.isEmpty())
                mOptions!!.activityTitle
            else
                resources.getString(R.string.crop_image_activity_title)
            actionBar.title = title
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onStart() {
        super.onStart()

        mCropImageView!!.setOnSetImageUriCompleteListener(this)
        mCropImageView!!.setOnCropImageCompleteListener(this)
    }

    override fun onStop() {
        super.onStop()
        mCropImageView!!.setOnSetImageUriCompleteListener(null)
        mCropImageView!!.setOnCropImageCompleteListener(null)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResultCancel()
    }

    @SuppressLint("NewApi")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        // handle result of pick image chooser
        if (requestCode == ImageCropper.PICK_IMAGE_CHOOSER_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_CANCELED) {
                //User cancelled the picker. We don't have anything to crop
                setResultCancel()
            }

            if (resultCode == Activity.RESULT_OK) {
                
                mCropImageUri = ImageCropper.getPickImageResultUri(this, data)

                // For API >= 23 we need to check specifically that we have permissions to read external storage.
                if (ImageCropper.isReadExternalStoragePermissionsRequired(this, mCropImageUri!!)) {
                    // request permissions and handle the result in onRequestPermissionsResult()
                    requestPermissions(
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        ImageCropper.PICK_IMAGE_PERMISSIONS_REQUEST_CODE
                    )


                } else {
                    // no permissions required or already granted, can start crop image activity
                    mCropImageView!!.setImageUriAsync(mCropImageUri)
                }
            }
        } else {
            if (resultCode == Activity.RESULT_CANCELED) {
                //User cancelled the picker. We don't have anything to crop
                setResultCancel()
            }

            if (resultCode == Activity.RESULT_OK) {
                mCropImageUri = ImageCropper.getPickImageResultUri(this, data)

                // For API >= 23 we need to check specifically that we have permissions to read external storage.
                if (ImageCropper.isExplicitCameraPermissionRequired(this)) {
                    // request permissions and handle the result in onRequestPermissionsResult()
                    requestPermissions(
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        ImageCropper.PICK_IMAGE_PERMISSIONS_REQUEST_CODE
                    )
                } else {
                    // no permissions required or already grunted, can start crop image activity

                    mCropImageView!!.setImageUriAsync(mCropImageUri)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == ImageCropper.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            if (mCropImageUri != null && grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // required permissions granted, start crop image activity

                mCropImageView!!.setImageUriAsync(mCropImageUri)
            } else {
                Toast.makeText(
                    this,
                    "Cancelling, required permissions are not granted",
                    Toast.LENGTH_LONG
                ).show()
                setResultCancel()
            }
        }

        if (requestCode == ImageCropper.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE) {
            //Irrespective of whether camera permission was given or not, we show the picker
            //The picker will not add the camera intent if permission is not available
            //    ImageCropper.startPickImageActivity(this);
        }
    }

    override fun onSetImageUriComplete(view: CropImageView, uri: Uri, error: Exception?) {
        if (error == null) {
            if (mOptions!!.initialCropWindowRectangle != null) {
                mCropImageView!!.cropRect = mOptions!!.initialCropWindowRectangle
            }
            //            if (mOptions.initialRotation > -1) {
            //                mCropImageView.setRotatedDegrees(mOptions.initialRotation);
            //            }
            mCropImageView!!.rotatedDegrees = autoRotate(uri)
            if (mOptions!!.initialRotation > -1) {
                mCropImageView!!.rotatedDegrees = mOptions!!.initialRotation
            }
        } else {
            setResult(null, error, 1)
        }
    }

    override fun onCropImageComplete(view: CropImageView, result: CropImageView.CropResult) {
        setResult(result.uri, result.error, result.sampleSize)
    }

    //region: Private methods

    /**
     * Execute crop image and save the result tou output uri.
     */
    protected fun cropImage() {
        if (mOptions!!.noOutputImage) {
            setResult(null, null, 1)
        } else {
            val outputUri = outputUri
            mCropImageView!!.saveCroppedImageAsync(
                outputUri!!,
                mOptions!!.outputCompressFormat,
                mOptions!!.outputCompressQuality,
                mOptions!!.outputRequestWidth,
                mOptions!!.outputRequestHeight,
                mOptions!!.outputRequestSizeOptions
            )

        }
    }

    /**
     * Rotate the image in the crop image view.
     */
    protected fun rotateImage(degrees: Int) {
        mCropImageView!!.rotateImage(degrees)
    }

    /**
     * Result with cropped image data or error if failed.
     */
    protected fun setResult(uri: Uri?, error: Exception?, sampleSize: Int) {
        val resultCode =
            if (error == null) Activity.RESULT_OK else ImageCropper.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE

        setResult(resultCode, getResultIntent(uri, error, sampleSize))
        finish()
    }

    /**
     * Cancel of cropping activity.
     */
    protected fun setResultCancel() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    /**
     * Get intent instance to be used for the result of this activity.
     */
    protected fun getResultIntent(uri: Uri?, error: Exception?, sampleSize: Int): Intent {
        val result = ImageCropper.ActivityResult(
            mCropImageView!!.imageUri!!,
            uri!!,
            error,
            mCropImageView!!.cropPoints,
            mCropImageView!!.cropRect!!,
            mCropImageView!!.rotatedDegrees,
            sampleSize
        )
        val intent = Intent()
        intent.putExtra(ImageCropper.CROP_IMAGE_EXTRA_RESULT, result)
        intent.putExtra("Data", uri.toString())
        return intent
    }

    /**
     * Update the color of a specific menu item to the given color.
     */
    private fun updateMenuItemIconColor(menu: Menu, itemId: Int, color: Int) {
        val menuItem = menu.findItem(itemId)
        if (menuItem != null) {
            val menuItemIcon = menuItem.icon
            if (menuItemIcon != null) {
                try {
                    menuItemIcon.mutate()
                    menuItemIcon.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
                    menuItem.icon = menuItemIcon
                } catch (e: Exception) {
                }

            }
        }
    }

    private fun autoRotate(selectedImage: Uri): Int {

        var picturePath : String? = selectedImage.path
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = this.contentResolver.query(selectedImage, filePathColumn, null, null, null)
        var degree = 0
        if (cursor != null) {
            cursor.moveToFirst()
            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
            picturePath = cursor.getString(columnIndex)
            cursor.close()
        }

        var loadedBitmap = BitmapFactory.decodeFile(picturePath)

        var exif: ExifInterface? = null
        try {
            val pictureFile = File(picturePath!!)
            exif = ExifInterface(pictureFile.absolutePath)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        var orientation = ExifInterface.ORIENTATION_NORMAL

        if (exif != null)
            orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )

        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> {
                loadedBitmap = rotateBitmap(loadedBitmap, 90)
                degree = 90
            }
            ExifInterface.ORIENTATION_ROTATE_180 -> {
                loadedBitmap = rotateBitmap(loadedBitmap, 180)
                degree = 180
            }

            ExifInterface.ORIENTATION_ROTATE_270 -> {
                loadedBitmap = rotateBitmap(loadedBitmap, 270)
                degree = 270
            }
        }
        return degree

    }

    companion object {

        fun rotateBitmap(bitmap: Bitmap, degrees: Int): Bitmap {
            val matrix = Matrix()
            matrix.postRotate(degrees.toFloat())
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        }
    }

    fun uriPermit(){

        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        val ur = intent.data

        val resInfoList = packageManager!!.queryIntentActivities(
            intent,
            PackageManager.MATCH_DEFAULT_ONLY
        )
        for (resolveInfo in resInfoList) {
            val packageName = resolveInfo.activityInfo.packageName
            grantUriPermission(
                packageName,
                ur,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }
        this.startActivityForResult(intent, PICK_IMAGE )

    }


}
