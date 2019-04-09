package com.example.mrkanapka_sprzedawca.api.model

data class OrderDto (
    var email: String,
    var order_number: String,
    var status: String,
    var registrationid: String
)