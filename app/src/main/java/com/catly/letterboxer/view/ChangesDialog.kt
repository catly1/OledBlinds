package com.catly.letterboxer.view

import android.app.Dialog
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.DialogFragment
import com.catly.letterboxer.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class ChangesDialog: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = requireContext()
        val changeLogTextView = TextView(context).apply {
            textSize = 14f
            setPadding(48, 24, 48, 24)
            setTextColor(ContextCompat.getColor(context, R.color.m3_text_primary))
            setLineSpacing(4f, 1.1f)
        }
        setChangeLogFromTxt(changeLogTextView)

        val scrollView = NestedScrollView(context).apply {
            addView(changeLogTextView)
        }

        return MaterialAlertDialogBuilder(context)
            .setTitle("Change Log")
            .setView(scrollView)
            .setPositiveButton(R.string.close) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
    }

    private fun setChangeLogFromTxt(changeLog: TextView) {
        var text = ""
        var reader: BufferedReader? = null

        try {
            reader = BufferedReader(InputStreamReader(requireContext().assets.open("CHANGELOG.txt")))
            text = reader.readLines().joinToString("\n")
        } catch (e: IOException) {
            Toast.makeText(requireContext(), "Error reading changelog", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        } finally {
            try {
                reader?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            changeLog.text = text
        }
    }
}