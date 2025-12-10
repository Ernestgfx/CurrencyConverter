package com.example.currencyconverter

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var convertButton: Button
    private lateinit var amountInput: EditText
    private lateinit var resultView: TextView
    private lateinit var fromCurrencySpinner: Spinner
    private lateinit var toCurrencySpinner: Spinner


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        amountInput = findViewById(R.id.amountInput)
        resultView = findViewById(R.id.resultView)
        convertButton = findViewById(R.id.convertBtn)
        fromCurrencySpinner = findViewById(R.id.fromCurrencySpinner)
        toCurrencySpinner = findViewById(R.id.toCurrencySpinner)

        // Populate the spinners with currencies
        val currencies = arrayOf("USD", "EUR", "INR", "GBP", "JPY", "AUD", "CAD", "CHF", "CNY", "MXN")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        fromCurrencySpinner.adapter = adapter
        toCurrencySpinner.adapter = adapter

        // listener for the convert button
        convertButton.setOnClickListener {
            val amount = amountInput.text.toString().toDoubleOrNull() ?: 1.0
            val from = fromCurrencySpinner.selectedItem.toString()
            val to = toCurrencySpinner.selectedItem.toString()

            // Fetching conversion rate
            fetchConversionRate(from, to, amount)
        }
    }

    private fun fetchConversionRate(from: String, to: String, amount: Double) {
        val apiKey = "fdeae2e917ee64a6a88920cb"
        val urlStr = "https://v6.exchangerate-api.com/v6/$apiKey/latest/$from"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL(urlStr)
                val connection = url.openConnection() as HttpURLConnection
                connection.connect()

                val reader = InputStreamReader(connection.inputStream)
                val json = JSONObject(reader.readText())
                val result = json.getString("result") // e.g. "success"
                val conversionRates = json.getJSONObject("conversion_rates")
                val rate = conversionRates.getDouble(to)

                val convertedAmount = rate * amount

                withContext(Dispatchers.Main) {
                    if (result == "success") {
                        resultView.text = "$amount $from = $convertedAmount $to"
                    } else {
                        resultView.text = "API call failed"
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    resultView.text = "Error: ${e.message}"
                }
            }
        }
    }
}