package hw_android.messengerclient.chatcontent

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hw_android.messengerclient.Message
import hw_android.messengerclient.R

class ChatsContentAdapter (var messages: ArrayList<Message>) : RecyclerView.Adapter<ChatsContentAdapter.ViewHolder>() {
    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val messageView : TextView = view.findViewById<TextView>(R.id.messageView)

    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.message_bubble, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.messageView.text = messages[position].text
    }

    fun setMessagesArray(mes: ArrayList<Message>) {
        this.messages = mes
        notifyDataSetChanged()
    }

}
