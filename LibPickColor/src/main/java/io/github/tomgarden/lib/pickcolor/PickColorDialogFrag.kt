package io.github.tomgarden.lib.pickcolor

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import io.github.tomgarden.lib.log.Logger
import java.util.*


/**
 * describe :使用本类需要注意一点，在本类中使用的颜色十六进制值，只有在交付给 Color 之前才会添加 # 。
 *
 *
 *onAttach
 *onCreate
 *onCreateDialog
 *onCreateView
 *onActivityCreated
 *
 * author : tom
 *
 * time : 18-9-14 11:28
 *
 * Git : https://github.com/TomGarden
 */
class PickColorDialogFrag : DialogFragment(), DialogInterface.OnShowListener, SeekBar.OnSeekBarChangeListener {

    /**标记是否重置 UI ,
     * 所谓重置: 例如 有两个 按钮 可以触发 这个 Dialog , 第一个按钮 在 自定义页面 关闭了 dialog
     * 第二个 按钮希望 打开 Dialog 的时候就像重新创建了一个 Dialog 一样 , 那么通过重置就可以完成 .
     *
     * 默认的 , 重新设置 title / transferColorStr / flag 会触发重置动作 .
     * 如果希望仅仅重置上述参数 而不 重置 可以设置参数完成后 单独设置 reset
     * */
    private var reset: Boolean = false
    private var title: String? = null
    private var transferColorStr: String = Utils.DEF_COLOR
    private var flag: Any? = null

    //region title / transferColorStr / flag  setter

    fun setTitle(title: String?, reset: Boolean = true): PickColorDialogFrag {
        this.title = title
        this.reset = true
        return this
    }

    fun setTransferColorStr(transferColorStr: String?, reset: Boolean = true): PickColorDialogFrag {
        this.transferColorStr = transferColorStr?.let { Utils.formatHexColorStr(transferColorStr) } ?: Utils.DEF_COLOR
        this.reset = true
        return this
    }

    fun setFlag(flag: Any?, reset: Boolean = true): PickColorDialogFrag {
        this.flag = flag
        this.reset = true
        return this
    }

    fun reset(reset: Boolean): PickColorDialogFrag {
        this.reset = reset
        return this
    }

    //endregion title / transferColorStr / flag  setter


    private var negativeBtnStr: String? = null
    private var neutralBtnStr: String? = null
    private var positiveBtnStr: String? = null

    private var defNegativeClickListener: ((dialogInterface: DialogInterface?, which: Int, flag: Any?) -> Unit)? = null
    private var defNeutralClickListener: ((dialogInterface: DialogInterface?, which: Int, flag: Any?) -> Unit)? = null
    private var defPositiveClickListener: ((dialogInterface: DialogInterface?, which: Int, selColorResult: PickColorResult?, flag: Any?) -> Unit)? = null

    private var negativeClickListener: ((dialogFrag: PickColorDialogFrag, btnNegative: Button, flag: Any?) -> Unit)? = null
    private var neutralClickListener: ((dialogFrag: PickColorDialogFrag, btnNeutral: Button, flag: Any?) -> Unit)? = { _, btnNeutral, _ ->

        when (btnNeutral.text.toString()) {

            resources.getString(R.string.back) -> fromCustomToSelLayout(btnNeutral)

            resources.getString(R.string.custom) -> fromSelToCustomLayout(btnNeutral)

            else -> throw RuntimeException("logic err")
        }

    }
    private var positiveClickListener: ((dialogFrag: PickColorDialogFrag, btnPositive: Button, flag: Any?) -> Unit)? = null

    private var onShowListener: (() -> Unit)? = null
    private var onDismissListener: (() -> Unit)? = null

    //设置了这三个接口就可以在颜色选择阶段做出某些响应了
    private var itemClickListener:(()->Unit)? = null
    private var itemLongClickListener:(()->Unit)? = null
    private var customColorChangeListener:(()->Unit)? = null


    //region btn str / listener setter

    fun setDefNegativeClickListener(
            negativeBtnStr: String,
            defNegativeClickListener: ((dialogInterface: DialogInterface?, which: Int, flag: Any?) -> Unit)?)
            : PickColorDialogFrag {
        this.negativeBtnStr = negativeBtnStr
        this.defNegativeClickListener = defNegativeClickListener
        return this
    }

