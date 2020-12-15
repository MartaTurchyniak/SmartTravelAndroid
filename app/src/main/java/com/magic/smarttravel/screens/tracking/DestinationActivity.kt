package com.magic.smarttravel.screens.tracking

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.magic.smarttravel.R
import kotlinx.android.synthetic.main.activity_destination.*


/**
 * Created by Marta Turchyniak on 12/1/20.
 */
class DestinationActivity : AppCompatActivity(), DestinationAdapter.InteractionListener {

    private lateinit var destinationAdapter: DestinationAdapter
    private val REQUEST_LOCATION = 1

    var lat: Double? = -1.0
    var lon: Double? = -1.0
    var predictionStart: AutocompletePrediction? = null
    var predictionEnd: AutocompletePrediction? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_destination)
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_LOCATION
        )
        initAdapter()
        back.setOnClickListener {
            val returnIntent = Intent()
            setResult(Activity.RESULT_CANCELED, returnIntent)
            finish()
        }
        saveChanges.setOnClickListener {
            val result = Autocomplete(predictionStart, predictionEnd)
            val returnIntent = Intent()
            returnIntent.putExtra("result", result)
            setResult(RESULT_OK, returnIntent)
            finish()
        }

        tvStartDest.addTextChangedListener(startTextWatcher)

        tvEndDest.addTextChangedListener(endTextWatcher)
    }

    val startTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            //autogen
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            //autogen
        }

        override fun afterTextChanged(p0: Editable?) {
            handlerStart.removeCallbacks(refreshActionStart)
            handlerStart.postDelayed(refreshActionStart, 300)
            query = p0.toString()
        }

    }

    val endTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            //autogen
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            //autogen
        }

        override fun afterTextChanged(p0: Editable?) {
            handlerEnd.removeCallbacks(refreshActionEnd)
            handlerEnd.postDelayed(refreshActionEnd, 300)
            query = p0.toString()
        }

    }

    private fun initAdapter() {
        rvDestination.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        destinationAdapter = DestinationAdapter(this)
        rvDestination.adapter = destinationAdapter
    }

    private var query = ""
    private val handlerStart = Handler()
    private val refreshActionStart = Runnable {
        showSuggestions(query, true)
    }
    private val handlerEnd = Handler()
    private val refreshActionEnd = Runnable {
        showSuggestions(query, false)
    }

    private fun showSuggestions(query: String, isStart: Boolean) {
        // Create a new PlacesClient instance
        val placesClient = Places.createClient(this)

        // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
        // and once again when the user makes a selection (for example when calling fetchPlace()).
        val token = AutocompleteSessionToken.newInstance()

        // Create a RectangularBounds object.
        val bounds = RectangularBounds.newInstance(
            LatLng(-33.880490, 151.184363),
            LatLng(-33.858754, 151.229596)
        )

        // Use the builder to create a FindAutocompletePredictionsRequest.
        val request =
            FindAutocompletePredictionsRequest.builder()
                // Call either setLocationBias() OR setLocationRestriction().
                .setLocationBias(bounds)
                .setOrigin(LatLng(-33.8749937, 151.2041382))
                .setSessionToken(token)
                .setQuery(query)
                .build()

        getSuggestionPlaces(placesClient, request, isStart)
    }

    private fun getSuggestionPlaces(
        placesClient: PlacesClient,
        request: FindAutocompletePredictionsRequest,
        isStart: Boolean
    ): Task<FindAutocompletePredictionsResponse> {
        return placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response: FindAutocompletePredictionsResponse ->
                rvDestination.visibility = View.VISIBLE
                destinationAdapter.items = response.autocompletePredictions
                destinationAdapter.isStart = isStart
                for (prediction in response.autocompletePredictions) {
                    Log.i("places", prediction.placeId)
                    Log.i("places", prediction.getPrimaryText(null).toString())
                }
            }.addOnFailureListener { exception: Exception? ->
                if (exception is ApiException) {
                    Log.e("places", "Place not found: " + exception.statusCode)
                }
            }
    }

    companion object {

        const val RESULT_CODE = 221

        fun startActivity(context: Activity) {
            val intent = Intent(context, DestinationActivity::class.java)
            context.startActivityForResult(intent, RESULT_CODE)
        }
    }

    override fun onClick(prediction: AutocompletePrediction, isStart: Boolean) {
        if (isStart) {
            tvStartDest.removeTextChangedListener(startTextWatcher)
            tvStartDest.setText(prediction.getFullText(null))
            predictionStart = prediction
            tvStartDest.addTextChangedListener(startTextWatcher)
        } else {
            tvStartDest.removeTextChangedListener(endTextWatcher)
            tvEndDest.setText(prediction.getFullText(null))
            predictionEnd = prediction
            tvStartDest.addTextChangedListener(endTextWatcher)
        }
        rvDestination.visibility = View.GONE
    }
}