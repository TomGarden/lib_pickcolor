package io.github.tomgarden.lib.pickcolor

import android.content.Context
import android.content.DialogInterface
import android.content.res.Configuration
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridView
import androidx.appcompat.app.AlertDialog
import io.github.tomgarden.lib.log.Logger

/**
 * 我们需要 context 上下文做重要的事情 , 比如加载布局等等
 * 但是 context 的初始化需要依赖生命周期
 * 所以我们先将依赖生命周期的操作的相关数据备份 , 等拿到了 context 在进一步使用它们完成初始化操作
 *
 * 对于 Parcelable 缓存的数据需要做一个严格区分 , 只对必要的内容做缓存动作
 *
 * "请知晓" : Builder 中的数据对象是写完所有代码后从 var 变量中 copy 来的
 */
open class BaseBuilder() : Parcelable {


    //***********************************************************************************
    //                                 需要执行 Parcel 操作的对象
    //***********************************************************************************
    /*默认展示的颜色控制面板*/
    var pickColorCurPanel = PickColorDefPanel.PANEL_SELECT

    /*弹窗标题*/
    var title: String? = null

    /*默认颜色*/
    var inputColor: PickColor = PickColor(Utils.DEF_COLOR)

    /*携带的其他数据*/
    var flag: Any? = null


    var negativeBtnStr: String? = null
    var neutralBtnSelectStr: String? = null
    var neutralBtnCustomStr: String? = null
    var positiveBtnStr: String? = null


    /*这些按钮点击后弹窗会消失 , 如果设置了 defXXX 和 XXX , XXX 会替换掉 defXXX 的响应*/
    var defNegativeClickListener:
            ((dialogInterface: DialogInterface?, which: Int, flag: Any?) -> Unit)? = null
    var defNeutralClickListener:
            ((dialogInterface: DialogInterface?, which: Int, flag: Any?) -> Unit)? = null
    var defPositiveClickListener:
            ((dialogInterface: DialogInterface?, which: Int, selColor: PickColor?, flag: Any?) -> Unit)? =
        null

    /*这些按钮点击后弹窗是否会消失由开发者主动控制*/
    var negativeClickListener:
            ((dialogFrag: PickColorDialogFrag, btnNegative: Button, flag: Any?) -> Unit)? = null
    open var neutralClickListener:
            ((dialogFrag: PickColorDialogFrag, btnNeutral: Button, flag: Any?) -> Unit)? = null
    var positiveClickListener:
            ((dialogFrag: PickColorDialogFrag, btnPositive: Button, flag: Any?) -> Unit)? = null

    var onShowListener: (() -> Unit)? = null
    var onDismissListener: (() -> Unit)? = null


    //***********************************************************************************
    //                                 构造函数和 Parcel 函数
    //***********************************************************************************


