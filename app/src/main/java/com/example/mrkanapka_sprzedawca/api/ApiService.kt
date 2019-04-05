package com.example.mrkanapka_sprzedawca.api

import com.example.mrkanapka_sprzedawca.api.model.*
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Url

interface ApiService {

    @GET
    fun fetchDestination(@Url url: String): Observable<ResponseDestination<List<DestinationDto>>>

    @GET
    fun fetchDates(@Url url: String): Observable<Response<List<DateDto>>>

    @GET
    fun fetchOrders(@Url url: String): Observable<Response<List<OrderDto>>>
}