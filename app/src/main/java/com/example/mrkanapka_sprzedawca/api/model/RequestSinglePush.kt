package com.example.mrkanapka_sprzedawca.api.model

data class RequestSinglePush<T,N> (
    val notification: T,
    val data: N,
    val to: String

)