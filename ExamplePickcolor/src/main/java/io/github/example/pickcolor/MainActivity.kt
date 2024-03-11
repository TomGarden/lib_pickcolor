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
import io.github.tomgaren.example.pickcolor.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.includeOne.tvTest.text = "123123123"


        initView()
    }

    private fun initView() {
        binding.btnInitColorFfffffffUnneutral.setOnClickListener {
            PickColorDialogFrag.builder()
                .setTitle("init color ffffffff")
                .setInputColor(PickColor("ffffffff"))
                .setFlag("I'm a Flag")
                .setDefPanel(PickColorDefPanel.PANEL_CUSTOM)
                //.setNeutralClickListener("to Custom", "to Select")
                .setDefNegativeClickListener("negative")
                { dialogInterface: DialogInterface?, which: Int, flag: Any? ->
                    log("dismiss")
                }
                .setDefPositiveClickListener("positive")
                { dialogInterface: DialogInterface?, which: Int, selColor: PickColor?, flag: Any? ->
                    log("dismiss : ${selColor?.toString(this)}")
                }
                .build()
                .show(supportFragmentManager, "btn_init_color_ffffffff_unneutral")
        }


        binding.btnInitColorFfffffff.setOnClickListener {
            PickColorDialogFrag.builder()
                .setTitle("init color ffffffff")
                .setInputColor(PickColor("ffffffff"))
                .setFlag("I'm a Flag")
                .setDefPanel(PickColorDefPanel.PANEL_CUSTOM)
                .setNeutralClickListener("to Custom", "to Select")
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

        binding.btnInitColorFf000000.setOnClickListener {
            PickColorDialogFrag.builder()
                .setTitle("init color 00000000")
                .setInputColor(PickColor("00000000"))
                .setFlag("I'm a Flag")

                .setDefPanel(PickColorDefPanel.PANEL_SELECT)

                //do dismiss action
                .setNeutralClickListener("to Custom", "to Select")
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

        binding.btnPrintViewTree.setOnClickListener { LibPickerColorUtils.printViewTree(it) }
    }


    @SuppressLint("SetTextI18n")
    private fun log(str: String) {
        Logger.i(str)
        binding.tvLogCat.text = binding.tvLogCat.text.toString() + "\n" + str
    }
}
