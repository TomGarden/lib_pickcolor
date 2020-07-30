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
import java.util.*

/**
 * describe : 自定义颜色布局
 *
 * author : Create by tom , on 2020/7/24-7:47 AM
 * github : https://github.com/TomGarden
 */
class ColorCustomLayoutDelegate : SeekBar.OnSeekBarChangeListener {

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
        etAValueD
        etRValueD
        etGValueD
        etBValueD
        etAValueH
        etRValueH
        etGValueH
        etBValueH
    }


    private lateinit var context: Context

    val colorCustomLayout: ViewGroup? by lazy {
        LayoutInflater.from(context).inflate(R.layout.color_custom, null) as ViewGroup
    }

    private val viewIndicator: View? by lazy { colorCustomLayout?.findViewById<View>(R.id.view_indicator) }
    val etHexInput: EditText? by lazy { colorCustomLayout?.findViewById<EditText>(R.id.et_hex_input) }
    private val sbA: SeekBar? by lazy {
        colorCustomLayout?.findViewById<SeekBar>(R.id.sb_a)
            ?.apply { setOnSeekBarChangeListener(this@ColorCustomLayoutDelegate) }
    }
    private val sbR: SeekBar? by lazy {
        colorCustomLayout?.findViewById<SeekBar>(R.id.sb_r)
            ?.apply { setOnSeekBarChangeListener(this@ColorCustomLayoutDelegate) }
    }
    private val sbG: SeekBar? by lazy {
        colorCustomLayout?.findViewById<SeekBar>(R.id.sb_g)
            ?.apply { setOnSeekBarChangeListener(this@ColorCustomLayoutDelegate) }
    }
    private val sbB: SeekBar? by lazy {
        colorCustomLayout?.findViewById<SeekBar>(R.id.sb_b)
            ?.apply { setOnSeekBarChangeListener(this@ColorCustomLayoutDelegate) }
    }
    private val etAValueD: EditText? by lazy {
        colorCustomLayout?.findViewById<EditText>(R.id.et_a_value_decimal)
            ?.apply { bindTextChangeListener(this) }
    }
    private val etRValueD: EditText? by lazy {
        colorCustomLayout?.findViewById<EditText>(R.id.et_r_value_decimal)
            ?.apply { bindTextChangeListener(this) }
    }
    private val etGValueD: EditText? by lazy {
        colorCustomLayout?.findViewById<EditText>(R.id.et_g_value_decimal)
            ?.apply { bindTextChangeListener(this) }
    }
    private val etBValueD: EditText? by lazy {
        colorCustomLayout?.findViewById<EditText>(R.id.et_b_value_decimal)
            ?.apply { bindTextChangeListener(this) }
    }
    private val etAValueH: EditText? by lazy {
        colorCustomLayout?.findViewById<EditText>(R.id.et_a_value_hex)
            ?.apply { bindTextChangeListener(this) }
    }
    private val etRValueH: EditText? by lazy {
        colorCustomLayout?.findViewById<EditText>(R.id.et_r_value_hex)
            ?.apply { bindTextChangeListener(this) }
    }
    private val etGValueH: EditText? by lazy {
        colorCustomLayout?.findViewById<EditText>(R.id.et_g_value_hex)
            ?.apply { bindTextChangeListener(this) }
    }
    private val etBValueH: EditText? by lazy {
        colorCustomLayout?.findViewById<EditText>(R.id.et_b_value_hex)
            ?.apply { bindTextChangeListener(this) }
    }

    /**联动事件的发起者*/
    private var authorView: View? = null

    //region 三个 SeekBar.OnSeekBarChangeListener 汇总到一个类文件(本类) 进行处理

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (fromUser) {
            this.authorView = seekBar
        } else {
            return
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

    private fun bindTextChangeListener(editText: EditText) {
        val textWatcher = EtValueTextWatcher(
            editText,
            { authorView },
            { authorView -> this@ColorCustomLayoutDelegate.authorView = authorView },
            { comeFrom, beforeChangedText, onChangedText ->
                onChangedText(comeFrom, beforeChangedText, onChangedText)
            })
        editText.addTextChangedListener(textWatcher)
    }


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
                            Toast.makeText(context, "[0,255]", Toast.LENGTH_SHORT).show()
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

}


class EtValueTextWatcher(
    val editText: EditText,
    val getAuthorView: () -> View?,
    val setAuthorView: (authorView: View?) -> Unit,
    val onChangedText: (comeFrom: EditText?, beforeChangedText: String?, onChangedText: Editable?) -> Unit
) : TextWatcher {

    private var beforeChangedText: String? = null

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        if (getAuthorView() == null) {
            setAuthorView(editText)
        }
        this.beforeChangedText = s?.toString()
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

    override fun afterTextChanged(s: Editable?) {
        onChangedText(editText, this.beforeChangedText, s/*this.onChangedText*/)
        if (getAuthorView() === editText) {
            setAuthorView(null)
        }
    }

}