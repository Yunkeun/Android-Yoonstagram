package kr.ac.dankook.mobile.yoonstagram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kr.ac.dankook.mobile.yoonstagram.databinding.ActivitySignupBinding
import kr.ac.dankook.mobile.yoonstagram.navigation.model.UserDTO


class SignupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivitySignupBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // init
        auth = FirebaseAuth.getInstance()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup)
        database = Firebase.database.reference

        binding.btnEmailSignup.setOnClickListener {
            signup()
        }
    }
    fun signup() {
        var userEmail = binding.signupEdtEmail.text.toString()
        var userPwd = binding.signupEdtPassword.text.toString()
        var userName = binding.signUpEdtName.text.toString()
        // 입력 칸이 비어있다면 리턴
        if (TextUtils.isEmpty(userEmail) or TextUtils.isEmpty(userPwd) or TextUtils.isEmpty(userName)) {
            Toast.makeText(this, "정보를 바르게 입력해주세요", Toast.LENGTH_LONG).show()
            return
        }

        // DKU 계정인지 확인
        var emailArr = userEmail.split("@")
        if ((emailArr[0].length == 8) and (emailArr[1].equals("dankook.ac.kr"))) {

            // 회원가입한 결과값을 받아오기 위해서 addOnCompleteListener {  }
            auth?.createUserWithEmailAndPassword(userEmail, userPwd)?.addOnCompleteListener {
                    task ->
                if (task.isSuccessful) {
                    // uid, name, time db에 저장
                    setUserData(task.result?.user, userName)
                    moveLoginPage(task.result?.user)
                } else {
                    //if you have account move to login page
                    if (task.exception?.message.equals("The email address is already in use by another account.")) {
                        Toast.makeText(this, "The email address is already in use by another account.", Toast.LENGTH_LONG).show()
                    }
                    //Show the error message
                    else {
                        Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
        else {
            Toast.makeText(this, "This email is not a valid DKU email", Toast.LENGTH_LONG).show()
            return
        }
    }

    fun setUserData(user: FirebaseUser?, userName: String?) {
        var UserDTO = UserDTO()

        // uid, userId, name, time db에 저장
        if (user != null) {
            // Insert userId
            UserDTO.userId = user.email

            // Insert name
            UserDTO.userName = userName

            // Insert timeStamp
            UserDTO.timestamp = System.currentTimeMillis()

            database.child("users").child(user.uid).setValue(UserDTO)

        }
    }

    fun moveLoginPage(user: FirebaseUser?) {
        if (user != null) {
            Toast.makeText(this, "Signup Success!", Toast.LENGTH_LONG).show()
            // 다음 페이지로 넘어가는 Intent
            startActivity(Intent(this,LoginActivity::class.java))
        }
    }
}