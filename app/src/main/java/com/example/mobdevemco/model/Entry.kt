package com.example.mobdevemco.model


class Entry (var title: String, var locationName: String, var images: ArrayList<EntryImages>, var description: String){
    var id = -1
    var createdAt : CustomDate = CustomDate()
    constructor(title: String, locationName: String, images: ArrayList<EntryImages>, description: String, createdAt: CustomDate) : this(title, locationName, images, description) {
        this.title = title
        this.locationName = locationName
        this.images = images
        this.description = description
        this.createdAt = createdAt
    }
    constructor(title: String, locationName: String, images: ArrayList<EntryImages>, description: String, createdAt: CustomDate, id: Int) : this(title, locationName, images, description) {
        this.title = title
        this.locationName = locationName
        this.images = images
        this.description = description
        this.createdAt = createdAt
        this.id = id
    }
}
//    var title: String
//        private set
//    var locationName: String
//        private set
//    var imageId: Int
//        private set
//    var description: String
//        private set
//    var createdAt: com.example.mobdevemco.model.CustomDate
//        private set


//    constructor(title: String, locationName: String, imageId: Int, description: String, createdAt: com.example.mobdevemco.model.CustomDate){
//        this.title = title
//        this.locationName = locationName
//        this.imageId = imageId
//        this.description = description
//        this.createdAt = createdAt
//    }
//
//    constructor(title: String, locationName: String, imageId: Int, description: String){
//        this.title = title
//        this.locationName = locationName
//        this.imageId = imageId
//        this.description = description
//        this.createdAt = com.example.mobdevemco.model.CustomDate()
//    }

