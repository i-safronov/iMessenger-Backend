package data.repository_impl

import domain.model.repository.SendTextMsgToChatParams
import domain.model.repository.UserRepository
import domain.model.user.SimpleUser
import domain.model.user.User
import ui.push.PushUtil
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.util.regex.Pattern

class UserRepositoryImpl(
    private val pushUtil: PushUtil
): UserRepository {

    override fun init(directoryPath: String): File {
        val directory = File(directoryPath)
        if (!directory.exists()) {
            directory.mkdir()
        }
        return directory
    }

    override fun registration(push: PrintWriter, user: User) {
        val directory = init(USER_DIRECTORY_PATH)
        val fileName = "${user.email}.txt"
        val userFile = File(directory, fileName)
        if (userFile.exists()) {
            pushUtil.pushException(push = push, "The same user exists, if it's you, please login")
        } else {
            userFile.writeText("${Field.PASSWORD}${user.password};")
            pushUtil.pushSuccess(push = push, user.email)
            println("Success registration: ${user.email}")
        }
    }

    override fun login(push: PrintWriter, user: User) {
        val directory = init(USER_DIRECTORY_PATH)
        val fileName = "${user.email}.txt"
        val userFile = File(directory, fileName)
        if (!userFile.exists()) {
            pushUtil.pushException(push = push, msg = "You're not not in the system, please register")
        } else {
            val content = userFile.readText()
            println("content: $content")
            val matcher = Pattern.compile("password=(.*?);").matcher(content)
            val matched = matcher.find()
            if (matched) {
                val password = matcher.group(1)

                if (password == user.password) {
                    pushUtil.pushSuccess(push = push, msg = user.email)
                } else {
                    pushUtil.pushException(push = push, msg = "Wrong password")
                }

            } else {
                pushUtil.pushException(push = push, msg = "When the system were reading a file, something went wrong with password, please try again")
            }
        }
    }

    override fun sendMsgToChat(push: PrintWriter, sendTextMsgToChatParams: SendTextMsgToChatParams) {
        val directory = init(USER_DIRECTORY_PATH)
        val user1File = File(directory, "${sendTextMsgToChatParams.sender.email}.txt")
        val user2File = File(directory, "${sendTextMsgToChatParams.receiver.email}.txt")
        if (!user1File.exists() || !user2File.exists()) {
            pushUtil.pushException(push = push, msg = "Cannot find all users, please make sure that all users in the system")
        } else {
            val chat = findChat(directory = init(CHAT_DIRECTORY_PATH), user1 = sendTextMsgToChatParams.sender, user2 = sendTextMsgToChatParams.receiver)
            val fileWriter = FileWriter(chat, true)
            val bufferedWriter = BufferedWriter(fileWriter)
            bufferedWriter.write(
                "${Field.SENDER}${sendTextMsgToChatParams.sender.email}\n"
            )
            bufferedWriter.write(
                "${Field.DATE}${sendTextMsgToChatParams.date}\n"
            )
            bufferedWriter.write(
                "${Field.MSG}${sendTextMsgToChatParams.newMsg};"
            )
            bufferedWriter.newLine()

            pushUtil.pushSuccess(push = push, msg = "Message saved successfully!")

            bufferedWriter.close()
            fileWriter.close()
        }
    }

    private fun findChat(directory: File, user1: SimpleUser, user2: SimpleUser): File {
        val part1 = File(directory, "chat_${user1.email}+${user2.email}")
        return if (!part1.exists()) {
            val part2 = File(directory, "chat_${user2.email}+${user1.email}")
            part2
        } else {
            part1
        }
    }

    companion object {
        private const val USER_DIRECTORY_PATH = "users"
        private const val CHAT_DIRECTORY_PATH = "chats"
        object Field {
            const val PASSWORD = "password="
            const val SENDER = "sender="
            const val MSG = "msg="
            const val DATE = "date="
        }
    }

}