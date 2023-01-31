package com.wst.acocscanner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.airbnb.lottie.LottieAnimationView
import com.wst.acocscanner.api.IMyApi
import com.wst.acocscanner.commonURL.Common
import com.wst.acocscanner.databinding.ActivityMainBinding
import com.wst.acocscanner.retrofitClient.APIResponse
import retrofit2.Call
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var loadingAnimation: LottieAnimationView
    private lateinit var mService : IMyApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val actionBar = supportActionBar
        actionBar?.hide()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        mService = Common.api

        loadingAnimation = binding.loadingAnimation
        binding.loginBtn.setOnClickListener {
            //play loading animation
            binding.contentLayout.visibility = View.GONE
            loadingAnimation.visibility = View.VISIBLE
            loadingAnimation.playAnimation()

            //login User
            val userName = binding.userNameEt.text.toString()
            Log.d("TAG", "$userName")
            val password = binding.passwordInputEt.text.toString()
            loginUser(userName, password)


                    /*val intent = Intent(this, HomeActivity::class.java)
                    intent.putExtra("userId", it)
                    startActivity(intent)
                    finish()*/
        }
    }

    private fun loginUser(userName: String, password: String) {

        mService.loginUser(userName, password)
            .enqueue(object : retrofit2.Callback<APIResponse>{
                override fun onResponse(call: Call<APIResponse>, response: Response<APIResponse>) {
                    loadingAnimation.visibility = View.GONE
                    loadingAnimation.cancelAnimation()

                    if (response.body()!!.error){
                        Toast.makeText(this@MainActivity, response.body()!!.error_msg,
                            Toast.LENGTH_LONG).show()
                    } else{
                        var userId = response.body()!!.id
                        val intent = Intent(this@MainActivity, HomeActivity::class.java)
                        intent.putExtra("userId", userId)
                        startActivity(intent)
                        finish()
//                        Toast.makeText(this@MainActivity, "Register Successful"
//                                + response.body()!!.uid, Toast.LENGTH_LONG).show()
                    }
                }
                override fun onFailure(call: Call<APIResponse>, t: Throwable) {
                    loadingAnimation.visibility = View.GONE
                    loadingAnimation.cancelAnimation()
                    Toast.makeText(this@MainActivity, t.message, Toast.LENGTH_LONG).show()
                }
            })

    }

}