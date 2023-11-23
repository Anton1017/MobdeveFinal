package com.example.mobdevemco.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Transformations.map
import com.example.mobdevemco.R
import com.example.mobdevemco.databinding.ActivityEditMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.SphericalUtil
import java.io.IOException
import java.util.Locale


class EntryMapActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener, GoogleMap.OnCameraIdleListener{
    private lateinit var newaddress : TextView
    private lateinit var newaddresstext : String
    private var longitude :Double = 0.0
    private var latitude :Double = 0.0
    private var editLongitude: Double = 0.0
    private var editLatitude: Double = 0.0
    private var accuracy: Float = 0.0F
    private lateinit var circle: Circle
    private lateinit var viewBinding: ActivityEditMapBinding
    private var mMap: GoogleMap? = null
    lateinit var mapView: MapView
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
        val original_location = LatLng(latitude, longitude)
        val edited_location = LatLng(editLatitude, editLongitude)
        circle = mMap!!.addCircle(
            CircleOptions()
                .center(original_location)
                .radius(accuracy.toDouble())
                .zIndex(0f)
                .strokeColor(R.color.seal_brown)
                .fillColor(R.color.beige)
        )
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(edited_location, zoomLevel))
        mMap!!.setMyLocationEnabled(false)
        mMap!!.setOnCameraIdleListener(this)
        mMap!!.addMarker(
            MarkerOptions()
                .position(original_location)
                .title("Estimate")
                .icon(
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
                )
        )
        when (this.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                mMap!!.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        this@EntryMapActivity,
                        R.raw.style_json
                    )
                )
            }
            Configuration.UI_MODE_NIGHT_NO -> {}
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {}
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewBinding : ActivityEditMapBinding = ActivityEditMapBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        longitude = intent.getDoubleExtra(LONGITUDE, 0.0)
        latitude = intent.getDoubleExtra(LATITUDE, 0.0)
        editLatitude = intent.getDoubleExtra(ADJUSTED_LATITUDE, 0.0)
        editLongitude = intent.getDoubleExtra(ADJUSTED_LONGITUDE, 0.0)
        accuracy = intent.getFloatExtra(ACCURACY, 0.0F)
        newaddresstext = intent.getStringExtra(CURRENTLOCATION).toString()
        newaddress = findViewById(R.id.currentLocationMap)
        newaddress.text = newaddresstext

        mapView = findViewById<MapView>(R.id.map)
        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null){
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY)
        }
        mapView.onCreate(mapViewBundle)
        mapView.getMapAsync(this)

        viewBinding.confirmBtn.setOnClickListener(View.OnClickListener {
            val intent= Intent()
            intent.putExtra(EntryMapActivity.CURRENTLOCATION, newaddresstext)
            intent.putExtra(EntryMapActivity.LONGITUDE, editLongitude)
            intent.putExtra(EntryMapActivity.LATITUDE, editLatitude)

            setResult(Activity.RESULT_OK, intent)
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

    override fun onLocationChanged(location: Location) {
        val geocoder = Geocoder(this, Locale.getDefault())
        var addresses: List<Address>? = null
        try{
            addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)

        }catch (e: IOException){
            e.printStackTrace()
        }
        setAddress(addresses!![0])
    }

    private fun setAddress(address: Address) {
        if (address != null){
            if (address.getAddressLine(0) != null){
                newaddress = findViewById(R.id.currentLocationMap)
                newaddress.text = address.getAddressLine((0))
                newaddresstext = address.getAddressLine((0))
            }
            if (address.getAddressLine(1) != null){
                newaddress = findViewById(R.id.currentLocationMap)
                newaddress.text = address.getAddressLine(1)
//                Log.d("TAG", address.getAddressLine((1)))
                newaddresstext = address.getAddressLine((1))
            }
        }
    }



    override fun onCameraIdle() {
        var addresses: List<Address>? = null
        val geocoder = Geocoder(this, Locale.getDefault())

        val original_location = LatLng(latitude, longitude)
        val edited_location = LatLng(editLatitude, editLongitude)
        val camera_location = LatLng(mMap!!.getCameraPosition().target.latitude, mMap!!.getCameraPosition().target.longitude)
//        Log.d("TAG", mMap!!.getCameraPosition().target.latitude.toString() + " " + mMap!!.getCameraPosition().target.longitude.toString())
        val distanceBetween = SphericalUtil.computeDistanceBetween(
            original_location,
            camera_location
        )
        Log.d("TAG", distanceBetween.toString() + " " + accuracy.toString())
        Log.d("TAG_EDITED", editLatitude.toString() + " " + editLongitude.toString())
        if(distanceBetween >= accuracy){
            val cameraPosition = CameraPosition.fromLatLngZoom(edited_location, zoomLevel)
            val cu = CameraUpdateFactory.newCameraPosition(cameraPosition)
            mMap!!.animateCamera(cu)
            //mMap!!.moveCamera(CameraUpdateFactory.newLatLng(edited_location))
        }else{
            try{
                addresses = geocoder.getFromLocation(mMap!!.getCameraPosition().target.latitude, mMap!!.getCameraPosition().target.longitude, 1)
                setAddress(addresses!![0])
                editLongitude = mMap!!.cameraPosition.target.longitude
                editLatitude = mMap!!.cameraPosition.target.latitude
                //val accuracy = mMap!!.myLocation.accuracy
                Log.d("Location", "Longitude: $longitude, Latitude: $latitude")
            }catch (e:IndexOutOfBoundsException){
                e.printStackTrace()
            }catch (e:IOException){
                e.printStackTrace()
            }
        }

    }

    companion object {
        const val LONGITUDE = "LONGITUDE"
        const val LATITUDE = "LATITUDE"
        const val ADJUSTED_LONGITUDE = "ADJUSTED_LONGITUDE"
        const val ADJUSTED_LATITUDE = "ADJUSTED_LATITUDE"
        const val ACCURACY = "ACCURACY"
        const val CURRENTLOCATION = "CURRENTLOCATION"
        const val MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey"
        const val zoomLevel = 20.0f
    }

}