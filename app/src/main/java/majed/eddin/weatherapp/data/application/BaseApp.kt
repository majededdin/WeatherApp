package majed.eddin.weatherapp.data.application

import android.app.Application
import majed.eddin.weatherapp.BuildConfig
import majed.eddin.weatherapp.data.consts.AppConst

class BaseApp : Application() {

    private lateinit var appConst: AppConst

    companion object {
        var instance: BaseApp = BaseApp()
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        appConst = AppConst.instance
        initAppConst()
    }


    private fun initAppConst() {
        appConst.appInstance = this
        appConst.isDebug = BuildConfig.DEBUG
        appConst.appBaseUrl = BuildConfig.BASE_URL
        appConst.appKey = "84c87dc8399d52a80f9a26d6e58c6615"
    }

}