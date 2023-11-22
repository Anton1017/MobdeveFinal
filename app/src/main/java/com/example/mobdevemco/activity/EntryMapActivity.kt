package com.example.mobdevemco.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
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
import com.example.mobdevemco.R
import com.example.mobdevemco.databinding.ActivityEditMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import java.io.IOException
import java.util.Locale


class EntryMapActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener,  GoogleMap.OnCameraIdleListener, GoogleMap.OnCameraMoveListener{
    private lateinit var newaddress : TextView
    private lateinit var newaddresstext : String
    private var longitude :Double = 0.0
    private var latitude :Double = 0.0
    private var editLongitude: Double = 0.0
    private var editLatitude: Double = 0.0
    private var accuracy: Float = 0.0F
    private lateinit var viewBinding: ActivityEditMapBinding
    private var mMap: GoogleMap? = null
    lateinit var mapView: MapView
    private val MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey"
    override fun onMapReady(googleMap: GoogleMap) {
        val zoomLevel = 13.0f
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

        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(original_location, zoomLevel))
//        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(original_location));
        mMap!!.setMyLocationEnabled(true)
        mMap!!.setOnCameraIdleListener(this)
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
        try{
            addresses = geocoder.getFromLocation(mMap!!.getCameraPosition().target.latitude, mMap!!.getCameraPosition().target.longitude, 1)
            setAddress(addresses!![0])
            editLongitude = mMap!!.getCameraPosition().target.longitude
            editLatitude = mMap!!.getCameraPosition().target.latitude
            //val accuracy = mMap!!.myLocation.accuracy
            Log.d("Location", "Longitude: $longitude, Latitude: $latitude")
        }catch (e:IndexOutOfBoundsException){
            e.printStackTrace()
        }catch (e:IOException){
            e.printStackTrace()
        }
    }

    override fun onCameraMove() {

        TODO("Not yet implemented")
    }

    companion object {
        const val LONGITUDE = "LONGITUDE"
        const val LATITUDE = "LATITUDE"
        const val ADJUSTED_LONGITUDE = "ADJUSTED_LONGITUDE"
        const val ADJUSTED_LATITUDE = "ADJUSTED_LATITUDE"
        const val ACCURACY = "ACCURACY"
        const val CURRENTLOCATION = "CURRENTLOCATION"
    }

}