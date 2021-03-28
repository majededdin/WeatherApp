package majed.eddin.weatherapp.utils

import android.util.Log
import majed.eddin.weatherapp.data.consts.AppConst
import org.json.JSONArray
import org.json.JSONException

class Utils {
    companion object {


        fun errorResponseHandler(arr: JSONArray): String {
            val errorKey = StringBuilder()
            for (i in 0 until arr.length()) {
                try {
                    if (AppConst.instance.isDebug)
                        Log.e("ErrorResponseHandler:", "arr " + arr.getString(i))
                    errorKey.append(arr[i])
                    if (i != arr.length() - 1) errorKey.append("\n")
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
            return errorKey.toString()
        }

    }

}