    fun setDefNeutralClickListener(
            neutralBtnStr: String,
            defNeutralClickListener: ((dialogInterface: DialogInterface?, which: Int, flag: Any?) -> Unit)?)
            : PickColorDialogFrag {
        this.neutralBtnStr = neutralBtnStr
        this.defNeutralClickListener = defNeutralClickListener
        return this
    }

    fun setDefPositiveClickListener(
            positiveBtnStr: String,
            defPositiveClickListener: ((dialogInterface: DialogInterface?, which: Int, selColorResult: PickColorResult?, flag: Any?) -> Unit)?)
            : PickColorDialogFrag {
        this.positiveBtnStr = positiveBtnStr
        this.defPositiveClickListener = defPositiveClickListener
        return this
    }


    fun setNegativeClickListener(
            negativeBtnStr: String,
            negativeClickListener: ((dialogFrag: PickColorDialogFrag, btnNegative: Button, flag: Any?) -> Unit)?)
            : PickColorDialogFrag {
        this.negativeBtnStr = negativeBtnStr
        this.negativeClickListener = negativeClickListener
        return this
    }

    fun setNeutralClickListener(
            neutralBtnStr: String,
            neutralClickListener: ((dialogFrag: PickColorDialogFrag, btnNeutral: Button, flag: Any?) -> Unit)?)
            : PickColorDialogFrag {
        this.neutralBtnStr = neutralBtnStr
        this.neutralClickListener = neutralClickListener
        return this
    }

    fun setPositiveClickListener(
            positiveBtnStr: String,
            positiveClickListener: ((dialogFrag: PickColorDialogFrag, btnPositive: Button, flag: Any?) -> Unit)?)
            : PickColorDialogFrag {
        this.positiveBtnStr = positiveBtnStr
        this.positiveClickListener = positiveClickListener
        return this
    }


    fun setOnShowListener(onShowListener: (() -> Unit)?): PickColorDialogFrag {
        this.onShowListener = onShowListener
        return this
    }

    fun setOnDismissListener(onDismissListener: (() -> Unit)?): PickColorDialogFrag {
        this.onDismissListener = onDismissListener
        return this
    }

    //endregion btn str / listener setter


    //region Pick Color layout 颜色选择布局

    val gridView: GridView? by lazy {

        val context = context?.let { it } ?: return@lazy null

        val result = LayoutInflater.from(context).inflate(R.layout.color_selector, null) as GridView

        if (this.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //横屏
            result.numColumns = 5
        } else if (this.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            //竖屏
            result.numColumns = 4
        }
        result.adapter = this.pickColorAdapter
        result.onItemClickListener = this.pickColorAdapter
        result.onItemLongClickListener = this.pickColorAdapter

//        result.onItemClickListener = AdapterView.OnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
//
//            pickColorAdapter?.let { pickColorAdapter ->
//
//                var position = position
//                //进退 按钮
//                if (winIsTopSel) {
//                    pickColorAdapter.positionIndex[0] = position
//                    winIsTopSel = false
//
//                } else {
//                    position--
//                    if (position == -1) {
//                        winIsTopSel = true
//                        pickColorAdapter.positionIndex[1] = -1
//
//                    } else {
//                        pickColorAdapter.positionIndex[1] = position
//                    }
//                }
//                pickColorAdapter.notifyDataSetChanged()
//            }
//        }
//        result.onItemLongClickListener = AdapterView.OnItemLongClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
//
//            pickColorAdapter?.let { pickColorAdapter ->
//                var position = position
//
//                val colorID: Int =
//                        if (winIsTopSel) {
//                            pickColorAdapter.colorPalette.COLORS_TOP_SORT[position]
//                        } else {
//                            position--
//                            pickColorAdapter.colorPalette.COLORS_SUB_SORT[pickColorAdapter.positionIndex[0]][position]
//                        }
//
//                val colorDexInt = ContextCompat.getColor(context, colorID)
//                val colorHexStr = String.format("# %08X", colorDexInt)
//
//                Toast.makeText(context, colorHexStr, Toast.LENGTH_SHORT).show()
//
//                true
//            }
//
//            false
//        }

        result
    }
    val pickColorAdapter: PickColorAdapter? by lazy {
        context?.let {
            val adapter = PickColorAdapter(it, transferColorStr)

            adapter
        }
    }

    //endregion Pick Color layout  一级颜色选择布局

