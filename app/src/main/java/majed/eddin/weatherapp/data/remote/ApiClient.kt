package majed.eddin.weatherapp.data.remote

import majed.eddin.weatherapp.data.consts.AppConst
import okhttp3.Cache
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

object ApiClient {

    private var ourInstance: Retrofit? = null

    private val cacheDir =
        File(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString())
    private val cache = Cache(cacheDir, 1024)
    private val logging = HttpLoggingInterceptor()
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .cache(cache)
        .retryOnConnectionFailure(true)
        .connectionPool(ConnectionPool(15, 500000, TimeUnit.MILLISECONDS))
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(logging)


    private fun getClient(domainUrl: String): Retrofit {
        logging.level =
            if (AppConst.instance.isDebug) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE

        ourInstance = Retrofit.Builder()
            .baseUrl(domainUrl)
            .client(okHttpClient.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return ourInstance!!
    }


    fun getInstance(): ApiService =
        getClient(AppConst.instance.appBaseUrl).create(ApiService::class.java)

}