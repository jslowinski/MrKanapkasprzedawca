package com.example.mrkanapka_sprzedawca.api

import com.example.mrkanapka_sprzedawca.api.model.*
import io.reactivex.Observable
import retrofit2.http.*

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

    @PUT("login")
    fun login(@Body body: RequestLogin): Observable<ResponseLogin>

    @Headers("Authorization: key=AAAA5vPcH3I:APA91bHAtWhBhuHv6EAfwxdu186kUT_0718-wd8LXW7psIEMfJLeRDopdgVHTKsj3gqQxJ8g7iQII3vywFmL8G8vx1ZnBvRlvyR5ClcAyg7VR31BpIVrKXOM4kZr_SKURUXcjLKy2KVU")
    @POST("send")
    fun sendSinglePush(@Body body: RequestSinglePush<PushNotification,PushData>): Observable<ResponseNotification>

    @PUT("shopping")
    fun fetchProducts(@Body body: RequestShopping): Observable<Response<List<ResponseShopping>>>
}