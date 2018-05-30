package com.chesire.malime.view.login.kitsu

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableBoolean
import android.net.Uri
import android.support.customtabs.CustomTabsIntent
import com.chesire.malime.R
import com.chesire.malime.kitsu.KitsuManagerFactory
import com.chesire.malime.util.SharedPref
import com.chesire.malime.view.login.LoginModel
import com.chesire.malime.view.login.LoginStatus
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable

private const val kitsuSignupUrl = "https://kitsu.io/explore/anime"

class KitsuLoginViewModel(
    private val context: Application,
    private val sharedPref: SharedPref,
    private val kitsuManagerFactory: KitsuManagerFactory,
    private val subscribeScheduler: Scheduler,
    private val observeScheduler: Scheduler
) : AndroidViewModel(context) {
    private val disposables = CompositeDisposable()
    val loginResponse = MutableLiveData<LoginStatus>()
    val errorResponse = MutableLiveData<Int>()
    val attemptingLogin = ObservableBoolean()
    val loginModel = LoginModel()

    fun createAccount() {
        CustomTabsIntent.Builder()
            .build()
            .launchUrl(context, Uri.parse(kitsuSignupUrl))
    }

    fun executeLogin() {
        if (!isValid(loginModel.email, loginModel.userName, loginModel.password)) {
            return
        }

        val kitsuManager = kitsuManagerFactory.get()
        disposables.add(kitsuManager.login(loginModel.email, loginModel.password)
            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)
            .doOnSubscribe { _ ->
                loginResponse.value = LoginStatus.PROCESSING
            }
            .doFinally {
                loginResponse.value = LoginStatus.FINISHED
            }
            .subscribe(
                { _ ->
                    sharedPref
                        .putUsername(loginModel.userName)
                    loginResponse.value = LoginStatus.SUCCESS
                },
                { _ ->
                    errorResponse.value = R.string.login_failure
                    loginResponse.value = LoginStatus.ERROR
                }
            )
        )
    }

    private fun isValid(email: String, username: String, password: String): Boolean {
        return when {
            email.isBlank() -> {
                errorResponse.value = R.string.login_failure_email
                false
            }
            username.isBlank() -> {
                errorResponse.value = R.string.login_failure_username
                false
            }
            password.isBlank() -> {
                errorResponse.value = R.string.login_failure_password
                false
            }
            else -> true
        }
    }

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }
}