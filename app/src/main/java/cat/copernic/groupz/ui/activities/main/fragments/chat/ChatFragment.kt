package cat.copernic.groupz.ui.activities.main.fragments.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.drawerlayout.widget.DrawerLayout
import androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cat.copernic.groupz.R
import cat.copernic.groupz.databinding.FragmentChatBinding
import cat.copernic.groupz.model.Message
import cat.copernic.groupz.network.FirestoreUtil
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.fragment_chat.*
import java.util.*
import kotlin.collections.ArrayList


class ChatFragment : Fragment() {
   // private var chatRecycler: RecyclerView? = null
   // private var chatAdapter: ChatAdapter? = null
    private lateinit var btndrawerLayout: ImageButton
    private lateinit var drawerLayout: DrawerLayout
    private val args by navArgs<ChatFragmentArgs>()
    private lateinit var binding: FragmentChatBinding
    private lateinit var messagesListenerRegistration: ListenerRegistration
    private var shouldInitRecyclerView = true
    private lateinit var messagesSection: Section
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_chat, container, false)
        binding = FragmentChatBinding.bind(view)
        btndrawerLayout = activity?.findViewById(R.id.btnMenu)!!
        drawerLayout = activity?.findViewById(R.id.drawerLayout)!!
        drawerLayout.setDrawerLockMode(LOCK_MODE_LOCKED_CLOSED)
        btndrawerLayout.visibility = View.GONE
        activity?.findViewById<ImageButton>(R.id.btnBack)!!.visibility = View.VISIBLE
        activity?.findViewById<ImageButton>(R.id.btnNotifications)!!.visibility = View.GONE
        activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)!!.visibility =
            View.GONE

        val otherUserId = args.userId
        FirestoreUtil.getOrCreateChatChannel(otherUserId) { channelId ->
            messagesListenerRegistration =
                FirestoreUtil.addChatMessageListener(channelId, this::updateRecyclerView)
            ivSendMessage.setOnClickListener {
                val messageToSend =
                    TextMessage(
                        etMessage.text.toString(),
                        Calendar.getInstance().time,
                        FirebaseAuth.getInstance().currentUser!!.uid,
                        MessageType.TEXT
                    )
                etMessage.setText("")
                FirestoreUtil.sendMessage(messageToSend, channelId)
            }
        }




        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
      //  chatRecycler = view.findViewById(R.id.rvMessages)
     //   val categoryItemList: MutableList<Message> = ArrayList()
        //  categoryItemList.add(Message("Hola", "Manolo"))
        // categoryItemList.add(Message("Hola", FirebaseClient.auth.currentUser?.email as String))
        //  categoryItemList.add(Message("Como Estas?", "Manolo"))
       // setChatRecycler(categoryItemList)

//        binding.sendButton.setOnClickListener{
//            binding.messageText.setText("")
//        }
    }

//    private fun setChatRecycler(Messages: List<Message>) {
//        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
//        chatRecycler!!.layoutManager = layoutManager
//        chatAdapter = ChatAdapter(Messages)
//        chatRecycler!!.adapter = chatAdapter
//    }

    private fun updateRecyclerView(messages: List<Item>) {
        fun init() {
            rvMessages.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = GroupAdapter<ViewHolder>().apply {
                    messagesSection = Section(messages)
                    this.add(messagesSection)
                }
            }
            shouldInitRecyclerView = false
        }

        fun updateItems() = messagesSection.update(messages)

        if (shouldInitRecyclerView)
            init()
        else
            updateItems()

        rvMessages.scrollToPosition(rvMessages.adapter!!.itemCount - 1)
    }


}