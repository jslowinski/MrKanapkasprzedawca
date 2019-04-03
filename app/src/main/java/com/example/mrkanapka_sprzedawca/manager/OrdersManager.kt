package com.example.mrkanapka_sprzedawca.manager

import com.example.mrkanapka_sprzedawca.api.ApiClient

class OrdersManager {
    //region API

    private val apiService by lazy {
        ApiClient.create()
    }

}