package ui.routing

import domain.model.repository.UserRepository
import domain.model.user.User
import ui.push.PushUtil
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket

class ServerRouting(
    private val userRepository: UserRepository,
    private val pushUtil: PushUtil,
    private val serverSocket: ServerSocket,
) {

    private lateinit var push: PrintWriter
    private lateinit var reader: BufferedReader
    private lateinit var clientSocket: Socket

    fun start() {
        try {
            while (true) {
                clientSocket = serverSocket.accept()
                push = PrintWriter(clientSocket.getOutputStream(), true)
                reader = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
                println("New client connected: $clientSocket, ${reader.readLine()}")

                val line = reader.readLine()

                if (line == null) {
                    pushUtil.pushException(push = push, "404")
                    println("404")
                } else {
                    val input = line.substringBefore("?")
                    if (input.isNotEmpty()) {
                        when (input) {

                            Url.REGISTRATION -> {
                                val params = input.substringAfter("?").split("&")
                                var email = ""
                                var password = ""

                                for (param in params) {
                                    val split = param.split("=")
                                    when (split[0]) {

                                        Params.EMAIL -> {
                                            email = split[1]
                                        }

                                        Params.PASSWORD -> {
                                            password = split[1]
                                        }

                                        else -> {
                                            pushUtil.pushException(push = push, "Cannot read this kind of value, please try agian")
                                            continue
                                        }
                                    }

                                    userRepository.registration(user = User(
                                        email = email, password = password
                                    ))

                                }
                            }

                            else -> {
                                pushUtil.pushException(push, "Cannot read this kind of url")
                            }

                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stop() {
        reader.close()
        push.close()
        clientSocket.close()
        serverSocket.close()
    }

    companion object {
        object Url {
            const val REGISTRATION = "registration"
        }
        object Params {
            const val EMAIL = "email"
            const val PASSWORD = "password"
        }
    }

}