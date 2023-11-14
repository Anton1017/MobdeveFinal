package com.example.mobdevemco.model

import android.net.Uri

class EntryImages (var imageUri: Uri){
    var id: Long = -1
    var entryId: Long = -1
    constructor(entryId: Long,
                imageUri: Uri,
                id: Long)
            : this(imageUri) {
        this.entryId = entryId
        this.imageUri = imageUri
        this.id = id
    }

    fun getEntryId(): Long {
        return this.entryId
    }

    fun getImageUri(): Uri {
        return this.imageUri
    }

    fun getId(): Long {
        return this.id
    }
}