package io.github.tomgarden.lib.pickcolor

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.widget.Button
import androidx.fragment.app.DialogFragment


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
 * Git : https://github.com/TomGarden
 */
class PickColorDialogFrag : DialogFragment, DialogInterface.OnShowListener, Parcelable {

    private val delegate by lazy { PickColorDelegate(requireContext(), fragBuilder!!) }
    private var fragBuilder: Builder? = null

    private constructor() : super()
    private constructor(parcel: Parcel) : this()
    constructor(builder: Builder) : this() {
        fragBuilder = builder
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = delegate.aDialog
        dialog.setOnShowListener(this)
        return dialog
    }


    override fun onShow(dialog: DialogInterface?) {

        delegate.negativeClickListener?.let {
            //否定事件拦截
            val btnNegative = delegate.aDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
            btnNegative.setOnClickListener { view -> it.invoke(this, btnNegative, delegate.flag) }
        }

        delegate.neutralClickListener?.let {
            //中性事件拦截
            val btnNeutral = delegate.aDialog.getButton(DialogInterface.BUTTON_NEUTRAL)
            btnNeutral.setOnClickListener { view -> it.invoke(this, btnNeutral, delegate.flag) }
        }

        delegate.positiveClickListener?.let {
            //肯定事件拦截
            val btnPositive = delegate.aDialog.getButton(DialogInterface.BUTTON_POSITIVE)
            btnPositive.setOnClickListener { view -> it.invoke(this, btnPositive, delegate.flag) }
        }

        delegate.onShowListener?.invoke()
    }

    override fun onDismiss(dialog: DialogInterface) {
        delegate.onDismissListener?.invoke()
        super.onDismiss(dialog)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PickColorDialogFrag> {
        override fun createFromParcel(parcel: Parcel): PickColorDialogFrag {
            return PickColorDialogFrag(parcel)
        }

        override fun newArray(size: Int): Array<PickColorDialogFrag?> {
            return arrayOfNulls(size)
        }
    }

    /**
     * 我们需要 context 上下文做重要的事情 , 比如加载布局等等
     * 但是 context 的初始化需要依赖生命周期
     * 所以我们先将依赖生命周期的操作的相关数据备份 , 等拿到了 context 在进一步使用它们完成初始化操作
     *
     * "请知晓" : Builder 中的数据对象是写完所有代码后从 var 变量中 copy 来的
     */
    class Builder {
        /*默认展示的颜色控制面板*/
        var pickColorDefPanel = PickColorDefPanel.PANEL_SELECT

        /*弹窗标题*/
        var title: String? = null

        /*默认颜色*/
        var inputColor: PickColor = PickColor(Utils.DEF_COLOR)

        /*携带的其他数据*/
        var flag: Any? = null


        var negativeBtnStr: String? = null
        var neutralBtnStr: String? = null
        var positiveBtnStr: String? = null


        var defNegativeClickListener:
                ((dialogInterface: DialogInterface?, which: Int, flag: Any?) -> Unit)? = null
        var defNeutralClickListener:
                ((dialogInterface: DialogInterface?, which: Int, flag: Any?) -> Unit)? = null
        var defPositiveClickListener: ((dialogInterface: DialogInterface?, which: Int, selColor: PickColor?, flag: Any?) -> Unit)? =
            null

        var negativeClickListener:
                ((dialogFrag: PickColorDialogFrag, btnNegative: Button, flag: Any?) -> Unit)? = null
        var neutralClickListener:
                ((dialogFrag: PickColorDialogFrag, btnNeutral: Button, flag: Any?) -> Unit)? = null
        var positiveClickListener:
                ((dialogFrag: PickColorDialogFrag, btnPositive: Button, flag: Any?) -> Unit)? = null

        var onShowListener: (() -> Unit)? = null
        var onDismissListener: (() -> Unit)? = null

        fun setDefPanel(defPanel: PickColorDefPanel): Builder {
            this.pickColorDefPanel = defPanel
            return this
        }

        fun setTitle(title: String?): Builder {
            this.title = title
            return this
        }

        fun setInputColor(inputColor: PickColor): Builder {
            this.inputColor = inputColor
            return this
        }

        fun setFlag(flag: Any?): Builder {
            this.flag = flag
            return this
        }


        fun setDefNegativeClickListener(
            negativeBtnStr: String,
            defNegativeClickListener: ((dialogInterface: DialogInterface?, which: Int, flag: Any?) -> Unit)?
        ): Builder {
            this.negativeBtnStr = negativeBtnStr
            this.defNegativeClickListener = defNegativeClickListener
            return this
        }

        fun setDefNeutralClickListener(
            neutralBtnStr: String,
            defNeutralClickListener: ((dialogInterface: DialogInterface?, which: Int, flag: Any?) -> Unit)?
        ): Builder {
            this.neutralBtnStr = neutralBtnStr
            this.defNeutralClickListener = defNeutralClickListener
            return this
        }

        fun setDefPositiveClickListener(
            positiveBtnStr: String,
            defPositiveClickListener: ((dialogInterface: DialogInterface?, which: Int, selColor: PickColor?, flag: Any?) -> Unit)?
        ): Builder {
            this.positiveBtnStr = positiveBtnStr
            this.defPositiveClickListener = defPositiveClickListener
            return this
        }


        fun setNegativeClickListener(
            negativeBtnStr: String,
            negativeClickListener: ((dialogFrag: PickColorDialogFrag, btnNegative: Button, flag: Any?) -> Unit)?
        ): Builder {
            this.negativeBtnStr = negativeBtnStr
            this.negativeClickListener = negativeClickListener
            return this
        }

        fun setNeutralClickListener(
            neutralBtnStr: String,
            neutralClickListener: ((dialogFrag: PickColorDialogFrag, btnNeutral: Button, flag: Any?) -> Unit)?
        ): Builder {
            this.neutralBtnStr = neutralBtnStr
            this.neutralClickListener = neutralClickListener
            return this
        }

        fun setPositiveClickListener(
            positiveBtnStr: String,
            positiveClickListener: ((dialogFrag: PickColorDialogFrag, btnPositive: Button, flag: Any?) -> Unit)?
        ): Builder {
            this.positiveBtnStr = positiveBtnStr
            this.positiveClickListener = positiveClickListener
            return this
        }


        fun setOnShowListener(onShowListener: (() -> Unit)?): Builder {
            this.onShowListener = onShowListener
            return this
        }

        fun setOnDismissListener(onDismissListener: (() -> Unit)?): Builder {
            this.onDismissListener = onDismissListener
            return this
        }

        fun build(): PickColorDialogFrag = PickColorDialogFrag(this)
    }

}

