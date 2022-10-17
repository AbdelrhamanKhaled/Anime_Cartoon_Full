package com.anime.rashon.speed.loyert.activities;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
        dialogUtilities.ShowDialog(this);
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        CompositeDisposable disposable = new CompositeDisposable();
        disposable.add(
                apiService
                        .createNewUserWithEmail(email, password, username , "")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UserResponse>() {
                            @Override
                            public void onSuccess(UserResponse userResponse) {
                                if (!userResponse.isError()) {
                                    saveUserImg(userResponse.getUser().getId(), username , LoginMethod.EMAIL);
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

    private void saveUserImg(int user_id, String username , LoginMethod loginMethod) {
        // add api call to save user img :) and get the url of the saved img !
        if (uploadedPhotoBitmap==null) {
            load("", loginMethod, username, user_id);
            return;
        }
        String base64Img = ImgUtilities.getBase64Image(uploadedPhotoBitmap);
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        CompositeDisposable disposable = new CompositeDisposable();
        disposable.add(
                apiService
                        .saveUserImg(base64Img , user_id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<String>() {
                            @Override
                            public void onSuccess(String imgUrl) {
                                if (imgUrl.contains("null")) {
                                    Toast.makeText(getApplicationContext(), "حدث خطأ ما أثناء حفظ الصورة", Toast.LENGTH_SHORT).show();
                                }
                                load(imgUrl, loginMethod, username, user_id);
                            }

                            @Override
                            public void onError(Throwable e) {
                                dialogUtilities.dismissDialog();
                                Toast.makeText(getApplicationContext(), "حدث خطأ ما أثناء حفظ الصورة", Toast.LENGTH_SHORT).show();
                                load("", loginMethod, username, user_id);
                            }
                        })
        );
    }

    private void load(String imgUrl, LoginMethod loginMethod, String username, int user_id) {
        loginUtil.saveLoginInformation(loginMethod, username, imgUrl, user_id);
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        dialogUtilities.dismissDialog();
        startActivity(intent);
        finish();
    }


    private boolean validEmail(String email) {
        return (email.contains(".com") || email.contains(".Com") && (email.contains("yahoo") || (email.contains("hotmail") || ((email.contains("gmail")) || email.contains("Gmail"))))) ;
    }


}