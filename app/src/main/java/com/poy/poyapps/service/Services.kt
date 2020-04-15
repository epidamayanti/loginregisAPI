package com.poy.poyapps.service

import com.poy.poyapps.commons.Utils
import com.poy.poyapps.model.LoginModel
import com.poy.poyapps.response.LoginRes
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

interface Services {
    @POST(Utils.ENDPOINT_LOGIN)
    fun Login(
        @Body  data: LoginModel
    ): Observable<LoginRes>



}