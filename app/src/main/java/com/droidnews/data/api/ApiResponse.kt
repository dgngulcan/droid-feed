package com.droidnews.data.api

import retrofit2.Response
import java.io.IOException

/**
 * Generic class used for API responses.
 *
 * Created by Dogan Gulcan on 9/22/17.
 */
class ApiResponse<T> {

    var code = 0
    var body: T? = null
    var errorMessage = ""

    constructor(error: Throwable) {
        code = 500
        body = null
        errorMessage = error.message ?: ""
    }

    constructor(response: Response<T>) {
        code = response.code()

        if (response.isSuccessful) {
            body = response.body()
            errorMessage = ""

        } else {
            var message: String? = null
            if (response.errorBody() != null) {
                try {
                    message = response.errorBody()?.string()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
            if (message == null || message.trim { it <= ' ' }.isBlank()) {
                message = response.message()
            }
            errorMessage = message ?: ""
            body = null
        }
    }

    fun isSuccessful(): Boolean {
        return code in 200..299
    }

}