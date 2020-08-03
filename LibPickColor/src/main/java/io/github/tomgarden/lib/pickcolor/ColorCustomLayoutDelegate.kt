package io.github.tomgarden.lib.pickcolor

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.SeekBar
import android.widget.Toast
import io.github.tomgarden.lib.log.Logger
import java.util.*

/**
 * describe : 自定义颜色布局
 * [√]优化点 : EditText 的录入事件怎么做到和原生一样自然 , 无法录入就录入无效 , 删除后光标未知合理
 *
 * author : Create by tom , on 2020/7/24-7:47 AM
 * github : https://github.com/TomGarden
 */
class ColorCustomLayoutDelegate {

    constructor(context: Context) {
        this.context = context

        /*初始化调用一次 , 否则后续无法使用这些对象 , 因为它们没有被实例化*/
        colorCustomLayout
        viewIndicator
        etHexInput
        sbA
        sbR
        sbG
        sbB
        /*
        初始化 SeekBar 会间接初始化 EditText
        etAValueD
        etRValueD
        etGValueD
        etBValueD
        etAValueH
        etRValueH
        etGValueH
        etBValueH*/
    }


    private lateinit var context: Context

    val colorCustomLayout: ViewGroup by lazy {
        LayoutInflater.from(context).inflate(R.layout.color_custom, null) as ViewGroup
    }

    private val viewIndicator: View by lazy {
        colorCustomLayout.findViewById<View>(R.id.view_indicator)
            ?: let { throw RuntimeException("Logic ERR") }
    }
    val etHexInput: EditText by lazy { initOneEditText(R.id.et_hex_input) }
    private val sbA: SeekBar by lazy { initOneSeekBar(R.id.sb_a, etAValueD, etAValueH) }
    private val sbR: SeekBar by lazy { initOneSeekBar(R.id.sb_r, etRValueD, etRValueH) }
    private val sbG: SeekBar by lazy { initOneSeekBar(R.id.sb_g, etGValueD, etGValueH) }
    private val sbB: SeekBar by lazy { initOneSeekBar(R.id.sb_b, etBValueD, etBValueH) }
    private val etAValueD: EditText by lazy { initOneEditText(R.id.et_a_value_decimal) }
    private val etRValueD: EditText by lazy { initOneEditText(R.id.et_r_value_decimal) }
    private val etGValueD: EditText by lazy { initOneEditText(R.id.et_g_value_decimal) }
    private val etBValueD: EditText by lazy { initOneEditText(R.id.et_b_value_decimal) }
    private val etAValueH: EditText by lazy { initOneEditText(R.id.et_a_value_hex) }
    private val etRValueH: EditText by lazy { initOneEditText(R.id.et_r_value_hex) }
    private val etGValueH: EditText by lazy { initOneEditText(R.id.et_g_value_hex) }
    private val etBValueH: EditText by lazy { initOneEditText(R.id.et_b_value_hex) }

    /**联动事件的发起者*/
    private var authorView: View? = null

    /*针对 SeekBar 右侧的 EditText 做初始化动作*/
    private fun initOneEditText(editTextId: Int): EditText {
        val editText =
            colorCustomLayout.findViewById<EditText>(editTextId)
                ?: let { throw RuntimeException("Logic ERR") }

        val textWatcher =
            EtValueTextWatcher(
                editText,
                getAuthorView = { authorView },
                setAuthorView = { authorView: View? ->
                    this@ColorCustomLayoutDelegate.authorView = authorView
                },
                onChangedText = this::onChangedText,
                debugViewName = this::debugViewName
            )

        editText.tag = textWatcher

        editText.addTextChangedListener(textWatcher)

        return editText
    }

    /*针对指定的 seek bar 做初始化*/
    private fun initOneSeekBar(sbId: Int, etD: EditText, etH: EditText): SeekBar {
        val sb =
            colorCustomLayout.findViewById<SeekBar>(sbId)
                ?: let { throw RuntimeException("Logic ERR") }

        val listener =
            SeekBarChangeListener(
                etD,
                etH,
                etHexInput,
                viewIndicator,
                getColorHexStrFromHex = this::getColorHexStrFromHex,
                etSetText = this::etSetText
            )

        sb.tag = listener
        sb.setOnSeekBarChangeListener(listener)

        return sb
    }

    private fun seekBarSetProgress(seekBar: SeekBar, progress: Int) {
        /*取消监听*/
        val sbListener = seekBar.tag as SeekBarChangeListener? ?: return
        seekBar.setOnSeekBarChangeListener(null)

        /*设置值*/
        seekBar.progress = progress

        /*恢复监听*/
        seekBar.setOnSeekBarChangeListener(sbListener)
    }

