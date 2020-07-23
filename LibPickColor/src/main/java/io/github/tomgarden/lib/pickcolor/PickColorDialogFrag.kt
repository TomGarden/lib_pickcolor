package io.github.tomgarden.lib.pickcolor

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
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
 *
 * Git : https://github.com/TomGarden
 */
class PickColorDialogFrag : DialogFragment(), DialogInterface.OnShowListener {

    private val delegate = PickColorDelegate()


    fun setTitle(title: String?, reset: Boolean = true): PickColorDialogFrag {
        delegate.title = title
        delegate.reset = true
        return this
    }

    fun setTransferColorStr(transferColorStr: String?, reset: Boolean = true): PickColorDialogFrag {
        delegate.transferColorStr =
            transferColorStr?.let { Utils.formatHexColorStr(transferColorStr) } ?: Utils.DEF_COLOR
        delegate.reset = true
        return this
    }

    fun setFlag(flag: Any?, reset: Boolean = true): PickColorDialogFrag {
        delegate.flag = flag
        delegate.reset = true
        return this
    }

    fun reset(reset: Boolean): PickColorDialogFrag {
        delegate.reset = reset
        return this
    }


    fun setDefNegativeClickListener(
        negativeBtnStr: String,
        defNegativeClickListener: ((dialogInterface: DialogInterface?, which: Int, flag: Any?) -> Unit)?
    ): PickColorDialogFrag {
        delegate.negativeBtnStr = negativeBtnStr
        delegate.defNegativeClickListener = defNegativeClickListener
        return this
    }

    fun setDefNeutralClickListener(
        neutralBtnStr: String,
        defNeutralClickListener: ((dialogInterface: DialogInterface?, which: Int, flag: Any?) -> Unit)?
    ): PickColorDialogFrag {
        delegate.neutralBtnStr = neutralBtnStr
        delegate.defNeutralClickListener = defNeutralClickListener
        return this
    }

    fun setDefPositiveClickListener(
        positiveBtnStr: String,
        defPositiveClickListener: ((dialogInterface: DialogInterface?, which: Int, selColorResult: PickColorResult?, flag: Any?) -> Unit)?
    ): PickColorDialogFrag {
        delegate.positiveBtnStr = positiveBtnStr
        delegate.defPositiveClickListener = defPositiveClickListener
        return this
    }


    fun setNegativeClickListener(
        negativeBtnStr: String,
        negativeClickListener: ((dialogFrag: PickColorDialogFrag, btnNegative: Button, flag: Any?) -> Unit)?
    ): PickColorDialogFrag {
        delegate.negativeBtnStr = negativeBtnStr
        delegate.negativeClickListener = negativeClickListener
        return this
    }

    fun setNeutralClickListener(
        neutralBtnStr: String,
        neutralClickListener: ((dialogFrag: PickColorDialogFrag, btnNeutral: Button, flag: Any?) -> Unit)?
    ): PickColorDialogFrag {
        delegate.neutralBtnStr = neutralBtnStr
        delegate.neutralClickListener = neutralClickListener
        return this
    }

    fun setPositiveClickListener(
        positiveBtnStr: String,
        positiveClickListener: ((dialogFrag: PickColorDialogFrag, btnPositive: Button, flag: Any?) -> Unit)?
    ): PickColorDialogFrag {
        delegate.positiveBtnStr = positiveBtnStr
        delegate.positiveClickListener = positiveClickListener
        return this
    }


    fun setOnShowListener(onShowListener: (() -> Unit)?): PickColorDialogFrag {
        delegate.onShowListener = onShowListener
        return this
    }

    fun setOnDismissListener(onDismissListener: (() -> Unit)?): PickColorDialogFrag {
        delegate.onDismissListener = onDismissListener
        return this
    }


    override fun onAttach(context: Context) {
        delegate.context = context
        super.onAttach(context)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = delegate.onCreateDialog(savedInstanceState)
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
        delegate.onDismiss(dialog)
        super.onDismiss(dialog)
    }
}

