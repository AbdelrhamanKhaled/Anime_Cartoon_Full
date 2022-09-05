package com.anime.rashon.speed.loyert.activities;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.anime.rashon.speed.loyert.R;
import com.anime.rashon.speed.loyert.Utilites.dialogUtilities;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends AppCompatActivity {
    Button CreateAccount ;
    TextView Login ;
    EditText Username , Password , Email ;
    dialogUtilities dialogUtilities ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Init();
        SetListenersToButtons();
    }

    private void SetListenersToButtons() {
        CreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAcc();
            }
        });
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext() , LoginActivity.class));
                finish();
            }
        });
    }

    private void Init() {
        CreateAccount = findViewById(R.id.CreateAccount);
        Login = findViewById(R.id.Login);
        Username = findViewById(R.id.Username);
        Password = findViewById(R.id.Password);
        Email = findViewById(R.id.email) ;
        dialogUtilities = new dialogUtilities();
    }

    private void CreateAcc() {

        String email = Email.getText().toString();
        if (TextUtils.isEmpty(Username.getText()) || TextUtils.isEmpty(Password.getText())) {
            Snackbar.make(CreateAccount , "يرجي ملأ كل الحقول أولا" , Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() || TextUtils.isEmpty(email) || !validEmail(email)) {
            Snackbar.make(CreateAccount , "يرجي إدخال بريد إالكتروني صحيح" , Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (Password.getText().length()<6) {
            Snackbar.make(CreateAccount , "يجب أن تكون كلمة المرور أطول من 6 حروف" , Snackbar.LENGTH_SHORT).show();
            return;
        }
        SaveAuth(email , Password.getText().toString());
    }

    private boolean validEmail(String email) {
        return (email.contains(".com") || email.contains(".Com") && (email.contains("yahoo") || (email.contains("hotmail") || ((email.contains("gmail")) || email.contains("Gmail"))))) ;
    }

    private void SaveAuth(String email , String password) {
        dialogUtilities.ShowDialog(this);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(Username.getText().toString()).build();
                if (mAuth.getCurrentUser()!=null)
                mAuth.getCurrentUser().updateProfile(profileUpdates);
                Intent intent = new Intent(getBaseContext() , MainActivity.class);
                dialogUtilities.dismissDialog();
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG","On Failure" + e.getMessage());
                dialogUtilities.dismissDialog();
                Snackbar.make(Login , "هناك خطأ ما يرجي إعادة المحاولة" , Snackbar.LENGTH_SHORT).show();
            }
        });
    }

}