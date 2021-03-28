package majed.eddin.weatherapp.utils.extentionUtils

import android.app.Service
import android.view.View
import android.view.inputmethod.InputMethodManager


fun View.hideKeyboard() = kotlin.run {
    (this.context.getSystemService(Service.INPUT_METHOD_SERVICE) as? InputMethodManager)
        ?.hideSoftInputFromWindow(this.windowToken, 0)
}


fun View.toVisible() = kotlin.run { this.visibility = View.VISIBLE }


fun View.toGone() = kotlin.run { this.visibility = View.GONE }