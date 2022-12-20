package com.shubham.demoapp

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.shubham.demoapp.databinding.ActivityOtpBinding
import java.util.*

class OtpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOtpBinding
    // get reference of the firebase auth
    private lateinit var auth: FirebaseAuth
    private lateinit var name:String
    lateinit var pd : ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.login_activity_bg)
        Objects.requireNonNull(supportActionBar)?.hide()
        auth= FirebaseAuth.getInstance()
        // get storedVerificationId from the intent
        val storedVerificationId= intent.getStringExtra("storedVerificationId")
        name = intent.getStringExtra("name").toString()
        pd = ProgressDialog(this)
        pd.setMessage("Please wait")

        binding.checkOtpBtn.setOnClickListener{
            pd.show()
            val otp = binding.otpEt.text.trim().toString()
            if(otp.isNotEmpty()){
                val credential : PhoneAuthCredential = PhoneAuthProvider.getCredential(
                    storedVerificationId.toString(), otp)
                signInWithPhoneAuthCredential(credential)
            }else{
                Toast.makeText(this,"Enter OTP", Toast.LENGTH_SHORT).show()
            }
        }
        }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    saveDataToDatabase()
                    val intent = Intent(this , UserType::class.java)
                    startActivity(intent)
                    pd.dismiss()
                    finish()
                } else {
                    // Sign in failed, display a message and update the UI
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        pd.dismiss()
                        Toast.makeText(this,"Invalid OTP", Toast.LENGTH_SHORT).show()
                    }
                }
    }
}

    private fun saveDataToDatabase() {
        val userId = auth.currentUser!!.uid
        FirebaseDatabase.getInstance().reference.child("user").child(userId).setValue(name)
    }
}