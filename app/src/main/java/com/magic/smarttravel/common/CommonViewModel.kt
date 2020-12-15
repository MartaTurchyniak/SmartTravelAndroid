package com.magic.smarttravel.common

import androidx.lifecycle.ViewModel
import com.magic.smarttravel.data.FirebaseRepository

abstract class CommonViewModel : ViewModel() {

    protected val signals = mutableListOf<FirebaseRepository.CancelSignal>()

    override fun onCleared() {
        signals.forEach { it.cancel() }
        signals.clear()
        super.onCleared()
    }
}