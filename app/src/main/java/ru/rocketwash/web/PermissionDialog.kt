package ru.rocketwash.web

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog

class PermissionDialog @Deprecated("Use newInstance()") constructor() : DialogFragment() {

    var listener: Listener? = null

    companion object {
        private val TAG: String = PermissionDialog::class.java.simpleName
        private val KEY_PERMISSIONS = "$TAG-KEY_PERMISSIONS"

        @Suppress("DEPRECATION")
        fun newInstance(permissions: Array<String>): PermissionDialog {
            return PermissionDialog().apply {
                val bundle = Bundle()

                bundle.putStringArray(KEY_PERMISSIONS, permissions)

                arguments = bundle
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val permissions = arguments!!.getStringArray(KEY_PERMISSIONS)
        return AlertDialog.Builder(context!!)
                .setTitle(R.string.dialog_permission_title)
                .setMessage(getString(R.string.dialog_permission_message, permissions.joinToString(",\n")))
                .setNegativeButton(R.string.dialog_permission_action_cancel) { _, _ ->
                    listener?.decline()
                    dismiss()
                }
                .setPositiveButton(R.string.dialog_permission_action_accept) { _, _ ->
                    listener?.accept()
                    dismiss()
                }
                .create()
    }

    interface Listener {
        fun accept()

        fun decline()
    }
}