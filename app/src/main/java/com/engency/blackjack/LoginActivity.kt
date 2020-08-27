package com.engency.blackjack

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.engency.blackjack.network.GroupInfo
import com.engency.blackjack.network.LoginResponse
import com.engency.blackjack.network.NetworkHelper
import com.engency.blackjack.network.OnNetworkResponseInterface

class LoginActivity : AppCompatActivity(), OnNetworkResponseInterface<LoginResponse> {

    private lateinit var etGroup: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var properties: GroupPropertyManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        etGroup = findViewById(R.id.input_name)
        etPassword = findViewById(R.id.input_password)
        btnLogin = findViewById(R.id.btn_login)

        properties = GroupPropertyManager(applicationContext)

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
        properties.put("token", data.token)
        properties.commit()
        NetworkHelper.getGroupInfo(
                token = data.token,
                success = { groupData: GroupInfo ->
                    properties.updateWithGroupInstance(groupData)
                    startActivity(MainActivity.newIntent(this).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
                },
                failure = { }
        )
    }

    override fun failure(message: String) {
        Snackbar.make(this.btnLogin, message, Snackbar.LENGTH_LONG)
        Log.e("FAILURE", message)
    }

}