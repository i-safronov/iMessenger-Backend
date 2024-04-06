import data.repository_impl.UserRepositoryImpl
import ui.push.PushUtil
import ui.routing.ServerRouting
import java.net.ServerSocket

fun main() {
    val socket = ServerSocket(8888)
    val pushUtil = PushUtil()

    val serverRouting = ServerRouting(
        userRepository = UserRepositoryImpl(pushUtil),
        pushUtil = pushUtil,
        serverSocket = socket,
    )

    serverRouting.start()
}

/** Documentation:
 * / - http://127.0.0.1:8888
 *
 * Registration:
 * /registration?email=some@gmail.com&password=*****
 * Response: success:some@gmail.com
 * Error: exception:msg
 *
 * Login:
 * /login?email=some@gmail.com&password=*****
 * Response: success:some@gmail.com
 * Error: exception:msg
 *
 * */