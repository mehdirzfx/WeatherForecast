package com.weatherForecast

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val city = "Karaj"
        getData(city, "fa", "metric")

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
            val url = URL(params[0])
            val connection = url.openConnection() as HttpsURLConnection
            connection.connectTimeout = 1500
            val json = convertStreamToString(connection.inputStream)
            publishProgress(json)
            return ""
        }

        override fun onProgressUpdate(vararg values: String?) {
            val jsonObject = values[0]?.let { JSONObject(it) }
            val state = jsonObject?.getInt("cod")
            if (state == 200) {
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

                }
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