package com.example.mobdevemco.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.io.Serializable

class EntryImages (private var uri: Uri) {
    private var entryId: Long = -1
    private var id: Long = -1
    private var isTemporaryImage = false
    constructor(entryId: Long,
                uri: Uri,
                id: Long)
            : this(uri) {
        this.entryId = entryId
        this.uri = uri
        this.id = id
    }

    fun getBitmapFromInputStream(context: Context): Bitmap {
        val fileUri = if (isTemporaryImage) this.uri else Uri.fromFile(File(this.uri.toString()))
        val inputStream: InputStream? = context.contentResolver.openInputStream(fileUri)
        return BitmapFactory.decodeStream(
            inputStream
        )
    }

    fun getEntryId(): Long {
        return this.entryId
    }

    fun getUri(): Uri {
        return this.uri
    }

    fun getId(): Long {
        return this.id
    }

    fun setTemporaryImage(isTemporaryImage: Boolean) {
        this.isTemporaryImage = isTemporaryImage
    }

//    fun toByteArrayStream(): ByteArray {
//        val stream = ByteArrayOutputStream()
//        this.bitmap.compress( Bitmap.CompressFormat.PNG, 100, stream)
//        return stream.toByteArray()
//    }

    companion object{
        val supportedImageFormats = arrayOf<String>(
            "image/jpeg",
            "image/png",
            "image/webp"
        )
        val supportedCompressFormats = arrayOf<Bitmap.CompressFormat>(
            Bitmap.CompressFormat.JPEG,
            Bitmap.CompressFormat.PNG,
            Bitmap.CompressFormat.WEBP
        )
    }
}