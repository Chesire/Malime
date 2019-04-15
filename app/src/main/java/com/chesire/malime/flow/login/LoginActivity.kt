package com.chesire.malime.flow.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.chesire.lifecyklelog.LogLifecykle
import com.chesire.malime.R
import com.chesire.malime.flow.OverviewActivity
import com.chesire.malime.flow.login.details.DetailsFragment

@LogLifecykle
class LoginActivity : AppCompatActivity(), DetailsFragment.LoginListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    override fun onLoginSuccess() {
        startActivity(Intent(this, OverviewActivity::class.java))
        finish()
    }
}
