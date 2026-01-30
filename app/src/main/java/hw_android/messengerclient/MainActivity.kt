package hw_android.messengerclient

import android.content.res.Configuration
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import hw_android.messengerclient.chatcontent.ChatContentFragment

class MainActivity : AppCompatActivity() {

    val networkService = NetworkService()
    private val contentFragmentView : FragmentContainerView by lazy { findViewById<FragmentContainerView>(R.id.contentFragmentView) }
    private val chatListFragmentView : FragmentContainerView by lazy {findViewById<FragmentContainerView>(R.id.fragView)}
    private var openedChat : Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        openedChat = savedInstanceState?.getInt(getString(R.string.OPENED_CHAT_KEY)) ?: -1
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            if (openedChat == -1) {
                supportFragmentManager.commit {
                    show(chatListFragmentView.getFragment())
                    hide(contentFragmentView.getFragment())
                }
                
            }
            else {
                
                supportFragmentManager.commit {
                    hide(chatListFragmentView.getFragment())
                    show(contentFragmentView.getFragment())
                    addToBackStack(null)
                }
            }
            //supportFragmentManager.clearBackStack()
        }
        else {
            supportFragmentManager.commit {
                show(chatListFragmentView.getFragment())
                show(contentFragmentView.getFragment())
            }
            
        }

        if (openedChat == -1)
            contentFragmentView.visibility = View.GONE



        /*supportFragmentManager.registerFragmentLifecycleCallbacks(
            object : FragmentManager.FragmentLifecycleCallbacks() {
                override fun onFragmentDestroyed(fm: FragmentManager, fragment: Fragment) {
                    super.onFragmentDestroyed(fm, fragment)
                    if (contentFragmentView == null)
                        openedChat = null
                }
            }, false)*/

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(getString(R.string.OPENED_CHAT_KEY), openedChat)
    }


    override fun onResume() {
        super.onResume()

        val rootView = findViewById<ConstraintLayout>(R.id.main)
        rootView.post {
            val normalrect = Rect()
            rootView.getWindowVisibleDisplayFrame(normalrect)
            val normalHeight = rootView.height
            rootView.viewTreeObserver.addOnGlobalLayoutListener {
                val rect = Rect()
                rootView.getWindowVisibleDisplayFrame(rect)


                val layoutParams = rootView.layoutParams as ViewGroup.MarginLayoutParams
                layoutParams.height = normalHeight - (normalrect.bottom - rect.bottom)


                rootView.layoutParams = layoutParams
            }
        }
    }



    fun openChat(id: Int, name: String) {
        openedChat = 1
        contentFragmentView.visibility = View.VISIBLE
        val frag = ChatContentFragment()

        val bundle = Bundle().apply {
            putInt(getString(R.string.ID_KEY), id)
            putString(getString(R.string.NAME_KEY), name)
        }
        frag.arguments = bundle
        supportFragmentManager.commit {
            replace(R.id.contentFragmentView, frag)
            //hide(contentFragmentView.getFragment())

        }
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            supportFragmentManager.commit {
                hide(chatListFragmentView.getFragment())
                show(frag)
                addToBackStack(null)
            }
        }




    }
}
