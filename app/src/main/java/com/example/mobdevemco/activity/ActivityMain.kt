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
import androidx.core.view.size
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobdevemco.R
import com.example.mobdevemco.adapter.EntryAdapter
import com.example.mobdevemco.databinding.ActivityMainBinding
import com.example.mobdevemco.helper.EntryDbHelper
import com.example.mobdevemco.model.Entry
import java.util.Locale
import java.util.concurrent.Executors

class ActivityMain : AppCompatActivity(), LocationListener {

    //private var entryData = DataGenerator.loadEntryData()
    private val entries: ArrayList<Entry> = ArrayList<Entry>()
    private lateinit var viewBinding: ActivityMainBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var myAdapter: EntryAdapter
    private val locationPermissionCode = 2
    private lateinit var locationManager: LocationManager
    private var longitude: Double = 0.0
    private var latitude: Double = 0.0
    private var accuracy: Float = 0.0F
    private var entryDbHelper: EntryDbHelper? = null
    private val executorService = Executors.newSingleThreadExecutor()
    private lateinit var currentLocation: String

    private val entryResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            val editCode: Int = result.data?.getIntExtra(EntryDetailsActivity.EDIT_CODE, -1)!!
            // If activity is either adding or editing
            if(editCode != 0){
                executorService.execute {
                    val id : Long = result.data?.getLongExtra(EntryDbHelper.ENTRY_ID, -1)!!
                    entryDbHelper = EntryDbHelper.getInstance(this@ActivityMain)
                    val entry: Entry? = entryDbHelper?.getEntry(id)

                    runOnUiThread { // Pass in the contacts to the needed components and set the adapter
                        // Adding entry
                        if(editCode == -1){
                            if (entry != null) {
                                entries.add(0, entry)
                            }
                            myAdapter.notifyItemInserted(0)
                            recyclerView.smoothScrollToPosition(0)
                        // Editing entry
                        } else if(editCode == 1){
                            val position : Int = result.data?.getIntExtra(EntryAdapter.ADAPTER_POS, -1)!!
                            Log.d("TAG", entry.toString())
                            if (entry != null) {
                                entries[position] = entry
                            }
                            myAdapter.notifyItemChanged(position)
                        // Deleting entry
                        } else if(editCode == 2){
                            val position : Int = result.data?.getIntExtra(EntryAdapter.ADAPTER_POS, -1)!!
                            if (entry == null) {
                                entries.removeAt(position)
                            }
                            myAdapter.notifyDataSetChanged()
                        }
                        
                        if(entries.size != 0){
                            viewBinding.firstEntryText.visibility = View.GONE
                        }else{
                            viewBinding.firstEntryText.visibility = View.VISIBLE
                        }
                    }
                }
            }

        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.viewBinding = ActivityMainBinding.inflate(layoutInflater)

        executorService.execute {
            // Get all contacts from the database
            entryDbHelper = EntryDbHelper.getInstance(this@ActivityMain)
            entryDbHelper?.allEntriesDefault?.let { entries.addAll(it) }

            if(entries.size != 0){
                viewBinding.firstEntryText.visibility = View.GONE
            }else{
                viewBinding.firstEntryText.visibility = View.VISIBLE
            }
            Log.d("TAG", entries.toString())

            runOnUiThread { // Pass in the contacts to the needed components and set the adapter
                myAdapter = entries.let { EntryAdapter(it, entryResultLauncher) }
                this.recyclerView.adapter = myAdapter
            }
        }


        setContentView(viewBinding.root)
        getLocation()
        //Logic for adding a new entry
        viewBinding.entryAddBtn.setOnClickListener(View.OnClickListener {
            if(viewBinding.currentLocationMain.text.toString()
                == getString(R.string.tournal_retrieving_addr)){

                Toast.makeText(this,
                    "Cannot create entry. Wait for address retrieval.",
                    Toast.LENGTH_SHORT
                ).show()

            }else{
                val intent = Intent(this@ActivityMain, NewEntryActivity::class.java)
                intent.putExtra(NewEntryActivity.ACTIVITY_TYPE, NewEntryActivity.ADD_ENTRY)
                intent.putExtra(NewEntryActivity.CURRENT_LOCATION, currentLocation)
                intent.putExtra(NewEntryActivity.LATITUDE, latitude)
                intent.putExtra(NewEntryActivity.LONGITUDE, longitude)
                intent.putExtra(NewEntryActivity.ACCURACY, accuracy)
                entryResultLauncher.launch(intent)
            }
        })

        viewBinding.searchLogo.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@ActivityMain, EntrySearchActivity::class.java)
            this.startActivity(intent)
        })

        this.recyclerView = viewBinding.entryRecyclerView
//        this.myAdapter = EntryAdapter(entryData,newEntryResultLauncher)
//        this.recyclerView.adapter = myAdapter

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(recyclerView.computeVerticalScrollOffset() == 0 && recyclerView.size != 0){
                    viewBinding.addressBar.alpha = 0.5F
                }else{
                    viewBinding.addressBar.alpha = 1F
                }

                if(recyclerView.canScrollVertically(-1) && !recyclerView.canScrollVertically(1)

                    ){
                    viewBinding.entryAddBtn.alpha = 0.1F
                }else{
                    viewBinding.entryAddBtn.alpha = 1F
                }

            }
        })


        viewBinding.entryRecyclerView.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@ActivityMain, EntrySearchActivity::class.java)
            this.startActivity(intent)
        })

        this.recyclerView.layoutManager = LinearLayoutManager(this)
    }
    @SuppressLint("MissingPermission")
    private fun getLocation() {
        if(checkPermissions()){
            if(isLocationEnabled()) {
                this.locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    5000,
                    5f,
                    this
                )
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
        if(ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
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
        longitude = location.longitude
        latitude = location.latitude
        accuracy = location.accuracy
//        viewBinding.locationText.text = "${list?.get(0)?.locality}"
        var locationAddress = "${list?.get(0)?.getAddressLine(0)}"
        viewBinding.currentLocationMain.text =
            if (locationAddress != "") locationAddress
            else getString(R.string.tournal_retrieving_addr)

        currentLocation = "${list?.get(0)?.getAddressLine(0)}"
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
