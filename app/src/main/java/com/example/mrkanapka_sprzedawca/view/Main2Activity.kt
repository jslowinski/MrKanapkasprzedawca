package com.example.mrkanapka_sprzedawca.view

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.example.mrkanapka_sprzedawca.R
import com.example.mrkanapka_sprzedawca.api.ApiClient
import com.example.mrkanapka_sprzedawca.api.model.PushData
import com.example.mrkanapka_sprzedawca.api.model.PushNotification
import com.example.mrkanapka_sprzedawca.api.model.RequestSinglePush
import com.example.mrkanapka_sprzedawca.api.model.RequestStatus
import com.example.mrkanapka_sprzedawca.database.entity.DateEntity
import com.example.mrkanapka_sprzedawca.database.entity.DestinationsEntity
import com.example.mrkanapka_sprzedawca.database.entity.OrderEntity
import com.example.mrkanapka_sprzedawca.database.entity.TokenEntity
import com.example.mrkanapka_sprzedawca.manager.OrdersManager
import com.example.mrkanapka_sprzedawca.view.list.OrderListItem
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import com.mikepenz.fastadapter.listeners.ClickEventHook
import io.github.kobakei.materialfabspeeddial.FabSpeedDial
import io.github.kobakei.materialfabspeeddial.FabSpeedDialMenu
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.app_bar_main2.*
import kotlinx.android.synthetic.main.content_main2.*
import kotlinx.android.synthetic.main.item_in_order.view.*
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.*


class Main2Activity : AppCompatActivity() {

    private val adapter: FastItemAdapter<OrderListItem> = FastItemAdapter()

    private val orderManager by lazy {
        OrdersManager()
    }

    private val apiService by lazy {
        ApiClient.create()
    }

    private val apiNotification by lazy{
        ApiClient.createPush()
    }

    private var token = ""

    private var idsellera = 0

    private val disposables: CompositeDisposable = CompositeDisposable()

    private lateinit var mHandler: Handler

    private lateinit var mRunnable:Runnable

    private fun handleFetchDestinationError(throwable: Throwable) {
//        text1.text = throwable.message
        println("ERROR DEST API")
    }

    private fun handleFetchDestinationCacheError(throwable: Throwable) {
//        text1.text = throwable.message
        println("ERROR DEST CACHE")
    }

    private var dayS: String = ""
    private var monthS: String = ""
    private var yearS: String = ""
    private fun handleFetchDataSuccess(date: List<DateEntity>, id: Int) {
        var string = "### API _ DATA\n"
        println(string)
        for (item in date) {
            string += "" + item.date + "\n"
        }
//        text3.text = string
        setCalendarDate(id)
    }

    private fun handleFetchDataCacheSuccess(date: List<DateEntity>, id: Int) {
        var string = "### CACHE _ DATA\n"
        println(string)
        for (item in date) {
            string += "" + item.id_destination + ":  " + item.date + "\n"

        }
//        text4.text = string
        setCalendarDate(id)

    }

    val c = Calendar.getInstance()
    var year = c.get(Calendar.YEAR)
    var month = c.get(Calendar.MONTH)
    var day = c.get(Calendar.DAY_OF_MONTH)
    val sdf2 = SimpleDateFormat("dd/MM/yyyy")
    val currentDate = sdf2.format(c.timeInMillis)
    val splitDate = currentDate.split("/")

    @SuppressLint("SimpleDateFormat")
    private fun setCalendarDate(id: Int){
        //region Calendar

        calendarIcon.setOnClickListener{
            val dpd = DatePickerDialog(this,DatePickerDialog.OnDateSetListener { _, mYear, mMonth, mDay ->
                day = mDay
                month = mMonth
                year = mYear
                if (mDay < 10){
                    dayS = "0$mDay"
                    monthS = if (mMonth < 10){
                        "0${mMonth + 1}"
                    } else{
                        "${mMonth + 1}"
                    }
                } else{
                    dayS = "$mDay"
                    monthS = if (mMonth < 10){
                        "0${mMonth + 1}"
                    } else{
                        "${mMonth + 1}"
                    }
                }
                yearS = "$mYear"
                Log.e("...", "$yearS-$monthS-$dayS")
                getOrder("$yearS-$monthS-$dayS", id)
            }, year, month, day)

            dpd.show()
        }
        mHandler = Handler()
        swpipeOrder.setOnRefreshListener {
            mRunnable = Runnable {
                refreshSpinner()
                getOrder("$yearS-$monthS-$dayS", id)
            }
            mHandler.post(mRunnable)
        }

        getOrder("$yearS-$monthS-$dayS", id)
        //endregion
    }

