## Override

Select color

## Usage

### 说明
1. Lib 内部的 strHexValue 是剔除了 '#' 符号的字符串
2. 当前只支持 androidx.fragment.app.FragmentManager 因为  PickColorDialogFrag : androidx.fragment.app
   - 如果需要支持 android.app.DialogFragment.FragmentManager
   - 拷贝文件替换 FragmentManager 即可 , 如无本文之外的需求其他均无需改动 
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

## 操作列表和进度存续

```kotlin
    //设置了这三个接口就可以在颜色选择阶段做出某些响应了
    private var itemClickListener:(()->Unit)? = null
    private var itemLongClickListener:(()->Unit)? = null
    private var customColorChangeListener:(()->Unit)? = null
```

1. 默认颜色设置应该对两个面板同时做出支持
2. [√]自定义面板操作代码应该更加简洁
3. 自定义面板按钮应该允许录入字母或者数字
    - [√]首先需要弹出键盘(⌨)️才行
    - 低版本拖动 SeekBar 抖动太严重了
4. [√]支持选择默认面板
5. 丢帧问题优化
6. 切换屏幕方向
    - [持久化必要数据用于页面恢复](https://developer.android.com/topic/libraries/architecture/saving-states)
    - 需要辨析 Serializable 和 Parcelable
    - 由于 Builder 后来赋值给 Delegate 这导致我们缓存 Builder 无法记录实时的 Delegate 状态 ,
      所以我们打算 合并 Delegate 和 Builder 然后缓存这个合并体
    - 关于函数参数不知道如何写入和读出缓存 , 等等 kotlin 书籍的同时了解下 @Parcelize
    - 关于函数参数(lambda表达式)的存储 , 当前的 dialog 好像是做了的 , 1-我们看看它是怎么做的 , 2-尝试模仿它
        - 这需要查看和调试源代码了


## 参考
- [关于 Android DialogFragment 的常见状况: ](https://www.cnblogs.com/guanxinjing/p/12044196.html)