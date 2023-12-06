package com.example.zshopfinal.models.validations

data class customerRegValidation(
    private var name: String,
    private var pass: String,
    private var email: String,
    private var phone: String,
    private var guphone: String,
    val guphone1: String,
){
    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

    fun validateName(): ValidationResult {
        return if(name.isEmpty()){
            ValidationResult.Empty("Please enter first name")
        } else {
            ValidationResult.Valid
        }
    }

    fun validatePass(): ValidationResult {
        return if(pass.isEmpty()){
            ValidationResult.Empty("Please enter Password")
        } else if(pass.length <= 5) {
            ValidationResult.Invalid("Password should have least 6 characters")
        } else {
            ValidationResult.Valid
        }
    }

//    fun validateEmail(): ValidationResult {
//        return if(email.isEmpty()){
//            ValidationResult.Empty("Email address should not be empty")
//        } else if(!email.matches(emailPattern.toRegex())) {
//            ValidationResult.Invalid("Email format is wrong")
//        } else {
//            ValidationResult.Valid
//        }
//    }

//    fun validatePhone(): ValidationResult {
//        return if(phone.isEmpty()){
//            ValidationResult.Empty("Please enter Contact number")
//        } else if(phone.length != 10) {
//            ValidationResult.Invalid("Enter a valid contact number")
//        } else {
//            ValidationResult.Valid
//        }
//    }

    fun validateGuphone(): ValidationResult {
        return if(guphone.isEmpty()){
            ValidationResult.Empty("Please enter Contact number")
        } else if(guphone.length != 10) {
            ValidationResult.Invalid("Enter a valid contact number")
        } else {
            ValidationResult.Valid
        }
    }
}
