package com.app.amazons3library.imageutils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.net.Uri
import rx.Observable
import java.io.File

class ImageCompressor private constructor(private val context: Context) {
    //max width and height values of the compressed image is taken as 612x816
    private var maxWidth = 612.0f
    private var maxHeight = 816.0f
    private var compressFormat = CompressFormat.JPEG
    private var bitmapConfig = Bitmap.Config.ARGB_8888
    private var quality = 60
    private var destinationDirectoryPath: String = context.cacheDir.path + File.pathSeparator + FileUtils.FILES_PATH

    fun compressToFile(file: File?): File {
        return ImageUtil.compressImage(
            context,
            Uri.fromFile(file),
            maxWidth,
            maxHeight,
            compressFormat,
            bitmapConfig,
            quality,
            destinationDirectoryPath
        )
    }

    fun compressToBitmap(file: File?): Bitmap {
        return ImageUtil.getScaledBitmap(
            context,
            Uri.fromFile(file),
            maxWidth,
            maxHeight,
            bitmapConfig,
            quality
        )!!
    }

    fun compressToFileAsObservable(file: File?): Observable<File> {
        return Observable.defer { Observable.just(compressToFile(file)) }
    }

    fun compressToBitmapAsObservable(file: File?): Observable<Bitmap> {
        return Observable.defer { Observable.just(compressToBitmap(file)) }
    }

    fun setQuality(quality: Int) {
        this.quality = quality
    }

    class Builder(context: Context) {
        private val compressor: ImageCompressor
        fun setMaxWidth(maxWidth: Float): Builder {
            compressor.maxWidth = maxWidth
            return this
        }

        fun setMaxHeight(maxHeight: Float): Builder {
            compressor.maxHeight = maxHeight
            return this
        }

        fun setCompressFormat(compressFormat: CompressFormat): Builder {
            compressor.compressFormat = compressFormat
            return this
        }

        fun setBitmapConfig(bitmapConfig: Bitmap.Config): Builder {
            compressor.bitmapConfig = bitmapConfig
            return this
        }

        fun setQuality(quality: Int): Builder {
            compressor.quality = quality
            return this
        }

        fun setDestinationDirectoryPath(destinationDirectoryPath: String): Builder {
            compressor.destinationDirectoryPath = destinationDirectoryPath
            return this
        }

        fun build(): ImageCompressor {
            return compressor
        }

        init {
            compressor =
                ImageCompressor(context)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ImageCompressor? = null

        @JvmStatic
        fun getDefault(context: Context): ImageCompressor {
            if (INSTANCE == null) {
                synchronized(ImageCompressor::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE =
                            ImageCompressor(
                                context
                            )
                    }
                }
            }
            return INSTANCE as ImageCompressor
        }
    }

}