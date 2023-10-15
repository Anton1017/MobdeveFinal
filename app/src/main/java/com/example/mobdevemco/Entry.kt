package com.example.mobdevemco
import CustomDate


class Entry{
    var title: String
        private set
    var imageId: Int
        private set
    var description: String
        private set
    var createdAt: CustomDate
        private set


    constructor(title: String, imageId: Int, description: String, createdAt: CustomDate){
        this.title = title
        this.imageId = imageId
        this.description = description
        this.createdAt = createdAt
    }

    constructor(title: String, imageId: Int, description: String){
        this.title = title
        this.imageId = imageId
        this.description = description
        createdAt = CustomDate()
    }

}