    //region Custom Color layout  自定义颜色布局

    val colorCustomLayout: ViewGroup? by lazy {

        val resultViewGroup = context?.let { LayoutInflater.from(context).inflate(R.layout.color_custom, null) as ViewGroup }

        resultViewGroup?.let { resultViewGroup ->
            this.viewIndicator = resultViewGroup.findViewById(R.id.view_indicator)

            this.etHexInput = resultViewGroup.findViewById(R.id.et_hex_input)

            this.sbA = resultViewGroup.findViewById(R.id.sb_a)
            this.sbR = resultViewGroup.findViewById(R.id.sb_r)
            this.sbG = resultViewGroup.findViewById(R.id.sb_g)
            this.sbB = resultViewGroup.findViewById(R.id.sb_b)

            this.etAValueD = resultViewGroup.findViewById(R.id.et_a_value_decimal)
            this.etRValueD = resultViewGroup.findViewById(R.id.et_r_value_decimal)
            this.etGValueD = resultViewGroup.findViewById(R.id.et_g_value_decimal)
            this.etBValueD = resultViewGroup.findViewById(R.id.et_b_value_decimal)

            this.etAValueH = resultViewGroup.findViewById(R.id.et_a_value_hex)
            this.etRValueH = resultViewGroup.findViewById(R.id.et_r_value_hex)
            this.etGValueH = resultViewGroup.findViewById(R.id.et_g_value_hex)
            this.etBValueH = resultViewGroup.findViewById(R.id.et_b_value_hex)


            this.sbA?.setOnSeekBarChangeListener(this)
            this.sbR?.setOnSeekBarChangeListener(this)
            this.sbG?.setOnSeekBarChangeListener(this)
            this.sbB?.setOnSeekBarChangeListener(this)

            this.etAValueD?.addTextChangedListener(object : TextWatcher {
                /**联动事件发起者 */
                private var beforeChangedText: String? = null

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    if (authorView == null) {
                        authorView = etAValueD
                    }
                    this.beforeChangedText = s?.toString()
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

                override fun afterTextChanged(s: Editable?) {
                    onChangedText(etAValueD, this.beforeChangedText, s/*this.onChangedText*/)
                    if (authorView === etAValueD) {
                        authorView = null
                    }
                }
            })
            this.etRValueD?.addTextChangedListener(object : TextWatcher {
                /**联动事件发起者 */
                private var beforeChangedText: String? = null

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    if (authorView == null) {
                        authorView = etRValueD
                    }
                    this.beforeChangedText = s?.toString()
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

                override fun afterTextChanged(s: Editable?) {
                    onChangedText(etRValueD, this.beforeChangedText, s/*this.onChangedText*/)
                    if (authorView === etRValueD) {
                        authorView = null
                    }
                }
            })
            this.etGValueD?.addTextChangedListener(object : TextWatcher {
                /**联动事件发起者 */
                private var beforeChangedText: String? = null

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    if (authorView == null) {
                        authorView = etGValueD
                    }
                    this.beforeChangedText = s?.toString()
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

                override fun afterTextChanged(s: Editable?) {
                    onChangedText(etGValueD, this.beforeChangedText, s/*this.onChangedText*/)
                    if (authorView === etGValueD) {
                        authorView = null
                    }
                }
            })
            this.etBValueD?.addTextChangedListener(object : TextWatcher {
                /**联动事件发起者 */
                private var beforeChangedText: String? = null

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    if (authorView == null) {
                        authorView = etBValueD
                    }
                    this.beforeChangedText = s?.toString()
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

                override fun afterTextChanged(s: Editable?) {
                    onChangedText(etBValueD, this.beforeChangedText, s/*this.onChangedText*/)
                    if (authorView === etBValueD) {
                        authorView = null
                    }
                }
            })

            this.etAValueH?.addTextChangedListener(object : TextWatcher {
                /**联动事件发起者 */
                private var beforeChangedText: String? = null

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    if (authorView == null) {
                        authorView = etAValueH
                    }
                    this.beforeChangedText = s?.toString()
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

                override fun afterTextChanged(s: Editable?) {
                    onChangedText(etAValueH, this.beforeChangedText, s/*this.onChangedText*/)
                    if (authorView === etAValueH) {
                        authorView = null
                    }
                }
            })
            this.etRValueH?.addTextChangedListener(object : TextWatcher {
                /**联动事件发起者 */
                private var beforeChangedText: String? = null

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    if (authorView == null) {
                        authorView = etRValueH
                    }
                    this.beforeChangedText = s?.toString()
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

                override fun afterTextChanged(s: Editable?) {
                    onChangedText(etRValueH, this.beforeChangedText, s/*this.onChangedText*/)
                    if (authorView === etRValueH) {
                        authorView = null
                    }
                }
            })
            this.etGValueH?.addTextChangedListener(object : TextWatcher {
                /**联动事件发起者 */
                private var beforeChangedText: String? = null

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    if (authorView == null) {
                        authorView = etGValueH
                    }
                    this.beforeChangedText = s?.toString()
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

                override fun afterTextChanged(s: Editable?) {
                    onChangedText(etGValueH, this.beforeChangedText, s/*this.onChangedText*/)
                    if (authorView === etGValueH) {
                        authorView = null
                    }
                }
            })
            this.etBValueH?.addTextChangedListener(object : TextWatcher {
                /**联动事件发起者 */
                private var beforeChangedText: String? = null

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    if (authorView == null) {
                        authorView = etBValueH
                    }
                    this.beforeChangedText = s?.toString()
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

                override fun afterTextChanged(s: Editable?) {
                    onChangedText(etBValueH, this.beforeChangedText, s/*this.onChangedText*/)
                    if (authorView === etBValueH) {
                        authorView = null
                    }
                }
            })

            this.etHexInput?.addTextChangedListener(object : TextWatcher {
                /**联动事件发起者 */
                private var beforeChangedText: String? = null

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    if (authorView == null) {
                        authorView = etHexInput
                    }
                    this.beforeChangedText = s?.toString()
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

                override fun afterTextChanged(s: Editable?) {
                    onChangedText(etHexInput, this.beforeChangedText, s/*this.onChangedText*/)
                    if (authorView === etHexInput) {
                        authorView = null
                    }
                }
            })

        }

        resultViewGroup
    }

