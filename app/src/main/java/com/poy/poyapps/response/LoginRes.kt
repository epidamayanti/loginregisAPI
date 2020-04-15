package com.poy.poyapps.response

import com.poy.poyapps.model.DataUser

class LoginRes (val status:Boolean,
                val data: DataUser,
                val message:Any
)