package com.tryamb.studentReside.study

import android.os.AsyncTask
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.barteksc.pdfviewer.PDFView
import com.tryamb.studentReside.R
import java.io.BufferedInputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import kotlin.coroutines.coroutineContext

class OpenPdfActivity : AppCompatActivity() {
    lateinit var pdfView: PDFView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_pdf)
        pdfView = findViewById(R.id.idPDFView)
        var pdfUrl = intent.getStringExtra("pdfUrl")
        RetrievePDFFromURL(pdfView).execute(pdfUrl)
    }

    inner class RetrievePDFFromURL(pdfView: PDFView) :
        AsyncTask<String, Void, InputStream>() {

        val mypdfView: PDFView = pdfView

        override fun doInBackground(vararg params: String?): InputStream? {
            // on below line we are creating a variable for our input stream.
            var inputStream: InputStream? = null
            try {
                // on below line we are creating an url
                // for our url which we are passing as a string.
                val url = URL(params[0])

                // on below line we are creating our http url connection.
                val urlConnection: HttpURLConnection = url.openConnection() as HttpsURLConnection

                if (urlConnection.responseCode == 200) {
                    inputStream = BufferedInputStream(urlConnection.inputStream)
                }
            }
            // on below line we are adding catch block to handle exception
            catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@OpenPdfActivity,"$e",Toast.LENGTH_LONG).show()
                return null;
            }
            // on below line we are returning input stream.
            return inputStream;
        }

        override fun onPostExecute(result: InputStream?) {
            // on below line we are loading url within our
            // pdf view on below line using input stream.
            mypdfView.fromStream(result).load()

        }
    }

}