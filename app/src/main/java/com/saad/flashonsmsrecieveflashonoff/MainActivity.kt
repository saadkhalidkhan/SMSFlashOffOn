package com.saad.flashonsmsrecieveflashonoff

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val CAMERA_REQUEST = 123
    internal var hasCameraFlash = false

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if ((ContextCompat.checkSelfPermission(
                applicationContext,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
                    ) && (ContextCompat.checkSelfPermission(
                applicationContext,
                android.Manifest.permission.RECEIVE_SMS
            ) == PackageManager.PERMISSION_GRANTED
                    ) && (ContextCompat.checkSelfPermission(
                applicationContext,
                android.Manifest.permission.BROADCAST_SMS
            ) == PackageManager.PERMISSION_GRANTED
                    ) && (ContextCompat.checkSelfPermission(
                applicationContext,
                android.Manifest.permission.READ_SMS
            ) == PackageManager.PERMISSION_GRANTED
                    )
        ) {
            hasCameraFlash = packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
        } else {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.BROADCAST_SMS,
                    Manifest.permission.READ_SMS
                ), CAMERA_REQUEST
            )
        }
        btnFlashLightToggle.setOnClickListener {
            if (hasCameraFlash) {
                if (btnFlashLightToggle.text.toString().contains("ON")) {
                    btnFlashLightToggle.text = "FLASHLIGHT OFF"
                    btnBlinkFlashLight.text = "BLINK FLASHLIGHT OFF"
                    flashLightOff()
                } else {
                    btnBlinkFlashLight.text = "BLINK FLASHLIGHT ON"
                    btnFlashLightToggle.text = "FLASHLIGHT ON"
                    flashLightOn()
                }
            } else {
                Toast.makeText(
                    this@MainActivity, "No flash available on your device",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        btnBlinkFlashLight.setOnClickListener {
            if (btnFlashLightToggle.text.toString().contains("ON")) {
                blinkFlash()
            } else {
                Toast.makeText(
                    this@MainActivity, "Press the above button first.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun flashLightOn() {
        val cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager

        try {
            val cameraId = cameraManager.cameraIdList[0]
            cameraManager.setTorchMode(cameraId, true)
        } catch (e: CameraAccessException) {
        }

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun flashLightOff() {
        val cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            val cameraId = cameraManager.cameraIdList[0]
            cameraManager.setTorchMode(cameraId, false)
        } catch (e: CameraAccessException) {
        }

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun blinkFlash() {
        val cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val myString = "01010101010101"
        val blinkDelay: Long = 50 //Delay in ms
        for (i in 0 until myString.length) {
            if (myString[i] == '0') {
                try {
                    val cameraId = cameraManager.cameraIdList[0]
                    cameraManager.setTorchMode(cameraId, true)
                } catch (e: CameraAccessException) {
                }

            } else {
                try {
                    val cameraId = cameraManager.cameraIdList[0]
                    cameraManager.setTorchMode(cameraId, false)
                } catch (e: CameraAccessException) {
                }

            }
            try {
                Thread.sleep(blinkDelay)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            CAMERA_REQUEST -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                hasCameraFlash = packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
            } else {
                btnFlashLightToggle.isEnabled = false
                btnBlinkFlashLight.isEnabled = false
                Toast.makeText(this@MainActivity, "Permission Denied for the Camera", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
