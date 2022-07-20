
import android.annotation.SuppressLint
import android.content.Context
import android.inputmethodservice.Keyboard
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.blankj.utilcode.util.LogUtils
import com.lxj.xpopup.interfaces.XPopupCallback
import com.wangyi.oldvehiclekeyboard.BottomVehicleKeyboardDialog
import com.wangyi.oldvehiclekeyboard.OnKeyboardActionAdapter
import com.wangyi.oldvehiclekeyboard.R
import com.wangyi.oldvehiclekeyboard.VehicleKeyboardView

/**
 *
 *     author wangyi
 *     create time: 2022/7/14 15:28
 *     description:
 *
 */
class MyVehicleKeyboardHelper {
    val PROVINCES = "京津渝沪冀晋辽吉黑苏浙皖闽赣鲁豫鄂湘粤琼川贵云陕甘青蒙桂宁新藏使领警学港澳"

    private var bottomVehicleKeyboardDialog: BasePopupView? = null

    @SuppressLint("ClickableViewAccessibility")
    fun bind(et: EditText, context: Context) {
        var customDialog: BottomVehicleKeyboardDialog? = null
        if (bottomVehicleKeyboardDialog == null) {
            if (customDialog == null) {
                customDialog = BottomVehicleKeyboardDialog(context)
            }
            bottomVehicleKeyboardDialog = XPopup.Builder(context)
                .hasShadowBg(false)
                .dismissOnTouchOutside(true)
                .setPopupCallback(object : XPopupCallback {
                    override fun onCreated(popupView: BasePopupView) {
                        val keyboard =
                            customDialog.popupContentView?.findViewById<VehicleKeyboardView>(R.id.keyboardView)
                        LogUtils.e(keyboard)
                        customDialog.popupContentView?.findViewById<ImageView>(R.id.downIv)
                            ?.setOnClickListener {
                                bottomVehicleKeyboardDialog?.dismiss()
                                et.clearFocus()
                            }
                        keyboard?.setOnKeyboardActionListener(object : OnKeyboardActionAdapter(et) {

                            override fun close() {
                                bottomVehicleKeyboardDialog?.dismiss()
                            }

                            override fun onKeyEvent(primaryCode: Int, keyCodes: IntArray): Boolean {
                                val s: String = et.text.toString()
                                return if ("ABC".hashCode() == primaryCode) {
                                    keyboard.switchToLetters()
                                    true
                                } else if (-2 == primaryCode) {
                                    keyboard.switchToProvinces()
                                    true
                                    //忽略IO两个按钮
                                } else if (73 == primaryCode || 79 == primaryCode) {
                                    true
                                } else {
                                    // 除功能键以外的键
                                    if (primaryCode != Keyboard.KEYCODE_DELETE && s.length >= 8) {
                                        return true // 车牌号到最长长度了
                                    }
                                    if (PROVINCES.contains(primaryCode.toChar())) {
                                        if (s.isEmpty()) {
                                            keyboard.switchToLetters()
                                        }
                                        false
                                    } else {
                                        super.onKeyEvent(primaryCode, keyCodes)
                                    }
                                }
                            }
                        })

                        et.addTextChangedListener(object : TextWatcher {
                            override fun beforeTextChanged(
                                s: CharSequence,
                                start: Int,
                                count: Int,
                                after: Int
                            ) {
                            }

                            override fun onTextChanged(
                                s: CharSequence,
                                start: Int,
                                before: Int,
                                count: Int
                            ) {
                            }

                            override fun afterTextChanged(s: Editable) {
                                if (s.isEmpty()) {
                                    keyboard?.switchToProvinces() // 当没有文字的时候键盘切换回省份
                                }
                            }
                        })

                    }

                    override fun beforeShow(popupView: BasePopupView?) {
                    }

                    override fun onShow(popupView: BasePopupView?) {
                    }

                    override fun onDismiss(popupView: BasePopupView?) {
                        et.clearFocus()
                    }

                    override fun beforeDismiss(popupView: BasePopupView?) {
                    }

                    override fun onBackPressed(popupView: BasePopupView?): Boolean {
                        bottomVehicleKeyboardDialog?.dismiss()
                        et.clearFocus()
                        return true
                    }

                    override fun onKeyBoardStateChanged(popupView: BasePopupView?, height: Int) {
                    }

                    override fun onDrag(
                        popupView: BasePopupView?,
                        value: Int,
                        percent: Float,
                        upOrLeft: Boolean
                    ) {
                    }

                    override fun onClickOutside(popupView: BasePopupView?) {
                    }

                })
                .asCustom(customDialog)

        }

        // 设置触摸的点击事件。为了显示光标。（直接设置InputType.TYPE_NULL的话光标会消失）
        // 监听输入框的焦点事件：获得焦点显示自定义键盘；失去焦点收起自定义键盘
        et.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                hideSysInput(et, context)
                bottomVehicleKeyboardDialog?.show()
            } else {
                bottomVehicleKeyboardDialog?.dismiss()
            }
        }

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

    fun unBind(et: EditText) {
        et.onFocusChangeListener = null
        bottomVehicleKeyboardDialog?.destroy()
    }

    /**
     * 隐藏系统输入法
     *
     * @param et 输入框控件
     */
    fun hideSysInput(et: EditText, context: Context) {
        val windowToken = et.windowToken
        if (windowToken != null) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }
}
