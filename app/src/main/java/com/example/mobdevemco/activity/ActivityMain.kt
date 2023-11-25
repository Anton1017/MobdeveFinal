package com.example.mobdevemco.activity


import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources.Theme
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.util.Locale
import java.util.concurrent.Executors


class ActivityMain : AppCompatActivity() {

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

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

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
                        changePlaceholderTextStatus()
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
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION),
                locationPermissionCode)
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                if(p0.locations.size != 0){
                    val location = p0.locations[0]
                    if (location == null)
                        Toast.makeText(this@ActivityMain, "Cannot get location.", Toast.LENGTH_SHORT).show()
                    else {
                        val geocoder = Geocoder(this@ActivityMain, Locale.getDefault())
                        val list: MutableList<Address>? =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        longitude = location.longitude
                        latitude = location.latitude
                        accuracy = location.accuracy
                        var locationAddress = ""
                        if(list!!.size != 0){
                            locationAddress = list[0].getAddressLine(0)
                        }
                        viewBinding.currentLocationMain.text =
                            if (locationAddress != "") locationAddress
                            else getString(R.string.tournal_retrieving_addr)

                        currentLocation = locationAddress
                        Log.d("TAG", location.latitude.toString())
                        Log.d("TAG", location.longitude.toString())
                        Log.d("TAG", "Address\n${locationAddress}")
                    }
                }
            }
        }

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            5000
        ).setMinUpdateIntervalMillis(3000).build()

        fusedLocationClient.requestLocationUpdates(locationRequest,
            locationCallback,
            Looper.getMainLooper())


        val typedValue = TypedValue()
        val theme: Theme = this.theme
        theme.resolveAttribute(com.google.android.material.R.attr.colorOnPrimary, typedValue, true)

        executorService.execute {
            // Get all contacts from the database
            entryDbHelper = EntryDbHelper.getInstance(this@ActivityMain)
            entryDbHelper?.allEntriesDefault?.let { entries.addAll(it) }

            changePlaceholderTextStatus()
            Log.d("TAG", "entries$entries")
            //entries.reverse()
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

        //the animation when scrolling for recyclerView
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
        //sets the query in searchView to check if there is query
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                Log.d("TAG", "FILTER ENTRY")
                filterList(newText)
                return true
            }
        })

        //Toggles the visibility of searchView
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

    //Filters the entries base on the query
    private fun filterList(query: String?){
        var tempEntryArrayList: ArrayList<Entry>?
        executorService.execute{
            entryDbHelper = EntryDbHelper.getInstance(this@ActivityMain)
            if (query != null){
                tempEntryArrayList = entryDbHelper!!.getFilteredEntries(query)
            }else{
                tempEntryArrayList = entryDbHelper!!.allEntriesDefault
            }

            runOnUiThread{
                entries.clear()
                tempEntryArrayList!!.reverse()
                entries.addAll(tempEntryArrayList!!)
                myAdapter.notifyDataSetChanged()
                changePlaceholderTextStatus()
            }
        }
    }

    //Checks if there is an entry in the recycler view of the main activity
    private fun changePlaceholderTextStatus(){
        if(entries.size != 0){
            viewBinding.firstEntryText.visibility = View.GONE
        }else{
            viewBinding.firstEntryText.visibility = View.VISIBLE
        }
    }

    //gets the current location of the user

    //Checks if phone has granted access to get location
    private fun isLocationEnabled(): Boolean {
        this.locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    //requests permission on Location
    private fun requestPermission() {
        ActivityCompat.requestPermissions(this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION),
            locationPermissionCode)
    }
    //Location has already changed
    //checks the result of requestpermission function
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                locationPermission = true
                viewBinding.currentLocationMain.text = getString(R.string.tournal_retrieving_addr)
                viewBinding.firstEntryText.text = getString(R.string.tournal_create_first_entry)
            }
            else {
                //Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                locationPermission = false
                viewBinding.currentLocationMain.text = getString(R.string.tournal_location_permission_denied)
                viewBinding.firstEntryText.text = getString(R.string.tournal_request_location_permission)
            }
        }
    }

}
