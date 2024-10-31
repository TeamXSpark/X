package com.example.uday.login

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.uday.R
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class otp_verification: AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var verifyButton: Button
    private lateinit var resendOtpTextView: TextView
    private lateinit var inputOtp1: EditText
    private lateinit var inputOtp2: EditText
    private lateinit var inputOtp3: EditText
    private lateinit var inputOtp4: EditText
    private lateinit var inputOtp5: EditText
    private lateinit var inputOtp6: EditText

    private lateinit var otp: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var phoneNumber: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_verification)

        otp = intent.getStringExtra("OTP") ?: ""
        intent.getParcelableExtra<PhoneAuthProvider.ForceResendingToken>("resendToken")?.let {
            resendToken = it
        }
        phoneNumber = intent.getStringExtra("phoneNumber") ?: ""

        initViews()
        addTextChangeListener()
        setupResendOtpVisibility()

        resendOtpTextView.setOnClickListener {
            resendVerificationCode()
            setupResendOtpVisibility()
        }

        verifyButton.setOnClickListener {
            val typedOtp = getTypedOtp()
            if (typedOtp.isNotEmpty() && typedOtp.length == 6) {
                val credential = PhoneAuthProvider.getCredential(otp, typedOtp)
                signInWithPhoneAuthCredential(credential)
            } else {
                Toast.makeText(this, "Please Enter Correct OTP", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupResendOtpVisibility() {
        clearOtpFields()
        resendOtpTextView.visibility = View.INVISIBLE
        resendOtpTextView.isEnabled = false

        Handler(Looper.getMainLooper()).postDelayed({
            resendOtpTextView.visibility = View.VISIBLE
            resendOtpTextView.isEnabled = true
        }, 60000)
    }

    private fun resendVerificationCode() {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .setForceResendingToken(resendToken)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            when (e) {
                is FirebaseAuthInvalidCredentialsException -> {
                    Toast.makeText(this@otp_verification, "Invalid phone number format.", Toast.LENGTH_SHORT).show()
                }
                is FirebaseTooManyRequestsException -> {
                    Toast.makeText(this@otp_verification, "Too many requests. Try again later.", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(this@otp_verification, "Verification failed. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }
            Log.d("TAG", "onVerificationFailed: ${e.message}")
        }

        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
            otp = verificationId
            resendToken = token
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Authenticated Successfully", Toast.LENGTH_SHORT).show()
                    sendToMain()
                } else {
                    Log.d("TAG", "signInWithPhoneAuthCredential: ${task.exception?.message}")
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(this, "Invalid verification code.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun sendToMain() {
        startActivity(Intent(this, signup::class.java))
    }

    private fun addTextChangeListener() {
        inputOtp1.addTextChangedListener(EditTextWatcher(inputOtp1))
        inputOtp2.addTextChangedListener(EditTextWatcher(inputOtp2))
        inputOtp3.addTextChangedListener(EditTextWatcher(inputOtp3))
        inputOtp4.addTextChangedListener(EditTextWatcher(inputOtp4))
        inputOtp5.addTextChangedListener(EditTextWatcher(inputOtp5))
        inputOtp6.addTextChangedListener(EditTextWatcher(inputOtp6))
    }

    private fun initViews() {
        auth = FirebaseAuth.getInstance()
        verifyButton = findViewById(R.id.submitotp)
        resendOtpTextView = findViewById(R.id.resendotp)
        inputOtp1 = findViewById(R.id.ic1)
        inputOtp2 = findViewById(R.id.ic2)
        inputOtp3 = findViewById(R.id.ic3)
        inputOtp4 = findViewById(R.id.ic4)
        inputOtp5 = findViewById(R.id.ic5)
        inputOtp6 = findViewById(R.id.ic6)
    }

    private fun clearOtpFields() {
        inputOtp1.setText("")
        inputOtp2.setText("")
        inputOtp3.setText("")
        inputOtp4.setText("")
        inputOtp5.setText("")
        inputOtp6.setText("")
    }

    private fun getTypedOtp(): String {
        return inputOtp1.text.toString() +
                inputOtp2.text.toString() +
                inputOtp3.text.toString() +
                inputOtp4.text.toString() +
                inputOtp5.text.toString() +
                inputOtp6.text.toString()
    }

    inner class EditTextWatcher(private val view: View) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            val text = s.toString()
            when (view.id) {
                R.id.ic1 -> if (text.length == 1) inputOtp2.requestFocus()
                R.id.ic2 -> {
                    if (text.length == 1) inputOtp3.requestFocus()
                    else if (text.isEmpty()) inputOtp1.requestFocus()
                }
                R.id.ic3 -> {
                    if (text.length == 1) inputOtp4.requestFocus()
                    else if (text.isEmpty()) inputOtp2.requestFocus()
                }
                R.id.ic4 -> {
                    if (text.length == 1) inputOtp5.requestFocus()
                    else if (text.isEmpty()) inputOtp3.requestFocus()
                }
                R.id.ic5 -> {
                    if (text.length == 1) inputOtp6.requestFocus()
                    else if (text.isEmpty()) inputOtp4.requestFocus()
                }
                R.id.ic6 -> if (text.isEmpty()) inputOtp5.requestFocus()
            }
        }
    }
}
