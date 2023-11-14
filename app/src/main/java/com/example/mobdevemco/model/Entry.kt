package com.example.mobdevemco.model


class Entry (var title: String, var locationName: String, var images: ArrayList<EntryImages>, var description: String){
    var id: Long = -1
    var createdAt : CustomDateTime = CustomDateTime()
    constructor(title: String,
                locationName: String,
                images: ArrayList<EntryImages>,
                description: String,
                createdAt: CustomDateTime)
            : this(title, locationName, images, description) {
        this.title = title
        this.locationName = locationName
        this.images = images
        this.description = description
        this.createdAt = createdAt
    }
    constructor(title: String,
                locationName: String,
                images: ArrayList<EntryImages>,
                description: String,
                createdAt: CustomDateTime,
                id: Long)
            : this(title, locationName, images, description) {
        this.title = title
        this.locationName = locationName
        this.images = images
        this.description = description
        this.createdAt = createdAt
        this.id = id
    }

    fun getTitle(): String {
        return this.title
    }

    fun getLocationName(): String {
        return this.locationName
    }

    fun getImages(): ArrayList<EntryImages> {
        return this.images
    }

    fun getDescription(): String {
        return this.description
    }

    fun getCreatedAt(): CustomDateTime {
        return this.createdAt
    }

    fun getId(): Long {
        return this.id
    }
}