    private fun etSetText(editText: EditText, text: String?) {
        /*清除原来的监听*/
        val textWatcher = editText.tag as EtValueTextWatcher? ?: return
        editText.removeTextChangedListener(textWatcher)

        /*设置新值*/
        editText.setText(text)

        /*重新监听*/
        editText.addTextChangedListener(textWatcher)
    }

    /**
     * 改变 EditText 内容，本方法严重依赖成员变量 authorView
     *
     * @param comeFrom          从那个 EditText 进入本方法的
     * @param beforeChangedText 修改之前的文本
     * @param onChangedText     即将修改的文本(修改成功后文本就是此内容)
     */
    private fun onChangedText(
        comeFrom: EditText,
        beforeChangedText: String?,
        onChangedText: Editable,
        start: Int?,
        count: Int?,
        after: Int?
    ) {
        if (authorView?.id != comeFrom.id) {
            return
        }

        var numDec: Int = 0
        var numHex: String = ""
        var colorHexStr: String


        when (comeFrom.id) {
            R.id.et_a_value_decimal,
            R.id.et_r_value_decimal,
            R.id.et_g_value_decimal,
            R.id.et_b_value_decimal -> {

                numDec = onChangedText.toString().toIntOrNull() ?: 0

                /*十进制转换成功*/
                if (numDec > 255) {
                    etSetText(comeFrom, beforeChangedText)
                    if (start != null && after != null && beforeChangedText != null) {
                        comeFrom.setSelection(beforeChangedText.length)
                    }
                    Toast.makeText(context, "[0,255]", Toast.LENGTH_SHORT).show()
                    return
                }

                numHex = String.format("%02X", numDec)

            }


            R.id.et_a_value_hex,
            R.id.et_r_value_hex,
            R.id.et_g_value_hex,
            R.id.et_b_value_hex -> {
                val tempNumHex = onChangedText.toString().toUpperCase(Locale.getDefault())
                etSetText(comeFrom, tempNumHex)
                if (start != null && after != null) {
                    comeFrom.setSelection(start + after)
                }
                numHex =
                    if (tempNumHex.isEmpty()) {
                        "00"
                    } else {
                        tempNumHex
                    }
                numDec = Integer.parseInt(numHex, 16)
            }


            R.id.et_hex_input -> {
                colorHexStr = onChangedText.toString().toUpperCase(Locale.getDefault())
                etSetText(comeFrom, colorHexStr)
                if (start != null && after != null) {
                    comeFrom.setSelection(start + after)
                }
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


                seekBarSetProgress(this.sbA, alphaDec)
                seekBarSetProgress(this.sbR, redDec)
                seekBarSetProgress(this.sbG, greenDec)
                seekBarSetProgress(this.sbB, blueDec)

                etSetText(this.etAValueD, alphaDec.toString())
                etSetText(this.etRValueD, redDec.toString())
                etSetText(this.etGValueD, greenDec.toString())
                etSetText(this.etBValueD, blueDec.toString())

                etSetText(this.etAValueH, alphaHex)
                etSetText(this.etRValueH, redHex)
                etSetText(this.etGValueH, greenHex)
                etSetText(this.etBValueH, blueHex)

                this.viewIndicator.setBackgroundColor(Color.parseColor("#$colorHexStr"))

                return
            }
        }



        when {
            comeFrom === etAValueD -> {
                etSetText(this.etAValueH, numHex)
                seekBarSetProgress(this.sbA, numDec)
                colorHexStr = this.getColorHexStrFromHex()
            }
            comeFrom === etRValueD -> {
                etSetText(this.etRValueH, numHex)
                seekBarSetProgress(this.sbR, numDec)
                colorHexStr = this.getColorHexStrFromHex()
            }
            comeFrom === etGValueD -> {
                etSetText(this.etGValueH, numHex)
                seekBarSetProgress(this.sbG, numDec)
                colorHexStr = this.getColorHexStrFromHex()
            }
            comeFrom === etBValueD -> {
                etSetText(this.etBValueH, numHex)
                seekBarSetProgress(this.sbB, numDec)
                colorHexStr = this.getColorHexStrFromHex()
            }
            comeFrom === etAValueH -> {
                etSetText(etAValueD, numDec.toString())
                seekBarSetProgress(sbA, numDec)
                colorHexStr = this.getColorHexStrFromDex()
            }
            comeFrom === etRValueH -> {
                etSetText(etRValueD, numDec.toString())
                seekBarSetProgress(sbR, numDec)
                colorHexStr = this.getColorHexStrFromDex()
            }
            comeFrom === etGValueH -> {
                etSetText(etGValueD, numDec.toString())
                seekBarSetProgress(sbG, numDec)
                colorHexStr = this.getColorHexStrFromDex()
            }
            comeFrom === etBValueH -> {
                etSetText(etBValueD, numDec.toString())
                seekBarSetProgress(sbB, numDec)
                colorHexStr = this.getColorHexStrFromDex()
            }
            else -> throw RuntimeException("logic err")
        }

        etSetText(this.etHexInput, colorHexStr)
        this.viewIndicator.setBackgroundColor(Color.parseColor("#$colorHexStr"))
    }

