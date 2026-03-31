package com.example.uitest.viewmodel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
        var connected = false
        while (!connected) {
            try {
                socket = Socket(host, port)
                writer = PrintWriter(socket!!.getOutputStream(), true)
                reader = BufferedReader(InputStreamReader(socket!!.getInputStream()))
                connected = true
                println("Successfully connected to Termux!")
            } catch (e: Exception) {
                println("Connection failed, retrying in 2 seconds...")
                delay(2000) // Wait 2 seconds before trying again
            }
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