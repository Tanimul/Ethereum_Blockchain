package com.example.ethereum_blockchain.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.*
import com.example.ethereum_blockchain.databinding.ActivityInputBinding
import com.example.ethereum_blockchain.utils.Constants.request_code
import com.example.ethereum_blockchain.utils.extentions.toast


class InputActivity : AppBaseActivity() {

    companion object {
        private const val TAG: String = "InputActivity"
    }

    private lateinit var binding: ActivityInputBinding
    private lateinit var codeScanner: CodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Check Camera Permission
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.CAMERA),
                request_code
            )
        }

        //some functionalities set for Qr code Scanner
        codeScanner = CodeScanner(this, binding.viewQrScanner)

        codeScanner.camera = CodeScanner.CAMERA_BACK
        codeScanner.formats = CodeScanner.ALL_FORMATS
        codeScanner.autoFocusMode = AutoFocusMode.SAFE
        codeScanner.scanMode = ScanMode.SINGLE
        codeScanner.isAutoFocusEnabled = true
        codeScanner.isFlashEnabled = false

        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                Log.d(TAG, "onCreate: ${it.text}")
                val address = it.text
                if (isAddressValidate(address)) {
                    startActivity(
                        Intent(this, InfoActivity::class.java).putExtra(
                            "address", address
                        )
                    )
                } else {
                    Log.d(TAG, "Error! Check your Public key address. ")
                    toast("Error! Check your Public key address.")
                }

            }
        }
        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            runOnUiThread {
                toast("Camera initialization error: ${it.message}")
            }
        }

        /*Test Purpose public address
        0x738d145faabb1e00cf5a017588a9c0f998318012
        0x9faaaaf9e2d101db242ecb23b833cd5f82b92cdc
        0x77EdD9eF8D639bE078507e79c3D2DBb5e513c839
        */

        binding.btnShow.setOnClickListener {
            val address = binding.etAddress.text.toString()
            if (isAddressValidate(address)) {
                startActivity(
                    Intent(this, InfoActivity::class.java).putExtra(
                        "address", address
                    )
                )
            } else {
                toast("Please Put Public key address.")
            }
        }

    }

    //Ethereum Address Validation
    private fun isAddressValidate(address: String): Boolean {
        return address.isNotEmpty() && address.startsWith("0x") && address.length == 42
    }

    //QR code start preview
    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    //QR code release Resources
    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    //Camera permission result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == request_code) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                toast("Permission granted")
                codeScanner.startPreview()
            } else {
                toast("Permission denied")
            }
        }
    }

}