    private var viewIndicator: View? = null
    private var etHexInput: EditText? = null
    private var sbA: SeekBar? = null
    private var sbR: SeekBar? = null
    private var sbG: SeekBar? = null
    private var sbB: SeekBar? = null
    private var etAValueD: EditText? = null
    private var etRValueD: EditText? = null
    private var etGValueD: EditText? = null
    private var etBValueD: EditText? = null
    private var etAValueH: EditText? = null
    private var etRValueH: EditText? = null
    private var etGValueH: EditText? = null
    private var etBValueH: EditText? = null
    /**联动事件的发起者*/
    private var authorView: View? = null

    //region 三个 SeekBar.OnSeekBarChangeListener 汇总到一个类文件(本类) 进行处理

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (!fromUser) {
            return
        } else {
            this.authorView = seekBar
        }

        val editTextD: EditText?
        val editTextH: EditText?

        if (seekBar === sbA) {
            editTextD = this.etAValueD
            editTextH = this.etAValueH
        } else if (seekBar === sbR) {
            editTextD = this.etRValueD
            editTextH = this.etRValueH
        } else if (seekBar === sbG) {
            editTextD = this.etGValueD
            editTextH = this.etGValueH
        } else if (seekBar === sbB) {
            editTextD = this.etBValueD
            editTextH = this.etBValueH
        } else {
            throw RuntimeException("logic err")
        }

        editTextD?.setText(String.format("%d", progress))
        editTextH?.setText(String.format("%02X", progress))

        val colorHexStr = getColorHexStrFromHex()
        this.etHexInput?.setText(colorHexStr)
        this.viewIndicator?.setBackgroundColor(Color.parseColor("#$colorHexStr"))

        this.authorView = null
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit

    override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit

    //endregion 三个 SeekBar.OnSeekBarChangeListener 汇总到一个类文件(本类) 进行处理

