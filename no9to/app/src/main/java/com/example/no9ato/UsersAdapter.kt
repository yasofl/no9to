package com.example.no9ato

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UsersAdapter(private val items: List<User>, private val onClick: (User)->Unit) :
    RecyclerView.Adapter<UsersAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvUserName)
        val ivAvatar: ImageView = view.findViewById(R.id.ivAvatar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val user = items[position]
        holder.tvName.text = user.name
        holder.ivAvatar.setImageResource(android.R.drawable.sym_def_app_icon)
        holder.itemView.setOnClickListener { onClick(user) }
    }

    override fun getItemCount(): Int = items.size
}