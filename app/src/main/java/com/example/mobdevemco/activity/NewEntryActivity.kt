package com.example.mobdevemco.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobdevemco.adapter.EntryImageAdapter
import com.example.mobdevemco.databinding.ActivityCreateEntryBinding
import com.example.mobdevemco.helper.EntryDbHelper
import com.example.mobdevemco.model.Entry
import com.example.mobdevemco.model.EntryImages
import java.io.InputStream
import java.util.Locale
import java.util.concurrent.Executors


class NewEntryActivity : AppCompatActivity(), LocationListener{
    private lateinit var viewBinding: ActivityCreateEntryBinding
    private val locationPermissionCode = 2
    private lateinit var locationManager: LocationManager

    private lateinit var recyclerView: RecyclerView
    private lateinit var newImagesAdapter: EntryImageAdapter

    private val newImageArray: ArrayList<EntryImages> = ArrayList<EntryImages>()
    private var currEntry: Entry? = null

    private var entryDbHelper: EntryDbHelper? = null
    private val executorService = Executors.newSingleThreadExecutor()

    private val myActivityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            try {
                if (result.data != null) {
                    newImageArray.clear()
                    val count: Int? = result.data!!.clipData?.itemCount
                    // IF there are 2 or more images picked
                    if(count != null){
                        for (i in 0 until count) {
                            val inputStream: InputStream? = result.data!!.clipData?.getItemAt(i)?.uri?.let {
                                contentResolver.openInputStream(
                                    it
                                )
                            }
                            val bitmap: Bitmap = BitmapFactory.decodeStream(
                                inputStream
                            )
//                            val stream = ByteArrayOutputStream()
//                            bitmap.compress( Bitmap.CompressFormat.PNG, 100, stream)
//                            val byteArray = stream.toByteArray()
                            newImageArray.add( EntryImages(bitmap) )
                        }
                    //if only one image was picked
                    }else{
                        val inputStream: InputStream? = result.data!!.data?.let {
                            contentResolver.openInputStream(
                                it
                            )
                        }
                        BitmapFactory.decodeStream(
                            inputStream
                        ).let { EntryImages(it) }.let { newImageArray.add(it) }
                    }
                    newImagesAdapter.notifyDataSetChanged()
                }
            } catch (exception: Exception) {
                Log.d("TAG", "" + exception.localizedMessage)
            }
        } else{
            newImageArray.clear()
            newImagesAdapter.notifyDataSetChanged()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.viewBinding = ActivityCreateEntryBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        this.recyclerView = viewBinding.imageListRecyclerView
        this.newImagesAdapter = EntryImageAdapter(newImageArray)
        this.newImagesAdapter.setViewType(EntryImageAdapter.NEW_IMAGE_LAYOUT)

        val intent: Intent = intent
        val activityType = intent.getStringExtra(ACTIVITY_TYPE).orEmpty()

        if(activityType == ADD_ENTRY){
            getLocation()
        }else if(activityType == EDIT_ENTRY){
            executorService.execute {
                entryDbHelper = EntryDbHelper.getInstance(this@NewEntryActivity)

                currEntry = entryDbHelper?.getEntry(intent.getLongExtra(Entry.ID, -1))
                currEntry?.let { newImageArray.addAll(it.getImages()) }

                runOnUiThread {
                    viewBinding.locationText.text = currEntry?.getLocationName()
                    viewBinding.titleText.setText(currEntry?.getTitle())
                    viewBinding.descriptionText.setText(currEntry?.getDescription())
                    newImagesAdapter.notifyDataSetChanged()
                }
            }
        }

        this.recyclerView.adapter = this.newImagesAdapter

        viewBinding.createBtn.setOnClickListener(View.OnClickListener {
            if(doAllFieldHaveEntries()){
                executorService.execute {
                    entryDbHelper = EntryDbHelper.getInstance(this@NewEntryActivity)

                    if(activityType == ADD_ENTRY){
                        val newEntryId: Long? = entryDbHelper?.insertEntry(
                            Entry(
                                viewBinding.titleText.text.toString(),
                                viewBinding.locationText.text.toString(),
                                newImageArray,
                                viewBinding.descriptionText.text.toString()
                            )
                        )
                        runOnUiThread {
                            val i: Intent = Intent()
                            i.putExtra(EntryDbHelper.ENTRY_ID, newEntryId)
                            setResult(RESULT_OK, i)
                            finish()
                        }
                    }else if(activityType == EDIT_ENTRY){
                        currEntry?.let { it1 ->
                            entryDbHelper?.updateEntry(
                                it1,
                                Entry(
                                    viewBinding.titleText.text.toString(),
                                    viewBinding.locationText.text.toString(),
                                    newImageArray,
                                    viewBinding.descriptionText.text.toString(),
                                    it1.getCreatedAt(),
                                    it1.getId()
                                )
                            )
                        }
                        runOnUiThread {
                            val i: Intent = Intent()
                            setResult(RESULT_OK, i)
                            finish()
                        }
                    }
                }
            }else{
                Toast.makeText(
                    this@NewEntryActivity,
                    "Please make sure to fill up every field.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        viewBinding.cancelBtn.setOnClickListener(View.OnClickListener {
            finish()
        })

        val galleryImage = registerForActivityResult(ActivityResultContracts.GetContent(),
            ActivityResultCallback {

            })

        viewBinding.addImageBtn.setOnClickListener(View.OnClickListener {
            val i = Intent()
            i.type = "image/*"
            i.action = Intent.ACTION_GET_CONTENT
            i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            myActivityResultLauncher.launch(Intent.createChooser(i, "Select Pictures"))
        })

        viewBinding.editLocationBtn.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@NewEntryActivity, EntryMapActivity::class.java)
            this.startActivity(intent)
        })

        this.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }


    @SuppressLint("MissingPermission")
    private fun getLocation() {
        if(checkPermissions()){
            if(isLocationEnabled()) {
                this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
            }
            else {

                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        }
        else {

            requestPermission()
        }
    }
    private fun isLocationEnabled(): Boolean {
        this.locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun checkPermissions(): Boolean {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            return true

        return false
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION),
            locationPermissionCode)
    }

    override fun onLocationChanged(location: Location) {
        val geocoder = Geocoder(this, Locale.getDefault())
        val list: MutableList<Address>? =
            geocoder.getFromLocation(location.latitude, location.longitude, 1)

//        viewBinding.locationText.text = "${list?.get(0)?.locality}"
        viewBinding.locationText.text = "${list?.get(0)?.getAddressLine(0)}"
        Log.d("TAG", location.latitude.toString())
        Log.d("TAG", location.longitude.toString())
        Log.d("TAG", "Address\n${list?.get(0)?.getAddressLine(0)}")

    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onProviderEnabled(provider: String) {}

    override fun onProviderDisabled(provider: String) {}

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

    private fun doAllFieldHaveEntries(): Boolean {
        return viewBinding.titleText.text.toString().isNotEmpty() &&
                viewBinding.descriptionText.text.toString().isNotEmpty() &&
                viewBinding.locationText.text.toString().isNotEmpty()
    }

    companion object {
        const val ACTIVITY_TYPE = "ACTIVITY_TYPE"
        const val ADD_ENTRY = "ADD_ENTRY"
        const val EDIT_ENTRY = "EDIT_ENTRY"
    }
}