    /**
     * 改变 EditText 内容，本方法严重依赖成员变量 authorView
     *
     * @param comeFrom          从那个 EditText 进入本方法的
     * @param beforeChangedText 修改之前的文本
     * @param onChangedText     即将修改的文本(修改成功后文本就是此内容)
     */
    fun onChangedText(comeFrom: EditText?, beforeChangedText: String?, onChangedText: Editable?) {
        if (authorView?.id != comeFrom?.id) {
            return
        }

        if (onChangedText == null || onChangedText.toString().isEmpty()) {
            return
        }

        var numDec = -1
        var numHex: String? = null
        var colorHexStr: String

        try {

            when (comeFrom?.id) {
                R.id.et_a_value_decimal,
                R.id.et_r_value_decimal,
                R.id.et_g_value_decimal,
                R.id.et_b_value_decimal -> {
                    numDec = Integer.parseInt(onChangedText.toString())
                    if (numDec > 255) {
                        if (beforeChangedText != onChangedText.toString()) {
                            onChangedText.replace(0, onChangedText.length, beforeChangedText)
                            Toast.makeText(activity, "[0,255]", Toast.LENGTH_SHORT).show()
                        }
                        return
                    } else {
                        numHex = String.format("%02X", numDec)
                    }
                }


                R.id.et_a_value_hex,
                R.id.et_r_value_hex,
                R.id.et_g_value_hex,
                R.id.et_b_value_hex -> {
                    numHex = onChangedText.toString()
                    numDec = Integer.parseInt(numHex, 16)
                }


                R.id.et_hex_input -> {
                    colorHexStr = onChangedText.toString()
                    if (colorHexStr.length != 8) {
                        colorHexStr += "00000000"
                        colorHexStr = colorHexStr.substring(0, 8)
                    }

                    val alphaHex = colorHexStr.substring(0, 2)
                    val redHex = colorHexStr.substring(2, 4)
                    val greenHex = colorHexStr.substring(4, 6)
                    val blueHex = colorHexStr.substring(6, 8)

                    val alphaDec = Integer.parseInt(alphaHex, 16)
                    val redDec = Integer.parseInt(redHex, 16)
                    val greenDec = Integer.parseInt(greenHex, 16)
                    val blueDec = Integer.parseInt(blueHex, 16)

                    this.sbA?.progress = alphaDec
                    this.sbR?.progress = redDec
                    this.sbG?.progress = greenDec
                    this.sbB?.progress = blueDec

                    this.etAValueD?.setText(alphaDec.toString())
                    this.etRValueD?.setText(redDec.toString())
                    this.etGValueD?.setText(greenDec.toString())
                    this.etBValueD?.setText(blueDec.toString())

                    this.etAValueH?.setText(alphaHex)
                    this.etRValueH?.setText(redHex)
                    this.etGValueH?.setText(greenHex)
                    this.etBValueH?.setText(blueHex)

                    this.viewIndicator?.setBackgroundColor(Color.parseColor("#$colorHexStr"))

                    val upperStr = onChangedText.toString().toUpperCase(Locale.getDefault())
                    if (upperStr != onChangedText.toString()) {
                        onChangedText.replace(0, onChangedText.length, upperStr)
                    }

                    return
                }
            }

        } catch (e: NumberFormatException) {
            throw e
            /*Logger.e(e);
            if (!beforeChangedText.equals(onChangedText.toString())) {
                onChangedText.replace(0, onChangedText.length(), beforeChangedText);
            }
            return;*/
        }


        if (numHex == null) {
            throw RuntimeException("logic err")
        }

        when {
            comeFrom === etAValueD -> {
                this.etAValueH?.setText(numHex)
                this.sbA?.progress = numDec
                colorHexStr = this.getColorHexStrFromHex()
            }
            comeFrom === etRValueD -> {
                this.etRValueH?.setText(numHex)
                this.sbR?.progress = numDec
                colorHexStr = this.getColorHexStrFromHex()
            }
            comeFrom === etGValueD -> {
                this.etGValueH?.setText(numHex)
                this.sbG?.progress = numDec
                colorHexStr = this.getColorHexStrFromHex()
            }
            comeFrom === etBValueD -> {
                this.etBValueH?.setText(numHex)
                this.sbB?.progress = numDec
                colorHexStr = this.getColorHexStrFromHex()
            }
            comeFrom === etAValueH -> {
                etAValueD?.setText(numDec.toString())
                sbA?.progress = numDec
                colorHexStr = this.getColorHexStrFromDex()
            }
            comeFrom === etRValueH -> {
                etRValueD?.setText(numDec.toString())
                sbR?.progress = numDec
                colorHexStr = this.getColorHexStrFromDex()
            }
            comeFrom === etGValueH -> {
                etGValueD?.setText(numDec.toString())
                sbG?.progress = numDec
                colorHexStr = this.getColorHexStrFromDex()
            }
            comeFrom === etBValueH -> {
                etBValueD?.setText(numDec.toString())
                sbB?.progress = numDec
                colorHexStr = this.getColorHexStrFromDex()
            }
            else -> throw RuntimeException("logic err")
        }

        this.etHexInput?.setText(colorHexStr)
        this.viewIndicator?.setBackgroundColor(Color.parseColor("#$colorHexStr"))
    }

