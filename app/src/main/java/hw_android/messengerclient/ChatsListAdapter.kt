package hw_android.messengerclient

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Visibility

class ChatsListAdapter (var chats: ArrayList<ChatHeader>, val onChatClick : (Int, String)-> Unit) : RecyclerView.Adapter<ChatsListAdapter.ViewHolder>() {
    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val chatNameTV : TextView = view.findViewById<TextView>(R.id.chatName)
    }

    override fun getItemCount(): Int {
        return chats.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.chatNameTV.text = if (!chats[position].name.isBlank()) chats[position].name else holder.view.context.getString(R.string.no_chat_name_notif)
        holder.chatNameTV.setTextColor(
            if (!chats[position].name.isEmpty())
                holder.view.context.getColor(R.color.black)
            else
                holder.view.context.getColor(R.color.red)
        )

       /* if (chats[position].lastMessage?.isEmpty() ?: true)
            //holder.lastMessageTV.setBackgroundColor(holder.view.context.getColor(R.color.gray))
        else
            //holder.lastMessageTV.setBackgroundColor(holder.view.context.getColor(R.color.white))*/
        holder.view.setOnClickListener {
            onChatClick(chats[position].id, chats[position].name)
        }
    }

    fun setChatsArray(cihats: ArrayList<ChatHeader>) {
        this.chats = cihats
        notifyDataSetChanged()
    }

}
