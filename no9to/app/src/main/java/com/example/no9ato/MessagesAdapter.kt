package com.example.no9ato

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class Message(var id: String = "", var senderId: String = "", var text: String = "", var type: String = "text", var timestamp: Long = 0)

class MessagesAdapter(private val items: List<Message>) : RecyclerView.Adapter<MessagesAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvMessage: TextView = view.findViewById(R.id.tvMessage)
        val ivImage: ImageView? = view.findViewById(R.id.ivMessageImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val m = items[position]
        if (m.type == "text") {
            holder.tvMessage.text = m.text
            holder.ivImage?.visibility = View.GONE
        } else if (m.type == "image") {
            holder.tvMessage.text = "[صورة]"
            holder.ivImage?.visibility = View.VISIBLE
            // open image in viewer on click
            holder.ivImage?.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(m.text))
                holder.itemView.context.startActivity(intent)
            }
        } else if (m.type == "audio") {
            holder.tvMessage.text = "[صوت] اضغط لتشغيل"
            holder.ivImage?.visibility = View.GONE
            holder.itemView.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(m.text))
                holder.itemView.context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int = items.size
}