package com.catly.oledsaver.features.view

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.catly.oledsaver.R
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class ChangesDialog: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val view = layoutInflater.inflate(R.layout.changes_dialog,null)
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Change Log")
            .setView(view)
            .create()

        view.apply {
            this.findViewById<Button>(R.id.closeButton).setOnClickListener {
                dialog.dismiss()
            }

            setChangeLogFromTxt(this.findViewById(R.id.changeLog))
        }

        return dialog
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
                //log the exception
                e.printStackTrace()
            }
            changeLog.text = text
        }
    }
}