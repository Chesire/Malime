package com.chesire.malime

import android.content.Intent
import android.os.Bundle
import com.chesire.malime.core.repositories.Authorization
import com.chesire.malime.view.MainActivity
import com.chesire.malime.view.login.LoginActivity
import dagger.android.DaggerActivity
import javax.inject.Inject

class LaunchActivity : DaggerActivity() {
    @Inject
    lateinit var authorization: Authorization

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val loadIntent = if (authorization.hasLoggedIn()) {
            Intent(this, MainActivity::class.java)
        } else {
            Intent(this, LoginActivity::class.java)
        }

        startActivity(loadIntent)
        finish()
    }
}
