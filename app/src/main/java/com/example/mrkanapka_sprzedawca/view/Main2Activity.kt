package com.example.mrkanapka_sprzedawca.view

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.example.mrkanapka_sprzedawca.R
import com.example.mrkanapka_sprzedawca.api.ApiClient
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
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.app_bar_main2.*
import kotlinx.android.synthetic.main.content_main2.*
import kotlinx.android.synthetic.main.item_in_order.view.*
import retrofit2.HttpException
import java.security.SecureRandom
import java.security.spec.KeySpec
import java.text.FieldPosition
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec


class Main2Activity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

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

    private val disposables: CompositeDisposable = CompositeDisposable()

    private lateinit var mHandler: Handler

    private lateinit var mRunnable:Runnable

    private fun handleFetchDestinationError(throwable: Throwable) {
//        text1.text = throwable.message
    }

    private fun handleFetchDestinationCacheError(throwable: Throwable) {
//        text1.text = throwable.message
    }

    private var dayS: String = ""
    private var monthS: String = ""
    private var yearS: String = ""
    private fun handleFetchDataSuccess(date: List<DateEntity>, id: Int) {
        var string = "### API\n"
        for (item in date) {
            string += "" + item.date + "\n"
        }
//        text3.text = string
        Log.e("...", "date z api")
        if (date.isEmpty()) {
            Log.e("...","pusto")
        }
        else {
            Log.e("...",date[0].date)
            setCalendarDate(id, date[0].date, date[date.size-1].date)
        }
    }

    private fun handleFetchDataCacheSuccess(date: List<DateEntity>, id: Int) {
        var string = "### CACHE\n"
        for (item in date) {
            string += "" + item.id_destination + ":  " + item.date + "\n"

        }
//        text4.text = string
        Log.e("...", "date z cache")
        if (date.isEmpty()) {
            Log.e("...","pusto")
        }
        else {
            Log.e("...",date[0].date)
            setCalendarDate(id, date[0].date, date[date.size-1].date)
        }

    }

    private fun setCalendarDate(id: Int, maxdate: String, mindate: String){
        //region Calendar
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val sdf2 = SimpleDateFormat("dd/MM/yyyy")
        val currentDate = sdf2.format(c.timeInMillis)
        val splitDate = currentDate.split("/")
        dayS = splitDate[0]
        monthS = splitDate[1]
        yearS = splitDate[2]
        calendarIcon.setOnClickListener{
            val dpd = DatePickerDialog(this,DatePickerDialog.OnDateSetListener { _, mYear, mMonth, mDay ->
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
                getOrder("/$yearS-$monthS-$dayS", id)
            }, year, month, day)

            var date = sdf.parse(mindate)

            var millis = date.time
            dpd.datePicker.minDate = millis
            dpd.datePicker

            date = sdf.parse(maxdate)
            millis = date.time
            dpd.datePicker.maxDate = millis


            dpd.show()
        }
        mHandler = Handler()
        swpipeOrder.setOnRefreshListener {
            mRunnable = Runnable {
                getOrder("/$yearS-$monthS-$dayS", id)
            }
            mHandler.post(mRunnable)
        }

        getOrder("/$yearS-$monthS-$dayS", id)
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
            .downloadOrders("delivery/" + 37 + "/" + id + date, date, id)
            .andThen(orderManager.getOrders(date, id))
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { showProgress() } //funkcje np progressbar show
            .doFinally { hideProgress() } //funkcje np progressbar show
            .subscribe(
                this::handleFetchOrderSuccess,
                this::handleFetchOrderError
            )
            .addTo(disposables)
        initializeRecyclerView()

    }

    private fun handleFetchDataError(throwable: Throwable) {
//        text3.text = throwable.message
    }

    private fun handleFetchDataCacheError(throwable: Throwable) {
//        text4.text = throwable.message
    }

    private fun handleFetchOrderSuccess(orders: List<OrderEntity>) {
        var string = "### API\n"
        for (item in orders) {
            string += "" + item.order_number + ": " + item.status + " " + item.email + "\n"
        }
//        text5.text = string
        val items = orders.map {
            OrderListItem(it)
        }

        Log.e("...", "orders z api")
        adapter.setNewList(items)

    }

    private fun handleFetchOrderCacheSuccess(orders: List<OrderEntity>) {
        var string = "### CACHE\n"
        for (item in orders) {
            string += "" + item.order_number + ": " + item.status + " " + item.email + "\n"
        }
//        text6.text = string
        val items = orders.map {
            OrderListItem(it)
        }
        Log.e("...", "orders z cache")
        adapter.setNewList(items)
    }

    private fun handleFetchOrderError(throwable: Throwable) {
//        text5.text = throwable.message
    }

    private fun handleFetchOrderCacheError(throwable: Throwable) {
//        text6.text = throwable.message
    }

    lateinit var destinations: List<DestinationsEntity>

    private fun handleFetchDestinationSuccess(destinations: List<DestinationsEntity>) {
        this.destinations = destinations
        val myDestination = ArrayList<String>()
        var string = "### API\n"
        for (item in destinations) {
            string += "" + item.id_destination + ":  " + item.name + "\n"
            myDestination.add(item.name)
        }
//        text1.text = string
        Log.e("...", "destination z api")
        setOfficeSpinner(myDestination,destinations)

    }

    private fun handleFetchDestinationCacheSuccess(destinations: List<DestinationsEntity>) {
        val myDestination = ArrayList<String>()
        var string = "### CACHE\n"
        for (item in destinations) {
            string += "" + item.id_destination + ":  " + item.name + "\n"
            myDestination.add(item.name)

        }
        Log.e("...", "destination z cache")
        setOfficeSpinner(myDestination,destinations)
//        text2.text = string
    }

    private fun setOfficeSpinner(myDestination: ArrayList<String>, destination: List<DestinationsEntity>){
        val officeSpinner: Spinner = findViewById(R.id.destinationSpinner)
        val adapterSpinner = ArrayAdapter(this, R.layout.spinner_item, myDestination)
        officeSpinner.adapter = adapterSpinner

        officeSpinner.setSelection(0,false) //nie wywołuje z cache automatycznie | 2x zamiast 4x normalnie

        officeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.e("...", " Nothing")
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                Toast.makeText(applicationContext,destination[position].name, Toast.LENGTH_LONG).show()
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
                    .downloadData("delivery/" + 37 + "/" + destination[position].id_destination, destination[position].id_destination)
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

    private fun setOfficeSpinnerCache(myDestination: ArrayList<String>, destination: List<DestinationsEntity>){
        val officeSpinner: Spinner = findViewById(R.id.destinationSpinner)
        val adapterSpinner = ArrayAdapter(this, R.layout.spinner_item, myDestination)
        officeSpinner.adapter = adapterSpinner
        officeSpinner.setSelection(0,false)
        officeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.e("...", " Nothing")
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Toast.makeText(applicationContext,destination[position].name, Toast.LENGTH_LONG).show()
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
                    .downloadData("delivery/" + 37 + "/" + destination[position].id_destination, destination[position].id_destination)
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
                    changeOnTransport(v, item, position)
                }
            }

        })
        adapter.withEventHook(object : ClickEventHook<OrderListItem>() {
            override fun onBindMany(viewHolder: RecyclerView.ViewHolder) =
                viewHolder.itemView.run { listOf(button2) }

            override fun onClick(v: View?, position: Int, fastAdapter: FastAdapter<OrderListItem>, item: OrderListItem?) {
                if (v != null && item != null){
                    changeOnReady(v, item, position)
                }
            }

        })
    }

    @SuppressLint("CheckResult")
    private fun changeOnReady(v: View, item: OrderListItem, position: Int) {
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
                    apiNotification.sendSinglePush(RequestSinglePush(PushNotification(title,body),item.model.registrationid))
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
                        Toast.makeText(applicationContext,"Sprawdź połączenie z internetem", Toast.LENGTH_LONG).show()
                },
                onComplete = {
                    adapter.notifyItemChanged(position)
                }
            )

    }

    @SuppressLint("CheckResult")
    private fun changeOnTransport(v: View, item: OrderListItem, position: Int) {
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
                    apiNotification.sendSinglePush(RequestSinglePush(PushNotification(title,body),item.model.registrationid))
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

        fab.setOnClickListener {
            orderManager.removeToken()
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }

        orderManager
            .getToken()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                this::handleTokenCacheSuccess,
                this::handleTokenCacheError
            )
            .addTo(disposables)



        //region Hamburger menu

//        val toggle = ActionBarDrawerToggle(
//            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
//        )
//        drawer_layout.addDrawerListener(toggle)
//        toggle.syncState()
//
//        nav_view.setNavigationItemSelectedListener(this)

        //endregion
    }

    override fun onBackPressed() {
        //region Hamburger menu
//        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
//            drawer_layout.closeDrawer(GravityCompat.START)
//        } else {
//            super.onBackPressed()
//        }
        //endregion
        super.onBackPressed()
    }

    //region Kropki góry róg

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.main2, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        when (item.itemId) {
//            R.id.action_settings -> return true
//            else -> return super.onOptionsItemSelected(item)
//        }
//    }

    //endregion

    //region Hamburger menu
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    //endregion
}
