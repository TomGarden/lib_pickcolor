package io.github.tomgarden.lib.pickcolor

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.ScrollView
import androidx.annotation.ColorInt
import androidx.core.view.children
import io.github.tomgarden.lib.log.Logger
import java.util.*

/**
 * describe : null
 *
 * <p>author : tom
 * <p>time : 20-2-16 16:50
 * <p>GitHub : https://github.com/TomGarden
 */
object LibPickerColorUtils {

    val DEF_COLOR = "00000000"

    /*根据资源名字获取资源 ID */
    fun getResourcesId(context: Context, resName: String, resType: String): Int {
        return context.resources.getIdentifier(resName, resType, context.packageName)
    }

    fun getColorResId(context: Context, resName: String): Int {
        return getResourcesId(context, resName, "color")
    }

    /**将输入的颜色字符串修正为不含 '#' 字符的 8 位字符串
     *
     * TODO : 意外格式和字符仍需要做足够的测试
     * */
    fun formatHexColorStr(inputHexColorStr: String): String {

        var hexColorStr = inputHexColorStr.replace("#", "")

        if (hexColorStr.length > 8) {
            val fixHexColorStr = hexColorStr.substring(hexColorStr.length - 8)
            Logger.e("颜色字符串格式 , 修正 : $hexColorStr > $fixHexColorStr")
            hexColorStr = fixHexColorStr
        }

        hexColorStr = String.format(Locale.getDefault(), "%8S", hexColorStr).replace(" ", "F")

        return hexColorStr
    }

    fun colorDexToHex(@ColorInt colorInt: Int): String = String.format(Locale.getDefault(), "%08X", colorInt)

    /*意在替换     fun formatHexColorStr  函数*/
    private fun _formatHexColorStr(inputHexColorStr: String): String {
        val colorStr =
            if (inputHexColorStr.startsWith('#')) {
                inputHexColorStr
            } else {
                "#$inputHexColorStr"
            }

        val intColor = Color.parseColor(colorStr)

        return String.format(Locale.getDefault(), "%08X", intColor)
    }

    fun printViewTree(input: View) {
        //获取跟view
        var view = input.parent
        while (view.parent is View || view.parent is ViewGroup) {
            view = view.parent
        }

        val stringBuilder = StringBuilder()

        fun check(aView: View, line: Int) {
            val ary = CharArray(line * 4)
            Arrays.fill(ary, '-')
            stringBuilder.append("\n")
                .append(String(ary))
                .append(aView.javaClass)
                .append("   ")
                .append(aView.id.toString())
            if (aView is ViewGroup) {
                aView.children.forEach { view ->
                    check(view, line + 1)
                }
            }
        }

        if (view is View) {
            check(view as View, 0)
            Logger.i(stringBuilder.toString())
        } else {
            Logger.i("EMPTY")
        }
    }
}