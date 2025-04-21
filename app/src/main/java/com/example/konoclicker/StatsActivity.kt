package com.example.konoclicker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StatsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        val monthName = SimpleDateFormat("LLLL", Locale.getDefault()).format(Date())
        val text = getString(R.string.clicks_monthly_text, monthName)
        findViewById<TextView>(R.id.monthly_clicks_textview).text = text

        findViewById<TextView>(R.id.clicksTodayTextView).text = intent.getIntExtra(getString(R.string.daily_clicks_key), 0).toString()
        findViewById<TextView>(R.id.clicksWeeklyTextView).text = intent.getIntExtra(getString(R.string.weekly_clicks_key), 0).toString()
        findViewById<TextView>(R.id.clicksMonthlyTextView).text = intent.getIntExtra(getString(R.string.monthly_clicks_key), 0).toString()
        findViewById<TextView>(R.id.clicksYearlyTextView).text = intent.getIntExtra(getString(R.string.yearly_clicks_key), 0).toString()

    }

    fun onBack(v0: View) {
        finish()
    }


}