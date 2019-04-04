package com.example.mrkanapka_sprzedawca.api.model

data class ResponseDestination<T> (
    var count: Int,
    var destinations: T
)