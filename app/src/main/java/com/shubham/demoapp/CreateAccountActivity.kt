package com.shubham.demoapp

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.shubham.demoapp.databinding.ActivityCreateAccountBinding
import java.util.*
import java.util.concurrent.TimeUnit

class CreateAccountActivity : AppCompatActivity() {

    lateinit var binding: ActivityCreateAccountBinding
    lateinit var auth: FirebaseAuth
    var number: String = ""
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    lateinit var storedVerificationId:String
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    lateinit var pd : ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.login_activity_bg)
        Objects.requireNonNull(supportActionBar)?.hide()
        auth = FirebaseAuth.getInstance()
        pd = ProgressDialog(this)
        pd.setMessage("Please wait")

        binding.sendOtpBtn.setOnClickListener {
            pd.show()
            login()
        }

        // Callback function for Phone Auth
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            // This method is called when the verification is completed
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
                Log.d("GFG" , "onVerificationCompleted Success")
            }

            // Called when verification is failed add log statement to see the exception
            override fun onVerificationFailed(e: FirebaseException) {
                Log.d("GFG" , "onVerificationFailed  $e")
            }

            // On code is sent by the firebase this method is called
            // in here we start a new activity where user can enter the OTP
            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d("GFG","onCodeSent: $verificationId")
                storedVerificationId = verificationId
                resendToken = token

                // Start a new activity using intent
                // also send the storedVerificationId using intent
                // we will use this id to send the otp back to firebase
                val intent = Intent(applicationContext,OtpActivity::class.java)
                intent.putExtra("storedVerificationId",storedVerificationId)
                intent.putExtra("name", binding.userNameEt.text.toString() )
                startActivity(intent)
                pd.dismiss()
                finish()
            }

        }


    }

        private fun login() {
            number = binding.mobileNumberEt.text.trim().toString()
            val name = binding.userNameEt.text.toString()
            // get the phone number from edit text and append the country cde with it
            if (number.isNotEmpty() && name.isNotEmpty()) {
                number = "+91$number"
                sendVerificationCode(number)
            } else {
                pd.dismiss()
                Toast.makeText(this, "please enter name and mobile number", Toast.LENGTH_SHORT).show()
            }
        }

        private fun sendVerificationCode(number: String) {
            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(number) // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this) // Activity (for callback binding)
                .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
            Log.d("GFG", "Auth started")
        }

    }
