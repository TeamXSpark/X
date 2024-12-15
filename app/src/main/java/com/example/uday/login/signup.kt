package com.example.uday.login

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.uday.R
import com.example.uday.database.RethinkDBHelper
import com.rethinkdb.RethinkDB
import com.rethinkdb.net.Connection

class signup : AppCompatActivity() {

    private lateinit var name: EditText
    private lateinit var contact: EditText
    private lateinit var aadhar: EditText
    private lateinit var email: EditText
    private lateinit var city: EditText
    private lateinit var signupButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_page)

        // Initialize UI elements
        init()

        // Establish RethinkDB connection (ensure it's called only once)
        RethinkDBHelper.connect()

        // Set an onClickListener for the signup button
        signupButton.setOnClickListener {
            val user = User(
                name = name.text.toString(),
                contact = contact.text.toString(),
                aadhar = aadhar.text.toString(),
                email = email.text.toString(),
                city = city.text.toString()
            )

            // Call function to insert user data
            insertData(user)
        }
    }

    fun init() {
        // Initialize the views
        name = findViewById(R.id.name)
        contact = findViewById(R.id.contact)
        aadhar = findViewById(R.id.aadhar)
        email = findViewById(R.id.email)
        city = findViewById(R.id.city)
        signupButton = findViewById(R.id.signupbutt)
    }

    // Data class for User
    data class User(
        var name: String? = null,
        var contact: String? = null,
        var aadhar: String? = null,
        var email: String? = null,
        var city: String? = null
    )

    // Function to insert data into RethinkDB
    private fun insertData(user: User) {
        val connection: Connection? = RethinkDBHelper.getConnection()
        if (connection == null) {
            println("Failed to insert: Connection is null. Ensure RethinkDB is connected.")
            return
        }

        try {
            // Insert user data into 'users' table
            val result = RethinkDB.r.db("User_data") // Replace with your DB name
                .table("user") // Replace with your table name
                .insert(
                    mapOf(
                        "name" to user.name,
                        "contact" to user.contact,
                        "aadhar" to user.aadhar,
                        "email" to user.email,
                        "city" to user.city
                    )
                )
                .run(connection)

            println("Insert result: $result")
        } catch (e: Exception) {
            e.printStackTrace()
            println("Failed to insert data: ${e.message}")
        } finally {
            // Close the connection only if you do not reuse it
            connection.close()
        }
    }
}
