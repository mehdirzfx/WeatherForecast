package com.weatherForecast

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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