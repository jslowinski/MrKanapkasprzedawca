package com.example.mrkanapka_sprzedawca.api

import com.example.mrkanapka_sprzedawca.api.model.*
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Url

interface ApiService {

    @GET
    fun fetchDestination(@Url url: String): Observable<Response<List<DestinationDto>>>

    @GET
    fun fetchDates(@Url url: String): Observable<Response<List<DateDto>>>

    @GET
    fun fetchOrders(@Url url: String): Observable<Response<List<OrderDto>>>

    @PUT("change/onetransport")
    fun changeOnTransport(@Body body: RequestStatus): Observable<ResponseStatus>

    @PUT("change/oneready")
    fun changeOnReady(@Body body: RequestStatus): Observable<ResponseStatus>
}