package majed.eddin.weatherapp.data.repository

import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import majed.eddin.weatherapp.data.consts.AppConst
import majed.eddin.weatherapp.data.consts.Params
import majed.eddin.weatherapp.data.consts.Params.Companion.APP_ID
import majed.eddin.weatherapp.data.model.service.Weather
import majed.eddin.weatherapp.data.remote.ApiClient
import majed.eddin.weatherapp.data.remote.ApiResponse
import majed.eddin.weatherapp.data.remote.ApiStatus

open class WeatherRepo {

    private var apiService = ApiClient.getInstance()

    //---------------------------------------- ApiResponse Methods ---------------------------------

    private fun <M> getApiError(throwable: Throwable) = ApiResponse<M>().getErrorBody(throwable)

    //---------------------------------------- Global Methods ---------------------------------

    fun getWeather(keyword: String?) = flow {
        emit(ApiResponse(ApiStatus.OnLoading))

        val map: java.util.HashMap<String, Any> = java.util.HashMap()

        map[APP_ID] = AppConst.instance.appKey

        if (!keyword.isNullOrEmpty())
            map["q"] = keyword
        else
            map.remove("q")

        val restaurantsResponse =
            ApiResponse<Weather>(
                apiService.getWeather(map).string(),
                Params.WEATHER, object : TypeToken<List<Weather>>() {}.type
            )

        emit(restaurantsResponse.getApiResult())
    }.catch { emit(getApiError(it)) }

}