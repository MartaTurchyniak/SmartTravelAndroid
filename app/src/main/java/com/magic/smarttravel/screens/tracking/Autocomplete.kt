package com.magic.smarttravel.screens.tracking

import android.os.Parcelable
import com.google.android.libraries.places.api.model.AutocompletePrediction
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

/**
 * Created by Marta Turchyniak on 12/2/20.
 */
@Parcelize
data class Autocomplete(
    val startDestination: AutocompletePrediction?,
    val endDestination: AutocompletePrediction?
): Parcelable