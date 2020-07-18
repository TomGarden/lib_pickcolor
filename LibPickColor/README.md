## Override

Select color

## Usage

### 说明
1. Lib 内部的 strHexValue 是剔除了 '#' 符号的字符串
2. 当前只支持 androidx.fragment.app.FragmentManager 因为  PickColorDialogFrag : androidx.fragment.app
   - 如果需要支持 android.app.DialogFragment.FragmentManager
   - 拷贝文件替换 FragmentManager 即可 , 如无本文之外的需求其他均无需改动 
     


```kotlin

    val pickColorDialog: PickColorDialogFrag by lazy {
        PickColorDialogFrag()
                .setDefNegativeClickListener(getString(R.string.cancel), null)
                .setDefNeutralClickListener(getString(R.string.custom), null)
                .setDefPositiveClickListener(getString(R.string.ok))
                { dialogInterface: DialogInterface?, which: Int, selColorResult: PickColorResult?, flag: Any? ->
                    Logger.d(selColorResult.toString())
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
                .setTitle("Title"/*,true*/)//隐藏参数 reset = true
                .setTransferColorStr("#FFFFFF"/*, true*/)//隐藏参数 reset = true
                .reset(true/false) //覆盖 setTitle / setTransferColorStr 中的隐藏参数
    }


    fun testShow(): Unit {
        if (pickColorDialog.isAdded) return
        pickColorDialog.setTitle("初始值 #0000FF")
                .setTransferColorStr("#0000FF")
                .reset(false/true)
                .show(supportFragmentManager, "展示默认蓝色")
    }
```

## TODO LIST

```kotlin
    //设置了这三个接口就可以在颜色选择阶段做出某些响应了
    private var itemClickListener:(()->Unit)? = null
    private var itemLongClickListener:(()->Unit)? = null
    private var customColorChangeListener:(()->Unit)? = null
```


## [VERSION RELEASE]()
