package com.example.musichead.activities

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.ViewGroup
import android.widget.EditText
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.example.musichead.R
import com.example.musichead.models.Places
import com.example.musichead.viewmodels.MainViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay


@AndroidEntryPoint
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val mainViewModel: MainViewModel by viewModels()
    private var fab : FloatingActionButton ? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        fab = findViewById(R.id.fab)
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fab?.setOnClickListener{
            showDialog()
        }
        showDialog()
    }

    private fun setMarkers(placesList: List<Places>) {
        mMap.clear()
        var count : Int = 0
        var timer : Int = 0
        val consYear : Int = 1990
        try {
            val builder = LatLngBounds.builder()
            for (place in placesList){
                if (place.coordinates!=null && place.life_span.begin!=null ){
                    if(place.life_span.begin.take(4).toInt()>1990){
                        count++
                        timer = place.life_span.begin.take(4).toInt().minus(consYear)
                        Log.d("timer :", consYear.toString()+":"+place.life_span.begin.take(4).toInt())
                        var timerNew : String = ""
                        val newPlace = LatLng(
                                place.coordinates.latitude.toDouble(),
                                place.coordinates.longitude.toDouble()
                        )
                        val markeroptions : MarkerOptions = (MarkerOptions().position(newPlace).icon(bitMapFromVector(R.drawable.ic_music)).title(place.name))
                        val marker : Marker = mMap.addMarker(markeroptions)
                        builder.include(newPlace)
                        timerNew = timer.toString()+"000"
                        Log.d("timer :", count.toString()+":"+timerNew)
                        Handler(Looper.myLooper() ?: return).postDelayed({
                            marker.remove()
                        }, (timerNew.toLong()))
                    }
                }
            }
            val bounds = builder.build()
            val cu = CameraUpdateFactory.newLatLngBounds(bounds, 200)
            mMap.moveCamera(cu)
            mMap.animateCamera(CameraUpdateFactory.zoomTo(0F), 2000, null)
            Toast.makeText(this, count.toString()+" places found", Toast.LENGTH_SHORT).show()
        }
        catch (e: Exception){
            Log.d("error :", e.toString())
            Toast.makeText(this, "No data found!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.style_json
                )
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showDialog() {
        val customDialog = Dialog(this)
        customDialog.setContentView(R.layout.search_dialog)
        customDialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val edSearch = customDialog.findViewById(R.id.edMerchantName) as EditText
        val searchBtn = customDialog.findViewById(R.id.searchimei) as CardView
        val appCompatSeekBar = customDialog.findViewById(R.id.appCompatSeekBar) as AppCompatSeekBar
        val seekTv = customDialog.findViewById(R.id.seekTv) as TextView

        appCompatSeekBar.progress = 25
        appCompatSeekBar.incrementProgressBy(5)
        appCompatSeekBar.max = 100
        appCompatSeekBar.min = 25
        var limit = 25

        appCompatSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                limit  = progress
                seekTv.text = progress.toString()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        searchBtn.setOnClickListener {
            //Do something here
            mainViewModel.GetplacesData(edSearch.text.toString(),limit).observe(this, Observer {
                if (it == null) {
                    Toast.makeText(this, "Error Occoured!", Toast.LENGTH_SHORT).show()
                } else {
                    setMarkers(it.places)
                }
            })
            customDialog.dismiss()
        }
        customDialog.show()
    }

    private fun bitMapFromVector(vectorResID: Int):BitmapDescriptor {
        val vectorDrawable= ContextCompat.getDrawable(this, vectorResID)
        vectorDrawable!!.setBounds(0, 0, vectorDrawable!!.intrinsicWidth, vectorDrawable.intrinsicHeight)
        val bitmap=Bitmap.createBitmap(vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888)
        val canvas= Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}