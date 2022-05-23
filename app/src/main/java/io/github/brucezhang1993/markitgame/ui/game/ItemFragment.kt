package io.github.brucezhang1993.markitgame.ui.game

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.kyuubiran.ezxhelper.utils.getObjectOrNull
import io.github.brucezhang1993.markitgame.R

/**
 * A fragment representing a list of Items.
 */
class ItemFragment : Fragment() {

    private var columnCount = 1
    private var activatedGames = arrayListOf("com.llfz.bilibili", "com.leiting.wf.bilibili")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_game_list, container, false)

        // load preference
        val preference = context?.getSharedPreferences(getString(R.string.default_preference_key), Context.MODE_PRIVATE)
        if (preference != null) {
            val pref = preference.getObjectOrNull(getString(R.string.activated_game_list), ArrayList::class.java)
            if (pref != null) {
                @Suppress("UNCHECKED_CAST")
                activatedGames = pref as ArrayList<String>
            }
        }

        // load installed apps asynchronously
        val installedApps: LiveData<List<ApplicationInfo>> = liveData {
            val pm: PackageManager? = context?.packageManager
            val installedApps: List<ApplicationInfo>? = pm?.getInstalledApplications(PackageManager.GET_META_DATA)
            if (installedApps != null) {
                emit(installedApps.filter {
                    val isGame: Boolean = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        it.category == ApplicationInfo.CATEGORY_GAME || it.flags and ApplicationInfo.FLAG_IS_GAME != 0
                    } else {
                        it.flags and ApplicationInfo.FLAG_IS_GAME != 0
                    }
                    return@filter isGame
                })
            }
        }

        installedApps.observeForever {
            if (it == null) {
                return@observeForever
            }
            // Set the adapter
            if (view is RecyclerView) {
                with(view) {
                    layoutManager = when {
                        columnCount <= 1 -> LinearLayoutManager(context)
                        else -> GridLayoutManager(context, columnCount)
                    }
                    adapter = GameItemRecyclerViewAdapter(it, context, activatedGames)
                }
            }
        }

        return view
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            ItemFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}