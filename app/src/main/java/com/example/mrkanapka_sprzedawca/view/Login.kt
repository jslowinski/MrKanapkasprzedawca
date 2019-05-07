package com.example.mrkanapka_sprzedawca.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.example.mrkanapka_sprzedawca.R
import com.example.mrkanapka_sprzedawca.api.ApiClient
import com.example.mrkanapka_sprzedawca.api.model.RequestLogin
import com.example.mrkanapka_sprzedawca.database.entity.TokenEntity
import com.example.mrkanapka_sprzedawca.manager.OrdersManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.HttpException

class Login : Activity() {

    private val apiService by lazy {
        ApiClient.create()
    }

    private val orderManager by lazy {
        OrdersManager()
    }

    private val disposables: CompositeDisposable = CompositeDisposable()

    private var loginInput: String = ""
    private var passwordInput: String = ""

    private fun handleTokenCacheSuccess(token: TokenEntity) {
        dialog.show()
        val intent = Intent(this, Main2Activity::class.java)
        startActivity(intent)
        finish()
    }

    private fun handleTokenCacheError(throwable: Throwable) {
        Log.e("...","brak tokenu")
    }

    private lateinit var dialog: AlertDialog

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.progress_dialog, null)
        val message = dialogView.findViewById<TextView>(R.id.textDialog)
        message.text = "Sprawdzanie danych logowania..."
        builder.setView(dialogView)
        builder.setCancelable(false)
        dialog = builder.create()

        orderManager
            .getToken()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                this::handleTokenCacheSuccess,
                this::handleTokenCacheError
            )
            .addTo(disposables)

        login_button.setOnClickListener {
            dialog.show()
            loginInput = login_text.text.toString().trim()
            passwordInput = password_text.text.toString().trim()
            apiService.login(RequestLogin(loginInput, passwordInput))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribeBy(
                    onNext = {
                        if (it.message == "Niepoprawne dane logowania")
                        {
                            dialog.cancel()
                            Toast.makeText(applicationContext,it.message, Toast.LENGTH_LONG).show()
                        } else{
                            orderManager.saveToken(it.message, it.id_seller)
                            Log.e("...",it.id_seller.toString())
                            dialog.cancel()
                            val intent = Intent(this, Main2Activity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    },
                    onError = {
                        if (it is HttpException){
                            Toast.makeText(applicationContext,it.message(), Toast.LENGTH_LONG).show()
                            dialog.cancel()
                        }

                        else
                        {
                            Toast.makeText(applicationContext,"Sprawdź połączenie z internetem", Toast.LENGTH_LONG).show()
                            dialog.cancel()
                        }

                    }
                )
        }
    }
}
