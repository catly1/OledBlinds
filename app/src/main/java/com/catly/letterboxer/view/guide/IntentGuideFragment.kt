package com.catly.letterboxer.view.guide

import android.content.*
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.catly.letterboxer.R


class IntentGuideFragment: BaseGuideFragment(R.layout.intent_guide_fragment, R.id.action_intentGuideFragment_to_guideIndexFragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val clipboard = requireActivity().applicationContext.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager

        view.findViewById<Button>(R.id.copyButton).setOnClickListener {
            val text = resources.getString(R.string.open_intent)
            clipboard.setPrimaryClip(ClipData.newPlainText(text, text))
        }

        view.findViewById<Button>(R.id.copyButton2).setOnClickListener {
            val text = resources.getString(R.string.close_intent)
            clipboard.setPrimaryClip(ClipData.newPlainText(text, text))
        }

        view.findViewById<Button>(R.id.guideButton).setOnClickListener {
            launchBrowser("https://github.com/catly1/OledBlinds/wiki/Tasker-Setup")
        }
    }
}