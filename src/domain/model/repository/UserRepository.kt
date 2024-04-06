package domain.model.repository

import domain.model.user.SimpleUser
import domain.model.user.User
import java.io.PrintWriter
import java.util.Date

interface UserRepository: BaseRepository {

    fun registration(push: PrintWriter, user: User)
    fun login(push: PrintWriter, user: User)
    fun sendMsgToChat(push: PrintWriter, sendTextMsgToChatParams: SendTextMsgToChatParams)
    fun fetchChat(push: PrintWriter, fetchChatParams: FetchChatParams)

}

data class SendTextMsgToChatParams(
    val sender: SimpleUser,
    val receiver: SimpleUser,
    val newMsg: String,
    val date: String = Date().toString()
)

data class FetchChatParams(
    val user1: SimpleUser,
    val user2: SimpleUser
)

data class Chat(
    val chats: List<ChatField>
)

data class ChatField(
    val sender: SimpleUser,
    val date: String,
    val msg: String
)