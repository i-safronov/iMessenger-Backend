package domain.model.repository

import domain.model.user.SimpleUser
import domain.model.user.User
import java.io.PrintWriter
import java.util.Date

interface UserRepository: BaseRepository {

    fun registration(push: PrintWriter, user: User)
    fun login(push: PrintWriter, user: User)
    fun sendMsgToChat(push: PrintWriter, sendTextMsgToChatParams: SendTextMsgToChatParams)

}

data class SendTextMsgToChatParams(
    val sender: SimpleUser,
    val receiver: SimpleUser,
    val newMsg: String,
    val date: String = Date().toString()
)