    constructor(parcel: Parcel) : this() {
//        parcel.readParcelable<PickColorDefPanel>(PickColorDefPanel::class.java.classLoader)
//            ?.let {
//                pickColorCurPanel = it
//            }
//        title = parcel.readString()
//        negativeBtnStr = parcel.readString()
//        neutralBtnSelectStr = parcel.readString()
//        neutralBtnCustomStr = parcel.readString()
//        positiveBtnStr = parcel.readString()

    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {

//        dest?.writeString(title)
//        dest?.writeString(negativeBtnStr)
//
//        dest?.writeValue(neutralClickListener)

        if (neutralClickListener != null) {
            Logger.i("neutralClickListener != null")
        }
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<BaseBuilder> {

        const val BUILDER_PARCELABLE = "BUILDER_PARCELABLE"

        override fun createFromParcel(parcel: Parcel): BaseBuilder {
            return BaseBuilder(parcel)
        }

        override fun newArray(size: Int): Array<BaseBuilder?> {
            return arrayOfNulls(size)
        }
    }


    //***********************************************************************************
    //                        执行 Parcel 操作的属性相关的赋值取值函数
    //***********************************************************************************

    fun setDefPanel(defPanel: PickColorDefPanel): BaseBuilder {
        this.pickColorCurPanel = defPanel
        return this
    }

    fun setTitle(title: String?): BaseBuilder {
        this.title = title
        return this
    }

    fun setInputColor(inputColor: PickColor): BaseBuilder {
        this.inputColor = inputColor
        return this
    }

    fun setFlag(flag: Any?): BaseBuilder {
        this.flag = flag
        return this
    }

    fun setDefNegativeClickListener(
        negativeBtnStr: String,
        defNegativeClickListener: ((dialogInterface: DialogInterface?, which: Int, flag: Any?) -> Unit)?
    ): BaseBuilder {
        this.negativeBtnStr = negativeBtnStr
        this.defNegativeClickListener = defNegativeClickListener
        return this
    }

    /**
     * @param neutralBtnSelectStr 选择面板 neutral 按钮文案
     * @param neutralBtnCustomStr 自定义面板 neutral 按钮文案
     */
    fun setDefNeutralClickListener(
        neutralBtnSelectStr: String,
        neutralBtnCustomStr: String,
        defNeutralClickListener: ((dialogInterface: DialogInterface?, which: Int, flag: Any?) -> Unit)?
    ): BaseBuilder {
        this.neutralBtnSelectStr = neutralBtnSelectStr
        this.neutralBtnCustomStr = neutralBtnCustomStr
        defNeutralClickListener?.let {
            this.defNeutralClickListener = defNeutralClickListener
        }
        return this
    }

    fun setDefPositiveClickListener(
        positiveBtnStr: String,
        defPositiveClickListener: ((dialogInterface: DialogInterface?, which: Int, selColor: PickColor?, flag: Any?) -> Unit)?
    ): BaseBuilder {
        this.positiveBtnStr = positiveBtnStr
        this.defPositiveClickListener = defPositiveClickListener
        return this
    }


    fun setNegativeClickListener(
        negativeBtnStr: String,
        negativeClickListener: ((dialogFrag: PickColorDialogFrag, btnNegative: Button, flag: Any?) -> Unit)?
    ): BaseBuilder {
        this.negativeBtnStr = negativeBtnStr
        this.negativeClickListener = negativeClickListener
        return this
    }

    fun setNeutralClickListener(
        neutralBtnSelectStr: String,
        neutralBtnCustomStr: String,
        neutralClickListener: ((dialogFrag: PickColorDialogFrag, btnNeutral: Button, flag: Any?) -> Unit)?
    ): BaseBuilder {
        this.neutralBtnSelectStr = neutralBtnSelectStr
        this.neutralBtnCustomStr = neutralBtnCustomStr
        this.neutralClickListener = neutralClickListener
        return this
    }

    fun setPositiveClickListener(
        positiveBtnStr: String,
        positiveClickListener: ((dialogFrag: PickColorDialogFrag, btnPositive: Button, flag: Any?) -> Unit)?
    ): BaseBuilder {
        this.positiveBtnStr = positiveBtnStr
        this.positiveClickListener = positiveClickListener
        return this
    }


    fun setOnShowListener(onShowListener: (() -> Unit)?): BaseBuilder {
        this.onShowListener = onShowListener
        return this
    }

    fun setOnDismissListener(onDismissListener: (() -> Unit)?): BaseBuilder {
        this.onDismissListener = onDismissListener
        return this
    }

    open fun build(): PickColorDialogFrag = throw RuntimeException("Not Override")
}


/**
 * 将 Dialog 内逻辑委托出来 , 方便继承不同的 DialogFragment 实现 PickColor
 */
class PickColorDelegate(var mContext: Context? = null) : BaseBuilder() {

    /*关键 dialog*/
    val aDialog: AlertDialog by lazy {
        val builder = AlertDialog.Builder(getContext())

        when (pickColorCurPanel) {
            PickColorDefPanel.PANEL_SELECT -> builder.setView(this.gridView)//默认首次展示一级颜色选区
            PickColorDefPanel.PANEL_CUSTOM -> builder.setView(this.colorCustomLayout)
        }

        title?.let { builder.setTitle(title) }

        val neutralBtnStr =
            when (pickColorCurPanel) {
                PickColorDefPanel.PANEL_SELECT -> neutralBtnSelectStr
                PickColorDefPanel.PANEL_CUSTOM -> neutralBtnCustomStr
            }

        builder
            .setNegativeButton(negativeBtnStr) { dialog, which ->
                defNegativeClickListener?.invoke(dialog, which, flag)
            }
            .setNeutralButton(neutralBtnStr) { dialog, which ->
                defNeutralClickListener?.invoke(dialog, which, flag)
            }
            .setPositiveButton(positiveBtnStr) { dialog, which ->
                val selColor: PickColor = getPickColorResult()
                defPositiveClickListener?.invoke(dialog, which, selColor, flag)
            }

        val alertDialog = builder.create()

        alertDialog
    }

    /*layout 颜色选择布局*/
    val gridView: GridView by lazy {

        val result =
            LayoutInflater.from(getContext()).inflate(R.layout.color_selector, null) as GridView

        if (getContext().resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //横屏
            result.numColumns = 5
        } else if (getContext().resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            //竖屏
            result.numColumns = 4
        }
        result.adapter = this.pickColorAdapter
        result.onItemClickListener = this.pickColorAdapter
        result.onItemLongClickListener = this.pickColorAdapter

        result
    }
    val pickColorAdapter: PickColorAdapter by lazy { PickColorAdapter(getContext(), inputColor) }

    /*自定义面板逻辑委托*/
    val cclDelegate by lazy { ColorCustomLayoutDelegate(getContext()) }
    val colorCustomLayout by lazy { cclDelegate.colorCustomLayout }
    val etHexInput by lazy { cclDelegate.etHexInput }

    override var neutralClickListener: ((dialogFrag: PickColorDialogFrag, btnNeutral: Button, flag: Any?) -> Unit)? =
        { dialogFrag: PickColorDialogFrag, btnNeutral: Button, flag: Any? ->

            when (pickColorCurPanel) {
                PickColorDefPanel.PANEL_SELECT -> fromSelToCustomLayout(btnNeutral)
                PickColorDefPanel.PANEL_CUSTOM -> fromCustomToSelLayout(btnNeutral)
            }

        }

    constructor(builder: BaseBuilder) : this(null) {
        this.pickColorCurPanel = builder.pickColorCurPanel
        this.title = builder.title
        this.inputColor = builder.inputColor
        this.flag = builder.flag
        this.negativeBtnStr = builder.negativeBtnStr
        this.neutralBtnSelectStr = builder.neutralBtnSelectStr
        this.neutralBtnCustomStr = builder.neutralBtnCustomStr
        this.positiveBtnStr = builder.positiveBtnStr
        this.defNegativeClickListener = builder.defNegativeClickListener
        this.defNeutralClickListener = builder.defNeutralClickListener
        this.defPositiveClickListener = builder.defPositiveClickListener
        this.negativeClickListener = builder.negativeClickListener
        this.neutralClickListener = builder.neutralClickListener ?: this.neutralClickListener
        this.positiveClickListener = builder.positiveClickListener
        this.onShowListener = builder.onShowListener
        this.onDismissListener = builder.onDismissListener
    }


    /**从颜色选择面板到自定义颜色面板*/
    private fun fromSelToCustomLayout(btnNeutralParam: Button?) {

        (gridView.parent as ViewGroup?)?.let { viewGroup ->
            viewGroup.removeView(gridView)

            val selColorResult = pickColorAdapter.getSelColor()

            getContext().let { context ->
                colorCustomLayout.let {
                    etHexInput.setText(selColorResult.getHexColorWithoutPrefix(context))
                }
            }

            viewGroup.addView(colorCustomLayout)

            val btnNeutral =
                btnNeutralParam ?: let { aDialog.getButton(DialogInterface.BUTTON_NEUTRAL) }

            btnNeutral.post { btnNeutral.text = neutralBtnCustomStr }
        }

        pickColorCurPanel = PickColorDefPanel.PANEL_CUSTOM
    }

    /**从自定义颜色面板到颜色选择面板*/
    private fun fromCustomToSelLayout(btnNeutralParam: Button?) {

        (colorCustomLayout.parent as ViewGroup?)?.let { viewGroup ->
            viewGroup.removeView(colorCustomLayout)
            viewGroup.addView(gridView)
            val btnNeutral =
                btnNeutralParam ?: let { aDialog.getButton(DialogInterface.BUTTON_NEUTRAL) }

            btnNeutral.post { btnNeutral.text = neutralBtnSelectStr }
        }

        pickColorCurPanel = PickColorDefPanel.PANEL_SELECT
    }

    private fun getContext(): Context = mContext!!

    fun getPickColorResult(): PickColor {
        val selColor: PickColor =
            when (pickColorCurPanel) {
                PickColorDefPanel.PANEL_SELECT -> {
                    pickColorAdapter.getSelColor()
                }
                PickColorDefPanel.PANEL_CUSTOM -> {
                    PickColor(cclDelegate.getColorHexStrFromDex())
                }
            }

        return selColor
    }

    override fun build(): PickColorDialogFrag = PickColorDialogFrag(this)
}


/**调色板初始状态*/
enum class PickColorDefPanel() : Parcelable {
    PANEL_SELECT,
    PANEL_CUSTOM;


    constructor(parcel: Parcel) : this()


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(this.name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PickColorDefPanel> {
        override fun createFromParcel(parcel: Parcel): PickColorDefPanel {
            return when (parcel.readString()) {
                PANEL_SELECT.name -> PANEL_SELECT
                PANEL_CUSTOM.name -> PANEL_CUSTOM
                else -> PANEL_SELECT
            }
        }

        override fun newArray(size: Int): Array<PickColorDefPanel?> {
            return arrayOfNulls(size)
        }
    }
}
