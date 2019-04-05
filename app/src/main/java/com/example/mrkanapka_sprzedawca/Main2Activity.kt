package com.example.mrkanapka_sprzedawca

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.example.mrkanapka_sprzedawca.database.entity.DateEntity
import com.example.mrkanapka_sprzedawca.database.entity.DestinationsEntity
import com.example.mrkanapka_sprzedawca.database.entity.OrderEntity
import com.example.mrkanapka_sprzedawca.manager.OrdersManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.app_bar_main2.*
import kotlinx.android.synthetic.main.content_main2.*

class Main2Activity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val orderManager by lazy {
        OrdersManager()
    }

    private var token = ""

    private val disposables: CompositeDisposable = CompositeDisposable()




    private fun handleFetchDestinationError(throwable: Throwable) {
        text1.text = throwable.message
    }

    private fun handleFetchDestinationCacheError(throwable: Throwable) {
        text1.text = throwable.message
    }

    private fun handleFetchDataSuccess(data: List<DateEntity>) {
        var string = "### API\n"
        for (item in data) {
            string += "" + item.date + "\n"
        }
        text3.text = string

    }

    private fun handleFetchDataCacheSuccess(date: List<DateEntity>) {
        var string = "### CACHE\n"
        for (item in date) {
            string += "" + item.id_destination + ":  " + item.date + "\n"

        }
        text4.text = string
    }

    private fun handleFetchDataError(throwable: Throwable) {
        text3.text = throwable.message
    }

    private fun handleFetchDataCacheError(throwable: Throwable) {
        text4.text = throwable.message
    }

    private fun handleFetchOrderSuccess(orders: List<OrderEntity>) {
        var string = "### API\n"
        for (item in orders) {
            string += "" + item.order_number + ": " + item.status + " " + item.email + "\n"
        }
        text5.text = string

    }

    private fun handleFetchOrderCacheSuccess(orders: List<OrderEntity>) {
        var string = "### CACHE\n"
        for (item in orders) {
            string += "" + item.order_number + ": " + item.status + " " + item.email + "\n"
        }
        text6.text = string
    }

    private fun handleFetchOrderError(throwable: Throwable) {
        text5.text = throwable.message
    }

    private fun handleFetchOrderCacheError(throwable: Throwable) {
        text6.text = throwable.message
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
        text1.text = string
        setOfficeSpinner(myDestination,destinations)

    }

    private fun handleFetchDestinationCacheSuccess(destinations: List<DestinationsEntity>) {
        val myDestination = ArrayList<String>()
        var string = "### CACHE\n"
        for (item in destinations) {
            string += "" + item.id_destination + ":  " + item.name + "\n"
            myDestination.add(item.name)

        }
        setOfficeSpinner(myDestination,destinations)
        text2.text = string
    }

    private fun setOfficeSpinner(myDestination: ArrayList<String>, destination: List<DestinationsEntity>){
        val officeSpinner: Spinner = findViewById(R.id.destinationSpinner)
        val adapter = ArrayAdapter(this, R.layout.spinner_item, myDestination)
        officeSpinner.adapter = adapter
        officeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Toast.makeText(applicationContext,destination[position].name, Toast.LENGTH_LONG).show()
                orderManager
                    .getData(destination[position].id_destination) //w domysle id_destination klienta ktore powinno byc pobierane z api włącznie z tokenem
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {handleFetchDataCacheSuccess(it)},
                        {handleFetchDataCacheError(it)}
                    )
                    .addTo(disposables)

                //From api
                orderManager
                    .downloadData("" + 37 + "/" + destination[position].id_destination, destination[position].id_destination)
                    .andThen(orderManager.getData(destination[position].id_destination))
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe {  } //funkcje np progressbar show
                    .doFinally {  } //funkcje np progressbar show
                    .subscribe(
                        {handleFetchDataSuccess(it)},
                        {handleFetchDataCacheError(it)}
                    )
                    .addTo(disposables)
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        setSupportActionBar(toolbar)

        fab.setOnClickListener {
            Toast.makeText(applicationContext,"Przycisk", Toast.LENGTH_LONG).show()
        }

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
            .downloadDestination("" + 37)
            .andThen(orderManager.getDestination())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {  } //funkcje np progressbar show
            .doFinally {  } //funkcje np progressbar show
            .subscribe(
                this::handleFetchDestinationSuccess,
                this::handleFetchDestinationError
            )
            .addTo(disposables)

        orderManager
            .getOrders("2019-04-02", 2) //w domysle id_destination klienta ktore powinno byc pobierane z api włącznie z tokenem
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                this::handleFetchOrderCacheSuccess,
                this::handleFetchOrderCacheError
            )
            .addTo(disposables)

        //From api
        orderManager
            .downloadOrders("" + 37 + "/" + 2 + "/2019-04-02", "2019-04-02", 2)
            .andThen(orderManager.getOrders("2019-04-02", 2))
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {  } //funkcje np progressbar show
            .doFinally {  } //funkcje np progressbar show
            .subscribe(
                this::handleFetchOrderSuccess,
                this::handleFetchOrderError
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
