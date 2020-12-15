package com.magic.smarttravel.screens.new_group

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.magic.smarttravel.R

/**
 * Created by Marta Turchyniak on 5/22/20.
 */
class CreateGroupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group)
        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.new_group_navigation) as NavHostFragment? ?: return
        val navController = host.navController
    }

    companion object{
        fun startActivity(context: Context){
            val intent = Intent(context, CreateGroupActivity::class.java)
            context.startActivity(intent)
        }
    }

}