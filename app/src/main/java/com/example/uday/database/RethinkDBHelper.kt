package com.example.uday.database
import com.rethinkdb.RethinkDB
import com.rethinkdb.net.Connection

object RethinkDBHelper {
    private val r: RethinkDB = RethinkDB.r
    private var connection: com.rethinkdb.net.Connection? = null

    fun connect() {
        try {
            connection = r.connection()
                .hostname("10.160.33.119") // e.g., "192.168.1.100" or "localhost"
                .port(8080)
                .connect()
            println("Connected to RethinkDB")
        } catch (e: Exception) {
            e.printStackTrace()
            println("Failed to connect to RethinkDB: ${e.message}")
        }
    }

    fun getConnection(): Connection? {
        return connection
    }
}
