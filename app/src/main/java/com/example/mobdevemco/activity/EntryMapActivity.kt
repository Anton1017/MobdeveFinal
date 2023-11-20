package com.example.mobdevemco.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import android.Manifest
import android.os.PersistableBundle
import com.example.mobdevemco.R
import com.example.mobdevemco.databinding.ActivityCreateEntryBinding
import com.example.mobdevemco.databinding.ActivityEditMapBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback

class EntryMapActivity : AppCompatActivity(), OnMapReadyCallback{

    private var mMap: GoogleMap? = null
    lateinit var mapView: MapView
    private val MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey"
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
        mMap!!.setMyLocationEnabled(true)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewBinding : ActivityEditMapBinding = ActivityEditMapBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        mapView = findViewById<MapView>(R.id.map)
        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null){
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY)
        }
        mapView.onCreate(mapViewBundle)
        mapView.getMapAsync(this)
        viewBinding.confirmBtn.setOnClickListener(View.OnClickListener {

            finish()
        })
        viewBinding.backBtn.setOnClickListener(View.OnClickListener {

            finish()
        })

    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        var mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY)
        if (mapViewBundle == null){
            mapViewBundle = Bundle()
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle)
        }
        mapView.onSaveInstanceState(mapViewBundle)
    }



}