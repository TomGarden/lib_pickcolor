package io.github.tomgarden.lib.pickcolor

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat

/**
 * describe : null
 *
 * <p>author : tom
 * <p>time : 20-2-16 11:57
 * <p>GitHub : https://github.com/TomGarden
 */
class PickColorAdapter(
    private val context: Context,
    /*必传参数 , 可以在逻辑上层设置一个默认值 , 降低使用控件的复杂度*/
    private var inputColor: PickColor
) : BaseAdapter(), AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {

    private val colorPalette = ColorPalette()

    /**
     * true , 当前颜色选择窗口是一级颜色选择窗口
     * false, 当前颜色选择窗口是二级颜色选择窗口
     *
     * 之所以创建 winIsTopPanel 这个参数而不是通过查看 positionIndex 来确定一/二级面板, 是因为:
     * * 存在两种情况状态相同,导致无法判断当前处于那个面板
     *      * (状态一) : 一级 面板 做出选择进入二级面板 后 的状态
     *      * (状态二) : 从状态已点击所上角返回按钮停留在 一级 面板的状态
     */
    var winIsTopPanel = true

    /**
     * 颜色数组(colorPalette)是一个二维数组
     *
     * positionIndex[0] : 一级面板选择的颜色值 . -1 表示尚未做出选择
     * positionIndex[1] : 二级面板选择的颜色值 . -1 表示尚未做出选择
     */
    val positionIndex = intArrayOf(-1, -1)


    override fun getCount(): Int {
        return if (winIsTopPanel) {
            this.colorPalette.COLORS_TOP_SORT.size
        } else {
            this.colorPalette.COLORS_SUB_SORT[positionIndex[0]].size + 1/*多一个放返回按钮*/
        }
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var position = position

        val imageView: ImageView = convertView?.let { it as ImageView }
            ?: let {
                LayoutInflater.from(context).inflate(R.layout.color_select_item, null) as ImageView
            }


        /**当前正在渲染的 Item 颜色 */
        var color = getIntColorByPosition(position)
        /*当前正在渲染的 Item 的样式*/
        val imageRes: Int


        //逻辑重构
        if (winIsTopPanel) {
            /*一级选区*/

            if (isTopSelected()) {

                /*一级选区 , 已经做出选择*/
                if (position == positionIndex[0]) {
                    imageRes = R.drawable.ic_ring_black_24dp
                } else {
                    imageRes = R.drawable.ic_circle_black_24dp
                }

            } else {

                /*一级选区 , 尚未做出选择*/
                if (inputColor.getDexColor(context) == color) {
                    imageRes = R.drawable.ic_ring_black_24dp
                } else {
                    imageRes = R.drawable.ic_circle_black_24dp
                }

            }

        } else {
            /*二级选区*/

            val position = position - 1

            if (position == -1) {
                /*二级选区 , 返回按钮 , 应该和一级选区中选中的颜色相同*/

                imageRes = R.drawable.ic_back_black_24dp
                color =
                    ContextCompat.getColor(context, colorPalette.COLORS_TOP_SORT[positionIndex[0]])

            } else {
                /*二级选区 , 非返回按钮 , 颜色按钮*/

                if (isSubSelected()) {

                    /*二级选区 , 已经做出选择*/
                    if (position == positionIndex[1]) {
                        /*二级选区 , 已经做出选择 , 当前渲染的就是选中项*/
                        imageRes = R.drawable.ic_ring_black_24dp
                    } else {
                        imageRes = R.drawable.ic_circle_black_24dp
                    }

                } else {

                    /*二级选区 , 尚未做出选择*/
                    if (getIntColorByPosition(position) == color) {
                        /*二级选区 , 尚未作出选择 , 当前渲染的颜色可能是一级选区中选中的颜色*/
                        imageRes = R.drawable.ic_ring_black_24dp
                    } else {
                        imageRes = R.drawable.ic_circle_black_24dp
                    }
                }
            }
        }

        imageView.setImageResource(imageRes)
        imageView.setColorFilter(color)

        return imageView
    }

    /**
     * 当前一级选是否完成选择了
     * @return Boolean  true 完成选择了 ; 否则 false
     */
    private fun isTopSelected(): Boolean = positionIndex[0] != -1

    /**
     * 当前二级选是否完成选择了
     * @return Boolean  true 完成选择了 ; 否则 false
     */
    private fun isSubSelected(): Boolean = positionIndex[1] != -1

    /**
     * 根据 Position 获取颜色值
     * @param position Int 当处于 二级面板的时候 传入的 position 已经取消了二级面板的返回按钮
     * @return Int
     */
    private fun getIntColorByPosition(position: Int): Int {
        val colorId =
            if (winIsTopPanel) {
                /*一级选区*/
                colorPalette.COLORS_TOP_SORT[position]
            } else {
                /*二级选取*/
                colorPalette.COLORS_SUB_SORT[positionIndex[0]][position]
            }

        return ContextCompat.getColor(context, colorId)
    }

    /**
     * 获取当前颜色选择器的颜色 , 如果用户通过操作选中了某个颜色 , 即返回这个选中的颜色 , 否则选择默认颜色
     */
    fun getSelColor(): PickColor {

        val selColor: PickColor

        if (winIsTopPanel) {
            /*一级选区*/

            if (isTopSelected()) {
                /*一级选区 , 已经做出选择*/
                val colorId = this.colorPalette.COLORS_TOP_SORT[positionIndex[0]]
                selColor = PickColor(colorId)
            } else {
                /*一级选区 , 尚未作出选择*/
                selColor = PickColor(inputColor)
            }

        } else {
            /*二级选区*/

            if (isSubSelected()) {
                /*二级选区 , 已经走做出选择*/
                val colorId = this.colorPalette.COLORS_SUB_SORT[positionIndex[0]][positionIndex[1]]
                selColor = PickColor(colorId)
            } else {
                /*二级选区 , 尚未作出选择*/
                val colorId = this.colorPalette.COLORS_TOP_SORT[positionIndex[0]]
                selColor = PickColor(colorId)
            }

        }

        return selColor
    }


    //region GirdView.ClickListener
    /**
     * 长按 Toast 当前选中的颜色
     *
     * AdapterView.OnItemLongClickListener
     */
    override fun onItemLongClick(
        parent: AdapterView<*>, view: View, position: Int, id: Long
    ): Boolean {


        val colorID: Int =
            if (winIsTopPanel) {
                colorPalette.COLORS_TOP_SORT[position]
            } else {
                val position = position - 1
                if (position < 0) {
                    return true
                }
                colorPalette.COLORS_SUB_SORT[positionIndex[0]][position]
            }

        val colorDexInt = ContextCompat.getColor(context, colorID)
        val colorHexStr = String.format("# %08X", colorDexInt)

        Toast.makeText(context, colorHexStr, Toast.LENGTH_SHORT).show()
        return true
    }

    /**
     * AdapterView.OnItemClickListener
     */
    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        //进退 按钮
        if (winIsTopPanel) {
            positionIndex[0] = position
            winIsTopPanel = false

            Toast.makeText(context, "切换选区样式", Toast.LENGTH_LONG).show()

        } else {
            val position = position - 1

            if (position == -1) {
                winIsTopPanel = true
                positionIndex[1] = -1
            } else {
                positionIndex[1] = position
            }
        }
        notifyDataSetChanged()
    }
    //endregion GirdView.ClickListener


}