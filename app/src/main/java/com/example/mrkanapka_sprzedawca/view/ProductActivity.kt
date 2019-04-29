package com.example.mrkanapka_sprzedawca.view

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import com.example.mrkanapka_sprzedawca.R
import com.example.mrkanapka_sprzedawca.api.ApiClient
import com.example.mrkanapka_sprzedawca.api.model.RequestShopping
import com.example.mrkanapka_sprzedawca.api.model.ResponseShopping
import com.example.mrkanapka_sprzedawca.view.list.ShoppingListItem
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_product.*

class ProductActivity : AppCompatActivity() {

    var token = ""
    var date = ""

    private val apiService by lazy {
        ApiClient.create()
    }

    private val adapter: FastItemAdapter<ShoppingListItem> = FastItemAdapter()

    @SuppressLint("CheckResult", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        token = intent.getStringExtra("token")
        date = intent.getStringExtra("date")


        textView2.text = "Lista zakupów na dzień $date"
        var shoppingList = ""
        apiService.fetchProducts(RequestShopping(token,date))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .unsubscribeOn(Schedulers.io())
            .subscribeBy(
                onNext = {
                    mapShoppingList(it.components)
                    for (i in 0 until  it.components.size){
                        shoppingList+= "- ${it.components[i].name} ${it.components[i].sum} ${it.components[i].unit}\n"
                    }
                },
                onError = {

                },
                onComplete = {
//                    productList.text = shoppingList

                }
            )

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.title = "Lista zakupów"
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        initializeRecyclerView()
    }
    private fun mapShoppingList(list: List<ResponseShopping>) {
        // Log the fact.
        // Convert to list items.
        val items = list.map {
            ShoppingListItem(it)
        }
        // Display result.
        adapter.setNewList(items)
    }

    private fun initializeRecyclerView() {
        shoppingListRecyclerView.layoutManager = LinearLayoutManager(this)
        shoppingListRecyclerView.itemAnimator = DefaultItemAnimator()
        shoppingListRecyclerView.adapter = adapter
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
