package com.anime.rashon.speed.loyert.activities;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anime.rashon.speed.loyert.Constants.Constants;
import com.anime.rashon.speed.loyert.R;
import com.anime.rashon.speed.loyert.Utilites.LoginMethod;
import com.anime.rashon.speed.loyert.Utilites.LoginUtil;
import com.anime.rashon.speed.loyert.Utilites.dialogUtilities;
import com.anime.rashon.speed.loyert.Utilites.sharedPreferencesUtil;
import com.anime.rashon.speed.loyert.model.Information;
import com.anime.rashon.speed.loyert.model.UserResponse;
import com.anime.rashon.speed.loyert.network.ApiClient;
import com.anime.rashon.speed.loyert.network.ApiService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.io.IOException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class RegisterActivity extends AppCompatActivity {
    Button CreateAccount ;
    ImageView addPhoto;
    TextView Login ;
    EditText Username , Password , Email ;
    dialogUtilities dialogUtilities ;
    LoginUtil loginUtil ;
    Uri uploadedImg = null ;
    ActivityResultLauncher<String> activityResultRegistry ;
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

        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityResultRegistry.launch("image/*");
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
        addPhoto = findViewById(R.id.add_photo);
        loginUtil = new LoginUtil(this);
        activityResultRegistry = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                Log.i("ab_do" , "onActivityResult " + result);
                if (result!=null) {
                    uploadedImg = result ;
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), result);
                        bitmap = Bitmap.createScaledBitmap(bitmap , 120 , 120 , true);
                        addPhoto.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
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
        //SaveAuth(email , Password.getText().toString());
        createNewUser(email , Password.getText().toString() , Username.getText().toString());
    }

    private void createNewUser(String email, String password, String username) {
        String img_url = "";
        if (uploadedImg!=null) img_url = uploadedImg.toString();
        dialogUtilities.ShowDialog(this);
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        CompositeDisposable disposable = new CompositeDisposable();
        String finalImg_url = img_url;
        disposable.add(
                apiService
                        .createNewUserWithEmail(email, password, username , finalImg_url)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UserResponse>() {
                            @Override
                            public void onSuccess(UserResponse userResponse) {
                                if (!userResponse.isError()) {
                                    loginUtil.saveLoginInformation(LoginMethod.EMAIL , username , finalImg_url, userResponse.getUser().getId());
                                    Intent intent = new Intent(getBaseContext() , MainActivity.class);
                                    dialogUtilities.dismissDialog();
                                    startActivity(intent);
                                    finish();
                                }
                                else {
                                    int code = userResponse.getCode();
                                    if (code == Constants.USER_ALREADY_EXISTS) {
                                        dialogUtilities.dismissDialog();
                                        Snackbar.make(Login , "هذا الحساب موجود بالفعل !" , Snackbar.LENGTH_SHORT).show();
                                    }
                                    else {
                                        dialogUtilities.dismissDialog();
                                        Snackbar.make(Login , "هناك خطأ ما يرجي إعادة المحاولة" , Snackbar.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                dialogUtilities.dismissDialog();
                                Snackbar.make(Login , "حدث خطأ ما يرجي إعادة المحاولة" , Snackbar.LENGTH_SHORT).show();
                            }
                        })
        );
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