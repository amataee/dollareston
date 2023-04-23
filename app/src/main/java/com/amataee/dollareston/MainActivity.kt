package com.amataee.dollareston

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {

    private val usdUrl = "https://dapi.p3p.repl.co/api/?currency=usd"
    private val cadUrl = "https://dapi.p3p.repl.co/api/?currency=cad"

    private lateinit var usdTextView: TextView
    private lateinit var cadTextView: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var usdImageView: ImageView
    private lateinit var cadImageView: ImageView
    private lateinit var swipeRefresh: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        usdTextView = findViewById(R.id.usdTextView)
        cadTextView = findViewById(R.id.cadTextView)
        usdImageView = findViewById(R.id.usdImageView)
        cadImageView = findViewById(R.id.cadImageView)
        progressBar = findViewById(R.id.progressBar)
        swipeRefresh = findViewById(R.id.swipeRefresh)

        usdImageView.visibility = View.GONE
        cadImageView.visibility = View.GONE

        request(usdUrl, usdTextView, this, applicationContext, progressBar, usdImageView)
        request(cadUrl, cadTextView, this, applicationContext, progressBar, cadImageView)

        swipeRefresh.setOnRefreshListener {
            usdImageView.visibility = View.GONE
            cadImageView.visibility = View.GONE
            usdTextView.visibility = View.INVISIBLE
            cadTextView.visibility = View.INVISIBLE
            progressBar.visibility = View.VISIBLE

            request(usdUrl, usdTextView, this, applicationContext, progressBar, usdImageView)
            request(cadUrl, cadTextView, this, applicationContext, progressBar, cadImageView)

            swipeRefresh.isRefreshing = false
        }

    }
}

private fun request(
    url: String,
    textView: TextView,
    context: Context,
    applicationContext: Context,
    progressBar: ProgressBar,
    imageView: ImageView
) {
    val queue: RequestQueue = Volley.newRequestQueue(applicationContext)

    val request = JsonObjectRequest(Request.Method.GET, url, null, { response ->
        try {
            val currency: String = response.getString("Currency")
            val price: String = response.getString("Price")
            Handler().postDelayed({
                progressBar.visibility = View.GONE
                imageView.visibility = View.VISIBLE
                textView.visibility = View.VISIBLE

                textView.text = "${currency.uppercase()}: ${priceFormatter(price)}"
            }, 1)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                context, "Oops! Something went failed.", Toast.LENGTH_SHORT
            ).show()
            progressBar.visibility = View.GONE
        }

    }, { error ->
        Log.e("tag", "RESPONSE IS $error")
        Toast.makeText(
            context, "Failed! Check your internet connection.", Toast.LENGTH_SHORT
        ).show()
        progressBar.visibility = View.GONE
    })
    queue.add(request)
}

private fun priceFormatter(priceString: String): String {
    val formatted = priceString.toInt() / 10
    return formatted.toString()
}