package com.example.mobdevemco.activity


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobdevemco.adapter.EntryAdapter
import com.example.mobdevemco.databinding.ActivityCreateEntryBinding
import com.example.mobdevemco.databinding.ActivityMainBinding
import com.example.mobdevemco.helper.EntryDbHelper
import com.example.mobdevemco.model.DataGenerator
import com.example.mobdevemco.model.Entry
import com.example.mobdevemco.model.EntryImages
import java.util.Locale
import java.util.concurrent.Executors

class ActivityMain : AppCompatActivity(), LocationListener {

    private var entryData = DataGenerator.loadEntryData()
    private lateinit var viewBinding: ActivityMainBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var myAdapter: EntryAdapter
    private val locationPermissionCode = 2
    private lateinit var locationManager: LocationManager

    private val newEntryResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        // Check to see if the result returned is appropriate (i.e. OK)
        if (result.resultCode == RESULT_OK) {
            val title : String = result.data?.getStringExtra(NewEntryActivity.TITLE_KEY)!!
            val locationName : String = result.data?.getStringExtra(NewEntryActivity.LOCATION_NAME_KEY)!!
            val images : ArrayList<EntryImages> = result.data?.getSerializableExtra(NewEntryActivity.IMAGE_KEY)!! as ArrayList<EntryImages>
            val description : String = result.data?.getStringExtra(NewEntryActivity.DESCRIPTION_KEY)!!

            val entry = Entry(title, locationName, images, description)

//            if(position != -1){
//                ActivityMain.data.set(position, email)
//                this.myAdapter.notifyItemChanged(position)
//            }
//            else{
//
//                ActivityMain.data.add(email)
//                this.myAdapter.notifyDataSetChanged()
//            }

        }
    }

    private var entryDbHelper: EntryDbHelper? = null
    private val executorService = Executors.newSingleThreadExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        getLocation()
        //Logic for adding a new entry
        viewBinding.entryAddBtn.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@ActivityMain, NewEntryActivity::class.java)
            this.startActivity(intent)
        })

        viewBinding.searchLogo.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@ActivityMain, EntrySearchActivity::class.java)
            this.startActivity(intent)
        })
        this.recyclerView = viewBinding.entryRecyclerView
//        this.myAdapter = EntryAdapter(entryData,newEntryResultLauncher)
//        this.recyclerView.adapter = myAdapter

        executorService.execute {
            // Get all contacts from the database
            entryDbHelper = EntryDbHelper.getInstance(this@ActivityMain)
            val entries = entryDbHelper?.allEntriesDefault

            runOnUiThread { // Pass in the contacts to the needed components and set the adapter
                myAdapter = entries?.let { EntryAdapter(it, newEntryResultLauncher) }!!
                this.recyclerView.adapter = myAdapter
            }
        }

        this.recyclerView.layoutManager = LinearLayoutManager(this)
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
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION),
            locationPermissionCode)
    }

    override fun onLocationChanged(location: Location) {
        val geocoder = Geocoder(this, Locale.getDefault())
        val list: MutableList<Address>? =
            geocoder.getFromLocation(location.latitude, location.longitude, 1)

//        viewBinding.locationText.text = "${list?.get(0)?.locality}"
        viewBinding.currentLocationMain.text = "${list?.get(0)?.getAddressLine(0)}"
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



}
