package com.example.mrkanapka_sprzedawca.manager

import com.example.mrkanapka_sprzedawca.api.ApiClient
import com.example.mrkanapka_sprzedawca.api.model.DateDto
import com.example.mrkanapka_sprzedawca.api.model.DestinationDto
import com.example.mrkanapka_sprzedawca.api.model.OrderDto
import com.example.mrkanapka_sprzedawca.api.model.ResponseLogin
import com.example.mrkanapka_sprzedawca.database.AndroidDatabase
import com.example.mrkanapka_sprzedawca.database.entity.DateEntity
import com.example.mrkanapka_sprzedawca.database.entity.DestinationsEntity
import com.example.mrkanapka_sprzedawca.database.entity.OrderEntity
import com.example.mrkanapka_sprzedawca.database.entity.TokenEntity
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class OrdersManager {


    private val apiService by lazy {
        ApiClient.create()
    }

    private val database by lazy {
        AndroidDatabase.database
    }


    //region ORDER
    fun downloadOrders(url: String, data: String, id_destination: Int): Completable =
        apiService
            .fetchOrders(url)
            .flatMapCompletable {
                saveOrders(it.orders, data, id_destination)
            }
            .subscribeOn(Schedulers.io())

    private fun saveOrders(
        orderDto: List<OrderDto>,
        data: String,
        id_destination: Int
    ) =
        Completable.fromAction {
            val entities = orderDto.map {
                OrderEntity(
                    it.email,
                    it.order_number,
                    it.status,
                    data,
                    id_destination
                )
            }
            database.OrderDao().removeAndInsert(entities, data, id_destination)
        }.subscribeOn(Schedulers.io())

    fun getOrders(data: String, id_destination: Int): Maybe<List<OrderEntity>> =
        database
            .OrderDao()
            .getOrders(data, id_destination)
            .subscribeOn(Schedulers.io())

    //endregion

    //region DATA
    fun downloadData(url: String, id_destination: Int): Completable =
        apiService
            .fetchDates(url)
            .flatMapCompletable {
                saveData(it.dates, id_destination)
            }
            .subscribeOn(Schedulers.io())

    private fun saveData(
        dateDto: List<DateDto>,
        id_destination: Int
    ) =
        Completable.fromAction {
            val entities = dateDto.map {
                DateEntity(
                    it.date,
                    id_destination
                )
            }
            database.DateDao().removeAndInsert(entities, id_destination)
        }.subscribeOn(Schedulers.io())

    fun getData(id_destination: Int): Maybe<List<DateEntity>> =
        database
            .DateDao()
            .getDate(id_destination)
            .subscribeOn(Schedulers.io())

    //endregion

    //region DESTINATIONS
    fun downloadDestination(url: String): Completable =
        apiService
            .fetchDestination(url)
            .flatMapCompletable {
                saveDestination(it.destinations)
            }
            .subscribeOn(Schedulers.io())

    private fun saveDestination(
        destinationDto: List<DestinationDto>
    ) =
        Completable.fromAction {
            val entities = destinationDto.map {
                DestinationsEntity(
                    it.id_destination,
                    it.name
                )
            }
            database.DestinationsDao().removeAndInsert(entities)
        }.subscribeOn(Schedulers.io())

    fun getDestination(): Maybe<List<DestinationsEntity>> =
        database
            .DestinationsDao()
            .getDestinations()
            .subscribeOn(Schedulers.io())
    //endregion

    //region STATUS
    fun updateStatus(order_number: String, status: String) =
            Completable.fromAction{
                database.OrderDao().updateStatus(order_number,status)
            }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    // data updated
                }
    //endregion

    //region TOKEN
    fun saveToken(token: String, id_seller: Int) =
            Completable.fromAction{
                database.TokenDao()
                    .removeAndInsert(TokenEntity(token, id_seller))
            }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    // data updated
                }

    fun getToken(): Single<TokenEntity> =
        database
            .TokenDao()
            .getToken()
            .subscribeOn(Schedulers.io())

    fun removeToken() =
            Completable.fromAction {
                database.TokenDao()
                    .removeToken()
            }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    // data updated
                }

    //endregion
}