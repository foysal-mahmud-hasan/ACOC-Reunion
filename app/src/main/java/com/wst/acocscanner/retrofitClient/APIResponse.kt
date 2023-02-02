package com.wst.acocscanner.retrofitClient

import com.wst.acocscanner.model.*

class APIResponse {
    var error : Boolean = false
    var error_msg : String? = null
    var user : User? = null
    var registrationdetails : Registrationdetails? = null
    var eventId : EventId? = null
    var cadetDetails : CadetDetails? = null
    var events : List<Event>? = null
    var registrationIdForOE : RegistrationIdForOE? = null
}

