package com.wangyi.easyvehiclekeyboard

import android.annotation.SuppressLint
import android.content.Context
import android.text.InputType
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.LogUtils
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.interfaces.SimpleCallback
import java.lang.ref.WeakReference

/**
 *
 *     author wangyi
 *     create time: 2022/7/15 15:45
 *     description:
 *     用单例模式写这个 一是会内存泄露 2是弹窗总是卡住 弹不出来 不知道为什么
 *     所以暂时去掉单例的写法
 *
 */
class EasyVehicleKeyboard {
    private lateinit var bottomVehicleKeyboardDialog: BasePopupView
    private lateinit var customPopView: BottomVehicleKeyboardPopView
//    private lateinit var customPopView: WeakReference<BottomVehicleKeyboardPopView>

    private var mPopupWindowOffsetY_px = 0
    private var mPopupWindowOffsetX_px = 0
    private var mPopupWindowWidth_px = BottomVehicleKeyboardPopView.DefaultPopupWindowWidth_px
    private var mPopupWindowHeight_px = BottomVehicleKeyboardPopView.DefaultPopupWindowHeight_px

    fun setPreViewOffset_px(offsetX_px: Int, offsetY_px: Int) {
        mPopupWindowOffsetY_px = offsetY_px
        mPopupWindowOffsetX_px = offsetX_px
    }

    fun setPreViewOffset_dp(offsetX_dp: Float, offsetY_dp: Float) {
        mPopupWindowOffsetY_px = ConvertUtils.dp2px(offsetY_dp)
        mPopupWindowOffsetX_px = ConvertUtils.dp2px(offsetX_dp)
    }

    fun setPreViewWidthHeight_dp(width_dp: Float, height_dp: Float) {
        mPopupWindowWidth_px = ConvertUtils.dp2px(width_dp)
        mPopupWindowHeight_px = ConvertUtils.dp2px(height_dp)
    }

    fun setPreViewWidthHeight_px(width_px: Int, height_px: Int) {
        mPopupWindowWidth_px = width_px
        mPopupWindowHeight_px = height_px
    }

    fun bind(activity: AppCompatActivity, et: EditText) {
        initBottomDialog(activity, et)
        initListener(activity, et)
        activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
            @SuppressLint("ClickableViewAccessibility")
            override fun onDestroy(owner: LifecycleOwner) {
                et.onFocusChangeListener = null
                et.setOnTouchListener(null)
                et.clearFocus()
                bottomVehicleKeyboardDialog.dismiss()
            }
        })
    }

    fun bind(fragment: Fragment, et: EditText) {
        initBottomDialog(fragment.requireContext(), et)
        initListener(fragment.requireContext(), et)
        fragment.lifecycle.addObserver(object : DefaultLifecycleObserver {
            @SuppressLint("ClickableViewAccessibility")
            override fun onDestroy(owner: LifecycleOwner) {
                et.onFocusChangeListener = null
                et.setOnTouchListener(null)
                et.clearFocus()
                bottomVehicleKeyboardDialog.dismiss()
            }
        })
    }

    private fun initBottomDialog(context: Context, et: EditText) {
        //静态类这样写就有用了 目前这样没啥大用..
        if(!this::customPopView.isInitialized) {
            customPopView = BottomVehicleKeyboardPopView(context, et)
            customPopView.popupWindowOffsetY_px = mPopupWindowOffsetY_px
            customPopView.popupWindowOffsetX_px = mPopupWindowOffsetX_px

            customPopView.popupWindowWidth_px = mPopupWindowWidth_px
            customPopView.popupWindowHeight_px = mPopupWindowHeight_px

            //同上
            if(!this::bottomVehicleKeyboardDialog.isInitialized) {
                bottomVehicleKeyboardDialog = XPopup.Builder(context)
                    //不显示灰色的遮罩
                    .hasShadowBg(false)
                    //点击弹窗外的区域隐藏弹窗
                    .dismissOnTouchOutside(true)
                    .dismissOnBackPressed(true)
                    .moveUpToKeyboard(true)
                    .autoOpenSoftInput(false)
                    .setPopupCallback(object : SimpleCallback() {
                        override fun onDismiss(popupView: BasePopupView) {
                            //点击其他区域会关闭窗口 但是et的焦点还在 这里需要手动清理
                            et.clearFocus()
                        }

                        override fun onBackPressed(popupView: BasePopupView): Boolean {
                            et.clearFocus()
                            return true //我来处理返回按下的事件 xpopup无需处理
                        }
                    })
                    .asCustom(customPopView)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initListener(context: Context, et: EditText) {
        et.isFocusable = true
        // 监听输入框的焦点事件：获得焦点显示自定义键盘；失去焦点收起自定义键盘
        et.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                hideSysInput(et, context)
                bottomVehicleKeyboardDialog.show()
            } else {
                bottomVehicleKeyboardDialog.dismiss()

            }
        }

        // 设置触摸的点击事件。为了显示光标。（直接设置InputType.TYPE_NULL的话光标会消失）
        et.setOnTouchListener { v, event ->
            val inType = et.inputType
            et.inputType = InputType.TYPE_NULL
            et.onTouchEvent(event)
            et.inputType = inType
            // 光标始终在内容的最后面
            et.setSelection(et.text.length)
            true
        }
    }

    /**
     * 隐藏系统输入法
     *
     * @param et 输入框控件
     */
    private fun hideSysInput(et: EditText, context: Context) {
        val windowToken = et.windowToken
        if (windowToken != null) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }


}
