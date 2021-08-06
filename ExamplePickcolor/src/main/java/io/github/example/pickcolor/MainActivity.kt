package io.github.example.pickcolor

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import io.github.tomgarden.lib.log.Logger
import io.github.tomgarden.lib.pickcolor.PickColor
import io.github.tomgarden.lib.pickcolor.PickColorDefPanel
import io.github.tomgarden.lib.pickcolor.PickColorDialogFrag
import io.github.tomgarden.lib.pickcolor.LibPickerColorUtils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
    }

    private fun initView() {

        btn_init_color_ffffffff.setOnClickListener {
            PickColorDialogFrag.builder()
                .setTitle("init color ffffffff")
                .setInputColor(PickColor("ffffffff"))
                .setFlag("I'm a Flag")
                .setDefPanel(PickColorDefPanel.PANEL_CUSTOM)
                .setDefNeutralClickListener("to Custom", "to Select")
                { dialogInterface: DialogInterface?, which: Int, flag: Any? ->
                    log("dismiss")
                }
                .setDefNegativeClickListener("negative")
                { dialogInterface: DialogInterface?, which: Int, flag: Any? ->
                    log("dismiss")
                }
                .setDefPositiveClickListener("positive")
                { dialogInterface: DialogInterface?, which: Int, selColor: PickColor?, flag: Any? ->
                    log("dismiss : ${selColor?.toString(this)}")
                }
                .build()
                .show(supportFragmentManager, "btn_init_color_ffffffff")
        }

        btn_init_color_ff000000.setOnClickListener {
            PickColorDialogFrag.builder()
                .setTitle("init color 00000000")
                .setInputColor(PickColor("00000000"))
                .setFlag("I'm a Flag")

                .setDefPanel(PickColorDefPanel.PANEL_SELECT)

                //do dismiss action
                .setDefNeutralClickListener("to Custom", "to Select")
                { dialogInterface: DialogInterface?, which: Int, flag: Any? ->
                    log("dismiss")
                }
                .setDefNegativeClickListener("negative")
                { dialogInterface: DialogInterface?, which: Int, flag: Any? ->
                    log("dismiss")
                }
                .setDefPositiveClickListener("positive")
                { dialogInterface: DialogInterface?, which: Int, selColor: PickColor?, flag: Any? ->
                    log("dismiss : ${selColor?.toString(this)}")
                }

                // don't dismiss action cover dismiss action
                //.setNeutralClickListener("to Custom", "to Select")
                //{ dialogFrag: PickColorDialogFrag, btnNeutral: Button, flag: Any? ->
                //    log("no dismiss , no switch")
                //}
                .setNegativeClickListener("negative")
                { dialogFrag: PickColorDialogFrag, btnNeutral: Button, flag: Any? ->
                    log("no dismiss")
                }
                .setPositiveClickListener("positive")
                { dialogFrag: PickColorDialogFrag, btnPositive: Button, flag: Any? ->
                    log(dialogFrag.getPickColorResult().toString(this))
                    dialogFrag.dismiss()
                }

                .setOnShowListener { log("show") }
                .setOnDismissListener { log("dismiss") }

                .build()

                .setCancelableCover(true)

                .show(supportFragmentManager, "btn_init_color_ffffffff")
        }

        btnPrintViewTree.setOnClickListener { LibPickerColorUtils.printViewTree(it) }
    }


    @SuppressLint("SetTextI18n")
    private fun log(str: String) {
        Logger.i(str)
        tvLogCat.text = tvLogCat.text.toString() + "\n" + str
    }
}
