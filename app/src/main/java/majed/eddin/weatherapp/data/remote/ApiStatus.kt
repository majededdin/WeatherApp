package majed.eddin.weatherapp.data.remote

enum class ApiStatus {
    OnAuth,
    OnBackEndError,
    OnConnectException,
    OnError,
    OnFailure,
    OnHttpException,
    OnLoading,
    OnNotFound,
    OnBadRequest,
    OnSuccess,
    OnTimeoutException,
    OnUnknownHost
}