    /**从四个十六进制颜色子集(两位)获取颜色的整体值*/
    private fun getColorHexStrFromHex(): String {
        return this.etAValueH?.text.toString() +
                this.etRValueH?.text +
                this.etGValueH?.text +
                this.etBValueH?.text
    }

    private fun getColorHexStrFromDex(): String {
        val alphaDec = Integer.parseInt(this.etAValueD?.text.toString())
        val redDec = Integer.parseInt(this.etRValueD?.text.toString())
        val greenDec = Integer.parseInt(this.etGValueD?.text.toString())
        val blueDec = Integer.parseInt(this.etBValueD?.text.toString())
        return String.format("%02X%02X%02X%02X", alphaDec, redDec, greenDec, blueDec)
    }

    //endregion Custom Color layout  二级颜色选择布局

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

            val btnNeutral = btnNeutralParam ?: let { aDialog.getButton(DialogInterface.BUTTON_NEUTRAL) }
            btnNeutral.setText(R.string.back)
        }
    }

    /**从自定义颜色面板到颜色选择面板*/
    private fun fromCustomToSelLayout(btnNeutralParam: Button?) {

        (colorCustomLayout?.parent as ViewGroup?)?.let { viewGroup ->
            viewGroup.removeView(colorCustomLayout)
            viewGroup.addView(gridView)
            val btnNeutral = btnNeutralParam ?: let { aDialog.getButton(DialogInterface.BUTTON_NEUTRAL) }
            btnNeutral.setText(R.string.custom)
        }
    }
    //endregion 选取变换


    //关键 dialog
    val aDialog: AlertDialog by lazy {
        val builder = AlertDialog.Builder(context)

        builder.setView(this.gridView)//默认首次展示一级颜色选区

        title?.let { builder.setTitle(title) }
        builder.setNegativeButton(negativeBtnStr) { dialog, which -> defNegativeClickListener?.invoke(dialog, which, flag) }
                .setNeutralButton(neutralBtnStr) { dialog, which -> defNeutralClickListener?.invoke(dialog, which, flag) }
                .setPositiveButton(positiveBtnStr) { dialog, which ->

                    var selColorResult: PickColorResult? = null

                    if (gridView?.parent != null) {//如果在颜色选择布局

                        selColorResult = pickColorAdapter?.getSelColor()

                    } else if (colorCustomLayout?.parent != null) {//如果在颜色自定义布局

                        etHexInput?.text?.let {
                            selColorResult = PickColorResult(it.toString())
                        }

                    } else {

                        //throw RuntimeException("logic err")

                    }

                    defPositiveClickListener?.invoke(dialog, which, selColorResult, flag)
                }

        val alertDialog = builder.create()
        alertDialog.setOnShowListener(this)

        alertDialog
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        if (reset) {
            aDialog.setTitle(this.title)
            pickColorAdapter?.reset(this.transferColorStr)

            fromCustomToSelLayout(null)
        }
        return aDialog
    }


    override fun onShow(dialog: DialogInterface?) {

        negativeClickListener?.let {
            //否定事件拦截
            val btnNegative = aDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
            btnNegative.setOnClickListener { view -> it.invoke(this, btnNegative, flag) }
        }

        neutralClickListener?.let {
            //中性事件拦截
            val btnNeutral = aDialog.getButton(DialogInterface.BUTTON_NEUTRAL)
            btnNeutral.setOnClickListener { view -> it.invoke(this, btnNeutral, flag) }
        }

        positiveClickListener?.let {
            //肯定事件拦截
            val btnPositive = aDialog.getButton(DialogInterface.BUTTON_POSITIVE)
            btnPositive.setOnClickListener { view -> it.invoke(this, btnPositive, flag) }
        }

        onShowListener?.invoke()
    }

    override fun onDismiss(dialog: DialogInterface) {
        onDismissListener?.invoke()
        super.onDismiss(dialog)
    }
}

