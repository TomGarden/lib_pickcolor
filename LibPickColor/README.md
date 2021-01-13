## Override

Select color

## Usage

[填充字段 `implementation '<groupId>:<artifactId>:<version>'`](https://github.com/TomGarden?tab=packages&repo_name=lib_pickcolor)

[GitHub_Packages_了解](https://github.com/TomGarden/tom-notes/issues/8)

```
//此节点位于 ModuleName/build.gradle
dependencies {
    //implementation '<groupId>:<artifactId>:<version>'
    implementation 'io.github.tomgarden:lib_pick_color:0.1.3'
}

//此节点位于 ModuleName/build.gradle , 或者 ProjectName/build.gradle
repositories {
    maven {
        //url = uri("https://maven.pkg.github.com/OWNER/REPOSITORY")
        url = uri("https://maven.pkg.github.com/TomGarden/lib_pickcolor")

        credentials {
            //不限的 github 账户名
            username = System.getenv("TOMGARADEN_USERNAME")
            //与 github 账户名成对的 具有 read:packages 权限的 token
            password = System.getenv("TOMGARADEN_READ_PACKAGES_TOKEN")
        }
    }
}
```




### 说明
1. Lib 内部的 strHexValue 是剔除了 '#' 符号的字符串


- 内存泄露示例
    ```kotlin
    val dialogFragment by lazy{ PickColorDialogFragment() }
    btnOne.setOnClickListener{
        if(!dialogFragment.isShow)  {
            dialogFragment.show(...)
        }
    }
    ```
- 正确的使用方式
    ```kotlin
    btnOne.setOnClickListener{
        PickColorDialogFragment().show(...)
    }
    ```


```kotlin
    fun testShow(): Unit {
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
```


## 参考
- [关于 Android DialogFragment 的常见状况: ](https://www.cnblogs.com/guanxinjing/p/12044196.html)