package com.example.mobdevemco.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.example.mobdevemco.adapter.EntryAdapter
import com.example.mobdevemco.adapter.EntryImageAdapter
import com.example.mobdevemco.databinding.ActivityViewEntryBinding
import com.example.mobdevemco.helper.EntryDbHelper
import com.example.mobdevemco.model.Entry
import com.example.mobdevemco.model.EntryImages
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.util.concurrent.Executors


class EntryDetailsActivity: AppCompatActivity(), OnMapReadyCallback {
    private lateinit var recyclerView: RecyclerView
    private lateinit var myAdapter: EntryImageAdapter
    private lateinit var entry: Entry
    private lateinit var entryImagesArray: ArrayList<EntryImages>

    private var longitude: Double = 0.0
    private var latitude: Double = 0.0
    private var mMap: GoogleMap? = null
    lateinit var mapView: MapView
    private lateinit var marker: Marker

    private lateinit var entryTitleTextView: TextView
    private lateinit var entryCreatedAtTextView: TextView
    private lateinit var entryLocationNameTextView: TextView
    private lateinit var entryDescriptionTextView: TextView
    private lateinit var totalImagesTextView: TextView
    private lateinit var imageCountLinearLayout: LinearLayout
    private lateinit var scrollView: ScrollView

    private var adapterPos = -1
    private var editCode: Int = 0
    private var entryDbHelper: EntryDbHelper? = null
    private val executorService = Executors.newSingleThreadExecutor()

