//package com.example.mrkanapka_sprzedawca
//
//import android.support.v7.app.AppCompatActivity
//import android.os.Bundle
//import com.example.mrkanapka_sprzedawca.database.entity.DateEntity
//import com.example.mrkanapka_sprzedawca.database.entity.DestinationsEntity
//import com.example.mrkanapka_sprzedawca.database.entity.OrderEntity
//import com.example.mrkanapka_sprzedawca.manager.OrdersManager
//import io.reactivex.android.schedulers.AndroidSchedulers
//import io.reactivex.disposables.CompositeDisposable
//import io.reactivex.rxkotlin.addTo
//import kotlinx.android.synthetic.main.activity_main.*
//
//class MainActivity : AppCompatActivity() {
//
//    private val orderManager by lazy {
//        OrdersManager()
//    }
//
//    private var token = ""
//
//    private val disposables: CompositeDisposable = CompositeDisposable()
//
//    private fun handleFetchDestinationSuccess(destinations: List<DestinationsEntity>) {
//        var string = "### API\n"
//        for (item in destinations) {
//            string += "" + item.id_destination + ":  " + item.name + "\n"
//        }
//        text1.text = string
//
//    }
//
//    private fun handleFetchDestinationCacheSuccess(destinations: List<DestinationsEntity>) {
//        var string = "### CACHE\n"
//        for (item in destinations) {
//            string += "" + item.id_destination + ":  " + item.name + "\n"
//
//        }
//        text2.text = string
//    }
//
//    private fun handleFetchDestinationError(throwable: Throwable) {
//        text1.text = throwable.message
//    }
//
//    private fun handleFetchDestinationCacheError(throwable: Throwable) {
//        text1.text = throwable.message
//    }
//
//    private fun handleFetchDataSuccess(data: List<DateEntity>) {
//        var string = "### API\n"
//        for (item in data) {
//            string += "" + item.date + "\n"
//        }
//        text3.text = string
//
//    }
//
//    private fun handleFetchDataCacheSuccess(date: List<DateEntity>) {
//        var string = "### CACHE\n"
//        for (item in date) {
//            string += "" + item.id_destination + ":  " + item.date + "\n"
//
//        }
//        text4.text = string
//    }
//
//    private fun handleFetchDataError(throwable: Throwable) {
//        text3.text = throwable.message
//    }
//
//    private fun handleFetchDataCacheError(throwable: Throwable) {
//        text4.text = throwable.message
//    }
//
//    private fun handleFetchOrderSuccess(orders: List<OrderEntity>) {
//        var string = "### API\n"
//        for (item in orders) {
//            string += "" + item.order_number + ": " + item.status + " " + item.email + "\n"
//        }
//        text5.text = string
//
//    }
//
//    private fun handleFetchOrderCacheSuccess(orders: List<OrderEntity>) {
//        var string = "### CACHE\n"
//        for (item in orders) {
//            string += "" + item.order_number + ": " + item.status + " " + item.email + "\n"
//        }
//        //text6.text = string
//    }
//
//    private fun handleFetchOrderError(throwable: Throwable) {
//        //text5.text = throwable.message
//    }
//
//    private fun handleFetchOrderCacheError(throwable: Throwable) {
//        //text6.text = throwable.message
//    }
//
//        override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        orderManager
//            .getDestination() //w domysle id_destination klienta ktore powinno byc pobierane z api włącznie z tokenem
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(
//                this::handleFetchDestinationCacheSuccess,
//                this::handleFetchDestinationCacheError
//            )
//            .addTo(disposables)
//
//        //From api
//        orderManager
//            .downloadDestination("" + 37)
//            .andThen(orderManager.getDestination())
//            .observeOn(AndroidSchedulers.mainThread())
//            .doOnSubscribe {  } //funkcje np progressbar show
//            .doFinally {  } //funkcje np progressbar show
//            .subscribe(
//                this::handleFetchDestinationSuccess,
//                this::handleFetchDestinationError
//            )
//            .addTo(disposables)
//
//        orderManager
//            .getData(2) //w domysle id_destination klienta ktore powinno byc pobierane z api włącznie z tokenem
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(
//                this::handleFetchDataCacheSuccess,
//                this::handleFetchDataCacheError
//            )
//            .addTo(disposables)
//
//        //From api
//        orderManager
//            .downloadData("" + 37 + "/" + 2, 2)
//            .andThen(orderManager.getData(2))
//            .observeOn(AndroidSchedulers.mainThread())
//            .doOnSubscribe {  } //funkcje np progressbar show
//            .doFinally {  } //funkcje np progressbar show
//            .subscribe(
//                this::handleFetchDataSuccess,
//                this::handleFetchDataError
//            )
//            .addTo(disposables)
//
//        orderManager
//            .getOrders("2019-04-02", 2) //w domysle id_destination klienta ktore powinno byc pobierane z api włącznie z tokenem
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(
//                this::handleFetchOrderCacheSuccess,
//                this::handleFetchOrderCacheError
//            )
//            .addTo(disposables)
//
//        //From api
//        orderManager
//            .downloadOrders("" + 37 + "/" + 2 + "/2019-04-02", "2019-04-02", 2)
//            .andThen(orderManager.getOrders("2019-04-02", 2))
//            .observeOn(AndroidSchedulers.mainThread())
//            .doOnSubscribe {  } //funkcje np progressbar show
//            .doFinally {  } //funkcje np progressbar show
//            .subscribe(
//                this::handleFetchOrderSuccess,
//                this::handleFetchOrderError
//            )
//            .addTo(disposables)
//
//    }
//}
