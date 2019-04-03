package com.example.mrkanapka_sprzedawca.api

import com.example.mrkanapka_sprzedawca.api.model.DateDto
import com.example.mrkanapka_sprzedawca.api.model.DestinationDto
import com.example.mrkanapka_sprzedawca.api.model.OrderDto
import com.example.mrkanapka_sprzedawca.api.model.Response
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Url

interface ApiService {

    @GET
    fun fetchDestination(@Url url: String): Single<Response<List<DestinationDto>>>

    @GET
    fun fetchDates(@Url url: String): Single<Response<List<DateDto>>>

    @GET
    fun fetchOrders(@Url url: String): Single<Response<List<OrderDto>>>
}