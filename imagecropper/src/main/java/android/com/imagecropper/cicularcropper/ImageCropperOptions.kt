package android.com.imagecropper.cicularcropper

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import android.util.TypedValue


class ImageCropperOptions : Parcelable {
    var cropShape: CropImageView.CropShape
    var snapRadius: Float = 0.toFloat()
    var touchRadius: Float = 0.toFloat()
    var guidelines: CropImageView.Guidelines
    var scaleType: CropImageView.ScaleType
    var showCropOverlay: Boolean = false
    var showProgressBar: Boolean = false
    var autoZoomEnabled: Boolean = false
    var multiTouchEnabled: Boolean = false
    var maxZoom: Int = 0
    var initialCropWindowPaddingRatio: Float = 0.toFloat()
    var fixAspectRatio: Boolean = false
    var aspectRatioX: Int = 0
    var aspectRatioY: Int = 0
    var borderLineThickness: Float = 0.toFloat()
    var borderLineColor: Int = 0
    var borderCornerThickness: Float = 0.toFloat()
    var borderCornerOffset: Float = 0.toFloat()
    var borderCornerLength: Float = 0.toFloat()
    var borderCornerColor: Int = 0
    var guidelinesThickness: Float = 0.toFloat()
    var guidelinesColor: Int = 0
    var backgroundColor: Int = 0
    var minCropWindowWidth: Int = 0
    var minCropWindowHeight: Int = 0
    var minCropResultWidth: Int = 0
    var minCropResultHeight: Int = 0
    var maxCropResultWidth: Int = 0
    var maxCropResultHeight: Int = 0
    var activityTitle: String
    var activityMenuIconColor: Int = 0
    var outputUri: Uri
    var outputCompressFormat: Bitmap.CompressFormat
    var outputCompressQuality: Int = 0
    var outputRequestWidth: Int = 0
    var outputRequestHeight: Int = 0
    var outputRequestSizeOptions: CropImageView.RequestSizeOptions
    var noOutputImage: Boolean = false
    var initialCropWindowRectangle: Rect? = null
    var initialRotation: Int = 0
    var allowRotation: Boolean = false
    var allowFlipping: Boolean = false
    var allowCounterRotation: Boolean = false
    var rotationDegrees: Int = 0
    var flipHorizontally: Boolean = false
    var flipVertically: Boolean = false
    var toolbarColor = Color.WHITE