    private fun getOrder(date: String, id: Int){

        orderManager
            .getOrders(date, id) //w domysle id_destination klienta ktore powinno byc pobierane z api włącznie z tokenem
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { showProgress() } //funkcje np progressbar show
            .doFinally { hideProgress() } //funkcje np progressbar show
            .subscribe(
                this::handleFetchOrderCacheSuccess,
                this::handleFetchOrderCacheError
            )
            .addTo(disposables)

        //From api
        orderManager
            .downloadOrders("delivery/$idsellera/$id/$date", date, id)
            .andThen(orderManager.getOrders(date, id))
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { showProgress() } //funkcje np progressbar show
            .doFinally { hideProgress() } //funkcje np progressbar show
            .subscribe(
                this::handleFetchOrderSuccess,
                this::handleFetchOrderError
            )
            .addTo(disposables)


    }

    private fun handleFetchDataError(throwable: Throwable) {
//        text3.text = throwable.message
    }

    private fun handleFetchDataCacheError(throwable: Throwable) {
//        text4.text = throwable.message
    }

    @SuppressLint("SetTextI18n")
    private fun handleFetchOrderSuccess(orders: List<OrderEntity>) {
        var string = "### API - ORDER\n"
        println(string)
        for (item in orders) {
            string += "" + item.order_number + ": " + item.status + " " + item.email + "\n"

        }

//        text5.text = string
        val items = orders.map {
            OrderListItem(it)
        }

        if(orders.isEmpty()){
            ordersList.visibility = View.GONE
            ordersEmpty.visibility = View.VISIBLE
            emptyText1.text = "Brak zamówień do wybranego biurowca/firmy na dzień $dayS-$monthS-$yearS. Datę możesz zmienić klikając ikonkę kalendarza."
        } else
        {
            ordersList.visibility = View.VISIBLE
            ordersEmpty.visibility = View.GONE
            if (orders.size == 1) {
                textView3.text = "${orders.size} zamówienie na dzień: $yearS-$monthS-$dayS"
            } else {
                textView3.text = "${orders.size} zamówień na dzień: $yearS-$monthS-$dayS"
            }
        }
        Log.e("...", "orders z api")
        adapter.setNewList(items)

    }

    @SuppressLint("SetTextI18n")
    private fun handleFetchOrderCacheSuccess(orders: List<OrderEntity>) {
        var string = "### CACHE - ORDER\n"
        println(string)
        for (item in orders) {
            string += "" + item.order_number + ": " + item.status + " " + item.email + "\n"
        }
//        text6.text = string
        val items = orders.map {
            OrderListItem(it)
        }

        if(orders.isEmpty()){
            ordersList.visibility = View.GONE
            ordersEmpty.visibility = View.VISIBLE
            emptyText1.text = "Brak zamówień do wybranego biurowca/firmy na dzień $dayS-$monthS-$yearS. Datę możesz zmienić klikając ikonkę kalendarza."
        } else
        {
            ordersList.visibility = View.VISIBLE
            ordersEmpty.visibility = View.GONE
            if (orders.size == 1) {
                textView3.text = "${orders.size} zamówienie na dzień: $yearS-$monthS-$dayS"
            } else {
                textView3.text = "${orders.size} zamówień na dzień: $yearS-$monthS-$dayS"
            }
        }
        Log.e("...", "orders z cache")
        adapter.setNewList(items)
    }

    private fun handleFetchOrderError(throwable: Throwable) {
//        text5.text = throwable.message
        println("ERROR FETCH API")
        println(throwable.message)
    }

    private fun handleFetchOrderCacheError(throwable: Throwable) {
//        text6.text = throwable.message
        println("ERROR DEST CACHE")
    }

    lateinit var destinations: List<DestinationsEntity>

    private fun handleFetchDestinationSuccess(destinations: List<DestinationsEntity>) {
        this.destinations = destinations
        val myDestination = ArrayList<String>()
        var string = "### API - DEST\n"
        println(string)
        for (item in destinations) {
            string += "" + item.id_destination + ":  " + item.name + "\n"
            myDestination.add(item.name)
        }
//        text1.text = string
        Log.e("...", "destination z api")
        setOfficeSpinner(myDestination,destinations)
        val officeSpinner: Spinner = findViewById(R.id.destinationSpinner)
        val adapterSpinner = ArrayAdapter(this, R.layout.spinner_item, myDestination)
        officeSpinner.adapter = adapterSpinner
        officeSpinner.setSelection(spinnerPos,false)

    }

