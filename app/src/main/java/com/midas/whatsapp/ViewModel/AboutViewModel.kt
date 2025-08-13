package com.midas.whatsapp.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AboutViewModel: ViewModel() {

    var aboutText = MutableLiveData<String>()
}