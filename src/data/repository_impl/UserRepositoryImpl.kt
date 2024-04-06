package data.repository_impl

import domain.model.repository.UserRepository
import domain.model.user.User
import ui.push.PushUtil
import java.io.File
import java.io.PrintWriter
import java.util.regex.Pattern

class UserRepositoryImpl(
    private val pushUtil: PushUtil
): UserRepository {

    override fun init(): File {
        val directory = File(DIRECTORY_PATH)
        if (!directory.exists()) {
            directory.mkdir()
        }
        return directory
    }

    override fun registration(push: PrintWriter, user: User) {
        val directory = init()
        val fileName = "${user.email}.txt"
        val userFile = File(directory, fileName)
        if (userExists(directory = userFile)) {
            pushUtil.pushException(push = push, "The same user exists, if it's you, please login")
        } else {
            userFile.writeText("${Field.PASSWORD}${user.password};")
            pushUtil.pushSuccess(push = push, user.email)
            println("Success registration: ${user.email}")
        }
    }

    override fun login(push: PrintWriter, user: User) {
        val directory = init()
        val fileName = "${user.email}.txt"
        val userFile = File(directory, fileName)
        if (!userExists(directory = userFile)) {
            pushUtil.pushException(push = push, msg = "You're not not in the system, please register")
        } else {
            val content = userFile.readText()
            println("content: $content")
            val matcher = Pattern.compile("password=(.*?);").matcher(content)
            val matched = matcher.find()
            if (matched) {
                val password = matcher.group(1)

                if (password == user.password) {
                    pushUtil.pushSuccess(push = push, msg = "You logged in, email: ${user.email}")
                } else {
                    pushUtil.pushException(push = push, msg = "Wrong password")
                }

            } else {
                pushUtil.pushException(push = push, msg = "When the system were reading a file, something went wrong with password, please try again")
            }
        }
    }

    private fun userExists(directory: File): Boolean {
        return directory.exists()
    }

    companion object {
        private const val DIRECTORY_PATH = "users"
        object Field {
            const val PASSWORD = "password="
        }
    }

}