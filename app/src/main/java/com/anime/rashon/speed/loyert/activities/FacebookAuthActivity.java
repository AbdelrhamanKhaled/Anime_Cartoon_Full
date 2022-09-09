package com.anime.rashon.speed.loyert.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.anime.rashon.speed.loyert.Utilites.LoginMethod;
import com.anime.rashon.speed.loyert.Utilites.LoginUtil;
import com.anime.rashon.speed.loyert.activities.MainActivity;
import com.anime.rashon.speed.loyert.model.UserResponse;
import com.anime.rashon.speed.loyert.network.ApiClient;
import com.anime.rashon.speed.loyert.network.ApiService;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class FacebookAuthActivity extends AppCompatActivity {

    private static final String TAG = "ab_do";
    CallbackManager callbackManager ;
    LoginUtil loginUtil ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("ab_do" , "create");
        loginUtil = new LoginUtil(this);
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends" , "email"));
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        Log.d("ab_do" , "onSuccess");
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        Log.d("ab_do" , "onCancel");
                        facebookFailure(null);
                    }

                    @Override
                    public void onError(@NonNull FacebookException exception) {
                        // App code
                        Log.d("ab_do" , "onError");
                        facebookFailure(exception);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        GraphRequest request = GraphRequest.newMeRequest(
                token,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                            String photo_url = "";
                        try {
                            String name = object.getString("name");
//                            String photo_url = object.getJSONObject("picture").getJSONObject("data")
//                                    .getString("url");
                            if (Profile.getCurrentProfile()!=null && Profile.getCurrentProfile().getId() !=null) {
                                photo_url = "http://graph.facebook.com/" + Profile.getCurrentProfile().getId() + "/picture?type=large";
                            }
                            createUserWithFacebook(token.getUserId(), name , photo_url);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            createUserWithFacebook(token.getUserId() , "None" , photo_url);
                        }
                        // Application code
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link");
        request.setParameters(parameters);
        request.executeAsync();
//        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithCredential:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            Load(user);
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "signInWithCredential:failure", task.getException());
//                            facebookFailure(task.getException());
//                        }
//                    }
//                });
    }

    private void createUserWithFacebook(String token, String name, String photo_url) {
        Log.i("ab_do_facebook" , "token " + token);
        Log.i("ab_do_facebook" , "name " + name);
        Log.i("ab_do_facebook" , "photo_url " + photo_url);
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        CompositeDisposable disposable = new CompositeDisposable();
        disposable.add(
                apiService
                        .createNewUserWithToken(token , "", name, photo_url)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UserResponse>() {
                            @Override
                            public void onSuccess(UserResponse userResponse) {
                                if (!userResponse.isError()) {
                                    loginUtil.saveLoginInformation(LoginMethod.FACEBOOK , name , photo_url , userResponse.getUser().getId());
                                    Load();
                                }
                                else {
                                    facebookFailure(null);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                facebookFailure(null);
                            }
                        })
        );
    }


    private void facebookFailure(Exception exception) {
        if (exception != null)
        Log.d("ab_do" , "facebookFailure " +  exception.getMessage());
        finish();
        Toast.makeText(getApplicationContext(), "حدث خطأ ما",
                Toast.LENGTH_SHORT).show();
    }

    private void Load() {
        Intent intent = new Intent(getBaseContext() , MainActivity.class);
        startActivity(intent);
        finish();
    }
}