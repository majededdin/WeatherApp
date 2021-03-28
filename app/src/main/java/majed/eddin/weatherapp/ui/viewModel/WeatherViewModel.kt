package majed.eddin.weatherapp.ui.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import majed.eddin.weatherapp.data.model.service.Weather
import majed.eddin.weatherapp.data.remote.ApiResponse
import majed.eddin.weatherapp.data.repository.WeatherRepo

class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    private val repo: WeatherRepo = WeatherRepo()
    private var searchJob: Job? = null

    private val weatherResponse = MutableLiveData<ApiResponse<Weather>>()

    val weatherResult: LiveData<ApiResponse<Weather>>
        get() = weatherResponse


    fun getWeather(name: String?) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch(Dispatchers.IO) {
            repo.getWeather(name).collect { weatherResponse.postValue(it) }
        }
    }

}