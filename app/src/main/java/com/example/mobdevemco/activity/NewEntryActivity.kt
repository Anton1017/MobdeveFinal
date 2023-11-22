package com.example.mobdevemco.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobdevemco.R
import com.example.mobdevemco.adapter.EntryImageAdapter
import com.example.mobdevemco.databinding.ActivityCreateEntryBinding
import com.example.mobdevemco.helper.EntryDbHelper
import com.example.mobdevemco.model.Entry
import com.example.mobdevemco.model.EntryImages
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.util.concurrent.Executors


class NewEntryActivity : AppCompatActivity(), OnMapReadyCallback {
    private var longitude: Double = 0.0
    private var latitude: Double = 0.0
    private var editLongitude: Double = 0.0
    private var editLatitude: Double = 0.0
    private var accuracy: Float = 0.0F
    private var mMap: GoogleMap? = null
    lateinit var mapView: MapView
    private lateinit var marker: Marker

    private lateinit var viewBinding: ActivityCreateEntryBinding
    private val locationPermissionCode = 2
    private lateinit var locationManager: LocationManager

    private lateinit var recyclerView: RecyclerView
    private lateinit var newImagesAdapter: EntryImageAdapter

    private val newImageArray: ArrayList<EntryImages> = ArrayList<EntryImages>()
    private var currEntry: Entry? = null

    private var entryDbHelper: EntryDbHelper? = null
    private val executorService = Executors.newSingleThreadExecutor()

