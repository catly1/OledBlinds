package com.catly.letterboxer.view

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.catly.letterboxer.R
import com.catly.letterboxer.adapter.InstalledAppAdapter
import com.catly.letterboxer.data.model.InstalledApp
import com.catly.letterboxer.floating_window.AppWatcherService

/**
 * Lets the user pick which apps the watcher reacts to. The list comes from the launcher intent
 * query, which the manifest `<queries>` entry makes visible without the Play restricted
 * QUERY_ALL_PACKAGES permission.
 */
class AppPickerFragment : Fragment() {

    private val handler = Handler(Looper.getMainLooper())
    private val selected = mutableSetOf<String>()
    private var allApps: List<InstalledApp> = emptyList()
    private var adapter: InstalledAppAdapter? = null
    private var loadThread: Thread? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.app_picker_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_white_24dp)
        toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        selected.clear()
        selected.addAll(
            AppWatcherService.watchedPackages(
                PreferenceManager.getDefaultSharedPreferences(requireContext())
            )
        )

        val itemAdapter = InstalledAppAdapter(requireContext().applicationContext, selected) {
            saveSelection()
        }
        adapter = itemAdapter
        view.findViewById<RecyclerView>(R.id.recycler_view).adapter = itemAdapter

        view.findViewById<EditText>(R.id.app_search).addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                applyFilter(s?.toString().orEmpty())
            }
        })

        loadApps()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
        loadThread?.interrupt()
        loadThread = null
        adapter?.shutdown()
        adapter = null
    }

    private fun loadApps() {
        val appContext = requireContext().applicationContext
        // Snapshot rather than reading the live set from the background thread: the adapter mutates
        // it from the main thread as soon as the list appears.
        val alreadySelected = HashSet(selected)
        // A couple of hundred label lookups each hit the target app's resources, which is far too
        // slow for the main thread.
        val thread = Thread {
            val packageManager = appContext.packageManager
            val launcherIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
            val resolved = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.queryIntentActivities(
                    launcherIntent,
                    PackageManager.ResolveInfoFlags.of(0L)
                )
            } else {
                @Suppress("DEPRECATION")
                packageManager.queryIntentActivities(launcherIntent, 0)
            }
            val apps = resolved
                // Several apps ship more than one launcher activity; duplicate rows would let the
                // checkbox state of one copy contradict the other.
                .distinctBy { it.activityInfo.packageName }
                .filter { it.activityInfo.packageName != appContext.packageName }
                .map {
                    InstalledApp(
                        it.activityInfo.packageName,
                        it.loadLabel(packageManager).toString()
                    )
                }
                .sortedWith(
                    compareBy({ !alreadySelected.contains(it.packageName) }, { it.label.lowercase() })
                )
            handler.post {
                if (!isAdded || view == null) return@post
                allApps = apps
                view?.findViewById<ProgressBar>(R.id.loading)?.visibility = View.GONE
                val query = view?.findViewById<EditText>(R.id.app_search)?.text?.toString().orEmpty()
                applyFilter(query)
            }
        }
        loadThread = thread
        thread.start()
    }

    private fun applyFilter(query: String) {
        val trimmed = query.trim()
        val filtered = if (trimmed.isEmpty()) {
            allApps
        } else {
            allApps.filter { it.label.contains(trimmed, ignoreCase = true) }
        }
        adapter?.update(filtered)
    }

    private fun saveSelection() {
        PreferenceManager.getDefaultSharedPreferences(requireContext()).edit()
            .putStringSet(AppWatcherService.WATCHED_PACKAGES_KEY, HashSet(selected))
            .apply()
        // An empty set means "watch nothing", not "fall back to the defaults", so say so rather
        // than leave the feature quietly dead.
        if (selected.isEmpty()) {
            Toast.makeText(requireContext(), R.string.watched_apps_empty, Toast.LENGTH_SHORT).show()
        }
    }
}
