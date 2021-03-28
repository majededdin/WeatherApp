package majed.eddin.weatherapp.ui.base

import am.networkconnectivity.NetworkConnectivity
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import majed.eddin.weatherapp.R
import majed.eddin.weatherapp.data.model.modified.ErrorHandler
import majed.eddin.weatherapp.data.remote.ApiResponse
import majed.eddin.weatherapp.data.remote.ApiStatus
import majed.eddin.weatherapp.utils.Utils.Companion.errorResponseHandler
import majed.eddin.weatherapp.utils.extentionUtils.toGone
import majed.eddin.weatherapp.utils.extentionUtils.toVisible
import org.json.JSONException
import org.json.JSONObject

abstract class BaseActivity<V : ViewModel> : AppCompatActivity() {

    private var viewModel: V? = null
    private lateinit var layout: RelativeLayout

    private var secondRun = false
    private lateinit var viewProgressBar: ProgressBar
    private lateinit var progressLoading: ProgressBar
    private lateinit var layoutNetworkStatus: LinearLayout
    private lateinit var txtNetworkStatus: MaterialTextView


    protected abstract fun getViewModel(): Class<V>?


    protected abstract fun viewModelInstance(viewModel: V?)


    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        handleNetworkResponse()
        viewModelInstance(initViewModel())

        super.onCreate(savedInstanceState)
    }


    abstract fun updateView()


    protected abstract fun setErrorHandler(handler: ErrorHandler)


    protected abstract fun viewInit()


    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }


    private fun initViewModel(): V? {
        if (getViewModel() != null)
            viewModel = ViewModelProvider(
                this, ViewModelProvider.AndroidViewModelFactory.getInstance(
                    application
                )
            ).get(getViewModel()!!)
        return viewModel
    }


    @SuppressLint("InflateParams")
    override fun setContentView(view: View?) {
        layout = layoutInflater.inflate(R.layout.activity_base, null) as RelativeLayout
        val coordinatorLayout: CoordinatorLayout = layout.findViewById(R.id.containerBase)
        baseInit()

        coordinatorLayout.addView(
            view, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )

        super.setContentView(layout)
    }


    private fun baseInit() {
        viewProgressBar = layout.findViewById(R.id.view_progress_bar)
        progressLoading = layout.findViewById(R.id.progress_loading)
        txtNetworkStatus = layout.findViewById(R.id.txt_networkStatus)
        layoutNetworkStatus = layout.findViewById(R.id.layout_networkStatus)
    }


    private fun showLoadingFragment(status: Boolean) {
        CoroutineScope(Main).launch {
            if (!status) viewProgressBar.toGone() else viewProgressBar.toVisible()
        }
    }


    open fun getCustomView(): View = findViewById(android.R.id.content)


    open fun showMessage(message: String?) =
        Snackbar.make(getCustomView(), message!!, BaseTransientBottomBar.LENGTH_SHORT).show()


    open fun getResponseErrors(jsonObject: JSONObject) {
        val iterator = jsonObject.keys()
        while (iterator.hasNext()) {
            val key = iterator.next()
            var errorValue: String? = null
            try {
                errorValue = errorResponseHandler(jsonObject.getJSONArray(key))
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            setErrorHandler(ErrorHandler(key, errorValue!!))
        }
    }


    fun handleApiResponse(apiResponse: ApiResponse<*>, failureListener: View.OnClickListener) {
        when (apiResponse.apiStatus) {
            ApiStatus.OnAuth -> {
                showLoadingFragment(false)
            }

            ApiStatus.OnBackEndError -> {
                showLoadingFragment(false)
            }

            ApiStatus.OnError -> {
                showLoadingFragment(false)
                if (apiResponse.errorBodyAsJSON != null)
                    getResponseErrors(apiResponse.errorBodyAsJSON!!)
                else
                    showMessage(apiResponse.message)
            }

            ApiStatus.OnFailure -> {
                showLoadingFragment(false)
                Log.e("OnFailure ", apiResponse.message)
            }


            ApiStatus.OnHttpException -> {
                showLoadingFragment(false)
                showMessage(apiResponse.message)
            }

            ApiStatus.OnLoading -> showLoadingFragment(true)

            ApiStatus.OnNotFound -> {
                showLoadingFragment(false)
                showMessage(apiResponse.message)
            }

            ApiStatus.OnBadRequest -> {
                showLoadingFragment(false)
                showMessage(apiResponse.message)
            }

            ApiStatus.OnTimeoutException -> {
                showLoadingFragment(false)
                Snackbar.make(
                    getCustomView(),
                    getString(R.string.request_timeout_please_check_your_connection),
                    Snackbar.LENGTH_INDEFINITE
                ).setAction(getString(R.string.retry), failureListener).show()
            }

            ApiStatus.OnUnknownHost, ApiStatus.OnConnectException -> {
                showLoadingFragment(false)
                onNetworkConnectionChanged(NetworkConnectivity.NetworkStatus.OnLost)
            }

            ApiStatus.OnSuccess -> showLoadingFragment(false)
        }
    }


    private fun handleNetworkResponse() {
        NetworkConnectivity(this).observe(this, { onNetworkConnectionChanged(it) })
    }


    open fun onNetworkConnectionChanged(status: NetworkConnectivity.NetworkStatus) {
        when (status) {
            NetworkConnectivity.NetworkStatus.OnConnected -> {
                if (secondRun) {
                    secondRun = false
                    txtNetworkStatus.text = getString(R.string.connection_is_back)
                    progressLoading.toGone()
                    layoutNetworkStatus.setBackgroundColor(
                        ContextCompat.getColor(this, R.color.colorNetworkConnected)
                    )

                    CoroutineScope(Main).launch {
                        delay(2000)
                        layoutNetworkStatus.toGone()
                    }
                }
            }

            NetworkConnectivity.NetworkStatus.OnWaiting -> {
                secondRun = true
                txtNetworkStatus.text = getString(R.string.waiting_for_connection)
                progressLoading.toVisible()
                layoutNetworkStatus.toVisible()
                layoutNetworkStatus.setBackgroundColor(
                    ContextCompat.getColor(this, R.color.colorNetworkWaiting)
                )
            }

            NetworkConnectivity.NetworkStatus.OnLost -> {
                secondRun = true
                txtNetworkStatus.text = getString(R.string.connection_is_lost)
                progressLoading.toGone()
                layoutNetworkStatus.toVisible()
                layoutNetworkStatus.setBackgroundColor(
                    ContextCompat.getColor(this, R.color.colorNetworkLost)
                )
            }
        }
    }

}