package io.github.tomgarden.lib.pickcolor

import android.content.Context
import io.github.tomgarden.lib.log.Logger
import java.util.*

/**
 * describe : null
 *
 * <p>author : tom
 * <p>time : 20-2-16 16:50
 * <p>GitHub : https://github.com/TomGarden
 */
object Utils {

    val DEF_COLOR = "00000000"

    fun getResourcesId(context: Context, resName: String, resType: String): Int {
        return context.resources.getIdentifier(resName, resType, context.packageName)
    }

    fun getColorResId(context: Context, resName: String): Int {
        return getResourcesId(context, resName, "color")
    }

    /**输出的颜色字符串不含 '#' */
    fun formatHexColorStr(hexColorStr: String): String {

        var hexColorStr = hexColorStr.replace("#", "")

        if (hexColorStr.length > 8) {
            Logger.e("颜色字符串格式异常 , 已经修正")
            hexColorStr = hexColorStr.substring(hexColorStr.length - 8)
        }

        hexColorStr = String.format(Locale.getDefault(), "%8S", hexColorStr).replace(" ", "F")

        return hexColorStr
    }
}