package com.poy.poyapps.view

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.poy.poyapps.R
import com.poy.poyapps.commons.LoadingAlert
import com.poy.poyapps.commons.RxBaseFragment
import com.poy.poyapps.commons.RxBus
import com.poy.poyapps.commons.Utils
import com.poy.poyapps.model.RegisModel
import com.poy.poyapps.service.Services
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_register.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class Register : RxBaseFragment() {

    private lateinit var retrofit: Retrofit
    private var loading: Dialog? = null
    private val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> {
                dialog.dismiss()
                RxBus.get().send(Utils.LOGIN)
            }

            DialogInterface.BUTTON_NEGATIVE -> {
                dialog.dismiss()

            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loading = LoadingAlert.regisDialog(this.context!!, this.activity!!)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onResume() {
        super.onResume()

        registerButton.setOnClickListener {
            loading?.show()
            uploadData(et_email.text.toString(), et_name.text.toString(), et_password.text.toString())
        }
    }

    private fun uploadData(email:String, name:String, password:String){
        val data = RegisModel(name, email, password)

        subscriptions.add(providRegistService().Register(data)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                    user ->
                loading?.dismiss()
                if (user.status) {
                    val builder = AlertDialog.Builder(context)
                    builder.setMessage("${user.message}").setPositiveButton("OK", dialogClickListener).setCancelable(false).show()

                } else {
                    Toast.makeText(context, "${user.message}", Toast.LENGTH_LONG).show()
                }
            }, {
                    err ->
                loading?.dismiss()
                val builder = AlertDialog.Builder(context)
                builder.setMessage("Gagal login : "+err.localizedMessage).setNegativeButton("coba lagi", dialogClickListener).show()

            })
        )
    }

    private fun providRegistService(): Services {
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
