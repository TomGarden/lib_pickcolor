package io.github.example.pickcolor

import android.content.DialogInterface
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.github.tomgarden.lib.log.Logger
import io.github.tomgarden.lib.pickcolor.PickColorDialogFrag
import io.github.tomgarden.lib.pickcolor.PickColor
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val pickColorDialog: PickColorDialogFrag by lazy {
        PickColorDialogFrag()
            .setDefNegativeClickListener(getString(R.string.lib_picker_color__str_cancel), null)
            .setDefNeutralClickListener(getString(R.string.lib_picker_color__str_custom), null)
            .setDefPositiveClickListener(getString(R.string.lib_picker_color__str_ok))
            { dialogInterface: DialogInterface?, which: Int, selColor: PickColor?, flag: Any? ->

                tvLogCat.text = selColor?.toString(this) + "\n" + flag

                Logger.d(selColor?.toString(this))
                Logger.d(flag)
            }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
    }

    private fun initView() {

        btn_init_color_ffffffff.setOnClickListener {

            if (pickColorDialog.isAdded) return@setOnClickListener
            pickColorDialog.setTitle("初始值 #FFFFFF")
                .show(supportFragmentManager, "展示默认白色")
        }

        btn_init_color_ff000000.setOnClickListener {
            if (pickColorDialog.isAdded) return@setOnClickListener
            pickColorDialog.setTitle("初始值 #000000")
                .show(supportFragmentManager, "展示默认黑色")
        }

        btn_click_not_reset_ui.setOnClickListener {
            if (pickColorDialog.isAdded) return@setOnClickListener
            pickColorDialog.setTitle("///////////////////")
                .show(supportFragmentManager, "展示默认蓝色")
        }

        tvLogCat.setOnClickListener {
            Toast.makeText(this, "haha", Toast.LENGTH_SHORT).show()
            test()
        }

        btn_recreate.setOnClickListener { test() }

        btnTest4Leak.setOnClickListener {

        }
    }

    private fun test(): Unit {
        PickColorDialogFrag()
            .setDefNegativeClickListener(getString(R.string.lib_picker_color__str_cancel), null)
            .setDefNeutralClickListener(getString(R.string.lib_picker_color__str_custom), null)
            .setDefPositiveClickListener(getString(R.string.lib_picker_color__str_ok))
            { dialogInterface: DialogInterface?, which: Int, selColor: PickColor?, flag: Any? ->
                Logger.d(selColor?.toString(this))
                Logger.d(flag)
            }
            /*Def 和 非 Def 的差别就是 非 Def 会覆盖 Def , 并且点击事件不会导致 dialog dismiss*/
            //.setNegativeClickListener(getString(R.string.cancel))
            //{ dialogFrag: PickColorDialogFrag, btnNegative: Button, flag: Any? -> /*TODO LOGIC*/}
            //.setNeutralClickListener(getString(R.string.custom))
            //{ dialogFrag: PickColorDialogFrag, btnNeutral: Button, flag: Any? -> /*TODO LOGIC*/}
            //.setPositiveClickListener(getString(R.string.ok))
            //{ dialogFrag: PickColorDialogFrag, btnPositive: Button, flag: Any? -> /*TODO LOGIC*/}
            .setOnShowListener { /*展示监听 , 实现细节 : 在 onShow 方法中调用本回调*/ }
            .setOnDismissListener { /*隐藏监听 , 实现细节 : 在 onDismiss 方法中调用本回调*/ }
            /*reset 只和下面二个属性相关*/
            .setTitle("Title"/*,true*/)
            .show(supportFragmentManager, "test()")
    }

    fun testShow(): Unit {
        if (pickColorDialog.isAdded) return
        pickColorDialog.setTitle("初始值 #0000FF")
            .show(supportFragmentManager, "展示默认蓝色")
    }
}