    /**从四个十六进制颜色子集(两位)获取颜色的整体值*/
    private fun getColorHexStrFromHex(): String {

        fun getHex(editText: EditText): String {
            return editText.text.toString().let {
                if (it.isEmpty()) {
                    "00"
                } else {
                    it
                }
            }
        }

        val alphaHex = getHex(etAValueH)
        val redHex = getHex(etRValueH)
        val greenHex = getHex(etGValueH)
        val blueHex = getHex(etBValueH)

        return alphaHex + redHex + greenHex + blueHex
    }

    private fun getColorHexStrFromDex(): String {
        val alphaDec = this.etAValueD.text.toString().toIntOrNull() ?: 0
        val redDec = this.etRValueD.text.toString().toIntOrNull() ?: 0
        val greenDec = this.etGValueD.text.toString().toIntOrNull() ?: 0
        val blueDec = this.etBValueD.text.toString().toIntOrNull() ?: 0
        return String.format("%02X%02X%02X%02X", alphaDec, redDec, greenDec, blueDec)
    }

    private fun debugViewName(view: View?): String {
        return when {
            view === colorCustomLayout -> "colorCustomLayout"
            view === viewIndicator -> "viewIndicator"
            view === etHexInput -> "etHexInput"
            view === sbA -> "sbA"
            view === sbR -> "sbR"
            view === sbG -> "sbG"
            view === sbB -> "sbB"
            view === etAValueD -> "etAValueD"
            view === etRValueD -> "etRValueD"
            view === etGValueD -> "etGValueD"
            view === etBValueD -> "etBValueD"
            view === etAValueH -> "etAValueH"
            view === etRValueH -> "etRValueH"
            view === etGValueH -> "etGValueH"
            view === etBValueH -> "etBValueH"
            view === authorView -> "authorView"
            else -> "UNKNOW"
        }
    }
}


/**
 * seekBar 运动 ,  带动三个 4 个控件运动
 * 1.2. seekBar 右侧的 10 进制颜色域 和 16 进制颜色域
 * 3.   整体颜色值
 * 4.   颜色预览 UI 变化
 */
class SeekBarChangeListener(
    private val editTextD: EditText,
    private val editTextH: EditText,
    private val etHexInput: EditText,
    private val viewIndicator: View,
    private val getColorHexStrFromHex: () -> String,
    private val etSetText: (editText: EditText, text: String?) -> Unit
) : SeekBar.OnSeekBarChangeListener {
    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (!fromUser) {
            return
        }

        etSetText(editTextD, String.format("%d", progress))
        etSetText(editTextH, String.format("%02X", progress))

        val colorHexStr = getColorHexStrFromHex()
        etSetText(this.etHexInput, colorHexStr)
        this.viewIndicator.setBackgroundColor(Color.parseColor("#$colorHexStr"))

    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit

    override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
}


class EtValueTextWatcher(
    private val editText: EditText,
    val getAuthorView: () -> View?,
    val setAuthorView: (authorView: View?) -> Unit,
    val onChangedText: (comeFrom: EditText, beforeChangedText: String?, onChangedText: Editable, start: Int?, count: Int?, after: Int?) -> Unit,
    val debugViewName: (view: View?) -> String
) : TextWatcher {

    private var beforeChangedText: String? = null
    private var start: Int? = null
    private var count: Int? = null
    private var after: Int? = null

    override fun beforeTextChanged(
        charSequence: CharSequence?,
        start: Int,
        count: Int,
        after: Int
    ) {
        Logger.d(
            "1-beforeTextChanged=charSequence=${charSequence.toString()}, start=${start}, count=${count}, after=${after}), ${debugViewName(
                editText
            )}"
        )

        if (getAuthorView() == null) {
            setAuthorView(editText)
        } else {
            Logger.e("Log ERR")
            return
        }
        this.beforeChangedText = charSequence?.toString()
        this.start = start
        this.count = count
        this.after = after

    }

    override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
        Logger.d(
            "2-onTextChanged(charSequence= ${charSequence.toString()} start= ${start} before= ${before} count= ${count}), ${debugViewName(
                editText
            )}"
        )
    }

    override fun afterTextChanged(editable: Editable?) {
        Logger.d("3-beforeTextChanged(editable= ${editable.toString()}), $${debugViewName(editText)}")

        if (getAuthorView() === editText) {
            editable?.let {
                onChangedText(
                    editText,
                    this.beforeChangedText,
                    editable,
                    start,
                    count,
                    after
                )
            }
            setAuthorView(null)
            this.beforeChangedText = null
            this.start = null
            this.count = null
            this.after = null
        } else {
            Logger.e("Log ERR")
        }
    }

}