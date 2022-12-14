package com.anime.rashon.speed.loyert.activities;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anime.rashon.speed.loyert.Constants.Constants;
import com.anime.rashon.speed.loyert.R;
import com.anime.rashon.speed.loyert.Utilites.ImgUtilities;
import com.anime.rashon.speed.loyert.Utilites.LoginMethod;
import com.anime.rashon.speed.loyert.Utilites.LoginUtil;
import com.anime.rashon.speed.loyert.Utilites.dialogUtilities;
import com.anime.rashon.speed.loyert.app.Config;
import com.anime.rashon.speed.loyert.model.UserResponse;
import com.anime.rashon.speed.loyert.network.ApiClient;
import com.anime.rashon.speed.loyert.network.ApiService;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;

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
    Bitmap uploadedPhotoBitmap;
    ActivityResultLauncher<String> activityResultRegistry ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Config.updateTheme(this);
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
                activityResultRegistry.launch("image/jpeg");
            }
        });

    }

//    public String getMimeType(Uri uri) {
//        String mimeType = null;
//        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
//            ContentResolver cr = getApplicationContext().getContentResolver();
//            mimeType = cr.getType(uri);
//        } else {
//            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
//                    .toString());
//            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
//                    fileExtension.toLowerCase());
//        }
//        return mimeType;
//    }

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
                   // Log.i("ab_do" , "mimeType = " + getMimeType(result));
                    uploadedImg = result ;
                    try {
                        uploadedPhotoBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), result);
                        // uploadedPhotoBitmap = Bitmap.createScaledBitmap(uploadedPhotoBitmap, 120 , 120 , true);
                        //addPhoto.setImageBitmap(uploadedPhotoBitmap);
                        Glide.with(getBaseContext())
                                .load(result)
                                .centerCrop()
                                .into(addPhoto);
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
            Snackbar.make(CreateAccount , "???????? ?????? ???? ???????????? ????????" , Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() || TextUtils.isEmpty(email) || !validEmail(email)) {
            Snackbar.make(CreateAccount , "???????? ?????????? ???????? ?????????????????? ????????" , Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (Password.getText().length()<6) {
            Snackbar.make(CreateAccount , "?????? ???? ???????? ???????? ???????? ???????? ???? 6 ????????" , Snackbar.LENGTH_SHORT).show();
            return;
        }
        //SaveAuth(email , Password.getText().toString());
        dialogUtilities.ShowDialog(this);
        CompositeDisposable disposable = new CompositeDisposable();
        ApiService apiService = ApiClient.getClient(getApplicationContext()).create(ApiService.class);
        disposable.add(
                apiService
                        .checkIfEmailExits(email)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UserResponse>() {
                            @Override
                            public void onSuccess(UserResponse response) {
                                if (response.getCode() == Constants.USER_ALREADY_EXISTS) {
                                    dialogUtilities.dismissDialog();
                                    Snackbar.make(Login , "?????? ???????????? ?????????? ???????????? !" , Snackbar.LENGTH_SHORT).show();
                                }
                                else {
                                       sendVerifyCode(email);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                dialogUtilities.dismissDialog();
                                Log.i("ab_do", "error when make report");
                                Toast.makeText(getApplicationContext(), "?????? ?????? ???? ???????? ?????????? ????????????????", Toast.LENGTH_SHORT).show();
                            }
                        })
        );
    }

    @SuppressLint("SuspiciousIndentation")
    private void goToVerifyActivity(String email , int code) {
        Intent intent = new Intent(getBaseContext() , OtpVerifyActivity.class);
        intent.putExtra("email" , email);
        intent.putExtra("code" , code);
        intent.putExtra("password" , Password.getText().toString());
        intent.putExtra("username" , Username.getText().toString());
        if (uploadedImg!=null)
        intent.putExtra("photo" , uploadedImg.toString());
        startActivity(intent);
        finish();
    }

    private void sendVerifyCode(String email) {
        int code = (int)(Math.random()*9000)+1000;
        CompositeDisposable disposable = new CompositeDisposable();
        ApiService apiService = ApiClient.getClient(getApplicationContext()).create(ApiService.class);
        disposable.add(
                apiService
                        .sendOtpToEmail(email, code)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UserResponse>() {
                            @Override
                            public void onSuccess(UserResponse response) {
                                if (!response.isError()) {
                                    dialogUtilities.dismissDialog();
                                    goToVerifyActivity(email  , code);
                                } else {
                                    Toast.makeText(getApplicationContext(), "?????? ?????? ???? ???????? ?????????? ????????????????", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.i("ab_do", "error when make report");
                                Toast.makeText(getApplicationContext(), "?????? ?????? ???? ???????? ?????????? ????????????????" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
        );

    }

    private boolean validEmail(String email) {
        return (email.contains(".com") || email.contains(".Com") && (email.contains("yahoo") || (email.contains("hotmail") || ((email.contains("gmail")) || email.contains("Gmail"))))) ;
    }


}