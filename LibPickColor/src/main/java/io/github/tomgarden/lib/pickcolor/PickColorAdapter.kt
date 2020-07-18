package io.github.tomgarden.lib.pickcolor

import android.content.Context
import android.graphics.Color
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
class PickColorAdapter(private val context: Context) : BaseAdapter(), AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {

    constructor(context: Context, transferColorStr: String) : this(context) {
        reset(transferColorStr)
    }

    val colorPalette = ColorPalette()

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

    /**通过 show 传入的 color 。存放着当前正设置着的颜色，如果本 Dialog 选出新值，这个值在下次打开Dialog 的时候就会被刚选的新值替代
     *
     * 传入的颜色字符串有两种格式
     * 1. #123456 或者 #12345678
     * 2. md_purple_50
     * */
    var transferColorStr: String = Utils.DEF_COLOR
        set(value) {
            if (field == value) return
            field = value

            this.transferColorIsCustom = !field.startsWith("md_")
            this.transferColorIsTopColor = field.endsWith("_500")
            if (transferColorIsCustom) {
                this.transferColorInt = Color.parseColor("#$field")
                field = field.substring(1)
            } else {
                val id = Utils.getColorResId(context, field)
                this.transferColorInt = ContextCompat.getColor(context, id)
            }

        }
    /**通过 show 传入的 color 。存放着当前正设置着的颜色，如果本 Dialog 选出新值，这个值在下次打开Dialog 的时候就会被刚选的新值替代*/
    private var transferColorInt: Int = 0
    /**通过 show 传入的 color 是否自定义 color*/
    private var transferColorIsCustom = false
    /**通过 show 传入的 color 是否 top color*/
    private var transferColorIsTopColor = false

    override fun getCount(): Int {
        return if (winIsTopPanel) this.colorPalette.COLORS_TOP_SORT.size
        else this.colorPalette.COLORS_SUB_SORT[positionIndex[0]].size + 1//多一个放返回按钮
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
                ?: let { LayoutInflater.from(context).inflate(R.layout.color_select_item, null) as ImageView }


        /**当前 Item 颜色 */
        var color = 0
        var imageRes: Int = R.drawable.ic_circle_black_24dp

        if (!winIsTopPanel && position == 0) {

            imageRes = R.drawable.ic_back_black_24dp
            color = ContextCompat.getColor(context, colorPalette.COLORS_TOP_SORT[positionIndex[0]])

        } else {

            if (!winIsTopPanel) {
                position--
            }

            //下面的 if-else 判断逻辑需要做进一步的理解和重构

            if (winIsTopPanel && !isTopSelected()) {
                //当前在一级颜色选取 且 一级选取尚未选取    >  按照传入的默认值 str 显示 color

                color = getIntColorByPosition(position)
                if (transferColorIsTopColor && transferColorInt == color) {
                    imageRes = R.drawable.ic_ring_black_24dp
                }

            } else if (winIsTopPanel && isTopSelected()) {
                //当前在一级颜色选取 且 一级选取已经选取

                color = getIntColorByPosition(position)
                if (position == positionIndex[0]) {
                    imageRes = R.drawable.ic_ring_black_24dp
                }

            } else if (!winIsTopPanel && isSubSelected()) {
                //当前处于二级选区，二级已经选取

                color = getIntColorByPosition(position)
                if (position == positionIndex[1]) {
                    imageRes = R.drawable.ic_ring_black_24dp
                }

            } else if (!winIsTopPanel && !isSubSelected()) {
                //当前处于二级选区，二级未选取

                color = getIntColorByPosition(position)
                if (transferColorInt == color || getSelColor()?.getDexColor(context) == color) {
                    imageRes = R.drawable.ic_ring_black_24dp
                }

            } else if (!isTopSelected() && isSubSelected()) {
                //一级未点击，二级点击了
                throw RuntimeException("Logie ERR")
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
     * 获取当前颜色
     */
    fun getSelColor(): PickColorResult? {

        val selColorResult: PickColorResult?

        if (winIsTopPanel && !isTopSelected()) {
            /*一级选区 , 尚未做出选择*/

            selColorResult =
                    //如果在选择布局尚未做选取动作；如果传入选择布局的颜色为自定义颜色而非颜色名称
                    if (transferColorIsCustom) {
                        PickColorResult(transferColorStr)
                    } else {
                        val colorId = Utils.getColorResId(context, transferColorStr)
                        PickColorResult(colorId)
                    }


        } else if (winIsTopPanel && isTopSelected()) {
            /*一级选取 , 已经做出选择*/

            val colorId = this.colorPalette.COLORS_TOP_SORT[positionIndex[0]]
            selColorResult = PickColorResult(colorId)

        } else if (!winIsTopPanel && !isSubSelected()) {
            /*二级选区 , 尚未做出选择*/

            val colorId = this.colorPalette.COLORS_TOP_SORT[positionIndex[0]]
            selColorResult = PickColorResult(colorId)

        } else if (!winIsTopPanel && isSubSelected()) {
            /*二级选区 , 已经做出选择*/

            val colorId = this.colorPalette.COLORS_SUB_SORT[positionIndex[0]][positionIndex[1]]
            selColorResult = PickColorResult(colorId)

        } else {

            selColorResult = null
            throw RuntimeException("Logic ERR")

        }

        return selColorResult
    }

    fun reset(transferColorStr: String) {
        this.transferColorStr = transferColorStr

        winIsTopPanel = true

        positionIndex[0] = -1
        positionIndex[1] = -1

        notifyDataSetChanged()
    }

    //region GirdView.ClickListener
    /**
     * 长按 Toast 当前选中的颜色
     *
     * AdapterView.OnItemLongClickListener
     */
    override fun onItemLongClick(
            parent: AdapterView<*>, view: View, position: Int, id: Long): Boolean {
        var position = position

        val colorID: Int =
                if (winIsTopPanel) {
                    colorPalette.COLORS_TOP_SORT[position]
                } else {
                    position--
                    if (position < 0) return true
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
        var position = position
        //进退 按钮
        if (winIsTopPanel) {
            positionIndex[0] = position
            winIsTopPanel = false

            Toast.makeText(context, "切换选区样式", Toast.LENGTH_LONG).show()

        } else {
            position--
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