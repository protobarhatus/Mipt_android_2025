package hw_android.messengerclient

import android.content.Context
import android.content.res.Configuration
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class ChatContentFragment : Fragment() {

    companion object {
        fun newInstance() = ChatContentFragment()
    }

    private val viewModel: ChatContentViewModel by viewModels()

    val chat_id : Int by lazy { arguments?.getInt(getString(R.string.ID_KEY), -1) ?: -1 }
    val networkService: NetworkService by lazy { (activity as MainActivity).networkService }
    lateinit var adapter : ChatsContentAdapter
    lateinit var messageInputField: EditText
    var shows_no_messages_picture = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        val view = inflater.inflate(R.layout.fragment_chat_content, container, false)
        if (chat_id == -1)
        {
            view.visibility = View.INVISIBLE
            return view
        }
        messageInputField = view.findViewById<EditText>(R.id.editTextText)

        val recyclerView = view.findViewById<RecyclerView>(R.id.messagesView)
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        layoutManager.stackFromEnd = true
        recyclerView.layoutManager = layoutManager
        adapter = ChatsContentAdapter(ArrayList<Message>())
        recyclerView.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { viewState ->

                    adapter.setMessagesArray(viewState.messages)
                    noMessagesImage(view, !viewState.messages.isEmpty())
                }
            }
        }


        view.findViewById<TextView>(R.id.nameView).text = arguments?.getString(getString(R.string.NAME_KEY))

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                while (isActive) {
                    val chat = networkService.apiService.chatContent(chat_id)
                    viewModel.pushMessages(chat)
                    delay(1000)
                }
            }

        }

        view.findViewById<ImageButton>(R.id.sendButton).setOnClickListener (::sendMessage)
        view.findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(messageInputField.windowToken, 0)
            requireActivity().supportFragmentManager.popBackStack()
        }

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
            view.findViewById<ImageButton>(R.id.backButton).visibility = View.GONE


        return view
    }

    fun noMessagesImage(view: View, has_messages: Boolean) {
        if (!viewModel.hasReceivedPush)
            return
        if (has_messages && shows_no_messages_picture)
        {
            view.findViewById<ImageView>(R.id.imageView).visibility = View.GONE
            shows_no_messages_picture = false
        }
        else if (!has_messages && !shows_no_messages_picture)
        {
            val imageView = view.findViewById<ImageView>(R.id.imageView)
            Glide.with(this)
                .load(getString(R.string.no_messages_picture))
                .into(imageView)
            imageView.visibility = View.VISIBLE
            shows_no_messages_picture = true
        }
    }

    fun sendMessage(p: View) {
        if (messageInputField.text.isBlank())
            return
        val message: String = messageInputField.text.toString()
        messageInputField.text.clear()
        lifecycleScope.launch {
            networkService.apiService.sendMessage(chat_id, message)
        }
    }
}