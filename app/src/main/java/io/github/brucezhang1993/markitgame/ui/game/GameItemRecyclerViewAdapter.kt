package io.github.brucezhang1993.markitgame.ui.game

import android.content.Context
import android.content.pm.ApplicationInfo
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.github.brucezhang1993.markitgame.databinding.FragmentGameBinding

/**
 * [RecyclerView.Adapter] that can display a [ApplicationInfo].
 */
class GameItemRecyclerViewAdapter(
    private val values: List<ApplicationInfo>,
    private val context: Context,
    private val activatedGames: ArrayList<String>
) : RecyclerView.Adapter<GameItemRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(FragmentGameBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        val pm = context.packageManager
        holder.idView.setImageDrawable(pm.getApplicationIcon(item))
        holder.contentView.text = pm.getApplicationLabel(item)
        holder.packageName = item.packageName
        holder.initPackages()
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentGameBinding) : RecyclerView.ViewHolder(binding.root), OnClickListener {
        val idView: ImageView = binding.appIcon
        val contentView: TextView = binding.content
        private val removeButton: ImageButton = binding.removeOne
        var packageName: String = ""

        private val removeButtonListener = OnClickListener {
            println("remove button clicked")
        }

        init {
            binding.root.setOnClickListener(this)
        }

        fun initPackages() {
            if (activatedGames.contains(packageName)) {
                removeButton.visibility = View.VISIBLE
                removeButton.setOnClickListener(removeButtonListener)
            }
        }

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }

        override fun onClick(v: View?) {
            val pm = context.packageManager
            val intent = pm.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                context.startActivity(intent)
            }
        }
    }

}