package hw_android.messengerclient

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class ChatsListFragment : Fragment() {

    companion object {
        fun newInstance() = ChatsListFragment()
    }

    private val viewModel: ChatsListViewModel by viewModels()
    private lateinit var adapter: ChatsListAdapter


    val networkService: NetworkService by lazy { (activity as MainActivity).networkService }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_chats_list, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.chatsView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        adapter = ChatsListAdapter(ArrayList<ChatHeader>()) { i, s->
            (activity as MainActivity).openChat(i, s)
        }
        recyclerView.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { viewState ->

                    adapter.setChatsArray(viewState)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            while (isActive) {
                val tasks = networkService.apiService.chatsList()
                viewModel.pushChatsList(tasks.chats)
                delay(1000)
            }
        }

        view.findViewById<ImageButton>(R.id.createNewChatButton).setOnClickListener (::createNewChatButton)


        return view
    }

    fun createNewChatButton(p: View) {
        val frag = NewChatNameDialogFragment()

        childFragmentManager.setFragmentResultListener(getString(R.string.NEW_CHAT_NAME_KEY), this) l@{ _, result->
            val chat_name = result.getString(getString(R.string.NEW_CHAT_NAME_KEY)) ?: return@l
            lifecycleScope.launch {
                networkService.apiService.createChat(chat_name)
            }
        }
        frag.show(childFragmentManager, "")
    }
}