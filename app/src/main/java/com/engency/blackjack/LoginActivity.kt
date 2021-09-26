package com.engency.blackjack

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.engency.blackjack.network.*

class LoginActivity : AppCompatActivity(), OnNetworkResponseInterface<LoginResponse> {

    private lateinit var etGroup: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        etGroup = findViewById(R.id.input_name)
        etPassword = findViewById(R.id.input_password)
        btnLogin = findViewById(R.id.btn_login)

        btnLogin.setOnClickListener { performLogin() }
    }

    private fun performLogin() {
        NetworkHelper.login(etGroup.text.toString(), etPassword.text.toString(), this)
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    }

    override fun success(data: LoginResponse) {
        NetworkHelper.getGroupInfo(
                token = data.token,
                success = { groupData: GroupInfo ->
                    val properties = GroupPropertyManager(applicationContext)
                    properties.put("token", data.token)
                    properties.updateWithGroupInstance(groupData)
                    FCMRegistrationManager().storeFirebaseId(properties)
                    startActivity(MainActivity.newIntent(this).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
                },
                failure = { }
        )
    }

    override fun failure(message: String) {
        Snackbar.make(btnLogin, message, Snackbar.LENGTH_LONG).show()
        Log.e("FAILURE", message)
    }

}