package ui.routing

import domain.model.repository.FetchChatParams
import domain.model.repository.SendTextMsgToChatParams
import domain.model.repository.UserRepository
import domain.model.user.SimpleUser
import domain.model.user.User
import ui.push.PushUtil
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
import java.util.Date

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
                println("New client connected: $clientSocket")

                val line = reader.readLine()

                if (line == null) {
                    pushUtil.pushException(push = push, "404")
                    println("404")
                } else {
                    val input = line.substringBefore("?")
                    if (input.isNotEmpty()) {
                        when (input) {

                            Url.REGISTRATION -> {
                                val (email, password) = parseRegistrationLoginParams(line)

                                userRepository.registration(push = push, user = User(
                                    email = email, password = password
                                ))
                                
                            }

                            Url.LOGIN -> {
                                val (email, password) = parseRegistrationLoginParams(line)

                                userRepository.login(push = push, user = User(
                                    email = email, password = password
                                ))
                            }

                            Url.SEND_MESSAGE -> {
                                val sendTextMsgToChatParams = parseSendTextMsgToChatParams(line)

                                sendTextMsgToChatParams?.let {
                                    userRepository.sendMsgToChat(
                                        push = push, sendTextMsgToChatParams = it
                                    )
                                }
                            }

                            Url.FETCH_CHAT -> {
                                val fetchChatParams = parseFetchChatParams(line)

                                fetchChatParams?.let {
                                    userRepository.fetchChat(
                                        push = push, fetchChatParams = FetchChatParams(
                                            user1 = it.first,
                                            user2 = it.second
                                        )
                                    )
                                }
                            }

                            else -> {
                                pushUtil.pushException(push, "Cannot read this kind of url")
                            }

                        }
                    } else {
                        println("404")
                        pushUtil.pushException(push, "Illegal request")
                    }
                }
            }
        } catch (e: Exception) {
            println("Exception: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun parseRegistrationLoginParams(line: String): Pair<String, String> {
        val params = line.substringAfter("?").split("&")
        var email = ""
        var password = ""

        for (param in params) {
            val split = param.split("=")
            println("split: $split")
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
        }
        return Pair(email, password)
    }

    private fun parseFetchChatParams(line: String): Pair<SimpleUser, SimpleUser>? {
        val params = line.substringAfter("?").split("&")
        var user1: String? = null
        var user2: String? = null

        for (param in params) {
            val split = param.split("=")

            when (split[0]) {

                Params.USER1 -> {
                    user1 = split[1]
                }

                Params.USER2 -> {
                    user2 = split[1]
                }

                else -> {
                    pushUtil.pushException(push = push, "Cannot read this kind of value, please try agian")
                    continue
                }
            }
        }

        if (user1 == null) {
            pushUtil.pushException(push, "Please, send user1")
            return null
        }

        if (user2 == null) {
            pushUtil.pushException(push, "Please, send user2")
            return null
        }

        return Pair(first = SimpleUser(user1), second = SimpleUser(user2))
    }

    private fun parseSendTextMsgToChatParams(line: String): SendTextMsgToChatParams? {
        val params = line.substringAfter("?").split("&")
        var sender: SimpleUser? = null
        var receiver: SimpleUser? = null
        var newMsg: String? = null
        var date: String? = null

        for (param in params) {
            val split = param.split("=")
            when (split[0]) {

                Params.SENDER -> {
                    sender = SimpleUser(email = split[1])
                }

                Params.RECEIVER -> {
                    receiver = SimpleUser(email = split[1])
                }

                Params.NEW_MSG -> {
                    newMsg = split[1]
                }

                Params.DATE -> {
                    date = split[1]
                }

                else -> {
                    pushUtil.pushException(push = push, msg = "Cannot read this kind of value, please try agian")
                    continue
                }
            }

        }

        if (sender == null) {
            pushUtil.pushException(push = push, msg = "Please, send a sender of message")
        }

        if (receiver == null) {
            pushUtil.pushException(push = push, msg = "Please, send a receiver of message")
        }

        if (newMsg == null) {
            pushUtil.pushException(push = push, msg = "Please, send a message")
        }

        if (date == null) {
            date = Date().toString()
        }

        return SendTextMsgToChatParams(
            sender = sender!!,
            receiver = receiver!!,
            newMsg = newMsg!!,
            date = date
        )
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
            const val LOGIN = "login"
            const val SEND_MESSAGE = "send_message"
            const val FETCH_CHAT = "fetch_chat"
        }
        object Params {
            const val EMAIL = "email"
            const val PASSWORD = "password"
            const val SENDER = "sender"
            const val RECEIVER = "receiver"
            const val NEW_MSG = "newMsg"
            const val DATE = "date"
            const val USER1 = "user1"
            const val USER2 = "user2"
        }
    }

}