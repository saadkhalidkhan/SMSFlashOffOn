package com.saad.flashonsmsrecieveflashonoff

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.telephony.SmsMessage
import android.util.Log

class RecieveSmsBroadCast: BroadcastReceiver() {
    private val SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED"
    private val TAG = "SMSBroadcastReceiver"
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent!!.action == SMS_RECEIVED) {
            val bundle = intent.extras
            if (bundle != null) {
                val pdus = bundle.get("pdus") as Array<Any>
                val messages = arrayOfNulls<SmsMessage>(pdus.size)
                for (i in pdus.indices) {
                    messages[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray)
                }
                if (messages.size > -1) {
                    blinkFlash(context!!)
                    Log.i(TAG, "Message recieved: " + messages[0]!!.messageBody)
                }
            }
        }
    }

    private fun blinkFlash(context: Context) {
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val myString = "010101010101"
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

}