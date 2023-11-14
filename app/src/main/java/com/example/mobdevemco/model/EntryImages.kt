package com.example.mobdevemco.model

class EntryImages (var imageUri: String){
    var id: Long = -1
    var entryId: Long = -1
    constructor(entryId: Long,
                imageUri: String,
                id: Long)
            : this(imageUri) {
        this.entryId = entryId
        this.imageUri = imageUri
        this.id = id
    }
}