package com.example.mrkanapka_sprzedawca.api

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class ApiClient {
    companion object {

        private const val BASE_URL = "http://zespol9-server.herokuapp.com/api/"

        private val retrofit by lazy {
            Retrofit.Builder()
                .addCallAdapterFactory(
                    RxJava2CallAdapterFactory.create())
                .addConverterFactory(
                    GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
        }

        fun create(): ApiService {
            return retrofit.create(ApiService::class.java)
        }

    }
}