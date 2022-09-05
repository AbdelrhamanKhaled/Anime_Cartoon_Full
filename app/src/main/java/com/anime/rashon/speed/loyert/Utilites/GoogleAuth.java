package com.anime.rashon.speed.loyert.Utilites;

import android.content.Context;

import com.anime.rashon.speed.loyert.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class GoogleAuth {

    private static GoogleSignInOptions getGoogleSignInOptions(Context context) {
        return new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestId()
                .build();
    }

    public static GoogleSignInClient getGoogleSignInClient (Context context) {
        return GoogleSignIn.getClient(context, getGoogleSignInOptions(context));
    }
}
