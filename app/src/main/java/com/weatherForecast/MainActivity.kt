package com.weatherForecast

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONObject
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection


/**
 * @author : Mehdi Rezaei Far
 * @Date : 20-3-1401 / 10-June-2023
 * @Project : WeatherForecast With API https://openweathermap.org
 * @Professor : AmirHadi Minoofam
 * @Email : mehdirzfx@gmail.com
 */

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btnSearch = findViewById<FloatingActionButton>(R.id.btn_search)
        val etLocation = findViewById<EditText>(R.id.et_location)

        getData("کرج", "fa", "metric")
        btnSearch.setOnClickListener {
            if (etLocation.text.equals("")) {
                Toast.makeText(this, "لطفا نام یک شهر را وارد کنید.", Toast.LENGTH_SHORT).show()
            } else {
                val cityName = etLocation.text.toString()
                val lang = "fa"
                val units = "metric"
                getData(cityName, lang, units)
            }
        }
    }
    fun btnInfo(v : View){
        if (v.id == R.id.btn_info){
            showAlertDialog(this@MainActivity, "درباره", R.string.alert_msg)
        }
    }
    private fun showAlertDialog(context: Context, title: String, message: Int) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = builder.create()
        alertDialog.show()
    }
    private fun getData(cityName: String, lang: String, units: String) {
        val token = "df80aaeecbec20e9dad24cb07f02270f"
        val lang = lang
        val units = units
        val myApi =
            "https://api.openweathermap.org/data/2.5/weather?q=$cityName&lang=$lang&units=$units&appid=$token"
        MyAsyncTask().execute(myApi)
    }

    inner class MyAsyncTask : AsyncTask<String, String, String>() {
        override fun doInBackground(vararg params: String?): String {
            try {
                val url = URL(params[0])
                val connection = url.openConnection() as HttpsURLConnection
                connection.connectTimeout = 1500
                val json = convertStreamToString(connection.inputStream)
                publishProgress(json)
            } catch (e: FileNotFoundException) {
                //Log.e("MyAsyncTask", "File not found exception: ${e.message}")
                getData("کرج", "fa", "metric")
            } catch (e: Exception) {
                Log.e("MyAsyncTask", "Exception: ${e.message}")
            }
            return ""
        }

        @SuppressLint("SetTextI18n")
        override fun onProgressUpdate(vararg values: String?) {
            val jsonObject = values[0]?.let { JSONObject(it) }
            val state = jsonObject?.getInt("cod")
            if (state == 200) {
                val weather = jsonObject.getJSONArray("weather")
                val wind = jsonObject.getJSONObject("wind")
                val main = jsonObject.getJSONObject("main")
                val sys = jsonObject.getJSONObject("sys")
                val name = jsonObject.getString("name")
                val array = weather.getJSONObject(0)
                val desc = array.getString("description")
                val wmain = array.getString("main")
                val country = sys.getString("country")
                val iconcode: String = array.getString("icon")
                val speed = wind.getDouble("speed")
                val humidity = main.getInt("humidity")
                val feels = main.getDouble("feels_like").toInt()
                val temp = main.getDouble("temp").toInt()

                val cityName = findViewById<TextView>(R.id.txt_cityName)
                val countryName = findViewById<TextView>(R.id.txt_countryName)
                val txtTemp = findViewById<TextView>(R.id.txt_temperature)
                val txtStatus = findViewById<TextView>(R.id.txt_status)
                val txtFeels = findViewById<TextView>(R.id.txt_feels)
                val txtHumidity = findViewById<TextView>(R.id.txt_humidity)
                val txtSpeed = findViewById<TextView>(R.id.txt_speed)

                cityName.text = name
                txtTemp.text = "$temp °C"
                countryName.text = country
                txtStatus.text = desc
                txtFeels.text = "حس واقعی دما : $feels"
                txtHumidity.text = "$humidity%"
                txtSpeed.text = "$speed m/H"

                val inputImageMap = mapOf(
                    "01d" to R.drawable._1d_4x,
                    "02d" to R.drawable._2d_4x,
                    "03d" to R.drawable._3d_4x,
                    "04d" to R.drawable._4d_4x,
                    "09d" to R.drawable._9d_4x,
                    "10d" to R.drawable._10d_4x,
                    "11d" to R.drawable._11d_4x,
                    "13d" to R.drawable._13d_4x,
                    "50d" to R.drawable._50d_4x,
                    "01n" to R.drawable._1n_4x,
                    "02n" to R.drawable._2n_4x,
                    "03n" to R.drawable._3n_4x,
                    "04n" to R.drawable._4n_4x,
                    "09n" to R.drawable._9n_4x,
                    "10n" to R.drawable._10n_4x,
                    "11n" to R.drawable._11n_4x,
                    "13n" to R.drawable._13n_4x,
                    "50n" to R.drawable._50n_4x
                )
                val imgView = findViewById<ImageView>(R.id.weather_icon_imageview)
                if (inputImageMap.containsKey(iconcode)) {
                    imgView.setImageResource(inputImageMap[iconcode]!!)
                } else {
                    imgView.setImageResource(inputImageMap["01d"]!!)
                }
            } else {
                Toast.makeText(applicationContext, "مکان مورد نظر یافت نشد!", Toast.LENGTH_SHORT)
                    .show()
            }
            super.onProgressUpdate(*values)
        }
    }
    private fun convertStreamToString(stream: InputStream): String {
        val bufferReader = BufferedReader(InputStreamReader(stream))
        var line: String
        val sb = StringBuilder()

        try {
            do {
                line = bufferReader.readLine()
                sb.append(line)
            } while (true)

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return sb.toString()
    }
}