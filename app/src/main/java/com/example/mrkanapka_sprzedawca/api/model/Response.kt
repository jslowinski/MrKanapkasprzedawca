package com.example.mrkanapka_sprzedawca.api.model

data class Response<T> (
    var count: Int,
    var destinations: T,
    var dates: T,
    var orders: T,
    var components: T
)