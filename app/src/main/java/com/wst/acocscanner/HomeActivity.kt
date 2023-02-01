package com.wst.acocscanner

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationView
import com.wst.acocscanner.databinding.ActivityHomeBinding
import java.util.concurrent.TimeUnit


class HomeActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private var handler: Handler? = null
    private var runnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityHomeBinding>(this, R.layout.activity_home)
        drawerLayout = binding.drawerLayout
        navigationView = binding.navView


        /*handler = Handler(Looper.getMainLooper())
        runnable = Runnable { checkTokenValidity() }
        handler?.postDelayed(runnable!!, TimeUnit.SECONDS.toMillis(10))*/


        val navController = this.findNavController(R.id.nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController,drawerLayout)

        navController.addOnDestinationChangedListener { nc: NavController, nd: NavDestination, args: Bundle? ->
            if (nd.id == nc.graph.getStartDestination()) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            } else {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
        }
        NavigationUI.setupWithNavController(binding.navView,navController)
        navigationView.setNavigationItemSelectedListener { item: MenuItem ->
                // your code
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
                drawerLayout.close()
                return@setNavigationItemSelectedListener true
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.nav_host_fragment)
        return NavigationUI.navigateUp(navController,drawerLayout)
    }
    /*private fun checkTokenValidity() {
        Log.d("Tag", "I was called")
        val sharedPref = getSharedPreferences("com.wst.acocscanner", Context.MODE_PRIVATE)
        val userId = sharedPref.getInt("Id", 0)
        val loginTime = sharedPref.getLong("login_time", 0)
        Log.d("Tag", "I was called ${loginTime.toString()}")
        if (userId.toString().isEmpty() || System.currentTimeMillis() - loginTime > TimeUnit.SECONDS.toMillis(10)) {

            Log.d("Tag", "I was called x2222 ${loginTime.toString()}")
            // User ID is invalid or time-out period has passed, navigate to LoginActivity
            val editor = sharedPref.edit()
            editor.remove("Id")
            editor.remove("login_time")
            editor.apply()

            val intent = Intent(this@HomeActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            // Token is still valid, keep checking
            handler?.postDelayed(runnable!!, TimeUnit.SECONDS.toMillis(10))
        }
    }*/

}




