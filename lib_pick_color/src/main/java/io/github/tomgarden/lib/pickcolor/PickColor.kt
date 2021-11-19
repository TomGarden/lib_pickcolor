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
 * TODO : 补充: 当前此类的初始化方法需要作出一定的限制 , 保证初始化出来的对象时符合组件内在逻辑的
 *
 * <p>author : tom
 * <p>time : 20-2-16 18:43
 * <p>GitHub : https://github.com/TomGarden
 */
class PickColor private constructor(
    /**
     * 需要将输入参数做出合理的规范 , 并且将修正结果和原因输出到控制台
     *
     * TODO: [colorID] 应该是可以被本库识别的 ID , 否则转换为自定义 [colorHexStr]
     * TODO: [colorHexStr] 应该被修正为不含有 '#' 字符的 8 位(指定)字符
     */
    private val colorID: Int,            /*如果是选中资源文件中预留的颜色，此值有意义*/
    private var colorHexStr: String,     /*如果是自定义颜色此值有意义  此值, 是剔除了 '#' 符号的*/
) {

    init {
        if (colorHexStr.isNotEmpty()) {
            this.colorHexStr = LibPickerColorUtils.formatHexColorStr(colorHexStr)
        } else {
            this.colorHexStr = ""
        }
    }

    /*TODO 暂时注释掉 , 注释掉的内容是更好的梳理颜色转换的方式 , 有时间可以梳理下*/
    /*companion object {
        fun parseColorFromInt(@ColorInt colorDex: Int): PickColor = PickColor(String.format("%08X", colorDex))
    }*/

    constructor(pickColor: PickColor) : this(pickColor.colorID, pickColor.colorHexStr)

    constructor(colorID: Int) : this(colorID, "")

    constructor(colorHexStr: String) : this(-1, colorHexStr)

    /**  @return true : 路径 ; false 资源*/
    fun isCustom(): Boolean {
        return when {
            (colorID == -1 && colorHexStr.isNotEmpty()) -> true
            (colorID != -1 && colorHexStr.isEmpty()) -> false
            else -> throw Throwable("未知异常")
        }
    }

    fun getResult(context: Context): String {
        if (isCustom()) {
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
    fun getHexColorWithoutPrefix(context: Context): String {
        if (isCustom()) {
            return colorHexStr
        } else {
            return String.format(
                Locale.getDefault(), "%08X", ContextCompat.getColor(context, colorID)
            )
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
            return ContextCompat.getColor(context, colorID)
        }
    }

    fun getDrawable(context: Context): ColorDrawable = ColorDrawable(getDexColor(context))

    /** 根据 colorId 获取 styleId */
    fun getStyleIdByColorId(context: Context, themeStyle: ThemeStyleEnum): Int? {
        try {
            if (isCustom()) return null
            val colorResPrefix = "lib_pick_color__md_"
            val entityName = context.resources.getResourceEntryName(colorID)
            if (!entityName.contains(colorResPrefix)) return null
            val colorSubName = entityName.substring(colorResPrefix.length)
            val styleResType = "style"
            val styleResName = "lib_pick_color__${themeStyle.styleKey}_${colorSubName}"
            val styleId = context.resources.getIdentifier(styleResName, styleResType, context.packageName)
            if (styleId == 0) return null/*没有找到对应主题资源*/
            return styleId
        } catch (throwable: Throwable) {
            Logger.e(throwable)
            return null
        }
    }

    fun toString(context: Context): String {

        val stringBuilder = StringBuilder()

        val colorIdStr =
            if (isCustom()) {
                "null"
            } else {
                context.resources?.getResourceName(colorID) ?: "get ColorId's res Name failed!!!"
            }
        val colorIdToHexStr =
            if (isCustom()) {
                "null"
            } else {
                String.format(Locale.getDefault(), "%08X", ContextCompat.getColor(context, colorID))
            }

        return stringBuilder.append("SelColorResult : \n")
            .append("    isCustomColor = ${isCustom()} \n")
            .append("    colorID = ${colorID} [$colorIdStr] [#$colorIdToHexStr] \n")
            .append("    colorHexStr=#$colorHexStr ")
            .toString()
    }

    override fun toString(): String {
        return "Please call PickColor#toString(context: Context) }"
    }

    override fun equals(other: Any?): Boolean {
        return when {
            other !is PickColor -> false
            other.colorID == this.colorID &&
                    other.colorHexStr == this.colorHexStr -> true
            else -> false
        }
    }
}