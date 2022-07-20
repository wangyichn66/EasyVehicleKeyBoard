android 原生自己写的车牌输入法 自带按键气泡预览效果 按键气泡秒弹窗

折腾了小几天 之前是继承系统的KeyboardView 自定义实现 也基本做出来了 但是发现气泡调起来非常痛苦
于是就有了这个项目



实现的原理其实就是画了一个布局 然后放到弹窗里面
至于为什么弹窗里面可以再加个预览的小气泡弹窗 留个悬念 
一切尽在The fucking source codec 

这样做的话 改起来就很方便 由于是xml画的布局 所以基本上可以随便改
预览的弹窗也是自定义的xml写的 所以也可以随便改

代码很简单 就只有2个类罢了 也没有写什么接口 

使用方法导入到你的AS项目里面 可以单独作为一个模块
代码 
用在 Activity
VehicleKeyboardView.bind(activity: AppCompatActivity, et: EditText)

用在 Fragment
VehicleKeyboardView.bind(fragment: Fragment, et: EditText)




