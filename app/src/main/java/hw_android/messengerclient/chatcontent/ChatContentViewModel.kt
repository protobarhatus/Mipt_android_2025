package hw_android.messengerclient.chatcontent

import androidx.lifecycle.ViewModel
import hw_android.messengerclient.ChatContent
import hw_android.messengerclient.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ChatContentViewModel : ViewModel() {
    private val _state = MutableStateFlow(ChatContent(0, ArrayList<Message>()))
    val state: StateFlow<ChatContent> = _state.asStateFlow()

    var hasReceivedPush = false
    fun pushMessages(mess: ChatContent) {
        hasReceivedPush = true
        _state.update { mess }
    }

}