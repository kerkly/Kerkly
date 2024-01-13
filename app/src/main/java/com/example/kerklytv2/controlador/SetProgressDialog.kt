package com.example.kerklytv2.controlador

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView

class SetProgressDialog {
    private lateinit var progressBar: ProgressBar
      var dialog: Dialog?= null
    fun setProgressDialog(context: Context) {
        val llPadding = 30
        val ll = LinearLayout(context)
        ll.orientation = LinearLayout.HORIZONTAL
        ll.setPadding(llPadding, llPadding, llPadding, llPadding)
        ll.gravity = Gravity.CENTER
        var llParam = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        llParam.gravity = Gravity.CENTER
        ll.layoutParams = llParam
        progressBar = ProgressBar(context)

        progressBar!!.isIndeterminate = true
        progressBar!!.setPadding(0, 0, llPadding, 0)
        progressBar!!.layoutParams = llParam
        llParam = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        llParam.gravity = Gravity.CENTER
        val tvText = TextView(context)
        tvText.text = "Por favor espere un momento..."
        tvText.setTextColor(Color.parseColor("#000000"))
        tvText.textSize = 20f
        tvText.layoutParams = llParam
        ll.addView(progressBar)
        ll.addView(tvText)
        val builder = AlertDialog.Builder(context)
        builder.setCancelable(true)
        builder.setView(ll)
        dialog = builder.create()
        (dialog as AlertDialog?)!!.show()
        val window: Window? = (dialog as AlertDialog?)!!.window
        if (window != null) {
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom((dialog as AlertDialog?)!!.window!!.attributes)
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
            (dialog as AlertDialog?)!!.window!!.attributes = layoutParams
        }
    }
}