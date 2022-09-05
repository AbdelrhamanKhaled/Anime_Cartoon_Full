package com.anime.rashon.speed.loyert.activities;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.anime.rashon.speed.loyert.R;
import com.anime.rashon.speed.loyert.Utilites.GoogleAuth;
import com.anime.rashon.speed.loyert.Utilites.dialogUtilities;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 100;
    Button Login , Google , Facebook;
    TextView Register ;
    EditText Email , password ;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    dialogUtilities dialogUtilities ;
    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        setListeners();
    }

    private void setListeners() {
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext() , RegisterActivity.class));
                finish();
            }
        });
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User_Login(Email.getText().toString() , password.getText().toString());
            }
        });
        Google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               signInWithGoogle();
            }
        });
        Facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             startActivity(new Intent(getBaseContext() , FacebookAuthActivity.class).setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
            }
        });
    }

    private void init() {
        Login = findViewById(R.id.Login);
        Register = findViewById(R.id.Register);
        Email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        Google = findViewById(R.id.google);
        Facebook = findViewById(R.id.facebook);
        dialogUtilities = new dialogUtilities();
        prepareSignInWithGoogle();

    }

    private void prepareSignInWithGoogle() {
        mGoogleSignInClient = GoogleAuth.getGoogleSignInClient(this);
        mAuth = FirebaseAuth.getInstance();
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Load(currentUser);
        }
    }

    private void Load(FirebaseUser firebaseUser) {
        Intent intent = new Intent(getBaseContext() , MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void User_Login(String email , String password) {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Snackbar.make(Login , "يرجي ملأ كل الحقول أولا" , Snackbar.LENGTH_SHORT).show();
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Load(user);
                        }
                        else {
                            // If sign in fails, display a message to the user.
                            Snackbar.make(Login , "المعلومات الذي أدخلتها غير صحيحه يرجي إعادة المحاولة" , Snackbar.LENGTH_SHORT).show();
                            // ...
                        }

                        // ...
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            dialogUtilities.ShowDialog(this);
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("ab_do", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                dialogUtilities.dismissDialog();
                Snackbar.make(Login , "فشل عملية تسجيل الدخول يرجي إعادة المحاولة" , Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            dialogUtilities.dismissDialog();
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("ab_do", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Load(user);
                        } else {
                            dialogUtilities.dismissDialog();
                            // If sign in fails, display a message to the user.
                            Log.w("ab_do", "signInWithCredential:failure", task.getException());
                            Snackbar.make(Login , "فشل عملية تسجيل الدخول يرجي إعادة المحاولة" , Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}