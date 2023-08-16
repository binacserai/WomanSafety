package com.example.womansafety.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.womansafety.R
import com.example.womansafety.model.Guardian


class GuardianListAdapter : RecyclerView.Adapter<GuardianListAdapter.ViewHolder>() {

    private val guardiansList = mutableListOf<Guardian>()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.textViewGuardianName)
        val phoneTextView: TextView = itemView.findViewById(R.id.textViewGuardianPhone)
        val emailTextView: TextView = itemView.findViewById(R.id.textViewGuardianEmail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_guardian, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val guardian = guardiansList[position]
        holder.nameTextView.text = guardian.name
        holder.phoneTextView.text = guardian.phone
        holder.emailTextView.text = guardian.email
    }

    override fun getItemCount(): Int {
        return guardiansList.size
    }

    fun updateData(newData: List<Guardian>) {
        guardiansList.clear()
        guardiansList.addAll(newData)
        notifyDataSetChanged()
    }


    fun addGuardian(guardian: Guardian) {
        guardiansList.add(guardian)
        notifyDataSetChanged()
    }
}
