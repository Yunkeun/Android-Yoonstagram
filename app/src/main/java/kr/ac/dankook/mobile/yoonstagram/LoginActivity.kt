package kr.ac.dankook.mobile.yoonstagram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kr.ac.dankook.mobile.yoonstagram.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        binding.btnEmailLogin.setOnClickListener {
            signinAndSignup()
        }
    }

    fun signinAndSignup() {
        // 회원가입한 결과값을 받아오기 위해서 addOnCompleteListener {  }
        auth?.createUserWithEmailAndPassword(binding.edtEmail.text.toString(),
            binding.edtPassword.text.toString())?.addOnCompleteListener {
                task ->
            if (task.isSuccessful) {
                moveMainPage(task.result?.user)
            } else {
                //Login if you have account
                if (task.exception?.message.equals("The email address is already in use by another account.")) {
                    signinEmail()
                }
                //Show the error message
                else {
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun signinEmail() {
        auth?.createUserWithEmailAndPassword(binding.edtEmail.text.toString(), binding.edtPassword.text.toString())
            ?.addOnCompleteListener {
                    task ->
                if (task.isSuccessful) {
                    // Login
                    moveMainPage(task.result?.user)
                } else {
                    //Show the error message
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    }

    fun moveMainPage(user: FirebaseUser?) {
        if (user != null) {
            // 다음 페이지로 넘어가는 Intent
            startActivity(Intent(this,MainActivity::class.java))
        }
    }
}