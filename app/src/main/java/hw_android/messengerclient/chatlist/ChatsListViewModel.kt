package hw_android.messengerclient.chatlist

import androidx.lifecycle.ViewModel
import hw_android.messengerclient.ChatHeader
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class ChatsListViewModel : ViewModel() {

    private val _state = MutableStateFlow(ArrayList<ChatHeader>())
    val state: StateFlow<java.util.ArrayList<ChatHeader>> = _state.asStateFlow()

    fun pushChatsList(chats: ArrayList<ChatHeader>) {
        _state.update { chats }

    }
}