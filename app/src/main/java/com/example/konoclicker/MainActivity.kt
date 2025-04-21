package com.example.konoclicker

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.widget.ImageButton
import java.util.Calendar
import kotlin.math.max
import kotlin.math.pow




class MainActivity : AppCompatActivity() {
    interface DataSource {
        fun getInt(key: String, defaultValue: Int): Int
    }

    // Реализация для Bundle
    class BundleDataSource(private val bundle: Bundle) : DataSource {
        override fun getInt(key: String, defaultValue: Int): Int {
            return bundle.getInt(key, defaultValue)
        }
    }

    // Реализация для SharedPreferences
    class SharedPreferencesDataSource(private val sharedPreferences: SharedPreferences) : DataSource {
        override fun getInt(key: String, defaultValue: Int): Int {
            return sharedPreferences.getInt(key, defaultValue)
        }
    }


    lateinit var text_v : TextView
    lateinit var to_next_level_v : TextView

    var clicks : Int = 0
    var level : Int = 1
    var clicks_at_next_level : Int = 0


    var clicks_today : Int = 0
    var clicks_this_week : Int = 0
    var clicks_this_month : Int = 0
    var clicks_this_year : Int = 0

    var current_day : Int = 0
    var current_month : Int = 0
    var current_week : Int = 0
    var current_year : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null)
        {
            var prefs = this.getPreferences(Context.MODE_PRIVATE)
            readStatsData(SharedPreferencesDataSource(prefs))
        }
        else
        {
            readStatsData(BundleDataSource(savedInstanceState))
        }
        checkDataStats()
        level = levelFromClicks(clicks)
        clicks_at_next_level = clicksToLevel(level + 1)

        text_v = findViewById<TextView>(R.id.clicksTextView)
        to_next_level_v = findViewById<TextView>(R.id.toNextLevelTextView)
        changeTexts()
        choosePicture()
    }

    override fun onResume() {
        super.onResume()
        checkDataStats()
    }

    fun  readStatsData(inf : DataSource ) {
        clicks = inf.getInt(getString(R.string.total_clicks_key), 0)
        current_year = inf.getInt(getString(R.string.year_key), 0)
        current_day = inf.getInt(getString(R.string.day_key), 0)
        current_week = inf.getInt(getString(R.string.week_key), 0)
        current_month = inf.getInt(getString(R.string.month_key), 0)
        clicks_today = inf.getInt(getString(R.string.daily_clicks_key), 0)
        clicks_this_week = inf.getInt(getString(R.string.weekly_clicks_key), 0)
        clicks_this_month = inf.getInt(getString(R.string.monthly_clicks_key), 0)
        clicks_this_year = inf.getInt(getString(R.string.yearly_clicks_key), 0)
    }

    fun checkDataStats() {
        var calendar = Calendar.getInstance()
        var changed_year = current_year != calendar.get(Calendar.YEAR)
        var changed_month = changed_year || current_month != calendar.get(Calendar.MONTH)
        var changed_week = changed_month || current_week != calendar.get(Calendar.WEEK_OF_YEAR)
        var changed_day = changed_week || current_day != calendar.get(Calendar.DAY_OF_YEAR)
        if (changed_day) {
            current_day = calendar.get(Calendar.DAY_OF_YEAR)
            clicks_today = 0
        }
        if (changed_week) {
            current_week = calendar.get(Calendar.WEEK_OF_YEAR)
            clicks_this_week = 0
        }
        if (changed_month) {
            current_month = calendar.get(Calendar.MONTH)
            clicks_this_month = 0
        }
        if (changed_year) {
            current_year = calendar.get(Calendar.YEAR)
            clicks_this_year = 0
        }
    }

    fun changeTexts() {
        text_v.text = clicks.toString()
        to_next_level_v.text = getString(R.string.to_next_level, level + 1, clicks_at_next_level - clicks)
    }
    fun onTap(p0: View) {
        checkDataStats()
        clicks++
        clicks_today++
        clicks_this_week++
        clicks_this_month++
        clicks_this_year++
        if (clicks == clicks_at_next_level) {
            level++
            clicks_at_next_level = clicksToLevel(level + 1)
            choosePicture()
        }
        changeTexts()
    }
    fun clicksToLevel(level_v : Int) =  10.0.pow(level_v - 1).toInt()
    fun levelFromClicks(clicks_amount : Int) = 1 + kotlin.math.log10(max(1.0, clicks_amount.toDouble())).toInt()

    fun resetClicks(p0: View?) {
        clicks = 0
        level = 1
        clicks_at_next_level = clicksToLevel(level + 1)
        clicks_today = 0
        clicks_this_month = 0
        clicks_this_week = 0
        clicks_this_year = 0
        changeTexts()
        choosePicture()
    }

    override fun onPause() {
        var prefs = this.getPreferences(Context.MODE_PRIVATE)
        with (prefs.edit()) {
            putInt(getString(R.string.total_clicks_key), clicks)
            putInt(getString(R.string.daily_clicks_key), clicks_today)
            putInt(getString(R.string.monthly_clicks_key), clicks_this_month)
            putInt(getString(R.string.weekly_clicks_key), clicks_this_week)
            putInt(getString(R.string.yearly_clicks_key), clicks_this_year)
            putInt(getString(R.string.day_key), current_day)
            putInt(getString(R.string.week_key), current_week)
            putInt(getString(R.string.month_key), current_month)
            putInt(getString(R.string.year_key), current_year)
            apply()
        }
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {

        with (outState) {
            putInt(getString(R.string.total_clicks_key), clicks)
            putInt(getString(R.string.daily_clicks_key), clicks_today)
            putInt(getString(R.string.monthly_clicks_key), clicks_this_month)
            putInt(getString(R.string.weekly_clicks_key), clicks_this_week)
            putInt(getString(R.string.yearly_clicks_key), clicks_this_year)
            putInt(getString(R.string.day_key), current_day)
            putInt(getString(R.string.week_key), current_week)
            putInt(getString(R.string.month_key), current_month)
            putInt(getString(R.string.year_key), current_year)
        }

        super.onSaveInstanceState(outState)
    }

    fun onExitButton(p0: View) {
        finish()
    }
    fun onProfileButton(p0: View) {
        val url = "https://web.telegram.org/k/#@protobarhatus"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    fun onStatsClick(p0: View) {
        val intent = Intent(this, StatsActivity::class.java)
        checkDataStats()
        intent.putExtra(getString(R.string.daily_clicks_key), clicks_today)
        intent.putExtra(getString(R.string.monthly_clicks_key), clicks_this_month)
        intent.putExtra(getString(R.string.weekly_clicks_key), clicks_this_week)
        intent.putExtra(getString(R.string.yearly_clicks_key), clicks_this_year)
        startActivity(intent)
    }

    fun choosePicture() {
        var pic = findViewById<ImageButton>(R.id.imageButton)
        if (level == 1)
            pic.setImageResource(R.drawable.tap_char_1)
        if (level == 2)
            pic.setImageResource(R.drawable.tap_char_2)
        if (level == 3)
            pic.setImageResource(R.drawable.tap_char_3)
        if (level >= 4)
            pic.setImageResource(R.drawable.tap_char_4)
    }
}


