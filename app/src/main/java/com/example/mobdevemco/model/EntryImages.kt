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
}