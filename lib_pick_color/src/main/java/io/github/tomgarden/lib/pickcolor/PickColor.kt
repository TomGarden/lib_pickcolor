package io.github.tomgarden.lib.pickcolor

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import java.lang.StringBuilder
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
    private var colorHexStr: String     /*如果是自定义颜色此值有意义  此值, 是剔除了 '#' 符号的*/
) {

    init {
        if (colorHexStr.isNotEmpty()) {
            this.colorHexStr = LibPickerColorUtils.formatHexColorStr(colorHexStr)
        } else {
            this.colorHexStr = ""
        }
    }


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
}