package com.example.musichead.viewmodels

import android.app.Application
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.example.musichead.api.AppRepository
import com.example.musichead.models.Places
import com.example.musichead.models.ResponseBase
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart


class MainViewModel @ViewModelInject constructor(
    application: Application,
    private val repository: AppRepository
) : AndroidViewModel(application) {


    fun  GetplacesData(place : String,limit : Int) = liveData<ResponseBase> {
            repository.getPlacesData(place,limit)
                .onStart {
                }
                .catch {
                }
                .collect {

                    emit(it)
                }
        }

}