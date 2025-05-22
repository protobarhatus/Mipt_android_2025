package hw_android.messengerclient

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText


class NewChatNameDialogFragment : DialogFragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_new_chat_name_dialog, container, false)
        val nameInputView = view.findViewById<TextInputEditText>(R.id.nameInputView)
        view.findViewById<Button>(R.id.button).setOnClickListener l@{
            if (nameInputView.text!!.isBlank())
                return@l
            val res = Bundle()
            res.putString(getString(R.string.NEW_CHAT_NAME_KEY), nameInputView.text.toString())
            parentFragmentManager.setFragmentResult(getString(R.string.NEW_CHAT_NAME_KEY), res)
            dismiss()
        }
        return view
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    companion object {
        fun newInstance() = NewChatNameDialogFragment()
    }


}