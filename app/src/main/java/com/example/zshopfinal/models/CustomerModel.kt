package com.example.zshopfinal.models

data class CustomerModel(
    var name: String? = null,
    var pass: String? = null,
    var conpass: String? = null,
    var email: String? = null,
    var phone: String? = null,
    var guphone: String? = null,//gurdian phone number
    var address: String? = null,
    var street: String? = null,
    var houseNo: String? = null,
    var cuid: String? = null,

)
