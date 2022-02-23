package com.example.google_sign_in

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.google_sign_in.databinding.ActivitySecondBinding
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.GraphResponse
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import org.json.JSONObject


class second_activity : AppCompatActivity() {

    private lateinit var secondBinding: ActivitySecondBinding
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        secondBinding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(secondBinding.root)

        // To Initialize the SharedPreference
        val localData = SharedPreferenceManager(this)
        val btnClickedValue = localData.fetchData("Login_Process-")

        // Work on GOOGLE Login
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        val acct = GoogleSignIn.getLastSignedInAccount(this)
        if (acct != null) {
            val personName = acct.displayName
            secondBinding.name.text = personName
            val personGivenName = acct.givenName
            val personFamilyName = acct.familyName
            val personEmail = acct.email
            secondBinding.email.text = personEmail
            val personId = acct.id
            val personPhoto: Uri? = acct.photoUrl
        }

       /* // Work on Facebook Login
        val accessToken = AccessToken.getCurrentAccessToken()
        val request = GraphRequest.newMeRequest(accessToken) { obj, _ ->
            val name = obj?.getString("name")
            secondBinding.name.text = name
            val urlProfileImg = obj?.getJSONObject("picture")?.getJSONObject("data")?.getString("url")
            secondBinding.email.text = urlProfileImg
        }
        val parameters = Bundle()
        parameters.putString("fields", "id,name,link,picture.type(large)")
        request.parameters = parameters
        request.executeAsync()*/

        secondBinding.signout.setOnClickListener {
            googleSignOut()
        }
    }

    private fun facebookSignOut(){
        LoginManager.getInstance().logOut()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun googleSignOut() {
        mGoogleSignInClient.signOut().addOnCompleteListener {
            finish()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

}