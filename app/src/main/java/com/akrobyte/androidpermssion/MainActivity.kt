package com.akrobyte.androidpermssion

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.telephony.SubscriptionManager
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import com.akrobyte.androidpermission.PermissionHandler
import com.akrobyte.androidpermission.Permissions
import com.akrobyte.androidpermssion.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAnchorView(R.id.fab)
                .setAction("Action", null).show()
        }

        val permissions = arrayOf(Manifest.permission.READ_PHONE_STATE)
        val rationale = "Please provide SMS Detect permission so that you can ..."
        val options = Permissions.Options()
            .setRationaleDialogTitle("SMS Detect permission")
            .setSettingsDialogTitle("SMS Detect permission")

        Permissions.check(
            this,
            permissions,
            rationale,
            options,
            object : PermissionHandler() {
                override fun onGranted() {
                    // do your task.
                    val sManager = getSystemService(TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
                    if (ActivityCompat.checkSelfPermission(this@MainActivity,
                            Manifest.permission.READ_PHONE_STATE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return
                    }
                    val infoSim1 = sManager.getActiveSubscriptionInfoForSimSlotIndex(0)
                    val infoSim2 = sManager.getActiveSubscriptionInfoForSimSlotIndex(1)
                    Log.d("Direct SIM Permission", "InfoSIM1:$infoSim1,InfoSIM2:$infoSim2")

                }

                override fun onDenied(context: Context, deniedPermissions: ArrayList<String>) {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }
            })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}