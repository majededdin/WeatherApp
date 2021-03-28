package majed.eddin.weatherapp.ui.view.activities

import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import majed.eddin.weatherapp.R
import majed.eddin.weatherapp.data.consts.Params.Companion.PERMISSIONS_LOCATION
import majed.eddin.weatherapp.data.model.modified.ErrorHandler
import majed.eddin.weatherapp.data.model.service.Weather
import majed.eddin.weatherapp.data.remote.ApiResponse
import majed.eddin.weatherapp.data.remote.ApiStatus
import majed.eddin.weatherapp.databinding.ActivityMainBinding
import majed.eddin.weatherapp.ui.base.BaseActivity
import majed.eddin.weatherapp.ui.viewModel.WeatherViewModel
import majed.eddin.weatherapp.utils.extentionUtils.getLocationWithCheckNetworkAndGPS
import majed.eddin.weatherapp.utils.extentionUtils.hideKeyboard
import majed.eddin.weatherapp.utils.extentionUtils.toGone
import majed.eddin.weatherapp.utils.extentionUtils.toVisible
import java.util.*

class MainActivity : BaseActivity<WeatherViewModel>(), View.OnClickListener, TextWatcher {
    private lateinit var binding: ActivityMainBinding

    private lateinit var weatherVM: WeatherViewModel

    private var apiResponse: ApiResponse<Weather> = ApiResponse()

    private var name: String? = null


    override fun getViewModel(): Class<WeatherViewModel> = WeatherViewModel::class.java


    override fun viewModelInstance(viewModel: WeatherViewModel?) {
        weatherVM = viewModel!!
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewInit()

        if (getLocationWithCheckNetworkAndGPS() != null)
            getCurrentCity(getLocationWithCheckNetworkAndGPS()!!)

        weatherVM.weatherResult.observe(this, this::weatherResult)
    }

    private fun getCurrentCity(location: Location) {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses: List<Address> =
            geocoder.getFromLocation(location.latitude, location.longitude, 1)
        val cityName: String = addresses[0].subAdminArea
        binding.editSearch.setText(cityName)
    }


    override fun updateView() {
        apiResponse.listOfModel.clear()

        weatherVM.getWeather(name)
    }


    private fun weatherResult(apiResponse: ApiResponse<Weather>) {
        this.apiResponse = apiResponse
        handleApiResponse(apiResponse) { updateView() }
        if (apiResponse.apiStatus == ApiStatus.OnSuccess) {
            setResponseResult(apiResponse.listOfModel)
        }
    }


    private fun setResponseResult(list: ArrayList<Weather>) {
        if (list.isNotEmpty()) {
            binding.txtWeatherValue.text = list[0].main
            binding.txtWeatherDescription.text = list[0].description
        } else {
            binding.txtWeatherValue.text = getString(R.string.no_data)
            binding.txtWeatherDescription.text = getString(R.string.no_data)
            apiResponse.listOfModel.clear()
        }
    }


    override fun setErrorHandler(handler: ErrorHandler) {
    }


    override fun viewInit() {
        binding.imgClose.setOnClickListener(this)
        binding.editSearch.addTextChangedListener(this)
        binding.editSearch.clearFocus()

    }


    override fun onClick(p0: View?) {
        when (p0?.id) {

            R.id.img_close -> {
                getCustomView().hideKeyboard()
                setInputSearch(null)
                binding.imgClose.toGone()
            }

        }

    }


    private fun setInputSearch(query: String?) {
        name = if (!query.isNullOrEmpty()) {
            binding.txtWeatherValue.text = getString(R.string.waiting)
            binding.txtWeatherDescription.text = getString(R.string.waiting)
            query
        } else {
            binding.editSearch.text!!.clear()
            binding.txtWeatherValue.text = getString(R.string.no_data)
            binding.txtWeatherDescription.text = getString(R.string.no_data)
            null
        }
        updateView()

    }


    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        binding.imgClose.toVisible()
        setInputSearch(p0.toString().trim())
    }

    override fun afterTextChanged(p0: Editable?) {

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSIONS_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentCity(getLocationWithCheckNetworkAndGPS()!!)
            } else
                getLocationWithCheckNetworkAndGPS()
        }
    }

}