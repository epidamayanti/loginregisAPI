package com.poy.poyapps.view

import android.app.AlertDialog
import android.app.Dialog
import android.app.Service
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.poy.poyapps.R
import com.poy.poyapps.commons.LoadingAlert
import com.poy.poyapps.commons.RxBaseFragment
import com.poy.poyapps.commons.Utils
import com.poy.poyapps.model.LoginModel
import com.poy.poyapps.service.Services
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_login.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class Login : RxBaseFragment() {

    private lateinit var retrofit: Retrofit
    private var loading: Dialog? = null
    private val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> {
                dialog.dismiss()
            }

            DialogInterface.BUTTON_NEGATIVE -> {

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loading = LoadingAlert.loginDialog(this.context!!, this.activity!!)
    }

    override fun onResume() {
        super.onResume()

        loginButton.setOnClickListener {
            validateUser(et_username.text.toString(), et_password.text.toString())
        }
    }


    private fun validateUser(username: String, password: String) {
        loading?.show()
        val login = LoginModel(username, password)


        subscriptions.add(provideLoginService().Login(login)
            .observeOn(AndroidSchedulers.mainThread())
            .retry(5)
            .subscribe({
                    user ->
                loading?.dismiss()
                if (user.status) {
                    Utils.dataUser = user
                    Log.d("Sukses Login", "LOGIN dengan data:\n "+Utils.dataUser?.data)

                } else {
                    Toast.makeText(context, "${user.message}", Toast.LENGTH_LONG).show()
                }
            }, {
                    err ->
                loading?.dismiss()
                val builder = AlertDialog.Builder(context)
                builder.setMessage("Gagal login : "+err.localizedMessage).setPositiveButton("coba lagi", dialogClickListener).show()

            })
        )
    }


    private fun provideLoginService(): Services {
        val clientBuilder: OkHttpClient.Builder = Utils.buildClient()

        retrofit = Retrofit.Builder()
            .baseUrl(Utils.ENDPOINT)
            .client(clientBuilder
                .connectTimeout(3, TimeUnit.SECONDS)
                .writeTimeout(3, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .build()
        return retrofit.create(Services::class.java)
    }

}
