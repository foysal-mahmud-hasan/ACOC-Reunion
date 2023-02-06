package com.wst.acocscanner.retrofitClient

import com.wst.acocscanner.model.*

class APIResponse {
    var error : Boolean = false
    var error_msg : String? = null
    var user : User? = null
    var registrationdetails : Registrationdetails? = null
    var event : Event? = null
    var cadetDetails : CadetDetails? = null
    var events : List<Event>? = null
    var registrationIdForOE : RegistrationIdForOE? = null
    var parkings : List<Parking>? = null
    var eventList : List<EventList>? = null
}

