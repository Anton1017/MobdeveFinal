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
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobdevemco.adapter.EntryImageAdapter
import com.example.mobdevemco.databinding.ActivityCreateEntryBinding
import com.example.mobdevemco.model.EntryImages
import java.util.Locale


class NewEntryActivity : AppCompatActivity(), LocationListener{
    private lateinit var viewBinding: ActivityCreateEntryBinding
    private var currentlocation : String = "sample"
    private val locationPermissionCode = 2
    private lateinit var locationManager: LocationManager

    private lateinit var recyclerView: RecyclerView
    private lateinit var newImagesAdapter: EntryImageAdapter
    private val newImageArray: ArrayList<EntryImages> = ArrayList<EntryImages>()

    private val myActivityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            try {
                if (result.data != null) {
                    newImageArray.clear()
                    val count: Int? = result.data!!.clipData?.itemCount
                    if(count != null){
                        for (i in 0 until count!!) {
                            result.data!!.clipData?.getItemAt(i)?.uri?.let {
                                newImageArray.add( EntryImages(it) )
                            }
                        }
                    }else{
                        result.data!!.data?.let { EntryImages(it) }?.let { newImageArray.add(it) }
                    }
                    newImagesAdapter.updateData()
                }
            } catch (exception: Exception) {
                Log.d("TAG", "" + exception.localizedMessage)
            }
        } else{
            newImageArray.clear()
            newImagesAdapter.updateData()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.viewBinding = ActivityCreateEntryBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        getLocation()

        this.recyclerView = viewBinding.imageListRecyclerView
        this.newImagesAdapter = EntryImageAdapter(newImageArray)
        this.newImagesAdapter.setViewType(EntryImageAdapter.NEW_IMAGE_LAYOUT)

        this.recyclerView.adapter = this.newImagesAdapter

        viewBinding.locationText.text =  this.currentlocation
        viewBinding.createBtn.setOnClickListener(View.OnClickListener {
            viewBinding.locationText.text = this.currentlocation
            finish()
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
        this.currentlocation = "Address\n${list?.get(0)?.getAddressLine(0)}"
//        viewBinding.apply {
//            textView5.text = "hello"
//        }
        Log.d("TAG", location.latitude.toString())
        Log.d("TAG", location.longitude.toString())
        Log.d("TAG", "Address\n${list?.get(0)?.getAddressLine(0)}")
        Log.d("TAG", currentlocation)

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

    companion object {
        const val POSITION_KEY = "POSITION_KEY"
        const val TITLE_KEY = "TITLE_KEY"
        const val LOCATION_NAME_KEY = "LOCATION_NAME_KEY"
        const val IMAGE_KEY = "IMAGE_KEY"
        const val DESCRIPTION_KEY = "DESCRIPTION_KEY"
        const val CREATED_AT_KEY = "CREATED_AT_KEY"
    }
}