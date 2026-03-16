package com.example.uitest.viewmodel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket


class TermuxClient(private val host: String = "127.0.0.1", private val port: Int = 5000) {

    private var socket: Socket? = null
    private var reader: BufferedReader? = null
    private var writer: PrintWriter? = null

    // Connect to Termux TCP server
    suspend fun connect() = withContext(Dispatchers.IO) {
        try {
            socket = Socket(host, port)
            writer = PrintWriter(socket!!.getOutputStream(), true)
            reader = BufferedReader(InputStreamReader(socket!!.getInputStream()))
        } catch (e: Exception) {
            e.printStackTrace()
            // Optional: throw a custom error here so the ViewModel knows it failed
        }
    }

    // Send a command to the server
    suspend fun send(message: String) = withContext(Dispatchers.IO) {
        writer?.println(message)
    }

    // Receive a single line (latest)
    suspend fun receiveLine(): String? = withContext(Dispatchers.IO) {
        reader?.readLine()
    }

    // Disconnect from server
    suspend fun disconnect() = withContext(Dispatchers.IO) {
        socket?.close()
        socket = null
        reader = null
        writer = null
    }
}