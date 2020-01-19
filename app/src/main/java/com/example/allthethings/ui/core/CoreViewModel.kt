package com.example.allthethings.ui.core

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CoreViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Toast, Snackbar & Notifications"
    }
    val text: LiveData<String> = _text
}