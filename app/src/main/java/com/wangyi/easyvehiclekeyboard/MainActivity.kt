package com.wangyi.easyvehiclekeyboard

import MyVehicleKeyboardHelper
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MyVehicleKeyboardHelper().bind(findViewById<EditText>(R.id.et), this)

        EasyVehicleKeyboard().bind(this, findViewById<EditText>(R.id.et2))

    }
}