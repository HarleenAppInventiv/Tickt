package com.app.amazons3library

import android.content.Context
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.services.s3.AmazonS3Client

object AmazonUtils {
    private var sS3Client: AmazonS3Client? = null
    private var sCredProvider: CognitoCachingCredentialsProvider? = null
    private var sTransferUtility: TransferUtility? = null
    /**
     * Gets an instance of the TransferUtility which is constructed using the
     * given Context
     *
     * @param context
     * @return a TransferUtility instance
     */
    fun getTransferUtility(context: Context): TransferUtility? {
        if (sTransferUtility == null) {
            sTransferUtility = TransferUtility.builder()
                    .context(context.applicationContext)
                    .s3Client(
                        getS3Client(
                            context.applicationContext
                        )
                    )
                    .build()
        }
        return sTransferUtility
    }

    /**
     * Gets an instance of a S3 client which is constructed using the given
     * Context.
     *
     * @param context An Context instance.
     * @return A default S3 client.
     */
    fun getS3Client(context: Context): AmazonS3Client? {
        if (sS3Client == null) {
            sS3Client = AmazonS3Client(
                getCredProvider(context.applicationContext)
            )
            sS3Client!!.endpoint =
                AmazonS3Constants.END_POINT
        }
        return sS3Client
    }

    /**
     * Gets an instance of CognitoCachingCredentialsProvider which is
     * constructed using the given Context.
     *
     * @param context An Context instance.
     * @return A default credential provider.
     */
    fun getCredProvider(context: Context?): CognitoCachingCredentialsProvider? {
        if (sCredProvider == null) {
            sCredProvider = CognitoCachingCredentialsProvider(
                    context,
                AmazonS3Constants.AMAZON_POOLID,  // Identity Pool ID
                AmazonS3Constants.REGIONS // Region
            )
        }
        return sCredProvider
    }

    /**
     * Converts number of bytes into proper scale.
     *
     * @param bytes number of bytes to be converted.
     * @return A string that represents the bytes in a proper scale.
     */
    fun getBytesString(bytes: Long): String {
        val quantifiers = arrayOf(
                "KB", "MB", "GB", "TB"
        )
        var speedNum = bytes.toDouble()
        var i = 0
        while (true) {
            if (i >= quantifiers.size) {
                return ""
            }
            speedNum /= 1024.0
            if (speedNum < 512) {
                return String.format("%.2f", speedNum) + " " + quantifiers[i]
            }
            i++
        }
    }
}