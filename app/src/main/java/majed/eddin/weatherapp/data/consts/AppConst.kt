package majed.eddin.weatherapp.data.consts

import android.app.Application

class AppConst {

    var isDebug = false
    lateinit var appInstance: Application
    lateinit var appBaseUrl: String
    lateinit var appKey: String

    companion object {
        var instance = AppConst()
    }

}