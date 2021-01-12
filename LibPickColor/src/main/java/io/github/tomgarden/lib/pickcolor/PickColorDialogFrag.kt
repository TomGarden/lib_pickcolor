package io.github.tomgarden.lib.pickcolor

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import io.github.tomgarden.lib.log.Logger


/**
 * describe :使用本类需要注意一点，在本类中使用的颜色十六进制值，只有在交付给 Color 之前才会添加 # 。
 *
 * onAttach
 * onCreate
 * onCreateDialog
 * onCreateView
 * onActivityCreated
 *
 * author : tom
 *
 * time : 18-9-14 11:28
 * Git : https://github.com/TomGarden
 */
class PickColorDialogFrag() : DialogFragment(), DialogInterface.OnShowListener {

    private lateinit var delegate: PickColorDelegate

    constructor(delegate: PickColorDelegate) : this() {
        this.delegate = delegate
    }

    companion object {
        fun builder(): BaseBuilder = PickColorDelegate()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        if (!this::delegate.isInitialized) {
            savedInstanceState?.getParcelable<BaseBuilder?>(BaseBuilder.BUILDER_PARCELABLE)
                ?.let { builder ->
                    if (builder is PickColorDelegate) {
                        delegate = builder
                    } else /*if (builder is BaseBuilder)*/ {
                        //delegate = PickColorDelegate(builder)
                        Logger.e("对象不对应需要跟进 : lister 函数对象如果赋值会造成对象不对应的异常 , 更新方式暂不探究")
                    }
                }
        }
        delegate.mContext = requireContext()
        delegate.onCreateDialog()

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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(BaseBuilder.BUILDER_PARCELABLE, delegate)
    }

    fun getPickColorResult() = delegate.getPickColorResult()

    fun setCancelableCover(cancelable: Boolean): PickColorDialogFrag {
        super.setCancelable(cancelable)
        return this
    }
}

