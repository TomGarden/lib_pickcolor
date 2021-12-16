package io.github.tomgarden.lib.pickcolor

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import io.github.tomgarden.lib.log.Logger
import java.util.*


/**
 * describe : 选择颜色的选择结果
 *
 * <p>author : tom
 * <p>time : 20-2-16 18:43
 * <p>GitHub : https://github.com/TomGarden
 */
class PickColor(
    /**
     * 需要将输入参数做出合理的规范 , 并且将修正结果和原因输出到控制台
     *
     * [colorIdMapResStr] 应该是可以被本库识别的 ID 获取的资源字符串 , 例 : `io.github.tomgarden.tetris_container:color/lib_beaker_simple_contrast_bg`
     * [colorHexStr] 应该被修正为不含有 '#' 字符的 8 位(指定)字符 ; 在 init 完成该动作
     */
    private var colorIdMapResStr: String = "",
    private var colorHexStr: String = "",     /*如果是自定义颜色此值有意义  此值, 是剔除了 '#' 符号的*/
) {

    companion object {
        private val styleResType = "style"
        private val colorResType = "color"
    }

    init {
        if (colorHexStr.isNotEmpty()) {
            this.colorHexStr = LibPickerColorUtils.formatHexColorStr(colorHexStr)
        } else {
            this.colorHexStr = ""
        }
    }

    constructor(context: Context, colorID: Int, colorHexStr: String) : this(context.resources.getResourceEntryName(colorID), colorHexStr)
    constructor(colorHexStr: String) : this("", colorHexStr)
    constructor(context: Context, colorID: Int) : this(context, colorID, "")
    constructor(pickColor: PickColor) : this(pickColor.colorIdMapResStr, pickColor.colorHexStr)


    /**  @return true : 路径 ; false 资源*/
    fun isCustom(): Boolean {
        return when {
            (colorIdMapResStr.isEmpty() && colorHexStr.isNotEmpty()) -> true
            (colorIdMapResStr.isNotEmpty() && colorHexStr.isEmpty()) -> false
            (colorIdMapResStr.isEmpty() && colorHexStr.isEmpty()) -> {
                val colorHex = String.format(Locale.getDefault(), "%08X", Color.TRANSPARENT)
                colorHexStr = LibPickerColorUtils.formatHexColorStr(colorHex)
                return true
            }
            (colorIdMapResStr.isNotEmpty() && colorHexStr.isNotEmpty()) -> {
                colorHexStr = ""
                return false
            }
            else -> throw Throwable("未知异常 , 需要跟进")
        }
    }

    /**
     * 获取十六进制不带前缀的颜色字符串
     *
     * @param context Context
     * @return String?
     */
    fun getHexColorWithoutPrefix(context: Context): String {
        if (isCustom()) {
            return colorHexStr
        } else {
            return String.format(Locale.getDefault(), "%08X", getDexColor(context))
        }
    }

    /**
     * 获取十进制颜色值
     * @param x Any
     * @return Unit
     */
    fun getDexColor(context: Context): Int {
        if (isCustom()) {
            return Color.parseColor("#${colorHexStr}")
        } else {
            val colorId = colorIdMapResStr.getColorIdByResStr(context)
            return try {
                ContextCompat.getColor(context, colorId)
            } catch (throwable: Throwable) {
                Logger.e(throwable, true, "获取资源颜色失败 , 将颜色用 透明色 替代")
                Color.TRANSPARENT
            }
        }
    }

    fun getDrawable(context: Context): ColorDrawable = ColorDrawable(getDexColor(context))

    /*根据颜色映射名返回颜色 ID , 如果没有找到对应的 id 返回 0 */
    fun String.getColorIdByResStr(context: Context) = context.resources.getIdentifier(this, colorResType, context.packageName)

    /** 根据 colorId 获取 styleId */
    fun getStyleIdByColorId(context: Context, themeStyle: ThemeStyleEnum): Int? {
        try {
            if (isCustom()) return null
            val colorResPrefix = "lib_pick_color__md_"
            val entityName = colorIdMapResStr
            if (!entityName.contains(colorResPrefix)) return null
            val colorSubName = entityName.substring(colorResPrefix.length)
            val styleResName = "lib_pick_color__${themeStyle.styleKey}_${colorSubName}"
            val styleId = context.resources.getIdentifier(styleResName, styleResType, context.packageName)
            if (styleId == 0) return null/*没有找到对应主题资源*/
            return styleId
        } catch (throwable: Throwable) {
            Logger.e(throwable, true, "根据颜色 名 获取 style 失败")
            return null
        }
    }

    fun toString(context: Context): String {

        val stringBuilder = StringBuilder()

        val colorIdStr = colorIdMapResStr.getColorIdByResStr(context)
        val colorIdToHexStr = String.format(Locale.getDefault(), "%08X", getDexColor(context))

        return stringBuilder.append("SelColorResult : \n")
            .append("    isCustomColor = ${isCustom()} \n")
            .append("    colorIdMapResStr = ${colorIdMapResStr} [$colorIdStr] [#$colorIdToHexStr] \n")
            .append("    colorHexStr=#$colorHexStr ")
            .toString()
    }

    override fun toString(): String {
        return "Please call PickColor#toString(context: Context) }"
    }

    override fun equals(other: Any?): Boolean {
        return when {
            other !is PickColor -> false
            other.colorIdMapResStr !== this.colorIdMapResStr -> false
            other.colorHexStr != this.colorHexStr -> false
            else -> true
        }
    }
}