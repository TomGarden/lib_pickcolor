package io.github.tomgarden.lib.pickcolor

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*

/**
 * 将 Dialog 内逻辑委托出来 , 方便继承不同的 DialogFragment 实现 PickColor
 */
class PickColorDelegate {

    var context: Context? = null

    /*自定义面板逻辑委托*/
    val cclDelegate by lazy { context?.let { context -> ColorCustomLayoutDelegate(context) } }
    val colorCustomLayout by lazy { cclDelegate?.colorCustomLayout }
    val etHexInput by lazy { cclDelegate?.etHexInput }

    /*默认展示的颜色控制面板*/
    var pickColorDefPanel = PickColorDefPanel.PANEL_SELECT

    /*弹窗标题*/
    var title: String? = null

    /*默认颜色*/
    var inputColor: PickColor = PickColor(Utils.DEF_COLOR)

    /*携带的其他数据*/
    var flag: Any? = null

    var negativeBtnStr: String? = null
    var neutralBtnStr: String? = null
    var positiveBtnStr: String? = null

    var defNegativeClickListener:
            ((dialogInterface: DialogInterface?, which: Int, flag: Any?) -> Unit)? = null
    var defNeutralClickListener:
            ((dialogInterface: DialogInterface?, which: Int, flag: Any?) -> Unit)? = null
    var defPositiveClickListener: ((dialogInterface: DialogInterface?, which: Int, selColor: PickColor?, flag: Any?) -> Unit)? =
        null

    var negativeClickListener:
            ((dialogFrag: PickColorDialogFrag, btnNegative: Button, flag: Any?) -> Unit)? = null
    var neutralClickListener: ((dialogFrag: PickColorDialogFrag, btnNeutral: Button, flag: Any?) -> Unit)? =
        { _, btnNeutral, _ ->

            when (btnNeutral.text.toString()) {

                context?.getString(R.string.lib_picker_color__str_back) -> {
                    fromCustomToSelLayout(btnNeutral)
                }

                context?.getString(R.string.lib_picker_color__str_custom) -> {
                    fromSelToCustomLayout(btnNeutral)
                }

                else -> throw RuntimeException("logic err")
            }

        }
    var positiveClickListener:
            ((dialogFrag: PickColorDialogFrag, btnPositive: Button, flag: Any?) -> Unit)? = null

    var onShowListener: (() -> Unit)? = null
    var onDismissListener: (() -> Unit)? = null

    //设置了这三个接口就可以在颜色选择阶段做出某些响应了
    private var itemClickListener: (() -> Unit)? = null
    private var itemLongClickListener: (() -> Unit)? = null
    private var customColorChangeListener: (() -> Unit)? = null


    //region Pick Color layout 颜色选择布局

    val gridView: GridView? by lazy {

        val context = context ?: return@lazy null

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
    val pickColorAdapter: PickColorAdapter? by lazy {
        context?.let {
            PickColorAdapter(it, inputColor)
        }
    }

    //endregion Pick Color layout  一级颜色选择布局


    //region 选取 布局 变换
    /**从颜色选择面板到自定义颜色面板*/
    private fun fromSelToCustomLayout(btnNeutralParam: Button?) {

        (gridView?.parent as ViewGroup?)?.let { viewGroup ->
            viewGroup.removeView(gridView)

            val selColorResult = pickColorAdapter?.getSelColor()

            context?.let { context ->
                colorCustomLayout?.let {
                    etHexInput?.setText(selColorResult?.getHexColorWithoutPrefix(context))
                }
            }

            viewGroup.addView(colorCustomLayout)

            val btnNeutral =
                btnNeutralParam ?: let { aDialog.getButton(DialogInterface.BUTTON_NEUTRAL) }
            btnNeutral.setText(R.string.lib_picker_color__str_back)
        }
    }

    /**从自定义颜色面板到颜色选择面板*/
    private fun fromCustomToSelLayout(btnNeutralParam: Button?) {

        (colorCustomLayout?.parent as ViewGroup?)?.let { viewGroup ->
            viewGroup.removeView(colorCustomLayout)
            viewGroup.addView(gridView)
            val btnNeutral =
                btnNeutralParam ?: let { aDialog.getButton(DialogInterface.BUTTON_NEUTRAL) }
            btnNeutral.setText(R.string.lib_picker_color__str_custom)
        }
    }
    //endregion 选取变换


    //关键 dialog
    val aDialog: AlertDialog by lazy {
        val builder = AlertDialog.Builder(context)

        when (pickColorDefPanel) {
            PickColorDefPanel.PANEL_SELECT -> builder.setView(this.gridView)//默认首次展示一级颜色选区
            PickColorDefPanel.PANEL_CUSTOM -> builder.setView(colorCustomLayout)
        }

        title?.let { builder.setTitle(title) }
        builder
            .setNegativeButton(negativeBtnStr) { dialog, which ->
                defNegativeClickListener?.invoke(dialog, which, flag)
            }
            .setNeutralButton(neutralBtnStr) { dialog, which ->
                defNeutralClickListener?.invoke(dialog, which, flag)
            }
            .setPositiveButton(positiveBtnStr) { dialog, which ->

                var selColor: PickColor? = null

                if (gridView?.parent != null) {//如果在颜色选择布局

                    selColor = pickColorAdapter?.getSelColor()

                } else if (colorCustomLayout?.parent != null) {//如果在颜色自定义布局

                    etHexInput?.text?.let {
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

}

/**调色板初始状态*/
enum class PickColorDefPanel {
    PANEL_SELECT,
    PANEL_CUSTOM
}