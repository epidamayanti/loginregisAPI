package com.poy.poyapps

import android.os.Bundle
import android.widget.Toast
import com.poy.poyapps.commons.BaseActivity
import com.poy.poyapps.commons.RxBus
import com.poy.poyapps.commons.Utils
import com.poy.poyapps.view.Login
import com.poy.poyapps.view.Register

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        changeFragment(Login(), false, Utils.LOGIN)
        manageSubscription()
    }

    override fun onResume() {
        super.onResume()

    }

    private fun manageSubscription(){
        subscriptions.add(RxBus.get().toObservable().subscribe({
                event -> manageBus(event)
        },{ Toast.makeText(this, "Timeout", Toast.LENGTH_SHORT).show()}))
    }

    private fun manageBus(event:Any) {
        when (event) {
            Utils.LOGIN -> changeFragment(Login(), false, Utils.LOGIN)
            Utils.REGISTER -> changeFragment(Register(), false, Utils.REGISTER)
        }
    }
}
