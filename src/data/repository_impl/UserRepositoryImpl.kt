package data.repository_impl

import domain.model.repository.UserRepository
import domain.model.user.User
import ui.push.PushUtil
import java.io.File
import java.io.PrintWriter

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
        if (userExists(directory = File(directory, fileName))) {
            pushUtil.pushException(push = push, "The same user exists, if it's you, please login")
        } else {
            val userFile = File(directory, fileName)
            userFile.writeText("${Field.PASSWORD}${user.password}")
            pushUtil.pushSuccess(push = push, user.email)
            println("Success registration: ${user.email}")
        }
    }

    private fun userExists(directory: File): Boolean {
        return directory.exists()
    }

    companion object {
        private const val DIRECTORY_PATH = "users"
        object Field {
            const val PASSWORD = "password:"
        }
    }

}