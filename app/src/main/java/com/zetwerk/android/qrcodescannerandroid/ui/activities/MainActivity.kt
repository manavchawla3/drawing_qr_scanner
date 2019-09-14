package com.zetwerk.android.qrcodescannerandroid.ui.activities

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.webkit.URLUtil
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.zxing.integration.android.IntentIntegrator
import com.zetwerk.android.qrcodescannerandroid.R
import com.zetwerk.android.qrcodescannerandroid.core.api.ApiModule
import com.zetwerk.android.qrcodescannerandroid.core.api.ApiServiceGenerator
import com.zetwerk.android.qrcodescannerandroid.models.drawings.Drawing
import com.zetwerk.android.qrcodescannerandroid.models.drawings.DrawingResponse
import com.zetwerk.android.qrcodescannerandroid.models.qr_details.QR_Details
import com.zetwerk.android.qrcodescannerandroid.utils.NetworkUtils
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    private lateinit var inclusionViewGroup: ViewGroup
    private var gson: Gson? = null
    private var downloadFileUrl: String? = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Create gson instance to parse json objects
        val gsonBuilder = GsonBuilder()
        gson = gsonBuilder.create()

        // open qr scanner as soon as activity launches
        IntentIntegrator(this).initiateScan()

        //initialize views and attach listeners
        inclusionViewGroup = drawing_details_card as ViewGroup

        button_scan_qr_code.setOnClickListener {
            IntentIntegrator(this).initiateScan()
        }
    }


    /**
     * Handles what happens when QR Scanner successfully scan the code or is canceled!
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        /**
         * If scanner was closed
         * ** Show Error View
         *
         * Else
         * ** Initialize drawing details
         */
        if (result != null) {
            if (result.contents == null) {

                // Show error screen with appropriate msg when scanner was closed before scanning
                setEmptyScreen("Could not scan. Try Again!")
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()

            } else {
                // parse the scanned contents
                var jsonObject: JSONObject? = JSONObject(result.contents)
                var qr_details: QR_Details? = gson?.fromJson(jsonObject.toString(), QR_Details::class.java)

                // Verify if the qr is from zetwerk only (has drawingId field)
                //TODO => verify the signature
                if (qr_details?.drawingId === null) {

                    // If Qr is not from zetwerk shoe error screen
                    setEmptyScreen("Invalid QR Code")
                    Toast.makeText(this, "Invalid QR Code", Toast.LENGTH_LONG).show()
                } else {

                    // Initialize drawing detail view
                    getDrawingDetails(qr_details?.drawingId.toString())
                    Toast.makeText(this, "Scanned: " + result.contents, Toast.LENGTH_LONG).show()
                }

            }
        }
    }

    /**
     * Ajax Call to fetch drawing details
     * id => drawing id (not number)
     */
    private fun getDrawingDetails(id: String) {

        //fetch only if client has internet connection
        if (NetworkUtils.isNetworkConnected(this)) {

            // Make retrofit client
            val apiService = ApiServiceGenerator.createService(ApiModule::class.java)
            val callGetDrawing: Call<DrawingResponse> = apiService.getDrawingDetails(id)

            callGetDrawing.enqueue(object : Callback<DrawingResponse> {
                override fun onResponse(call: Call<DrawingResponse>, response: Response<DrawingResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        Log.d("api", response.body().toString())
                        setDrawingDetails(response.body()?.data)
                    }
                }

                override fun onFailure(call: Call<DrawingResponse>, t: Throwable) {
                    setEmptyScreen("Data Not Available. Try Again!")
                }
            })

        } else {
            val contextView = findViewById<View>(R.id.drawing_details_card)

            val snackbar = Snackbar
                .make(contextView, R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
                .setAction("RETRY", View.OnClickListener { getDrawingDetails(id) })
            snackbar.setActionTextColor(ContextCompat.getColor(this, R.color.colorBlue))

        }
    }

    private fun setDrawingDetails(drawing: Drawing?) {
        val drawingDetails = layoutInflater.inflate(R.layout.layout_drawing_details, null)
        inclusionViewGroup.removeAllViews()
        inclusionViewGroup.addView(drawingDetails)

        val textDrawingNo = findViewById<TextView>(R.id.drawing_no)
        val textQty = findViewById<TextView>(R.id.quantity)
        val textUnit = findViewById<TextView>(R.id.unit)
        val textReceivedOn = findViewById<TextView>(R.id.received_date)
        val downloadFile = findViewById<RelativeLayout>(R.id.download_file)

        textDrawingNo.text = drawing?.drawing_no
        textQty.text = drawing?.qty
        textUnit.text = drawing?.unit
        textReceivedOn.text = drawing?.received_on

        this.downloadFileUrl = if (drawing?.processed_drawing !== null) {
            drawing.processed_drawing?.file_url

        } else {
            drawing?.unprocessed_drawing?.file_url
        }

        downloadFile.setOnClickListener {
            startDownload(this.downloadFileUrl)
        }

    }

    private fun setEmptyScreen(errorMsg: String?) {
        val errorScreen = layoutInflater.inflate(R.layout.layout_empty_screen, null)
        inclusionViewGroup.removeAllViews()
        inclusionViewGroup.addView(errorScreen)

        if (errorMsg !== null) {
            val textErrorMsg = findViewById<TextView>(R.id.error_msg)
            textErrorMsg.text = errorMsg
        }
    }

    private fun haveStoragePermission(): Boolean {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
            ) {
                Log.e("Permission error", "You have permission");
                return true
            } else {

                Log.e("Permission error", "You have asked for permission");
                ActivityCompat.requestPermissions(
                    this,
                    Array<String>(1) { Manifest.permission.WRITE_EXTERNAL_STORAGE },
                    1
                );
                return false
            }
        } else { //you dont need to worry about these stuff below api level 23
            Log.e("Permission error", "You already have the permission");
            return true;
        }
    }

    /**
     * Start Download
     */
    fun startDownload(url: String?) {

        if (url == null) {
            Log.d("api", "Error")
            return
        }

        if (haveStoragePermission().not()) return

        val dmr = DownloadManager.Request(Uri.parse(url))

        val fileName = URLUtil.guessFileName(url, null, MimeTypeMap.getFileExtensionFromUrl(url))

        dmr.setTitle(fileName)
        dmr.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
        dmr.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        dmr.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
        val manager = this.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        manager.enqueue(dmr)
    }


}
