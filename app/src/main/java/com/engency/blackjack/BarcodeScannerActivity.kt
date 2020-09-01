package com.engency.blackjack

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.engency.blackjack.Models.Product
import com.engency.blackjack.network.BarcodeSuccess
import com.engency.blackjack.network.NetworkHelper
import com.engency.blackjack.stores.ProductStore
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector

class BarcodeScannerActivity : AppCompatActivity() {
    private var detectorActive: Boolean = false
    private lateinit var etBarcode: EditText
    private lateinit var detector: BarcodeDetector
    private lateinit var svBarcode: SurfaceView
    private lateinit var cameraSource: CameraSource
    private lateinit var btnSubmitCode: Button

    private var REQUEST_CAMERA = 1
    private var LOG_TAG = "bclog"

    private lateinit var properties: GroupPropertyManager
    private lateinit var productStore: ProductStore

    private var usedCodes: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.properties = GroupPropertyManager(applicationContext)
        this.productStore = ProductStore(applicationContext)

        setContentView(R.layout.activity_barcode_scanner)

        etBarcode = findViewById<View>(R.id.barcodeText) as EditText
        svBarcode = findViewById(R.id.sv_barcode)
        btnSubmitCode = findViewById(R.id.btn_submit_code)

        detector = BarcodeDetector.Builder(applicationContext)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build()

        cameraSource = CameraSource.Builder(this, detector)
                .setRequestedPreviewSize(1024, 768)
                .setRequestedFps(1f)
                .setAutoFocusEnabled(true)
                .build()


        svBarcode.holder!!.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource.stop()
            }

            override fun surfaceCreated(holder: SurfaceHolder) {
                if (ContextCompat.checkSelfPermission(this@BarcodeScannerActivity, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    cameraSource.start(holder)
                } else ActivityCompat.requestPermissions(this@BarcodeScannerActivity, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA)
            }
        })

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)


        btnSubmitCode.setOnClickListener {
            verifyCode(etBarcode.text.toString())
            etBarcode.setText("")
        }
    }

    private fun parseDetections(detections: Detector.Detections<Barcode>?) {
        val barcodes = detections?.detectedItems

        for (index in 0 until barcodes!!.size()) {
            val code = barcodes.valueAt(index)

            val type = barcodes.valueAt(index).valueFormat
            Log.i(LOG_TAG, type.toString())
            when (type) {
                Barcode.TEXT -> verifyCode(code.rawValue)
                else -> Log.i(LOG_TAG, "type " + type + ", " + code.rawValue)
            }
        }
    }

    private fun verifyCode(barcode: String) {
        if (usedCodes.indexOf(barcode) > -1) {
            return
        }

        usedCodes.add(barcode)

        if (productStore.hasProductWithCode(barcode)) {
            Snackbar.make(this.etBarcode, "Hee, je hebt dit product al joh!", Snackbar.LENGTH_LONG).show()
        } else {
            NetworkHelper.submitProduct(barcode, properties.get("token")!!,
                    success = { data: BarcodeSuccess -> this.success(data) },
                    failure = { message: String -> this.failure(message) }
            )
        }
    }

    private fun success(data: BarcodeSuccess) {
        properties.updateWithGroupInstance(data.groupInfo)

        when (data.type) {
            "points" -> {
                val message: String = data.message
                showTextPopup(message)
            }
            "product" -> {
                foundProduct(productStore.getById(data.productId)!!)
            }
            "product_reward" -> {
                val message: String = data.message
                showTextPopup(message)
            }
        }

        Snackbar.make(this.etBarcode, "yesss, code is goedgekeurd!", Snackbar.LENGTH_LONG).show()
    }

    private fun failure(message: String) {
        Snackbar.make(this.etBarcode, message, Snackbar.LENGTH_LONG).show()
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CAMERA -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                cameraSource.start(svBarcode.holder)
            } else {
                Toast.makeText(this@BarcodeScannerActivity, "Permission Denied!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (!detectorActive) {
            startDetector()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        detector.release()
        cameraSource.stop()
        cameraSource.release()
    }

    fun foundProduct(product: Product) {
        stopDetector()
        startActivity(ProductPreviewActivity.newIntent(this, product))
    }

    fun showTextPopup(text: String) {
        stopDetector()
        startActivity(SimpleTextActivity.newIntent(this, text))
    }

    private fun startDetector() {
        detector.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {}

            override fun receiveDetections(detections: Detector.Detections<Barcode>?) {
                parseDetections(detections)
            }
        })
        detectorActive = true
    }

    private fun stopDetector() {
        detector.release()
        detectorActive = false
    }

    companion object {

        fun newIntent(context: Context): Intent {
            return Intent(context, BarcodeScannerActivity::class.java)
        }
    }

}