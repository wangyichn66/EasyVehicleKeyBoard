package com.wangyi.easyvehiclekeyboard

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ScreenUtils
import com.hjq.shape.view.ShapeTextView
import com.lxj.xpopup.core.BottomPopupView
import com.wangyi.easyvehiclekeyboard.databinding.LayoutBottomVehicleKeyboardBinding
import java.lang.RuntimeException


/**
 *
 *     author wangyi
 *     create time: 2022/7/14 14:33
 *     description:
 *
 */
@SuppressLint("ViewConstructor")
class BottomVehicleKeyboardPopView(context: Context, var mEditText: EditText) :
    BottomPopupView(context) {
    val str = "京津渝沪冀晋辽吉黑苏浙皖闽赣鲁豫鄂湘粤琼川贵云陕甘青蒙桂宁新藏使领警学港澳"

    private lateinit var binding: LayoutBottomVehicleKeyboardBinding

    override fun getImplLayoutId() = R.layout.layout_bottom_vehicle_keyboard

    /**
     * 按下按键的小气泡的弹窗
     */
    private lateinit var mPreviewPopup: PopupWindow

    private lateinit var mPreviewView: View

    private val mLettersButtonList = mutableListOf<ShapeTextView>()

    private val mProvincesButtonList = mutableListOf<ShapeTextView>()

    private val ProvincesKeyBoard = 1

    private val LettersKeyBoard = 2

    private var mCurrentKeyBoard = ProvincesKeyBoard

    var popupWindowWidth_px = DefaultPopupWindowWidth_px
    var popupWindowHeight_px = DefaultPopupWindowHeight_px

    companion object {
        var DefaultPopupWindowWidth_px = ConvertUtils.dp2px(52f)
        var DefaultPopupWindowHeight_px = ConvertUtils.dp2px(66f)
    }


    /**
     * 预览小气泡的偏移量y轴 正值往下偏移 反之负值往上偏移
     */
    var popupWindowOffsetY_px = ConvertUtils.dp2px(0f)

    /**
     * 预览小气泡的偏移量x轴 正值往下偏移 反之负值往上偏移
     */
    var popupWindowOffsetX_px = ConvertUtils.dp2px(0f)

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate() {
        binding = LayoutBottomVehicleKeyboardBinding.bind(popupImplView)
        binding.run {
            //隐藏弹窗的按钮点击
            ivHide.setOnClickListener {
//                mEditText.clearFocus()
                dismiss()
            }

            //默认显示省份的数字键盘
            switchToProvinces()

            //emm 去查一遍子view找到所有的按钮...
            for (index in 1..36) {
                val id = rootLayout.resources.getIdentifier("tv_$index", "id", context.packageName)
                val button = rootLayout.findViewById<ShapeTextView>(id)
                if (button != null) {
                    //直接过滤 I O 两个键 车牌里面不会包含这两个按键
                    if (button.text != "I" && button.text != "O") {
                        mLettersButtonList.add(button)
                        bindKeyListener(button)
                    }
                } else {
                    LogUtils.e("无法找到对应的按钮Id:tv_$index 请检查xml文档id是否拼写错误")
                    throw RuntimeException("无法找到对应的按钮Id:tv_$index 请检查xml文档id是否拼写错误")
                }
            }

            //emm 去查一遍子view找到所有的按钮...
            for (index in 1..37) {
                val id = rootLayout.resources.getIdentifier("tv_p$index", "id", context.packageName)
                val button = rootLayout.findViewById<ShapeTextView>(id)
                if (button != null) {
                    mProvincesButtonList.add(button)
                    bindKeyListener(button)

                } else {
                    LogUtils.e("无法找到对应的按钮Id:tv_p$index 请检查xml文档id是否拼写错误")
                    throw RuntimeException("无法找到对应的按钮Id:tv_p$index 请检查xml文档id是否拼写错误")
                }
            }

            //删除的按钮点击
            ivClear.setOnClickListener {
                val start = mEditText.selectionStart.coerceAtLeast(0)
                val end = mEditText.selectionEnd.coerceAtLeast(0)
                if (start != end) {
                    mEditText.text.delete(start, end)
                } else if (start > 0) {
                    mEditText.text.delete(start - 1, end)
                }
            }

            ivClear.setOnLongClickListener {
                mEditText.text.delete(0, mEditText.selectionEnd)
                return@setOnLongClickListener true
            }

            //删除的按钮点击
            ivClear2.setOnClickListener {
                val start = mEditText.selectionStart.coerceAtLeast(0)
                val end = mEditText.selectionEnd.coerceAtLeast(0)
                if (start != end) {
                    mEditText.text.delete(start, end)
                } else if (start > 0) {
                    mEditText.text.delete(start - 1, end)
                }
            }

            ivClear2.setOnLongClickListener {
                mEditText.text.delete(0, mEditText.selectionEnd)
                return@setOnLongClickListener true
            }

            //ABC按钮点击
            tvABC.setOnClickListener {
                switchToLetters()
            }

            //切换省份的按钮点击
            tvProvince.setOnClickListener {
                switchToProvinces()
            }

            mEditText.addTextChangedListener(object : TextWatcher {
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
                        switchToProvinces() // 当没有文字的时候键盘切换回省份
                    }
                }
            })
        }

        initPerView()
    }


    /**
     * 切换到数字字母的键盘
     */
    private fun switchToLetters() {
        binding.llLetters.visibility = View.VISIBLE
        binding.llProvinces.visibility = View.GONE
        mCurrentKeyBoard = LettersKeyBoard
    }

    /**
     * 切换到省份的键盘
     */
    private fun switchToProvinces() {
        binding.llLetters.visibility = View.GONE
        binding.llProvinces.visibility = View.VISIBLE
        mCurrentKeyBoard = ProvincesKeyBoard
    }

    private fun initPerView() {
        mPreviewView = inflate(context, R.layout.layout_preview, null)
        mPreviewPopup =
            PopupWindow(mPreviewView, popupWindowWidth_px, popupWindowHeight_px)
        mPreviewPopup.isFocusable = false
        mPreviewPopup.enterTransition = null
        mPreviewPopup.animationStyle
        mPreviewPopup.exitTransition = null
        mPreviewPopup.setBackgroundDrawable(null)
        mPreviewPopup.isTouchable = false
        mPreviewPopup.animationStyle = 0

//        研究了一两天才发现 android 官方的 KeyboardView里面的这个弹窗不是每次按下的时候去show
//        然后松手的时候去dismiss因为这样 弹窗肯定没有那么快相应 然后官方的做法是第一次按下的时候调用show的函数 这样不能马上显示弹窗
//        会有一点点的延时 然后松手的时候去设置弹窗内容体view 的visibility 为gone 这样弹窗其实一直都在 只是不可见的
//        然后下一次点击 先更新弹窗的位置 然后再让内容体可见 这样就能达到按下键的瞬间显示小气泡了
//        同样我这里也这样做吧 但是我做了一些优化 保证第一次按下的时候也能瞬间显示弹窗
        mPreviewView.visibility = GONE
    }

    override fun beforeShow() {
        //这行代码也研究半天 主要是一个问题 输入法的窗口没有显示的时候他找的根节点不对
        //然后后面调用的update之后的位置就会被弹窗挡住
        //反正一堆坑
        //主要是定位一个锚点 这样 才不会被弹窗给挡住 所以这里的锚点只需要给 rootLayout的子view就行了
        //发现不管怎么设置 这个气泡的左上角 都在我的锚点的左下角
        mPreviewPopup.showAsDropDown(binding.llLetters)
    }

    /**
     * 显示弹窗 原理上面创建的时候说过了
     * 这里说一下坐标的计算原理
     * 首先 肯定是通过按钮的中心去定位弹窗的位置
     * 但是按钮的坐标是 相对于...
     *
     * 想了半天 然后实际他么的根本调不出来 最好只能手动去用dp调到居中了...
     * @param tv ShapeTextView
     */
    private fun showPreView(tv: ShapeTextView) {
        mPreviewView.findViewById<TextView>(R.id.tv).text = tv.text
        val p1 = tv.parent as LinearLayout
        val p2 = p1.parent as LinearLayout

        //tv相对于 这个底部弹窗的top
        val topToRootView =
            tv.top + p1.top + p2.top + (ScreenUtils.getAppScreenHeight() - BarUtils.getActionBarHeight() - binding.rootLayout.height)

        //tv相对于 这个底部弹窗的left
        val leftToRootView = tv.left + p1.left + p2.left

        //update之后这个锚点的定位点又是哪呢？？？
        //实在找不到 定位的点了 用dp去调吧... 我太菜了 哎
        mPreviewPopup.update(
            leftToRootView - ConvertUtils.dp2px(10f) + popupWindowOffsetY_px,
            topToRootView + ConvertUtils.dp2px(5f) + popupWindowOffsetX_px,
            -1,
            -1
        )

        mPreviewView.visibility = View.VISIBLE
    }

    private fun hidePreView() {
        mPreviewView.visibility = View.INVISIBLE
    }

    /**
     * 键盘dismiss 把预览的气泡也隐藏
     */
    override fun onDismiss() {
        hidePreView()
        mPreviewPopup.dismiss()
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun bindKeyListener(tv: ShapeTextView) {
//        //监听按下和抬起的触摸事件 显示/隐藏 气泡
        tv.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    showPreView(tv)
                }
                MotionEvent.ACTION_UP -> {
                    hidePreView()
                }
                //move无法实现... 果然还是要像官方的那样做一个整体的 自定义view才是最完美 这种写法目前无法实现滑动 气泡
//                MotionEvent.ACTION_MOVE -> {
//                    showPreView(tv)
//                }

            }
            //仅监听事件 不拦截触摸事件 让view的onTouchEvent继续执行 从而响应OnClickListener
            false
        }
        tv.setOnClickListener {
            val text = tv.text
            if (mEditText.text.length <= 8) {     //车牌号最多8位
                val start = mEditText.selectionStart.coerceAtLeast(0)
                val end = mEditText.selectionEnd.coerceAtLeast(0)
                mEditText.text.replace(
                    start.coerceAtMost(end), start.coerceAtLeast(end),
                    text, 0, text.length
                )
                if (str.contains(mEditText.text)) {   //如果已经输入过省份了 那么跳转到字母键盘
                    switchToLetters()
                }
            }

        }
    }


}