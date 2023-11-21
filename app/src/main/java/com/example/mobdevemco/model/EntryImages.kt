package com.example.mobdevemco.model

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream
import java.io.Serializable

class EntryImages (private var bitmap: Bitmap) {
    private var entryId: Long = -1
    private var id: Long = -1
    constructor(entryId: Long,
                bitmap: Bitmap,
                id: Long)
            : this(bitmap) {
        this.entryId = entryId
        this.bitmap = bitmap
        this.id = id
    }

    fun getEntryId(): Long {
        return this.entryId
    }

    fun getBitmap(): Bitmap {
        return this.bitmap
    }

    fun getId(): Long {
        return this.id
    }

    fun toByteArrayStream(): ByteArray {
        val stream = ByteArrayOutputStream()
        this.bitmap.compress( Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

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