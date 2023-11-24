package com.example.mobdevemco.activity


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources.Theme
import android.content.res.TypedArray
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
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

    private lateinit var searchView: SearchView
    private val entries: ArrayList<Entry> = ArrayList<Entry>()
    private lateinit var viewBinding: ActivityMainBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var myAdapter: EntryAdapter
    private val locationPermissionCode = 2
    private lateinit var locationManager: LocationManager
    private var searchToggle: Boolean = false
    private var longitude: Double = 0.0
    private var latitude: Double = 0.0
    private var accuracy: Float = 0.0F
    private var entryDbHelper: EntryDbHelper? = null
    private val executorService = Executors.newSingleThreadExecutor()
    private lateinit var currentLocation: String

    private var locationPermission: Boolean = false

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
                        searchView.setQuery("", true)
                        searchView.clearFocus()
                        filterList("")
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
        val animationFadeOut = AlphaAnimation(1.0f, 0.5f)
        animationFadeOut.duration = 250
        animationFadeOut.startOffset = 350
        animationFadeOut.fillAfter = true
        val animationFadeIn = AlphaAnimation(0.5f, 1.0f)
        animationFadeIn.duration = 250
        animationFadeIn.startOffset = 350
        animationFadeIn.fillAfter = true

        this.viewBinding = ActivityMainBinding.inflate(layoutInflater)

        val typedValue = TypedValue()
        val theme: Theme = this.theme
        theme.resolveAttribute(com.google.android.material.R.attr.colorOnPrimary, typedValue, true)

        executorService.execute {
            // Get all contacts from the database
            entryDbHelper = EntryDbHelper.getInstance(this@ActivityMain)
            entryDbHelper?.allEntriesDefault?.let { entries.addAll(it) }

            if(entries.size != 0){
                viewBinding.firstEntryText.visibility = View.GONE
            }else{
                viewBinding.firstEntryText.visibility = View.VISIBLE
            }
            Log.d("TAG", "entries$entries")
            entries.reverse()
            runOnUiThread { // Pass in the contacts to the needed components and set the adapter
                myAdapter = entries.let { EntryAdapter(it, entryResultLauncher) }
                this.recyclerView.adapter = myAdapter
            }
        }

        setContentView(viewBinding.root)
        searchView = findViewById(R.id.searchEntry)
        val searchIcon = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon)
        val closeIcon = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
        val editText = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        editText.setTextColor(typedValue.data)
        editText.setHintTextColor(typedValue.data)
        searchIcon.setColorFilter(typedValue.data, android.graphics.PorterDuff.Mode.SRC_IN)
        closeIcon.setColorFilter(typedValue.data, android.graphics.PorterDuff.Mode.SRC_IN)
        searchView.visibility = View.GONE

        requestPermission()
        Log.d("TAG", "location permission request")
        //Logic for adding a new entry
        viewBinding.entryAddBtn.setOnClickListener(View.OnClickListener {
            if(locationPermission){
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
            }else{
                Log.d("TAG", "location permission request")
                requestPermission()
            }

        })

        this.recyclerView = viewBinding.entryRecyclerView

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if(recyclerView.computeVerticalScrollOffset() == 0 && recyclerView.size != 0){
                    val animationFadeOutAddressBar = AlphaAnimation(viewBinding.addressBar.alpha, 0.5f)
                    animationFadeOutAddressBar.duration = 250
                    animationFadeOutAddressBar.startOffset = 350
                    animationFadeOutAddressBar.fillAfter = true
                    viewBinding.addressBar.startAnimation(animationFadeOutAddressBar)
                }else{

                    val animationFadeInAddressBar = AlphaAnimation(viewBinding.addressBar.alpha, 1.0f)
                    animationFadeInAddressBar.duration = 250
                    animationFadeInAddressBar.startOffset = 350
                    animationFadeInAddressBar.fillAfter = true
                    viewBinding.addressBar.startAnimation(animationFadeInAddressBar)
                }

                if(recyclerView.canScrollVertically(-1) && !recyclerView.canScrollVertically(1)
                    ){
                    val animationFadeOutAddButton = AlphaAnimation(viewBinding.entryAddBtn.alpha, 0.5f)
                    animationFadeOutAddButton.duration = 250
                    animationFadeOutAddButton.startOffset = 350
                    animationFadeOutAddButton.fillAfter = true
                    viewBinding.entryAddBtn.startAnimation(animationFadeOutAddButton)
                }else{
                    val animationFadeInAddButton = AlphaAnimation(viewBinding.entryAddBtn.alpha, 1.0f)
                    animationFadeInAddButton.duration = 250
                    animationFadeInAddButton.startOffset = 350
                    animationFadeInAddButton.fillAfter = true
                    viewBinding.entryAddBtn.startAnimation(animationFadeInAddButton)
                }

            }
        })

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }
        })

        //
        viewBinding.searchLogo.setOnClickListener(View.OnClickListener {
            if(searchToggle){
                searchView.setQuery("", false);
                searchView.clearFocus();
                searchView.visibility = View.GONE
                viewBinding.searchLogo.setImageResource(R.drawable.ic_search_api_material)
            }else{
                searchView.visibility = View.VISIBLE
                viewBinding.searchLogo.setImageResource(R.drawable.baseline_search_off_24)
            }
            searchToggle = !searchToggle
        })

        this.recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun filterList(query: String?){
        if (query != null){
            val filteredList = ArrayList<Entry>()
            for (i in entries){
                if (i.getTitle().lowercase(Locale.getDefault()).contains(query.lowercase(Locale.getDefault()))){
                    filteredList.add(i)
                }
            }
            if (filteredList.isNotEmpty()){
                myAdapter.setFilteredList(filteredList)
            }
        }
    }
    @SuppressLint("MissingPermission")
    private fun getLocation() {
        Log.d("TAG", "getting location")
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
                //Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                locationPermission = true
                viewBinding.currentLocationMain.text = getString(R.string.tournal_retrieving_addr)
                viewBinding.firstEntryText.text = getString(R.string.tournal_create_first_entry)

                getLocation()
            }
            else {
                //Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                locationPermission = false
                viewBinding.currentLocationMain.text = getString(R.string.tournal_location_permission_denied)
                viewBinding.firstEntryText.text = getString(R.string.tournal_request_location_permission)
            }
        }
    }
   override fun onProviderEnabled(provider: String) {}
   override fun onProviderDisabled(provider: String) {}
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}



}
