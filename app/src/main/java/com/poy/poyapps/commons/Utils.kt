package com.poy.poyapps.commons

import android.annotation.SuppressLint
import com.poy.poyapps.model.Loc
import com.poy.poyapps.response.Response
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

class Utils {

    @SuppressLint("StaticFieldLeak")
    companion object {

        //endpoint
        const val ENDPOINT = "https://phyton06.site/poyapps/index.php/api/"
        const val ENDPOINT_LOGIN = "authentication/login"
        const val ENDPOINT_REGISTER = "authentication/registration"

        //link to view fragment
        const val LOGIN = "login"
        const val REGISTER = "register"
        const val DASHBOARD = "dashboard"

        //get location
        var latitude = ""
        var longitude = ""
        var loc: Loc? = null

        //get data
        var dataUser: Response? = null

        //retrofit
        fun buildClient(): OkHttpClient.Builder {
            val clientBuilder = OkHttpClient.Builder()
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            clientBuilder.addInterceptor(loggingInterceptor)
                .connectTimeout(5 , TimeUnit.MINUTES)
            return clientBuilder
        }
    }
}