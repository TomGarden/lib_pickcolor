package io.github.tomgarden.lib.pickcolor

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import java.util.*


/**
 * describe : 选择颜色的选择结果
 *
 * <p>author : tom
 * <p>time : 20-2-16 18:43
 * <p>GitHub : https://github.com/TomGarden
 */
class PickColorResult {
    val isCustomColor: Boolean
    val colorID: Int         //如果是选中资源文件中预留的颜色，此值有意义
    /**如果是自定义颜色此值有意义  此值, 是剔除了 '#' 符号的*/
    val colorHexStr: String?

    private constructor(isCustomColor: Boolean, colorID: Int, colorHexStr: String?) {
        this.isCustomColor = isCustomColor
        this.colorID = colorID
        this.colorHexStr = colorHexStr?.let { Utils.formatHexColorStr(colorHexStr) }
    }

    constructor(colorID: Int) : this(false, colorID, null)

    constructor(colorHexStr: String) : this(true, -1, colorHexStr)

    fun getResult(context: Context): String? {
        if (isCustomColor) {
            return colorHexStr
        } else {

            val colorResName = context.resources.getResourceName(colorID)
            return colorResName.substring(colorResName.indexOf('/') + 1)
        }
    }

    /**
     * 获取十六进制不带前缀的颜色字符串
     *
     * @param context Context
     * @return String?
     */
    fun getHexColorWithoutPrefix(context: Context): String? {
        if (isCustomColor) {
            return colorHexStr
        } else {
            return String.format(Locale.getDefault(), "%08X", ContextCompat.getColor(context, colorID))
        }
    }

    /**
     * 获取十进制颜色值
     * @param x Any
     * @return Unit
     */
    fun getDexColor(context: Context): Int {
        if (isCustomColor) {
            return Color.parseColor("#${colorHexStr}")
        } else {
            return ContextCompat.getColor(context, colorID)
        }
    }

    fun toString(context: Context): String {
        return String.format(
                "SelColorResult : isCustomColor=%b;\ncolorID=%d[%s][#%s];\ncolorHexStr=#%s;",
                isCustomColor,

                colorID,
                if (isCustomColor) "null" else context.resources?.getResourceName(colorID) ?: "get ColorId's res Name failed!!!",
                if (isCustomColor) "null" else String.format(Locale.getDefault(), "%08X", ContextCompat.getColor(context, colorID)),

                colorHexStr)
    }

    override fun toString(): String {
        return "Please call PickColorResult#toString(context: Context) }"
    }
}