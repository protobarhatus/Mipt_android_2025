package hw_android.messengerclient

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ChatContentViewModel : ViewModel() {
    private val _state = MutableStateFlow(ChatContent(0,  ArrayList<Message>()))
    val state: StateFlow<ChatContent> = _state.asStateFlow()

    fun pushMessages(mess: ChatContent) {
        _state.update { mess }

    }

}