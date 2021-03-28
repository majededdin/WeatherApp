package majed.eddin.weatherapp.data.model.modified

data class ErrorHandler(var key: String?) {

    var value: String? = null
    var isDataValid: Boolean = true

    constructor(key: String, value: String?) : this(key) {
        this.value = value
        this.isDataValid = false
    }


    override fun toString(): String {
        return "ErrorHandler(key=$key, value=$value, isDataValid=$isDataValid)"
    }

}