    private val editlocation = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            viewBinding.locationText.text = result.data?.getStringExtra(EntryMapActivity.CURRENTLOCATION)!!
            editLongitude = result.data?.getDoubleExtra(EntryMapActivity.LONGITUDE, 0.0)!!
            editLatitude = result.data?.getDoubleExtra(EntryMapActivity.LATITUDE, 0.0)!!
            val edited_location = LatLng(editLatitude, editLongitude)
            marker.remove()
            marker = mMap!!.addMarker(
                MarkerOptions()
                    .position(edited_location)
                    .title("Estimate")
                    .icon(
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
                    )
            )!!
            mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(edited_location, EntryMapActivity.zoomLevel-5.0F))
        }
    }

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
                            val inputUri: Uri? = result.data!!.clipData?.getItemAt(i)?.uri
                            val entry = EntryImages(inputUri!!)
                            entry.setTemporaryImage(true)
                            newImageArray.add(entry)
                        }
                    //if only one image was picked
                    }else{
                        val inputUri = result.data!!.data
                        val entry = EntryImages(inputUri!!)
                        entry.setTemporaryImage(true)
                        newImageArray.add(entry)
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

    override fun onMapReady(googleMap: GoogleMap) {
        mapView.onResume()
        mMap = googleMap

        if(ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ){
            return
        }
        val edited_location = LatLng(editLatitude, editLongitude)

        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(edited_location, EntryMapActivity.zoomLevel-5.0F))
        mMap!!.uiSettings.isScrollGesturesEnabled = false
        mMap!!.setMyLocationEnabled(false)
        marker = mMap!!.addMarker(
            MarkerOptions()
                .position(edited_location)
                .title("Estimate")
                .icon(
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
                )
        )!!
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
            viewBinding.locationText.text = intent.getStringExtra(CURRENT_LOCATION)
            latitude = intent.getDoubleExtra(LATITUDE, 0.0)
            longitude = intent.getDoubleExtra(LONGITUDE, 0.0)
            editLatitude = this.latitude
            editLongitude = this.longitude
            accuracy = intent.getFloatExtra(ACCURACY, 0.0F)

            mapView = findViewById<MapView>(R.id.map)
            var mapViewBundle: Bundle? = null
            if (savedInstanceState != null){
                mapViewBundle = savedInstanceState.getBundle(EntryMapActivity.MAP_VIEW_BUNDLE_KEY)
            }
            mapView.onCreate(mapViewBundle)
            mapView.getMapAsync(this)

//            getLocation()
            Log.d("TAG", "add entry")
        }else if(activityType == EDIT_ENTRY){
            viewBinding.createBtn.text = getString(R.string.tournal_edit_action)
            executorService.execute {
                entryDbHelper = EntryDbHelper.getInstance(this@NewEntryActivity)

                currEntry = entryDbHelper?.getEntry(intent.getLongExtra(Entry.ID, -1))
                currEntry?.let { newImageArray.addAll(it.getImages()) }

                Log.d("ADJUSTED", currEntry!!.getAdjustedLatitude().toString() + " " + currEntry!!.getAdjustedLongitude().toString())


                runOnUiThread {
                    viewBinding.locationText.text = currEntry?.getLocationName()
                    viewBinding.titleText.setText(currEntry?.getTitle())
                    viewBinding.descriptionText.setText(currEntry?.getDescription())
                    latitude = currEntry!!.getOriginalLatitude()
                    longitude = currEntry!!.getOriginalLongitude()
                    editLatitude = currEntry!!.getAdjustedLatitude()
                    editLongitude = currEntry!!.getAdjustedLongitude()
                    accuracy = currEntry!!.getAccuracy()
                    newImagesAdapter.notifyDataSetChanged()

                    mapView = viewBinding.mapView2
                    var mapViewBundle: Bundle? = null
                    if (savedInstanceState != null){
                        mapViewBundle = savedInstanceState.getBundle(EntryMapActivity.MAP_VIEW_BUNDLE_KEY)
                    }
                    mapView.onCreate(mapViewBundle)
                    mapView.getMapAsync(this)

                    Log.d("EDIT_longitude", "longitude" + editLatitude.toString())
                    Log.d("EDIT_latitude", "latitude" + editLongitude.toString())
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
                                viewBinding.descriptionText.text.toString(),
                                latitude,
                                longitude,
                                editLatitude,
                                editLongitude,
                                accuracy
                            ),
                            this@NewEntryActivity
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
                                    it1.getOriginalLatitude(),
                                    it1.getOriginalLongitude(),
                                    it1.getAdjustedLatitude(),
                                    it1.getAdjustedLongitude(),
                                    it1.getAccuracy(),
                                    it1.getId()
                                ),
                                this@NewEntryActivity
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

        viewBinding.addImageBtn.setOnClickListener(View.OnClickListener {
            val i = Intent()
            i.type = "image/*"
            i.action = Intent.ACTION_GET_CONTENT
            i.putExtra(Intent.EXTRA_MIME_TYPES, EntryImages.supportedImageFormats)
            i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            myActivityResultLauncher.launch(Intent.createChooser(i, "Select Pictures"))
        })

        viewBinding.editLocationBtn.setOnClickListener(View.OnClickListener {
            val i = Intent(this@NewEntryActivity, EntryMapActivity::class.java)
            i.putExtra(EntryMapActivity.LATITUDE, latitude)
            i.putExtra(EntryMapActivity.LONGITUDE, longitude)
            i.putExtra(EntryMapActivity.ADJUSTED_LATITUDE, editLatitude)
            i.putExtra(EntryMapActivity.ADJUSTED_LONGITUDE, editLongitude)
            i.putExtra(EntryMapActivity.ACCURACY, accuracy)
            i.putExtra(EntryMapActivity.CURRENTLOCATION, viewBinding.locationText.text.toString())

            editlocation.launch(i)
        })

        this.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun doAllFieldHaveEntries(): Boolean {
        return viewBinding.titleText.text.toString().isNotEmpty() &&
                viewBinding.descriptionText.text.toString().isNotEmpty() &&
                viewBinding.locationText.text.toString().isNotEmpty()
    }

    companion object {
        const val ACTIVITY_TYPE = "ACTIVITY_TYPE"
        const val ADD_ENTRY = "ADD_ENTRY"
        const val EDIT_ENTRY = "EDIT_ENTRY"
        const val CURRENT_LOCATION = "CURRENT_LOCATION"
        const val LONGITUDE = "LONGITUDE"
        const val LATITUDE = "LATITUDE"
        const val ACCURACY = "ACCURACY"
    }


}