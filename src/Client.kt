
import java.io.BufferedReader
import java.io.InputStreamReader

import java.io.PrintWriter

import java.net.Socket


fun main() {
    val clientSocket = Socket("127.0.0.1", 8888)
    val out = PrintWriter(clientSocket.getOutputStream(), true)
    val request = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
}