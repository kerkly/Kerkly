package com.example.kerklytv2.controlador

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.util.Calendar

class TimePricker(val l: (hora: Int, minuto: Int) -> Unit) : DialogFragment(),
    TimePickerDialog.OnTimeSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        var h = c.get(Calendar.HOUR_OF_DAY)
        var m = c.get(Calendar.MINUTE)

        return TimePickerDialog(requireActivity(), this, h, m,
            DateFormat.is24HourFormat(requireActivity()))
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        l(hourOfDay, minute)
    }
}