    /**
     * Init options with defaults.
     */
    constructor() {

        val dm = Resources.getSystem().displayMetrics

        cropShape = CropImageView.CropShape.RECTANGLE
        snapRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3f, dm)
        touchRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24f, dm)
        guidelines = CropImageView.Guidelines.ON_TOUCH
        scaleType = CropImageView.ScaleType.FIT_CENTER
        showCropOverlay = true
        showProgressBar = true
        autoZoomEnabled = true
        multiTouchEnabled = false
        maxZoom = 4
        initialCropWindowPaddingRatio = 0.1f

        fixAspectRatio = false
        aspectRatioX = 1
        aspectRatioY = 1

        borderLineThickness = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3f, dm)
        borderLineColor = Color.argb(170, 255, 255, 255)
        borderCornerThickness = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, dm)
        borderCornerOffset = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5f, dm)
        borderCornerLength = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14f, dm)
        borderCornerColor = Color.WHITE

        guidelinesThickness = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, dm)
        guidelinesColor = Color.argb(170, 255, 255, 255)
        backgroundColor = Color.argb(119, 0, 0, 0)

        minCropWindowWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 42f, dm).toInt()
        minCropWindowHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 42f, dm).toInt()
        minCropResultWidth = 40
        minCropResultHeight = 40
        maxCropResultWidth = 99999
        maxCropResultHeight = 99999

        activityTitle = ""
        activityMenuIconColor = 0

        outputUri = Uri.EMPTY
        outputCompressFormat = Bitmap.CompressFormat.JPEG
        outputCompressQuality = 90
        outputRequestWidth = 0
        outputRequestHeight = 0
        outputRequestSizeOptions = CropImageView.RequestSizeOptions.NONE
        noOutputImage = false

        initialCropWindowRectangle = null
        initialRotation = -1
        allowRotation = true
        allowFlipping = true
        allowCounterRotation = false
        rotationDegrees = 90
        flipHorizontally = false
        flipVertically = false
    }

    /**
     * Create object from parcel.
     */
    protected constructor(`in`: Parcel) {
        cropShape = CropImageView.CropShape.values()[`in`.readInt()]
        snapRadius = `in`.readFloat()
        touchRadius = `in`.readFloat()
        guidelines = CropImageView.Guidelines.values()[`in`.readInt()]
        scaleType = CropImageView.ScaleType.values()[`in`.readInt()]
        showCropOverlay = `in`.readByte().toInt() != 0
        showProgressBar = `in`.readByte().toInt() != 0
        autoZoomEnabled = `in`.readByte().toInt() != 0
        multiTouchEnabled = `in`.readByte().toInt() != 0
        maxZoom = `in`.readInt()
        initialCropWindowPaddingRatio = `in`.readFloat()
        fixAspectRatio = `in`.readByte().toInt() != 0
        aspectRatioX = `in`.readInt()
        aspectRatioY = `in`.readInt()
        borderLineThickness = `in`.readFloat()
        borderLineColor = `in`.readInt()
        borderCornerThickness = `in`.readFloat()
        borderCornerOffset = `in`.readFloat()
        borderCornerLength = `in`.readFloat()
        borderCornerColor = `in`.readInt()
        guidelinesThickness = `in`.readFloat()
        guidelinesColor = `in`.readInt()
        backgroundColor = `in`.readInt()
        minCropWindowWidth = `in`.readInt()
        minCropWindowHeight = `in`.readInt()
        minCropResultWidth = `in`.readInt()
        minCropResultHeight = `in`.readInt()
        maxCropResultWidth = `in`.readInt()
        maxCropResultHeight = `in`.readInt()
        activityTitle = `in`.readString()!!
        activityMenuIconColor = `in`.readInt()
        outputUri = `in`.readParcelable(Uri::class.java.classLoader)!!
        outputCompressFormat = Bitmap.CompressFormat.valueOf(`in`.readString()!!)
        outputCompressQuality = `in`.readInt()
        outputRequestWidth = `in`.readInt()
        outputRequestHeight = `in`.readInt()
        outputRequestSizeOptions = CropImageView.RequestSizeOptions.values()[`in`.readInt()]
        noOutputImage = `in`.readByte().toInt() != 0
        initialCropWindowRectangle = `in`.readParcelable(Rect::class.java.classLoader)
        initialRotation = `in`.readInt()
        allowRotation = `in`.readByte().toInt() != 0
        allowFlipping = `in`.readByte().toInt() != 0
        allowCounterRotation = `in`.readByte().toInt() != 0
        rotationDegrees = `in`.readInt()
        flipHorizontally = `in`.readByte().toInt() != 0
        flipVertically = `in`.readByte().toInt() != 0
        toolbarColor = `in`.readInt()
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(cropShape.ordinal)
        dest.writeFloat(snapRadius)
        dest.writeFloat(touchRadius)
        dest.writeInt(guidelines.ordinal)
        dest.writeInt(scaleType.ordinal)
        dest.writeByte((if (showCropOverlay) 1 else 0).toByte())
        dest.writeByte((if (showProgressBar) 1 else 0).toByte())
        dest.writeByte((if (autoZoomEnabled) 1 else 0).toByte())
        dest.writeByte((if (multiTouchEnabled) 1 else 0).toByte())
        dest.writeInt(maxZoom)
        dest.writeFloat(initialCropWindowPaddingRatio)
        dest.writeByte((if (fixAspectRatio) 1 else 0).toByte())
        dest.writeInt(aspectRatioX)
        dest.writeInt(aspectRatioY)
        dest.writeFloat(borderLineThickness)
        dest.writeInt(borderLineColor)
        dest.writeFloat(borderCornerThickness)
        dest.writeFloat(borderCornerOffset)
        dest.writeFloat(borderCornerLength)
        dest.writeInt(borderCornerColor)
        dest.writeFloat(guidelinesThickness)
        dest.writeInt(guidelinesColor)
        dest.writeInt(backgroundColor)
        dest.writeInt(minCropWindowWidth)
        dest.writeInt(minCropWindowHeight)
        dest.writeInt(minCropResultWidth)
        dest.writeInt(minCropResultHeight)
        dest.writeInt(maxCropResultWidth)
        dest.writeInt(maxCropResultHeight)
        dest.writeString(activityTitle)
        dest.writeInt(activityMenuIconColor)
        dest.writeParcelable(outputUri, flags)
        dest.writeString(outputCompressFormat.name)
        dest.writeInt(outputCompressQuality)
        dest.writeInt(outputRequestWidth)
        dest.writeInt(outputRequestHeight)
        dest.writeInt(outputRequestSizeOptions.ordinal)
        dest.writeInt(if (noOutputImage) 1 else 0)
        dest.writeParcelable(initialCropWindowRectangle, flags)
        dest.writeInt(initialRotation)
        dest.writeByte((if (allowRotation) 1 else 0).toByte())
        dest.writeByte((if (allowFlipping) 1 else 0).toByte())
        dest.writeByte((if (allowCounterRotation) 1 else 0).toByte())
        dest.writeInt(rotationDegrees)
        dest.writeByte((if (flipHorizontally) 1 else 0).toByte())
        dest.writeByte((if (flipVertically) 1 else 0).toByte())
        dest.writeInt(toolbarColor)
    }

    override fun describeContents(): Int {
        return 0
    }

    /**
     * Validate all the options are withing valid range.
     *
     * @throws IllegalArgumentException if any of the options is not valid
     */
    fun validate() {
        if (maxZoom < 0) {
            throw IllegalArgumentException("Cannot set max zoom to a number < 1")
        }
        if (touchRadius < 0) {
            throw IllegalArgumentException("Cannot set touch radius value to a number <= 0 ")
        }
        if (initialCropWindowPaddingRatio < 0 || initialCropWindowPaddingRatio >= 0.5) {
            throw IllegalArgumentException("Cannot set initial crop window padding value to a number < 0 or >= 0.5")
        }
        if (aspectRatioX <= 0) {
            throw IllegalArgumentException("Cannot set aspect ratio value to a number less than or equal to 0.")
        }
        if (aspectRatioY <= 0) {
            throw IllegalArgumentException("Cannot set aspect ratio value to a number less than or equal to 0.")
        }
        if (borderLineThickness < 0) {
            throw IllegalArgumentException("Cannot set line thickness value to a number less than 0.")
        }
        if (borderCornerThickness < 0) {
            throw IllegalArgumentException("Cannot set corner thickness value to a number less than 0.")
        }
        if (guidelinesThickness < 0) {
            throw IllegalArgumentException("Cannot set guidelines thickness value to a number less than 0.")
        }
        if (minCropWindowHeight < 0) {
            throw IllegalArgumentException("Cannot set min crop window height value to a number < 0 ")
        }
        if (minCropResultWidth < 0) {
            throw IllegalArgumentException("Cannot set min crop result width value to a number < 0 ")
        }
        if (minCropResultHeight < 0) {
            throw IllegalArgumentException("Cannot set min crop result height value to a number < 0 ")
        }
        if (maxCropResultWidth < minCropResultWidth) {
            throw IllegalArgumentException("Cannot set max crop result width to smaller value than min crop result width")
        }
        if (maxCropResultHeight < minCropResultHeight) {
            throw IllegalArgumentException("Cannot set max crop result height to smaller value than min crop result height")
        }
        if (outputRequestWidth < 0) {
            throw IllegalArgumentException("Cannot set request width value to a number < 0 ")
        }
        if (outputRequestHeight < 0) {
            throw IllegalArgumentException("Cannot set request height value to a number < 0 ")
        }
        if (rotationDegrees < 0 || rotationDegrees > 360) {
            throw IllegalArgumentException("Cannot set rotation degrees value to a number < 0 or > 360")
        }
    }

    companion object {

        @JvmField val CREATOR: Parcelable.Creator<ImageCropperOptions> = object : Parcelable.Creator<ImageCropperOptions> {
            override fun createFromParcel(`in`: Parcel): ImageCropperOptions {
                return ImageCropperOptions(`in`)
            }

            override fun newArray(size: Int): Array<ImageCropperOptions?> {
                return arrayOfNulls(size)
            }
        }
    }
}

