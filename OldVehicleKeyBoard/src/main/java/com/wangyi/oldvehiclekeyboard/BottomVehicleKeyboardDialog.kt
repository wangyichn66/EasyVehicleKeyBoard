package com.wangyi.oldvehiclekeyboard

import android.annotation.SuppressLint
import android.content.Context
import android.widget.EditText
import com.lxj.xpopup.core.BottomPopupView


/**
 *
 *     author wangyi
 *     create time: 2022/7/14 14:33
 *     description:
 *
 */
@SuppressLint("ViewConstructor")
class BottomVehicleKeyboardDialog(context: Context) : BottomPopupView(context) {

    override fun getImplLayoutId() = R.layout.keyboard_window
}