    private val myActivityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            executorService.execute {
                entryDbHelper = EntryDbHelper.getInstance(this@EntryDetailsActivity)
                val e = entryDbHelper?.getEntry(intent.getLongExtra(Entry.ID, -1))
                runOnUiThread {
                    if (e != null) {
                        this.entry = e

                        entryTitleTextView.text = entry.getTitle()
                        entryCreatedAtTextView.text = entry.getCreatedAt().toStringFormatted()
                        entryLocationNameTextView.text = entry.getLocationName()
                        entryDescriptionTextView.text = entry.getDescription()

                        entryImagesArray.clear()
                        entryImagesArray.addAll(entry.getImages())
                        totalImagesTextView.text = entry.getImages().size.toString()
                        disableRecyclerViewOnNoImage()

                        myAdapter.notifyDataSetChanged()

                        latitude = entry.getAdjustedLatitude()
                        longitude = entry.getAdjustedLongitude()

                        val edited_location = LatLng(latitude, longitude)
                        marker.remove()
                        marker = mMap!!.addMarker(
                            MarkerOptions()
                                .position(edited_location)
                                .title("Estimate")
                                .icon(
                                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
                                )
                        )!!
                        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(edited_location, EntryMapActivity.zoomLevel-4.0F))

                        editCode = 1

                    }
                }
            }
        }
    }
    @SuppressLint("ClickableViewAccessibility")
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
        val edited_location = LatLng(latitude, longitude)

        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(edited_location, EntryMapActivity.zoomLevel-4.0F))
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

        val viewBinding : ActivityViewEntryBinding = ActivityViewEntryBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        entryTitleTextView = viewBinding.entryTitle
        entryCreatedAtTextView = viewBinding.entryCreatedAt
        entryLocationNameTextView = viewBinding.entryLocationName
        entryDescriptionTextView = viewBinding.entryDescription
        totalImagesTextView = viewBinding.totalImg
        imageCountLinearLayout = viewBinding.imageCountView
        scrollView = viewBinding.entryScrollView

        val intent: Intent = intent
        adapterPos = intent.getIntExtra(EntryAdapter.ADAPTER_POS, -1)

        executorService.execute {
            entryDbHelper = EntryDbHelper.getInstance(this@EntryDetailsActivity)
            val e = entryDbHelper?.getEntry(intent.getLongExtra(Entry.ID, -1))
            runOnUiThread {
                if (e != null) {
                    this.entry = e

                    latitude = entry.getAdjustedLatitude()
                    longitude = entry.getAdjustedLongitude()

                    mapView = viewBinding.entryMapView
//                    mapView.setOnTouchListener { v, event ->
//                        when (event.action) {
//                            MotionEvent.ACTION_MOVE -> scrollView.requestDisallowInterceptTouchEvent(true)
//                            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> scrollView.requestDisallowInterceptTouchEvent(
//                                false
//                            )
//                        }
//                        mapView.onTouchEvent(event)
//                    }
                    var mapViewBundle: Bundle? = null
                    if (savedInstanceState != null){
                        mapViewBundle = savedInstanceState.getBundle(EntryMapActivity.MAP_VIEW_BUNDLE_KEY)
                    }
                    mapView.onCreate(mapViewBundle)
                    mapView.getMapAsync(this)

                    entryTitleTextView.text = entry.getTitle()
                    entryCreatedAtTextView.text = entry.getCreatedAt().toStringFormatted()
                    entryLocationNameTextView.text = entry.getLocationName()
                    entryDescriptionTextView.text = entry.getDescription()

                    viewBinding.editBtn.setOnClickListener(View.OnClickListener {
                        val i = Intent(this@EntryDetailsActivity, NewEntryActivity::class.java)
                        i.putExtra(NewEntryActivity.ACTIVITY_TYPE, NewEntryActivity.EDIT_ENTRY)
                        i.putExtra(Entry.ID, entry.getId())
                        myActivityResultLauncher.launch(i)
                    })

                    this.recyclerView = viewBinding.entryImageRecyclerView
                    entryImagesArray = entry.getImages()
                    this.myAdapter = EntryImageAdapter(entryImagesArray)
                    this.myAdapter.setViewType(EntryImageAdapter.ENTRY_IMAGE_LAYOUT)
                    this.recyclerView.adapter = myAdapter
                    val linearLayoutManager = LinearLayoutManager(this)
                    linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
                    recyclerView.layoutManager = linearLayoutManager

                    //this.viewBinding.entryImage.setImageResource(entry.imageId)
                    val snapHelper: SnapHelper = PagerSnapHelper()
                    recyclerView.setOnFlingListener(null);
                    snapHelper.attachToRecyclerView(recyclerView)
                    recyclerView.addOnScrollListener(SnapOnScrollListener(snapHelper, SnapOnScrollListener.NOTIFY_ON_SCROLL) { position ->
                        val curr_pos = position + 1
                        viewBinding.currentImg.text = curr_pos.toString().replace(" ", "")
                    })

                    totalImagesTextView.text = entry.getImages().size.toString()

                    disableRecyclerViewOnNoImage()
                }
            }
        }



        val builder = AlertDialog.Builder(this)
        viewBinding.deletebtn.setOnClickListener(View.OnClickListener{
            builder.setTitle("Alert!")
                .setMessage("Are you sure you want to delete the entry?")
                .setCancelable(true)
                .setPositiveButton("Yes"){dialogInterface,it ->
                    executorService.execute {
                        val e = entryDbHelper?.deleteEntry(entry)
                    }
                    editCode = 2
                    val i: Intent = Intent()
                    i.putExtra(EDIT_CODE, editCode)
                    i.putExtra(EntryAdapter.ADAPTER_POS, adapterPos)
                    i.putExtra(EntryDbHelper.ENTRY_ID, entry.getId())
                    setResult(RESULT_OK, i)
                    finish()
                }
                .setNegativeButton("No"){dialogInterface,it ->
                    dialogInterface.cancel()
                }
                .show()
        })
        viewBinding.backBtn.setOnClickListener(View.OnClickListener{
            onBackPressed()
        })
    }

    override fun onStart(){
        super.onStart()


    }
    override fun onBackPressed() {
        val i: Intent = Intent()
        i.putExtra(EDIT_CODE, editCode)
        i.putExtra(EntryAdapter.ADAPTER_POS, adapterPos)
        i.putExtra(EntryDbHelper.ENTRY_ID, entry.getId())
        setResult(RESULT_OK, i)
        finish()
    }

    private fun disableRecyclerViewOnNoImage(){
        if(entry.getImages().size == 0){
            recyclerView.visibility = View.GONE
            imageCountLinearLayout.visibility = View.GONE
        } else{
            recyclerView.visibility = View.VISIBLE
            imageCountLinearLayout.visibility = View.VISIBLE
        }
    }

    companion object {
        val EDIT_CODE = "EDIT_CODE"
    }

}