package majed.eddin.weatherapp.data.remote

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.QueryMap

interface ApiService {

    @GET("weather")
    @Headers("Accept: application/json", "Content-Type: application/json")
    suspend fun getWeather(
        @QueryMap map: HashMap<String, Any>
    ): ResponseBody

}