    private fun handleFetchDestinationCacheSuccess(destinations: List<DestinationsEntity>) {
        val myDestination = ArrayList<String>()
        var string = "### CACHE - DEST\n"
        println(string)
        for (item in destinations) {
            string += "" + item.id_destination + ":  " + item.name + "\n"
            myDestination.add(item.name)

        }
        Log.e("...", "destination z cache")
        setOfficeSpinner(myDestination,destinations)
//        text2.text = string
    }

    private fun refreshSpinner(){
        orderManager
            .downloadDestination("delivery/" + idsellera)
            .andThen(orderManager.getDestination())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {  } //funkcje np progressbar show
            .doFinally {  } //funkcje np progressbar show
            .subscribe(
                {handleFetchDestinationSuccess(it)},
                {handleFetchDestinationError(it)}
            )
            .addTo(disposables)
    }

    var spinnerPos = 0
    private fun setOfficeSpinner(myDestination: ArrayList<String>, destination: List<DestinationsEntity>){
        val officeSpinner: Spinner = findViewById(R.id.destinationSpinner)
        val adapterSpinner = ArrayAdapter(this, R.layout.spinner_item, myDestination)
        officeSpinner.adapter = adapterSpinner
        officeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.e("...", " Nothing")
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                Toast.makeText(applicationContext,destination[position].name, Toast.LENGTH_LONG).show()
                spinnerPos = position
                orderManager
                    .getData(destination[position].id_destination) //w domysle id_destination klienta ktore powinno byc pobierane z api włącznie z tokenem
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {handleFetchDataCacheSuccess(it,destination[position].id_destination)},
                        {handleFetchDataCacheError(it)}
                    )
                    .addTo(disposables)

                //From api

