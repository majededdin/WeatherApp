package majed.eddin.weatherapp.data.model.service

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Weather(
    var id: Int,
    var main: String,
    var description: String,
    var icon: String
) : Parcelable