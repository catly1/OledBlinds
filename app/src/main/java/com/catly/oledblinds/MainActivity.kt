package com.catly.oledblinds


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast


class MainActivity : AppCompatActivity() {
    private val drawOtherAppPermissionCode = 2084

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == drawOtherAppPermissionCode){
            if (resultCode == RESULT_OK){

            } else {
                Toast.makeText(this, "Draw over other app permission not available. Closing the application", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

}
