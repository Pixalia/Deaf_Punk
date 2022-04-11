package com.example.fumolizer


import android.annotation.SuppressLint
import android.content.*
import android.graphics.Color
import android.media.MediaPlayer
import android.media.audiofx.PresetReverb
import android.view.MenuInflater
import android.view.MenuItem
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.*
import android.util.Log
import android.widget.Button
import android.widget.PopupMenu
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.lang.String
import kotlin.AssertionError


class EqualizerActivity : AppCompatActivity() {

    lateinit var broadCastReceiver : BroadcastReceiver
    var iF = IntentFilter()

    val SHARED_PREFS = "sharedPrefs"
    val SAVEHUE = "savehue"
    val SAVESAT = "savesat"
    val SAVELIGHT = "savelight"
    val SAVEHEX = "savehex"

    var hue = 0
    var sat = 0F
    var light = 0F
    var red: Float = 0F
    var green: Float = 0F
    var blue: Float = 0F
    var hex = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_equalizer)


        var presets = findViewById(R.id.button_equalizer_preset) as Button
        var equal = findViewById(R.id.button_equalizer_equal) as Button
        var cancel = findViewById(R.id.button_equalizer_cancel) as Button
        var switch = findViewById(R.id.switch_equalizer_check) as Switch

        val barTitle = findViewById<Button>(R.id.button_equalizer_title)

        fun updateViews() {
            presets.setBackgroundColor(Color.parseColor(hex))
            equal.setBackgroundColor(Color.parseColor(hex))
            cancel.setBackgroundColor(Color.parseColor(hex))
        }

        loadData()
        updateViews()

        if (intent == null){
            barTitle.text = "Error: Please change song"
        }
        else{
            barTitle.text = intent.getStringExtra("barTitle")
        }

        iF.addAction("com.android.music.metachanged")
        iF.addAction("com.htc.music.metachanged")
        iF.addAction("fm.last.android.metachanged")
        iF.addAction("com.sec.android.app.music.metachanged")
        iF.addAction("com.nullsoft.winamp.metachanged")
        iF.addAction("com.amazon.mp3.metachanged")
        iF.addAction("com.miui.player.metachanged")
        iF.addAction("com.real.IMP.metachanged")
        iF.addAction("com.sonyericsson.music.metachanged")
        iF.addAction("com.rdio.android.metachanged")
        iF.addAction("com.samsung.sec.android.MusicPlayer.metachanged")
        iF.addAction("com.andrew.apollo.metachanged")
        iF.addAction("in.krosbits.musicolet")
        iF.addAction("in.krosbits.musicolet.metachanged")
        iF.addAction("AudioEffect.ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION")
        iF.addAction("AudioEffect.ACTION_CLOSE_AUDIO_EFFECT_CONTROL_SESSION")

        broadCastReceiver = object : BroadcastReceiver() {
            @RequiresApi(Build.VERSION_CODES.Q)
            override fun onReceive(contxt: Context?, intent: Intent?) {

                val newTitle = intent?.getStringExtra("track").toString()
                barTitle.text = "Now Playing: $newTitle"

            }
        }

        registerReceiver(broadCastReceiver, iF)

        // Various buttons that work with the equalizer

        presets.setOnClickListener {
            val popup = PopupMenu(this, it)
            val inflater: MenuInflater = popup.menuInflater
            inflater.inflate(R.menu.actions, popup.menu)
            popup.show()
            popup.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.Preset1 -> {
                        switch.isChecked = true
                        presets.text = "Base"
                        val intent = Intent(this, BackgroundSoundService::class.java)
                        intent.putExtra("action", "preset1")
                        startService(intent)
                        true
                    }
                    R.id.Preset2 -> {
                        switch.isChecked = true
                        presets.text = "Vocal"
                        val intent = Intent(this, BackgroundSoundService::class.java)
                        intent.putExtra("action", "preset2")
                        startService(intent)
                        true
                    }
                    R.id.Preset3 -> {
                        switch.isChecked = true
                        presets.text = "Treble"
                        val intent = Intent(this, BackgroundSoundService::class.java)
                        intent.putExtra("action", "preset3")
                        startService(intent)
                        true
                    }
                    else -> throw AssertionError()
                }
            }
        }

        cancel.setOnClickListener{
            switch.isChecked = false
            val intent = Intent(this, BackgroundSoundService::class.java)
            intent.putExtra("action", "cancel")
            startService(intent)
        }

        switch.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {
                if(presets.text == "Base") {
                    val intent = Intent(this, BackgroundSoundService::class.java)
                    intent.putExtra("action", "preset1")
                    startService(intent)
                }
                else if(presets.text == "Vocal") {
                    val intent = Intent(this, BackgroundSoundService::class.java)
                    intent.putExtra("action", "preset2")
                    startService(intent)
                }
                else if(presets.text == "Treble") {
                    val intent = Intent(this, BackgroundSoundService::class.java)
                    intent.putExtra("action", "preset3")
                    startService(intent)
                }
                else {
                    Log.e("test", "else'd")
                    val intent = Intent(this, BackgroundSoundService::class.java)
                    intent.putExtra("action", "cancel")
                    startService(intent)
                }
            }
            else {
                Log.e("test", "cancel")
                val intent = Intent(this, BackgroundSoundService::class.java)
                intent.putExtra("action", "cancel")
                startService(intent)
            }
        }



        val nextButton = findViewById<ImageButton>(R.id.imageButton_equalizer_next)
        val backButton = findViewById<ImageButton>(R.id.imageButton_equalizer_back)
        val playButton = findViewById<ImageButton>(R.id.imageButton_equalizer_play)

        nextButton.setOnClickListener{
            val intent = Intent(this, BackgroundSoundService::class.java)
            intent.putExtra("action", "forward")
            startService(intent)
        }

        backButton.setOnClickListener {
            val intent = Intent(this, BackgroundSoundService::class.java)
            intent.putExtra("action", "backwards")
            startService(intent)
        }

        playButton.setOnClickListener {
            val intent = Intent(this, BackgroundSoundService::class.java)
            intent.putExtra("action", "playing")
            startService(intent)
        }

        val appSettingPrefs: SharedPreferences = getSharedPreferences( "AppSettingsPrefs", 0)
        val isNightModeOn: Boolean = appSettingPrefs.getBoolean( "NightMode", false)

        if(isNightModeOn) {
            nextButton.setColorFilter(ContextCompat.getColor(this, R.color.black))
            playButton.setColorFilter(ContextCompat.getColor(this, R.color.black))
            backButton.setColorFilter(ContextCompat.getColor(this, R.color.black))
            barTitle.setTextColor(ContextCompat.getColor(this, R.color.black))

        }

        // Navigation Menu Code that allows you to change activity.
        findViewById<BottomNavigationView>(R.id.bottom_navigation).selectedItemId = R.id.ic_equalizer
        findViewById<BottomNavigationView>(R.id.bottom_navigation).setOnNavigationItemSelectedListener {
            when (it.itemId){
                R.id.ic_volume -> {
                    val intent = Intent(this, VolumeActivity::class.java)
                    intent.putExtra("barTitle", barTitle.text)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.ic_equalizer -> {
                    true
                }
                R.id.ic_fumolizer -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("barTitle", barTitle.text)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.ic_compressor -> {
                    val intent = Intent(this, CompressorActivity::class.java)
                    intent.putExtra("barTitle", barTitle.text)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.ic_settings -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    intent.putExtra("barTitle", barTitle.text)
                    startActivity(intent)
                    finish()
                    true
                }
                else -> throw AssertionError()
            }
        }

        fun converthex(num:Int): kotlin.String {
            var list = listOf("0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F")
            var yeet = list[num]
            return yeet
        }

        fun rgbtohex(r:Float, g:Float, b:Float): kotlin.String {
            var d1="0";var d2="0";var d3="0";var d4="0";var d5="0";var d6="0"
            Log.e("d1",""+hue)
            //red = (NEWhsl2hex(hue,sat,light) >> 16) & 0xff;
            d1 = converthex((r/16.0).toInt())
            d2 = converthex(((r/16.0 - (r/16).toInt())*16).toInt())
            d3 = converthex((g/16).toInt())
            d4 = converthex(((g/16 - (g/16).toInt())*16).toInt())
            d5 = converthex((b/16).toInt())
            d6 = converthex(((b/16 - (b/16).toInt())*16).toInt())
            hex = "#" + d1 + d2 + d3 + d4 + d5 + d6
            return "#" + d1 + d2 + d3 + d4 + d5 + d6

        }

        fun hsltorgb(h:Int, s:Float, l:Float){
            var redt = 0F
            var greent = 0F
            var bluet = 0F
            //var C = ((1-Math.abs(2*l-1)) * s)
            var C = l * s
            var X = (C * (1-Math.abs((h/60)%2-1)))
            //var m = l - C/2
            var m = l - C

            if (h >= 0 && h < 60){redt=C; greent=X; bluet=0F}
            else if (h >= 60 && h < 120){redt=X; greent=C; bluet=0F}
            else if (h >= 120 && h < 180){redt=0F; greent=C; bluet=X}
            else if (h >= 180 && h < 240){redt=0F; greent=X; bluet=C}
            else if (h >= 240 && h < 300){redt=X; greent=0F; bluet=C}
            else if (h >= 300 && h < 360){redt=C; greent=0F; bluet=X}
            red = (redt+m)*255
            green = (greent+m)*255
            blue = (bluet+m)*255

        }

        fun updateViews() {
            hsltorgb(hue,sat,light)
            nextButton.background.setTint(Color.parseColor(rgbtohex(red,green,blue)))
            playButton.background.setTint(Color.parseColor(rgbtohex(red,green,blue)))
            backButton.background.setTint(Color.parseColor(rgbtohex(red,green,blue)))
            barTitle.setBackgroundColor(Color.parseColor(rgbtohex(red,green,blue)))
        }


    fun loadData() {

        var sharedPreferences: SharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE)
        hex = sharedPreferences.getString(SAVEHEX,"#7C0696").toString()

        loadData()
        updateViews()
    }

}



