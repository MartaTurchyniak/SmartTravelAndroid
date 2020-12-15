package com.magic.smarttravel.screens.tracking

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.magic.smarttravel.R
import com.magic.smarttravel.data.SharedPrefs
import com.magic.smarttravel.entity.Group
import kotlinx.android.synthetic.main.activity_tracking.*
import org.koin.android.viewmodel.ext.android.viewModel

class TrackingActivity : AppCompatActivity() {

    private val viewModel: MapViewModel by viewModel()

    var destination: Autocomplete? = null
    lateinit var group: Group

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracking)
        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.my_nav_host_fragment) as NavHostFragment? ?: return
        val navController = host.navController
        val intent = intent
        setUpBottomNav(navController)
        bottom_nav_view.itemIconTintList = null
        iv_add.setOnClickListener { DestinationActivity.startActivity(this) }

        cacheGroupId(intent)
        // force create viewModel
        viewModel
    }

    private fun cacheGroupId(intent: Intent) {
        group = intent.extras?.getSerializable(GROUP) as Group
        SharedPrefs(this).putGroupId(group.id)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            destination = data?.getParcelableExtra("result")
        }
    }

    private fun setUpBottomNav(navController: NavController) {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        bottomNav?.setupWithNavController(navController)
    }

    companion object {

        private const val GROUP = "group"

        fun startActivity(context: Context, group: Group) {
            val intent = Intent(context, TrackingActivity::class.java).putExtra(GROUP, group)
            context.startActivity(intent)
        }
    }

}