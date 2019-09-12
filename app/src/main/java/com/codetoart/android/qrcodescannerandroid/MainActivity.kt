package com.codetoart.android.qrcodescannerandroid

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var inclusionViewGroup: ViewGroup


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        IntentIntegrator(this).initiateScan()

        inclusionViewGroup = drawing_details_card as ViewGroup

        button_scan_qr_code.setOnClickListener {
            IntentIntegrator(this).initiateScan()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)


        if (result != null) {
            if (result.contents == null) {
                setEmptyScreen()
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            } else {
                setDrawingDetails(result.contents)
                Toast.makeText(this, "Scanned: " + result.contents, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setDrawingDetails(result: String) {
        val drawingDetails = layoutInflater.inflate(R.layout.layout_drawing_details, null)
        inclusionViewGroup.removeAllViews()
        inclusionViewGroup.addView(drawingDetails)
    }

    private fun setEmptyScreen() {
        val errorScreen = layoutInflater.inflate(R.layout.layout_empty_screen, null)
        inclusionViewGroup.removeAllViews()
        inclusionViewGroup.addView(errorScreen)
    }
}

