package com.poc.zoom

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.poc.zoom.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import us.zoom.sdk.ZoomError
import us.zoom.sdk.ZoomSDK
import us.zoom.sdk.ZoomSDKInitParams
import us.zoom.sdk.ZoomSDKInitializeListener

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), ZoomSDKInitializeListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        initSDK()
    }

    private fun initSDK() {
        val params = ZoomSDKInitParams()
        // add sdk keys here
        ZoomSDK.getInstance().initialize(this, this, params)
    }

    override fun onZoomSDKInitializeResult(errorCode: Int, internalErrorCode: Int) {
        if (errorCode == ZoomError.ZOOM_ERROR_SUCCESS) {
            Toast.makeText(this, "SDK Success", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "SDK FAIL", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onZoomAuthIdentityExpired() {

    }
}