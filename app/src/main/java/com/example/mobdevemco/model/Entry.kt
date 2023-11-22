package com.example.mobdevemco.model


class Entry (private var title: String,
             private var locationName: String,
             private var images: ArrayList<EntryImages>,
             private var description: String,
             private var originalLatitude: Double,
             private var originalLongitude: Double,
             adjustedLatitude: Double,
             adjustedLongitude: Double,
             private var accuracy: Float,
){
    private var id: Long = -1
    private var createdAt : CustomDateTime = CustomDateTime()
    private var adjustedLatitude: Double = 0.0
    private var adjustedLongitude: Double = 0.0
    constructor(title: String,
                locationName: String,
                images: ArrayList<EntryImages>,
                description: String,
                createdAt: CustomDateTime,
                originalLatitude: Double,
                originalLongitude: Double,
                adjustedLatitude: Double,
                adjustedLongitude: Double,
                accuracy: Float,
        )
            : this(title, locationName, images, description, originalLatitude, originalLongitude, adjustedLatitude, adjustedLongitude, accuracy) {
        this.title = title
        this.locationName = locationName
        this.images = images
        this.description = description
        this.createdAt = createdAt
        this.originalLatitude = originalLatitude
        this.originalLongitude = originalLongitude
        this.adjustedLatitude = adjustedLatitude
        this.adjustedLongitude = adjustedLongitude
        this.accuracy = accuracy
    }
    constructor(
        title: String,
        locationName: String,
        images: ArrayList<EntryImages>,
        description: String,
        createdAt: CustomDateTime,
        originalLatitude: Double,
        originalLongitude: Double,
        adjustedLatitude: Double,
        adjustedLongitude: Double,
        accuracy: Float,
        id: Long)
            : this(title, locationName, images, description, originalLatitude, originalLongitude, adjustedLatitude, adjustedLongitude, accuracy) {
        this.title = title
        this.locationName = locationName
        this.images = images
        this.description = description
        this.createdAt = createdAt
        this.originalLatitude = originalLatitude
        this.originalLongitude = originalLongitude
        this.adjustedLatitude = adjustedLatitude
        this.adjustedLongitude = adjustedLongitude
        this.accuracy = accuracy
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

    fun getOriginalLatitude(): Double {
        return this.originalLatitude
    }

    fun getOriginalLongitude(): Double {
        return this.originalLongitude
    }

    fun getAdjustedLatitude(): Double {
        return this.adjustedLatitude
    }

    fun getAdjustedLongitude(): Double {
        return this.adjustedLongitude
    }

    fun getAccuracy() : Float {
        return this.accuracy
    }

    fun getId(): Long {
        return this.id
    }

    companion object {
        val TITLE = "TITLE"
        val LOCATION_NAME = "LOCATION_NAME"
        val IMAGES = "IMAGES"
        val DESCRIPTION = "DESCRIPTION"
        val CREATED_AT = "CREATED_AT"
        val ID = "ID"
    }
}