                orderManager
                    .downloadData("delivery/" + idsellera + "/" + destination[position].id_destination, destination[position].id_destination)
                    .andThen(orderManager.getData(destination[position].id_destination))
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe {  } //funkcje np progressbar show
                    .doFinally {  } //funkcje np progressbar show
                    .subscribe(
                        {handleFetchDataSuccess(it,destination[position].id_destination)},
                        {handleFetchDataError(it)}
                    )
                    .addTo(disposables)
            }
        }
    }

    private fun initializeRecyclerView(){
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = adapter

        adapter.withEventHook(object : ClickEventHook<OrderListItem>() {
            override fun onBindMany(viewHolder: RecyclerView.ViewHolder) =
                viewHolder.itemView.run { listOf(button) }

            override fun onClick(v: View?, position: Int, fastAdapter: FastAdapter<OrderListItem>, item: OrderListItem?) {
                if (v != null && item != null){
                    changeOnTransport(item, position)
                }
            }

        })
        adapter.withEventHook(object : ClickEventHook<OrderListItem>() {
            override fun onBindMany(viewHolder: RecyclerView.ViewHolder) =
                viewHolder.itemView.run { listOf(button2) }

            override fun onClick(v: View?, position: Int, fastAdapter: FastAdapter<OrderListItem>, item: OrderListItem?) {
                if (v != null && item != null){
                    changeOnReady(item, position)
                }
            }

        })
    }

    @SuppressLint("CheckResult")
    private fun changeOnReady(item: OrderListItem, position: Int) {
        apiService.changeOnReady(RequestStatus(token, item.model.order_number))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .unsubscribeOn(Schedulers.io())
            .subscribeBy(
                onNext = {
                    item.model.status = it.new_status
                    orderManager.updateStatus(item.model.order_number, it.new_status)
                    val title = "Pan Kanapka przyjechał."
                    val body = "Zamówienie ${item.model.order_number} gotowe do odbioru."
                    apiNotification.sendSinglePush(RequestSinglePush(PushNotification(title,body,"OPEN_ACTIVITY_1"), PushData(item.model.order_number),item.model.registrationid))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .unsubscribeOn(Schedulers.io())
                        .subscribeBy(
                            onNext = { response ->
                                Log.e("...",response.success.toString())
                            },
                            onError = { error ->
                                if (error is HttpException)
                                    Toast.makeText(applicationContext,error.message(), Toast.LENGTH_LONG).show()
                                else
                                    Toast.makeText(applicationContext,"Sprawdź połączenie z internetem", Toast.LENGTH_LONG).show()
                            }
                        )
                },
                onError = {
                    if (it is HttpException)
                        Toast.makeText(applicationContext,it.message(), Toast.LENGTH_LONG).show()
                    else
                        Toast.makeText(applicationContext,"Sprawdź połączenie z internetem", Toast.LENGTH_LONG).show()
                },
                onComplete = {
                    adapter.notifyItemChanged(position)
                }
            )

    }

    @SuppressLint("CheckResult")
    private fun changeOnTransport(item: OrderListItem, position: Int) {
        apiService.changeOnTransport(RequestStatus(token, item.model.order_number))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .unsubscribeOn(Schedulers.io())
            .subscribeBy(
                onNext = {
                    item.model.status = it.new_status
                    orderManager.updateStatus(item.model.order_number, it.new_status)
                    val title = "Pan Kanapka już do Ciebie jedzie."
                    val body = "Zamówienie ${item.model.order_number} jest w drodze."
                    apiNotification.sendSinglePush(RequestSinglePush(PushNotification(title,body,"OPEN_ACTIVITY_1"),PushData(item.model.order_number),item.model.registrationid))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .unsubscribeOn(Schedulers.io())
                        .subscribeBy(
                            onNext = {
                                Log.e("...",it.success.toString())
                            },
                            onError = {
                                if (it is HttpException)
                                    Toast.makeText(applicationContext,it.message(), Toast.LENGTH_LONG).show()
                                else
                                    Toast.makeText(applicationContext,"Sprawdź połączenie z internetem", Toast.LENGTH_LONG).show()
                            }
                        )
                },
                onError = {
                    if (it is HttpException)
                        Toast.makeText(applicationContext,it.message(), Toast.LENGTH_LONG).show()
                    else
                        Toast.makeText(applicationContext,it.message, Toast.LENGTH_LONG).show()
                },
                onComplete = {
                    adapter.notifyItemChanged(position)
                }
            )

    }

    private fun handleTokenCacheSuccess(token: TokenEntity) {
        this.token = token.token
        this.idsellera = token.id_seller
        Log.e("...", token.id_seller.toString())
        orderManager
            .getDestination() //w domysle id_destination klienta ktore powinno byc pobierane z api włącznie z tokenem
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                this::handleFetchDestinationCacheSuccess,
                this::handleFetchDestinationCacheError
            )
            .addTo(disposables)

        //From api
        orderManager
            .downloadDestination("delivery/" + token.id_seller)
            .andThen(orderManager.getDestination())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {  } //funkcje np progressbar show
            .doFinally {  } //funkcje np progressbar show
            .subscribe(
                {handleFetchDestinationSuccess(it)},
                {handleFetchDestinationError(it)}
            )
            .addTo(disposables)
    }

    private fun handleTokenCacheError(throwable: Throwable) {
        Log.e("...","brak tokenu")
    }

    private fun showProgress() {
        swpipeOrder.isRefreshing = true
    }

    private fun hideProgress() {
        swpipeOrder.isRefreshing = false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        setSupportActionBar(toolbar)

        val fab = findViewById<FabSpeedDial>(R.id.fab)

        val menu = FabSpeedDialMenu(this)
        menu.add("Gotowe do odbioru").setIcon(R.drawable.ic_notifications)
        menu.add("W drodze").setIcon(R.drawable.ic_notifications)
        fab.setMenu(menu)


        fab.addOnMenuItemClickListener { _, _, itemId ->
            if (itemId == 1) {
                Log.e("...", " Gotowe do odbioru")

                for (item in adapter.adapterItems)
                {
                    if(item.model.status == "W transporcie")
                        changeOnReady(item, adapter.getAdapterPosition(item))
                }
            }

            if (itemId == 2) {
                Log.e("...", " W drodze")

                for (item in adapter.adapterItems)
                {
                    if(item.model.status == "Do realizacji")
                        changeOnTransport(item, adapter.getAdapterPosition(item))
                }
            }
        }

        dayS = splitDate[0]
        monthS = splitDate[1]
        yearS = splitDate[2]

        orderManager
            .getToken()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                this::handleTokenCacheSuccess,
                this::handleTokenCacheError
            )
            .addTo(disposables)

        initializeRecyclerView()

        productIcon.setOnClickListener {
            val intent = Intent(this, ProductActivity::class.java)
            intent.putExtra("token", token)
            intent.putExtra("date","$yearS-$monthS-$dayS")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }

    //region Kropki góry róg

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main2, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                orderManager.removeToken()
                val intent = Intent(this, Login::class.java)
                startActivity(intent)
                finish()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
    //endregion
}
