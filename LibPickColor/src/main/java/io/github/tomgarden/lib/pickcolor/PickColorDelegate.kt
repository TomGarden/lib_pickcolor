package io.github.tomgarden.lib.pickcolor

import android.content.Context
import android.content.DialogInterface
import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridView
import androidx.appcompat.app.AlertDialog


/**
 * 将 Dialog 内逻辑委托出来 , 方便继承不同的 DialogFragment 实现 PickColor
 */
class PickColorDelegate(val context: Context, private val builder: PickColorDialogFrag.Builder) {

    //关键 dialog
    val aDialog: AlertDialog by lazy {
        val builder = AlertDialog.Builder(context)

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

                var selColor: PickColor? = null

                if (gridView.parent != null) {//如果在颜色选择布局

                    selColor = pickColorAdapter.getSelColor()

                } else if (colorCustomLayout.parent != null) {//如果在颜色自定义布局

                    etHexInput.text?.let {
                        selColor = PickColor(it.toString())
                    }

                } else {

                    //throw RuntimeException("logic err")

                }

                defPositiveClickListener?.invoke(dialog, which, selColor, flag)
            }

        val alertDialog = builder.create()

        alertDialog
    }

    //region Pick Color layout 颜色选择布局

    val gridView: GridView by lazy {

        val result = LayoutInflater.from(context).inflate(R.layout.color_selector, null) as GridView

        if (context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //横屏
            result.numColumns = 5
        } else if (context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            //竖屏
            result.numColumns = 4
        }
        result.adapter = this.pickColorAdapter
        result.onItemClickListener = this.pickColorAdapter
        result.onItemLongClickListener = this.pickColorAdapter

        result
    }
    val pickColorAdapter: PickColorAdapter by lazy { PickColorAdapter(context, inputColor) }

    //endregion Pick Color layout  一级颜色选择布局

    /*自定义面板逻辑委托*/
    val cclDelegate by lazy { ColorCustomLayoutDelegate(context) }
    val colorCustomLayout by lazy { cclDelegate.colorCustomLayout }
    val etHexInput by lazy { cclDelegate.etHexInput }

    /*默认展示的颜色控制面板*/
    private var pickColorCurPanel = builder.pickColorCurPanel

    /*弹窗标题*/
    val title: String? by lazy { builder.title }

    /*默认颜色*/
    val inputColor: PickColor by lazy { builder.inputColor }

    /*携带的其他数据*/
    val flag: Any? by lazy { builder.flag }

    val negativeBtnStr: String? by lazy { builder.negativeBtnStr }

    /*处于 sel 页面的时候显示的 neutral 文案*/
    val neutralBtnSelectStr: String? by lazy {
        builder.neutralBtnSelectStr ?: context.getString(R.string.lib_picker_color__str_custom)
    }

    /*处于 custom 页面的时候显示的 neutral 文案*/
    val neutralBtnCustomStr: String? by lazy {
        builder.neutralBtnCustomStr ?: context.getString(R.string.lib_picker_color__str_back)
    }
    val positiveBtnStr: String? by lazy { builder.positiveBtnStr }

    val defNegativeClickListener: ((dialogInterface: DialogInterface?, which: Int, flag: Any?) -> Unit)? by lazy { builder.defNegativeClickListener }
    val defNeutralClickListener: ((dialogInterface: DialogInterface?, which: Int, flag: Any?) -> Unit)? by lazy { builder.defNeutralClickListener }
    val defPositiveClickListener: ((dialogInterface: DialogInterface?, which: Int, selColor: PickColor?, flag: Any?) -> Unit)? by lazy { builder.defPositiveClickListener }

    val negativeClickListener: ((dialogFrag: PickColorDialogFrag, btnNegative: Button, flag: Any?) -> Unit)? by lazy { builder.negativeClickListener }
    val neutralClickListener: ((dialogFrag: PickColorDialogFrag, btnNeutral: Button, flag: Any?) -> Unit)? by lazy {
        builder.neutralClickListener ?: let {
            return@let { dialogFrag: PickColorDialogFrag, btnNeutral: Button, flag: Any? ->

                when (pickColorCurPanel) {
                    PickColorDefPanel.PANEL_SELECT -> fromSelToCustomLayout(btnNeutral)
                    PickColorDefPanel.PANEL_CUSTOM -> fromCustomToSelLayout(btnNeutral)
                }

            }
        }
    }

    val positiveClickListener: ((dialogFrag: PickColorDialogFrag, btnPositive: Button, flag: Any?) -> Unit)? by lazy { builder.positiveClickListener }

    val onShowListener: (() -> Unit)? by lazy { builder.onShowListener }
    val onDismissListener: (() -> Unit)? by lazy { builder.onDismissListener }

    //设置了这三个接口就可以在颜色选择阶段做出某些响应了
    var itemClickListener: (() -> Unit)? = null
    var itemLongClickListener: (() -> Unit)? = null
    var customColorChangeListener: (() -> Unit)? = null


    /**从颜色选择面板到自定义颜色面板*/
    private fun fromSelToCustomLayout(btnNeutralParam: Button?) {

        (gridView.parent as ViewGroup?)?.let { viewGroup ->
            viewGroup.removeView(gridView)

            val selColorResult = pickColorAdapter.getSelColor()

            context.let { context ->
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

}

/**调色板初始状态*/
enum class PickColorDefPanel {
    PANEL_SELECT,
    PANEL_CUSTOM
}
