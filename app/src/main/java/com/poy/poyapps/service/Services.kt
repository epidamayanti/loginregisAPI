package com.poy.poyapps.service

import com.poy.poyapps.commons.Utils
import com.poy.poyapps.model.LoginModel
import com.poy.poyapps.model.RegisModel
import com.poy.poyapps.response.Response
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

interface Services {
    @POST(Utils.ENDPOINT_LOGIN)
    fun Login(
        @Body  data: LoginModel
    ): Observable<Response>

    @POST(Utils.ENDPOINT_REGISTER)
    fun Register(
        @Body  data: RegisModel
    ): Observable<Response>


}