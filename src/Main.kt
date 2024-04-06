import domain.model.repository.UserRepository
import domain.model.user.User
import ui.push.PushUtil
import ui.routing.ServerRouting
import java.net.ServerSocket

fun main() {
    val socket = ServerSocket(8888)

    val serverRouting = ServerRouting(
        userRepository = object: UserRepository {
            override fun registration(user: User): User {
                return User("", "")
            }
        },
        pushUtil = PushUtil(),
        serverSocket = socket,
    )

    serverRouting.start()

}