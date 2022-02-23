package com.example.google_sign_in

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.google_sign_in.databinding.ActivityMainBinding
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import java.security.MessageDigest


class MainActivity : AppCompatActivity() {

    private lateinit var mainActivityBinding : ActivityMainBinding
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var callbackManager: CallbackManager
    private val RC_SIGN_IN = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivityBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainActivityBinding.root)
        printHashKey(applicationContext)

        /*//Hare we just checking the last Sign in Account
        if (isLoggedIn()){
            navigateToNextPage()
        }*/

        // To Initialize the SharedPreference
        val localData = SharedPreferenceManager(this)

        // Init google sign_in object
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        // Hare we just checking the last Sign in Account
        val acct = GoogleSignIn.getLastSignedInAccount(this)
        if (acct == null) {
            //navigateToNextPage()
        }else{
            navigateToNextPage()
        }
        mainActivityBinding.googleBtn.setOnClickListener {
            localData.saveDataToSharedPreference("Login_Process-", "Google")
            signIn()
        }

        //callbackManager to handle login responses by calling CallbackManager.Factory.create
        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult?> {
                override fun onCancel() {
                }

                override fun onError(error: FacebookException) {
                    error.printStackTrace()
                }

                override fun onSuccess(result: LoginResult?) {
                    navigateToNextPage()
                }

            })

        mainActivityBinding.facebookBtn.setOnClickListener {
            localData.saveDataToSharedPreference("Login_Process-", "Facebook")
            LoginManager.getInstance().logInWithReadPermissions(this, listOf("public_profile"));
        }
    }

    //This Function used for checking the last Sign in Account
    private fun isLoggedIn(): Boolean {
        val accessToken = AccessToken.getCurrentAccessToken()
        return accessToken != null && !accessToken.isExpired
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleSignInResult(task)
            navigateToNextPage()
        }
    }

    private fun signIn() {
        // This is the Old Process startActivityForResult() method was Deprecated
        /*val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(
            signInIntent, RC_SIGN_IN
        )*/
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    // This is the Old Process onActivityResult() method was Deprecated
    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
            navigateToNextPage()
        }
    }*/

    private fun navigateToNextPage() {
            val intent = Intent(this, second_activity::class.java)
            startActivity(intent)
            finish()
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(
                ApiException::class.java
            )
            // Signed in successfully
            val googleId = account?.id ?: ""
            Log.i("Google ID",googleId)

            val googleFirstName = account?.givenName ?: ""
            Log.i("Google First Name", googleFirstName)

            val googleLastName = account?.familyName ?: ""
            Log.i("Google Last Name", googleLastName)

            val googleEmail = account?.email ?: ""
            Log.i("Google Email", googleEmail)

            val googleProfilePicURL = account?.photoUrl.toString()
            Log.i("Google Profile Pic URL", googleProfilePicURL)

            val googleIdToken = account?.idToken ?: ""
            Log.i("Google ID Token", googleIdToken)

        } catch (e: ApiException) {
            // Sign in was unsuccessful
            Log.e(
                "failed code=", e.statusCode.toString()
            )
        }
    }

    private fun printHashKey(context: Context) {

        try {
            val info = context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey = String(Base64.encode(md.digest(), 0))
                Log.i("AppLog", "key:$hashKey=")
            }
        } catch (e: Exception) {
            Log.e("AppLog", "error:", e)
        }

    }

}