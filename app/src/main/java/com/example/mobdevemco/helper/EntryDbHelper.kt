package com.example.mobdevemco.helper

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import com.example.mobdevemco.model.CustomDateTime
import com.example.mobdevemco.model.Entry
import com.example.mobdevemco.model.EntryImages
import kotlin.Int
import kotlin.arrayOf


class EntryDbHelper(context: Context?) :
    SQLiteOpenHelper(context, DbReferences.DATABASE_NAME, null, DbReferences.DATABASE_VERSION) {
    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL(DbReferences.ENTRIES_CREATE_TABLE_STATEMENT)
        sqLiteDatabase.execSQL(DbReferences.ENTRY_IMAGES_CREATE_TABLE_STATEMENT)
    }

    // Called when a new version of the DB is present; hence, an "upgrade" to a newer version
    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {
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
                DbReferences.ENTRIES_COLUMN_NAME_CREATED_AT + " DESC, " ,
                null
            )
            val entries: ArrayList<Entry> = ArrayList<Entry>()
            while (e.moveToNext()) {
                val imgQuery = database.query(
                    DbReferences.TABLE_NAME_ENTRY_IMAGES,
                    null,
                    DbReferences.ENTRY_IMAGES_COLUMN_NAME_ENTRY_ID + "=?",
                    arrayOf(e.getString(e.getColumnIndexOrThrow(DbReferences.ENTRIES_COLUMN_NAME_TITLE))),
                    null,
                    null,
                    DbReferences._ID,
                    null,
                )
                val imgArray : ArrayList<EntryImages> = ArrayList<EntryImages>()
                while(imgQuery.moveToNext()){
                    imgArray.add(
                        EntryImages(
                            imgQuery.getLong(imgQuery.getColumnIndexOrThrow(DbReferences.ENTRY_IMAGES_COLUMN_NAME_ENTRY_ID)),
                            Uri.parse(imgQuery.getString(imgQuery.getColumnIndexOrThrow(DbReferences.ENTRY_IMAGES_COLUMN_NAME_URI))),
                            imgQuery.getLong(imgQuery.getColumnIndexOrThrow(DbReferences.ENTRY_IMAGES_COLUMN_NAME_ENTRY_ID))
                        )
                    )
                }
                imgQuery.close()

                entries.add(
                    Entry(
                        e.getString(e.getColumnIndexOrThrow(DbReferences.ENTRIES_COLUMN_NAME_TITLE)),
                        e.getString(e.getColumnIndexOrThrow(DbReferences.ENTRIES_COLUMN_NAME_LOCATION_NAME)),
                        imgArray,
                        e.getString(e.getColumnIndexOrThrow(DbReferences.ENTRIES_COLUMN_NAME_DESCRIPTION)),
                        CustomDateTime(e.getString(e.getColumnIndexOrThrow(DbReferences.ENTRIES_COLUMN_NAME_CREATED_AT))),
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
    fun insertEntry(e: Entry): Long {
        val database = this.writableDatabase

        // Create a new map of values, where column names are the keys
        val values = ContentValues()
        values.put(DbReferences.ENTRIES_COLUMN_NAME_TITLE, e.getTitle())
        values.put(DbReferences.ENTRIES_COLUMN_NAME_LOCATION_NAME, e.getLocationName())
        values.put(DbReferences.ENTRIES_COLUMN_NAME_DESCRIPTION, e.getDescription())
        values.put(DbReferences.ENTRIES_COLUMN_NAME_CREATED_AT, e.getCreatedAt().toString())

        // The actual insertion operation. As inserting returns the primary key value of the new
        // row, we can use this and return it to whomever is calling so they can be aware of what
        // ID the new contact was referenced with.
        val _id = database.insert(DbReferences.TABLE_NAME_ENTRIES, null, values)

        for(image in e.getImages()){
            val imgValues = ContentValues()
            imgValues.put(DbReferences.ENTRY_IMAGES_COLUMN_NAME_ENTRY_ID, e.getId())
            imgValues.put(DbReferences.ENTRY_IMAGES_COLUMN_NAME_URI, image.getEntryId())

            val imgId = database.insert(DbReferences.TABLE_NAME_ENTRY_IMAGES, null, imgValues)
        }

        database.close()
        return _id
    }

//    // Performs an UPDATE operation by comparing the old contact with the new contact. This method
//    // tries to reduce the length of the update statement by only including attributes that have
//    // been changed. If no changed are present, the update statement is simply not called.
//    fun updateContact(cOld: Contact, cNew: Contact) {
//        var withChanges = false
//        val values = ContentValues()
//        if (!cNew.getLastName().equals(cOld.getLastName())) {
//            values.put(DbReferences.COLUMN_NAME_LAST_NAME, cNew.getLastName())
//            withChanges = true
//        }
//        if (!cNew.getFirstName().equals(cOld.getFirstName())) {
//            values.put(DbReferences.COLUMN_NAME_FIRST_NAME, cNew.getFirstName())
//            withChanges = true
//        }
//        if (!cNew.getNumber().equals(cOld.getNumber())) {
//            values.put(DbReferences.COLUMN_NAME_NUMBER, cNew.getNumber())
//            withChanges = true
//        }
//        if (!cNew.getImageUri().equals(cOld.getImageUri())) {
//            values.put(DbReferences.COLUMN_NAME_IMAGE_URI, cNew.getImageUri().toString())
//            withChanges = true
//        }
//        if (withChanges) {
//            val database = this.writableDatabase
//            database.update(
//                DbReferences.TABLE_NAME,
//                values,
//                DbReferences._ID + " = ?", arrayOf(String.valueOf(cNew.getId()))
//            )
//            database.close()
//        }
//    }
//
//    // The delete contact method that takes in a contact object and uses its ID to find and delete
//    // the entry.
//    fun deleteContact(c: Contact) {
//        val database = this.writableDatabase
//        database.delete(
//            DbReferences.TABLE_NAME,
//            DbReferences._ID + " = ?", arrayOf(String.valueOf(c.getId()))
//        )
//        database.close()
//    }

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
        const val ENTRIES_CREATE_TABLE_STATEMENT =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_ENTRIES + " (" +
                    _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    ENTRIES_COLUMN_NAME_TITLE + " TEXT, " +
                    ENTRIES_COLUMN_NAME_LOCATION_NAME + " TEXT, " +
                    ENTRIES_COLUMN_NAME_DESCRIPTION + " TEXT, " +
                    ENTRIES_COLUMN_NAME_CREATED_AT + " TEXT)"
        const val ENTRIES_DROP_TABLE_STATEMENT = "DROP TABLE IF EXISTS " + TABLE_NAME_ENTRIES

        const val TABLE_NAME_ENTRY_IMAGES = "entry_images"
        const val ENTRY_IMAGES_COLUMN_NAME_ENTRY_ID = "entry_id"
        const val ENTRY_IMAGES_COLUMN_NAME_URI = "uri"
        const val ENTRY_IMAGES_CREATE_TABLE_STATEMENT =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_ENTRY_IMAGES + " (" +
                    _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "FOREIGN KEY (" + ENTRY_IMAGES_COLUMN_NAME_ENTRY_ID + ") REFERENCES " + TABLE_NAME_ENTRIES + " (" + _ID + ")" +
                    ENTRY_IMAGES_COLUMN_NAME_URI + " TEXT)"
        const val ENTRY_IMAGES_DROP_TABLE_STATEMENT = "DROP TABLE IF EXISTS " + TABLE_NAME_ENTRY_IMAGES
    }

    companion object {
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
