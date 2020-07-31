package io.github.example.pickcolor

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

/**
 * describe :
 *
 * author : Create by tom , on 2020/7/31-7:34 AM
 * github : https://github.com/TomGarden
 */
class EditTextDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(this.requireContext())
            .setView(R.layout.layout_edit_text)
            .create()
    }
}