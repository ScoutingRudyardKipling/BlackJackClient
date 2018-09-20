package com.engency.blackjack

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.engency.blackjack.network.NetworkHelper
import com.engency.blackjack.network.OnNetworkResponseInterface
import com.engency.blackjack.stores.ProductStore
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import org.json.JSONObject

class BarcodeScannerActivity : AppCompatActivity(), OnNetworkResponseInterface {
    private lateinit var etBarcode: EditText
    private lateinit var detector: BarcodeDetector
    private lateinit var svBarcode: SurfaceView
    private lateinit var cameraSource: CameraSource

    private var REQUEST_CAMERA = 1
    private var LOG_TAG = "bclog"

    private lateinit var properties: GroupPropertyManager
    private lateinit var productStore : ProductStore

    private var usedCodes: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.properties = GroupPropertyManager(applicationContext)
        this.productStore = ProductStore(applicationContext)

        setContentView(R.layout.activity_barcode_scanner)

        etBarcode = findViewById<View>(R.id.barcodeText) as EditText
        svBarcode = findViewById(R.id.sv_barcode)

        detector = BarcodeDetector.Builder(applicationContext)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build()

        detector.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {}

            override fun receiveDetections(detections: Detector.Detections<Barcode>?) {
                parseDetections(detections)
            }
        })

        cameraSource = CameraSource.Builder(this, detector)
                .setRequestedPreviewSize(1024, 768)
                .setRequestedFps(1f)
                .setAutoFocusEnabled(true)
                .build()

        svBarcode.holder.addCallback(object : SurfaceHolder.Callback2 {
            override fun surfaceRedrawNeeded(holder: SurfaceHolder?) {}
            override fun surfaceChanged(holder: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {}
            override fun surfaceDestroyed(holder: SurfaceHolder?) {
                cameraSource.stop()
            }

            override fun surfaceCreated(holder: SurfaceHolder?) {
                if (ContextCompat.checkSelfPermission(this@BarcodeScannerActivity, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    cameraSource.start(holder)
                } else ActivityCompat.requestPermissions(this@BarcodeScannerActivity, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA)
            }
        })

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
    }

    private fun parseDetections(detections: Detector.Detections<Barcode>?) {
        val barcodes = detections?.detectedItems

        for (index in 0 until barcodes!!.size()) {
            val code = barcodes.valueAt(index)

            val type = barcodes.valueAt(index).valueFormat
            Log.i(LOG_TAG, type.toString())
            when (type) {
                Barcode.PRODUCT -> verifyCode(code.rawValue)
                else -> Log.i(LOG_TAG, "type " + type + ", " + code.rawValue)
            }
        }
    }

    private fun verifyCode(barcode: String) {
        if (usedCodes.indexOf(barcode) > -1) {
            return
        }

        usedCodes.add(barcode)

        if(productStore.hasProductWithCode(barcode)) {
            Snackbar.make(this.etBarcode, "Hee, je hebt dit product al joh!", Snackbar.LENGTH_LONG).show()
        } else {
            NetworkHelper.submitProduct(barcode, properties.get("token")!!, this)
        }
    }

    override fun success(data: JSONObject) {
        Log.e("SUCCESS", data.toString())

        properties.updateWithGroupInstance(data)

        Snackbar.make(this.etBarcode, "yesss, item is toegevoegd!", Snackbar.LENGTH_LONG).show()
    }

    override fun failure(message: String) {
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

    override fun onDestroy() {
        super.onDestroy()
        detector.release()
        cameraSource.stop()
        cameraSource.release()
    }



    companion object {

        fun newIntent(context: Context): Intent {
            return Intent(context, BarcodeScannerActivity::class.java)
        }
    }

}