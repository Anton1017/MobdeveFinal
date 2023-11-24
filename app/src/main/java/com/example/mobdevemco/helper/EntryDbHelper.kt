package com.example.mobdevemco.helper

import android.content.ContentValues
import android.content.Context
import android.content.ContextWrapper
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.example.mobdevemco.model.CustomDateTime
import com.example.mobdevemco.model.Entry
import com.example.mobdevemco.model.EntryImages
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream


class EntryDbHelper(context: Context?) :
    SQLiteOpenHelper(context, DbReferences.DATABASE_NAME, null, DbReferences.DATABASE_VERSION) {
    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL(DbReferences.ENTRIES_CREATE_TABLE_STATEMENT)
        sqLiteDatabase.execSQL(DbReferences.ENTRY_IMAGES_CREATE_TABLE_STATEMENT)
    }

    // Called when a new version of the DB is present; hence, an "upgrade" to a newer version
    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {
        val e = sqLiteDatabase.query(DbReferences.TABLE_NAME_ENTRIES,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        )
        while(e.moveToNext()){
            val entry = Entry(
                e.getString(e.getColumnIndexOrThrow(DbReferences.ENTRIES_COLUMN_NAME_TITLE)),
                e.getString(e.getColumnIndexOrThrow(DbReferences.ENTRIES_COLUMN_NAME_LOCATION_NAME)),
                this.getEntryImages(e.getLong(e.getColumnIndexOrThrow(DbReferences._ID)), sqLiteDatabase),
                e.getString(e.getColumnIndexOrThrow(DbReferences.ENTRIES_COLUMN_NAME_DESCRIPTION)),
                CustomDateTime(e.getString(e.getColumnIndexOrThrow(DbReferences.ENTRIES_COLUMN_NAME_CREATED_AT))),
                e.getDouble(e.getColumnIndexOrThrow(DbReferences.ENTRIES_COLUMN_NAME_LATITUDE)),
                e.getDouble(e.getColumnIndexOrThrow(DbReferences.ENTRIES_COLUMN_NAME_LONGITUDE)),
                e.getDouble(e.getColumnIndexOrThrow(DbReferences.ENTRIES_COLUMN_NAME_ADJUSTED_LATITUDE)),
                e.getDouble(e.getColumnIndexOrThrow(DbReferences.ENTRIES_COLUMN_NAME_ADJUSTED_LONGITUDE)),
                e.getFloat(e.getColumnIndexOrThrow(DbReferences.ENTRIES_COLUMN_NAME_ACCURACY)),
                e.getLong(e.getColumnIndexOrThrow(DbReferences._ID))
            )
            deleteAllEntryImages(entry)

        }
        sqLiteDatabase.execSQL(DbReferences.ENTRIES_DROP_TABLE_STATEMENT)
        sqLiteDatabase.execSQL(DbReferences.ENTRY_IMAGES_DROP_TABLE_STATEMENT)
        onCreate(sqLiteDatabase)
    }

    val allEntriesDefault: ArrayList<Entry>
        // Method that returns an ArrayList of all stored contacts. This method was named with the term
        get() {
            val database = this.readableDatabase
            val e = database.query(
                DbReferences.TABLE_NAME_ENTRIES,
                null,
                null,
                null,
                null,
                null,
                null,
                null
            )
            val entries: ArrayList<Entry> = ArrayList<Entry>()
            while (e.moveToNext()) {
                entries.add(
                    Entry(
                        e.getString(e.getColumnIndexOrThrow(DbReferences.ENTRIES_COLUMN_NAME_TITLE)),
                        e.getString(e.getColumnIndexOrThrow(DbReferences.ENTRIES_COLUMN_NAME_LOCATION_NAME)),
                        this.getEntryImages(e.getLong(e.getColumnIndexOrThrow(DbReferences._ID)), database),
                        e.getString(e.getColumnIndexOrThrow(DbReferences.ENTRIES_COLUMN_NAME_DESCRIPTION)),
                        CustomDateTime(e.getString(e.getColumnIndexOrThrow(DbReferences.ENTRIES_COLUMN_NAME_CREATED_AT))),
                        e.getDouble(e.getColumnIndexOrThrow(DbReferences.ENTRIES_COLUMN_NAME_LATITUDE)),
                        e.getDouble(e.getColumnIndexOrThrow(DbReferences.ENTRIES_COLUMN_NAME_LONGITUDE)),
                        e.getDouble(e.getColumnIndexOrThrow(DbReferences.ENTRIES_COLUMN_NAME_ADJUSTED_LATITUDE)),
                        e.getDouble(e.getColumnIndexOrThrow(DbReferences.ENTRIES_COLUMN_NAME_ADJUSTED_LONGITUDE)),
                        e.getFloat(e.getColumnIndexOrThrow(DbReferences.ENTRIES_COLUMN_NAME_ACCURACY)),
                        e.getLong(e.getColumnIndexOrThrow(DbReferences._ID))
                    )
                )
            }
            e.close()
            database.close()
            return entries
        }

    // The insert operation, which takes a contact object as a parameter. It also returns the ID of
    // the row so that the Contact can have that properly referenced within itself.
    @Synchronized
    fun insertEntry(e: Entry, context: Context): Long {
        val database = this.writableDatabase

        // Create a new map of values, where column names are the keys
        val values = ContentValues()
        values.put(DbReferences.ENTRIES_COLUMN_NAME_TITLE, e.getTitle())
        values.put(DbReferences.ENTRIES_COLUMN_NAME_LOCATION_NAME, e.getLocationName())
        values.put(DbReferences.ENTRIES_COLUMN_NAME_DESCRIPTION, e.getDescription())
        values.put(DbReferences.ENTRIES_COLUMN_NAME_CREATED_AT, e.getCreatedAt().toString())
        values.put(DbReferences.ENTRIES_COLUMN_NAME_LATITUDE, e.getOriginalLatitude())
        values.put(DbReferences.ENTRIES_COLUMN_NAME_LONGITUDE, e.getOriginalLongitude())
        values.put(DbReferences.ENTRIES_COLUMN_NAME_ADJUSTED_LATITUDE, e.getAdjustedLatitude())
        values.put(DbReferences.ENTRIES_COLUMN_NAME_ADJUSTED_LONGITUDE, e.getAdjustedLongitude())
        values.put(DbReferences.ENTRIES_COLUMN_NAME_ACCURACY, e.getAccuracy())

        // The actual insertion operation. As inserting returns the primary key value of the new
        // row, we can use this and return it to whomever is calling so they can be aware of what
        // ID the new contact was referenced with.
        val _id = database.insert(DbReferences.TABLE_NAME_ENTRIES, null, values)

        this.insertEntryImages(_id, e.getImages(), database, context)

        database.close()
        return _id
    }

    @Synchronized
    fun getEntry(id: Long): Entry? {
        val database = this.readableDatabase
        val e = database.query(
            DbReferences.TABLE_NAME_ENTRIES,
            null,
            DbReferences._ID + " =?",
            arrayOf(id.toString()),
            null,
            null,
            null,
            null
        )
        Log.d("TAG", e.toString())


        var entry: Entry? = null
        while(e.moveToNext()){
            entry = Entry(
                e.getString(e.getColumnIndexOrThrow(DbReferences.ENTRIES_COLUMN_NAME_TITLE)),
                e.getString(e.getColumnIndexOrThrow(DbReferences.ENTRIES_COLUMN_NAME_LOCATION_NAME)),
                this.getEntryImages(e.getLong(e.getColumnIndexOrThrow(DbReferences._ID)), database),
                e.getString(e.getColumnIndexOrThrow(DbReferences.ENTRIES_COLUMN_NAME_DESCRIPTION)),
                CustomDateTime(e.getString(e.getColumnIndexOrThrow(DbReferences.ENTRIES_COLUMN_NAME_CREATED_AT))),
                e.getDouble(e.getColumnIndexOrThrow(DbReferences.ENTRIES_COLUMN_NAME_LATITUDE)),
                e.getDouble(e.getColumnIndexOrThrow(DbReferences.ENTRIES_COLUMN_NAME_LONGITUDE)),
                e.getDouble(e.getColumnIndexOrThrow(DbReferences.ENTRIES_COLUMN_NAME_ADJUSTED_LATITUDE)),
                e.getDouble(e.getColumnIndexOrThrow(DbReferences.ENTRIES_COLUMN_NAME_ADJUSTED_LONGITUDE)),
                e.getFloat(e.getColumnIndexOrThrow(DbReferences.ENTRIES_COLUMN_NAME_ACCURACY)),
                e.getLong(e.getColumnIndexOrThrow(DbReferences._ID))
            )
        }

        e.close()
        database.close()
        return entry
    }

    // Performs an UPDATE operation by comparing the old contact with the new contact. This method
    // tries to reduce the length of the update statement by only including attributes that have
    // been changed. If no changed are present, the update statement is simply not called.
    fun updateEntry(eOld: Entry, eNew: Entry, context: Context) {
        var withChanges = false
        val values = ContentValues()
        if (eNew.getTitle() != eOld.getTitle()) {
            values.put(DbReferences.ENTRIES_COLUMN_NAME_TITLE, eNew.getTitle())
            withChanges = true
        }
        if (eNew.getLocationName() != eOld.getLocationName()) {
            values.put(DbReferences.ENTRIES_COLUMN_NAME_LOCATION_NAME, eNew.getLocationName())
            withChanges = true
        }
        if (eNew.getImages() != eOld.getImages()) {
            this.deleteAllEntryImages(eNew)
            val db = this.writableDatabase
            this.insertEntryImages(eNew.getId(), eNew.getImages(), db, context)
            db.close()
            withChanges = true
        }
        if (eNew.getDescription() != eOld.getDescription()) {
            values.put(DbReferences.ENTRIES_COLUMN_NAME_DESCRIPTION, eNew.getDescription())
            withChanges = true
        }
        if (eNew.getCreatedAt() != eOld.getCreatedAt()) {
            values.put(DbReferences.ENTRIES_COLUMN_NAME_CREATED_AT, eNew.getCreatedAt().toString())
            withChanges = true
        }

        if (eNew.getAdjustedLatitude() != eOld.getAdjustedLatitude()) {
            values.put(DbReferences.ENTRIES_COLUMN_NAME_ADJUSTED_LATITUDE, eNew.getAdjustedLatitude())
            withChanges = true
        }

        if (eNew.getAdjustedLongitude() != eOld.getAdjustedLongitude()) {
            values.put(DbReferences.ENTRIES_COLUMN_NAME_ADJUSTED_LONGITUDE, eNew.getAdjustedLongitude())
            withChanges = true
        }

        if (withChanges) {
            val database = this.writableDatabase
            database.update(
                DbReferences.TABLE_NAME_ENTRIES,
                values,
                DbReferences._ID + " = ?", arrayOf(eNew.getId().toString())
            )
            database.close()
        }
    }

    // The delete contact method that takes in a contact object and uses its ID to find and delete
    // the entry.
    fun deleteEntry(e: Entry) {
        this.deleteAllEntryImages(e)

        val database = this.writableDatabase
        database.delete(
            DbReferences.TABLE_NAME_ENTRIES,
            DbReferences._ID + " = ?", arrayOf(e.getId().toString())
        )
        database.close()
    }

    private fun deleteAllEntryImages(e: Entry) {
        val database = this.writableDatabase

        database.delete(
            DbReferences.TABLE_NAME_ENTRY_IMAGES,
            DbReferences.ENTRY_IMAGES_COLUMN_NAME_ENTRY_ID + "=?",
            arrayOf(e.getId().toString())
        )

        for(eImage in e.getImages()){
            val file = File(eImage.getUri().toString())
            try{
                val bool = file.delete()
                if(!bool)
                    throw Exception("File not deleted.")
            }catch(e: Exception){
                e.message?.let { Log.d("EXCEPTION", it) }
            }
        }

        database.close()
    }

    private fun getEntryImages(id: Long, database: SQLiteDatabase): ArrayList<EntryImages> {
        val imgQuery = database.query(
            DbReferences.TABLE_NAME_ENTRY_IMAGES,
            null,
            DbReferences.ENTRY_IMAGES_COLUMN_NAME_ENTRY_ID + "=?",
            arrayOf(id.toString()),
            null,
            null,
            DbReferences._ID,
            null,
        )
        Log.d("imgQuery", id.toString())
        val imgArray : ArrayList<EntryImages> = ArrayList<EntryImages>()
        while(imgQuery.moveToNext()){

            val imgUri = Uri.parse(
                imgQuery.getString(
                    imgQuery.getColumnIndexOrThrow(DbReferences.ENTRY_IMAGES_COLUMN_NAME_IMAGE_URI)
                )
            )
            imgArray.add(
                EntryImages(
                    imgQuery.getLong(imgQuery.getColumnIndexOrThrow(DbReferences.ENTRY_IMAGES_COLUMN_NAME_ENTRY_ID)),
                    imgUri,
                    imgQuery.getLong(imgQuery.getColumnIndexOrThrow(DbReferences.ENTRY_IMAGES_COLUMN_NAME_ENTRY_ID))
                )
            )
        }
        imgQuery.close()

        return imgArray
    }

    private fun insertEntryImages(id: Long, imgArray: ArrayList<EntryImages>, database: SQLiteDatabase, context: Context){
        for(image in imgArray){
            val imgValues = ContentValues()
            imgValues.put(DbReferences.ENTRY_IMAGES_COLUMN_NAME_ENTRY_ID, id)
            imgValues.put(DbReferences.ENTRY_IMAGES_COLUMN_NAME_IMAGE_URI, image.getUri().toString())

            val imgId = database.insert(DbReferences.TABLE_NAME_ENTRY_IMAGES, null, imgValues)
            val absolutePath = saveImageToInternalStorage(image.getUri(), imgId.toString(), context)

            val values = ContentValues()
            values.put(DbReferences.ENTRY_IMAGES_COLUMN_NAME_IMAGE_URI, absolutePath)
            database.update(
                DbReferences.TABLE_NAME_ENTRY_IMAGES,
                values,
                DbReferences._ID + " = ?", arrayOf(imgId.toString())
            )
            image.setTemporaryImage(false)
        }
    }
    private fun saveImageToInternalStorage(uri: Uri, fileName: String, context: Context): String? {
        val inputStream: InputStream = context.contentResolver.openInputStream(uri)!!
        val bitmap: Bitmap = BitmapFactory.decodeStream(
            inputStream
        )

        val cw = ContextWrapper(context)
        // path to /data/data/yourapp/app_data/imageRepo
        val directory = cw.getDir("imageRepo", Context.MODE_PRIVATE)
        // Create imageRepo
        val myPath = File(directory, fileName)
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(myPath)
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fos!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return directory.path + "/$fileName"
    }

    // Our class for holding DB references.
    private object DbReferences {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "entry_database.db"
        const val _ID = "id"

        const val TABLE_NAME_ENTRIES = "entries"
        const val ENTRIES_COLUMN_NAME_TITLE = "title"
        const val ENTRIES_COLUMN_NAME_LOCATION_NAME = "location_name"
        const val ENTRIES_COLUMN_NAME_DESCRIPTION = "description"
        const val ENTRIES_COLUMN_NAME_CREATED_AT = "created_at"
        const val ENTRIES_COLUMN_NAME_LATITUDE = "latitude"
        const val ENTRIES_COLUMN_NAME_LONGITUDE = "longitude"
        const val ENTRIES_COLUMN_NAME_ADJUSTED_LATITUDE = "adjusted_latitude"
        const val ENTRIES_COLUMN_NAME_ADJUSTED_LONGITUDE = "adjusted_longitude"
        const val ENTRIES_COLUMN_NAME_ACCURACY = "accuracy"
        const val ENTRIES_CREATE_TABLE_STATEMENT =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_ENTRIES + " (" +
                    _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    ENTRIES_COLUMN_NAME_TITLE + " TEXT, " +
                    ENTRIES_COLUMN_NAME_LOCATION_NAME + " TEXT, " +
                    ENTRIES_COLUMN_NAME_DESCRIPTION + " TEXT, " +
                    ENTRIES_COLUMN_NAME_CREATED_AT + " TEXT," +
                    ENTRIES_COLUMN_NAME_LATITUDE + " DOUBLE," +
                    ENTRIES_COLUMN_NAME_LONGITUDE + " DOUBLE," +
                    ENTRIES_COLUMN_NAME_ADJUSTED_LATITUDE + " DOUBLE," +
                    ENTRIES_COLUMN_NAME_ADJUSTED_LONGITUDE + " DOUBLE," +
                    ENTRIES_COLUMN_NAME_ACCURACY + " FLOAT)"
        const val ENTRIES_DROP_TABLE_STATEMENT = "DROP TABLE IF EXISTS " + TABLE_NAME_ENTRIES

        const val TABLE_NAME_ENTRY_IMAGES = "entry_images"
        const val ENTRY_IMAGES_COLUMN_NAME_ENTRY_ID = "entry_id"
        const val ENTRY_IMAGES_COLUMN_NAME_IMAGE_URI = "image_uri"
        const val ENTRY_IMAGES_CREATE_TABLE_STATEMENT =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_ENTRY_IMAGES + " (" +
                    _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    ENTRY_IMAGES_COLUMN_NAME_ENTRY_ID + " INTEGER, " +
                    ENTRY_IMAGES_COLUMN_NAME_IMAGE_URI + " TEXT," +
                    "FOREIGN KEY (" + ENTRY_IMAGES_COLUMN_NAME_ENTRY_ID + ") REFERENCES " + TABLE_NAME_ENTRIES + " (" + _ID + "))"

        const val ENTRY_IMAGES_DROP_TABLE_STATEMENT = "DROP TABLE IF EXISTS " + TABLE_NAME_ENTRY_IMAGES
    }

    companion object {
        val ENTRY_ID = "ENTRY_ID"
        // Our single instance of the class
        var instance: EntryDbHelper? = null

        // Method that ensures that we're only getting one instance of the helper class.
        @Synchronized
        fun getInstance(context: Context): EntryDbHelper? {
            if (instance == null) instance = EntryDbHelper(context.applicationContext)
            return instance
        }
    }
}
