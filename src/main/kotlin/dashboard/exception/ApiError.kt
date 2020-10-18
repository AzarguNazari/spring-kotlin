package dashboard.exception

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id.CUSTOM
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver
import org.springframework.http.HttpStatus
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import java.time.LocalDateTime
import java.util.*
import java.util.function.Consumer

@JsonTypeInfo(include = WRAPPER_OBJECT, use = CUSTOM, property = "error", visible = true)
@JsonTypeIdResolver(LowerCaseClassNameResolver::class)
class ApiError  {

    var status: HttpStatus? = null

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    var timestamp: LocalDateTime = LocalDateTime.now()
    var message: String? = null
    var debugMessage: String? = null
    var subErrors: MutableList<ApiSubError>? = null

    private constructor(){
        timestamp = LocalDateTime.now()
    }

    constructor(status: HttpStatus) : this() {
        this.status = status
    }

    constructor(status: HttpStatus, ex: Throwable) : this() {
        this.status = status
        message = "Unexpected error"
        debugMessage = ex.localizedMessage
    }

    constructor(status: HttpStatus, message: String, ex: Throwable) : this() {
        this.status = status
        this.message = message
        debugMessage = ex.localizedMessage
    }

    constructor(message: String, status: HttpStatus) : this() {
        this.status = status
        this.message = message
    }

    private fun addSubError(subError: ApiSubError) {
        if (subErrors == null) {
            subErrors = ArrayList()
        }
        subErrors!!.add(subError)
    }

    private fun addValidationError(data: String, field: String, rejectedValue: Any?, message: String?) {
        addSubError(ApiValidationError(data, field, rejectedValue, message))
    }

    private fun addValidationError(data: String, message: String?) {
        addSubError(ApiValidationError(data, message))
    }

    private fun addValidationError(fieldError: FieldError) {
        this.addValidationError(
                fieldError.objectName,
                fieldError.field,
                fieldError.rejectedValue,
                fieldError.defaultMessage)
    }

    fun addValidationErrors(fieldErrors: List<FieldError>) {
        fieldErrors.forEach(Consumer { fieldError: FieldError -> this.addValidationError(fieldError) })
    }

    private fun addValidationError(objectError: ObjectError) {
        this.addValidationError(
                objectError.objectName,
                objectError.defaultMessage)
    }

    fun addValidationError(globalErrors: List<ObjectError>) {
        globalErrors.forEach(Consumer { objectError: ObjectError -> this.addValidationError(objectError) })
    }

}