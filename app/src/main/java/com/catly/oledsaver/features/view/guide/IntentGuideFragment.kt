package com.catly.oledsaver.features.view.guide

import android.content.*
import android.content.Context.CLIPBOARD_SERVICE
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat.getSystemService
import com.catly.oledsaver.R


class IntentGuideFragment: BaseGuideFragment(R.layout.intent_guide_fragment, R.id.action_intentGuideFragment_to_guideIndexFragment) {
    fun openYoutubeLink(youtubeID: String) {
        val intentApp = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + youtubeID))
        val intentBrowser = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + youtubeID))
        try {
            this.startActivity(intentApp)
        } catch (ex: ActivityNotFoundException) {
            this.startActivity(intentBrowser)
        }
    }

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
            openYoutubeLink("mJ1N7-HyH1A")
        }
    }
}