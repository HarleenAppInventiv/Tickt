package com.googlelibrary;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.googlelibrary.interfaces.GoogleSignInCallback;
import com.googlelibrary.interfaces.GoogleSignOutCallback;

public class GoogleSignInAI {
    private Activity mActivity;
    private GoogleSignInOptions mGoogleSignInOptions;
    private int GOOGLE_SIGN_IN_REQUEST_CODE;
    private GoogleSignInCallback mGoogleSignInCallback;
    private GoogleSignOutCallback mGoogleSignOutCallback;
    private GoogleSignInClient mGoogleSignInClient;


    /*
     *  Initialize activity instance
     */
    public void setActivity(Activity activity) {
        this.mActivity = activity;
    }

    /*
     *  Initialize Google sign in callback
     */
    public void setSignInCallback(GoogleSignInCallback mGoogleSignInCallback) {
        this.mGoogleSignInCallback = mGoogleSignInCallback;
    }

    /*
     *  Initialize Google sign out callback
     */
    public void setSignOutCallback(GoogleSignOutCallback mGoogleSignInCallback) {
        this.mGoogleSignOutCallback = mGoogleSignInCallback;
    }

    /*
     *  Initialize Google request code
     */
    public void setRequestCode(int GOOGLE_SIGN_IN_REQUEST_CODE) {
        this.GOOGLE_SIGN_IN_REQUEST_CODE = GOOGLE_SIGN_IN_REQUEST_CODE;

    }

    /*
     * Configure google sign in request for contacts
     */
    public void setUpGoogleClientForGoogleLogin() {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        mGoogleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();

        // Build a GoogleSignInClient with the options specified by mGoogleSignInOptions.
        mGoogleSignInClient = GoogleSignIn.getClient(mActivity, mGoogleSignInOptions);
    }


    public void doSignIn() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(mActivity);
        if (account != null) {
            mGoogleSignInCallback.googleSignInSuccessResult(account);
        } else if (mGoogleSignInClient != null) {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            mActivity.startActivityForResult(signInIntent, GOOGLE_SIGN_IN_REQUEST_CODE);
        } else
            Toast.makeText(mActivity, "Google SignIn Client is not connected", Toast.LENGTH_SHORT).show();
    }

    public void onActivityResult(Intent data) {
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (data != null) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }


    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null) {
                // Signed in successfully, show authenticated UI.
                mGoogleSignInCallback.googleSignInSuccessResult(account);
            } else
                Toast.makeText(mActivity, "Something went wrong", Toast.LENGTH_SHORT).show();
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            mGoogleSignInCallback.googleSignInFailureResult(e.getStatusCode() + "");
        }
    }

    public void doSignout() {
        if (mGoogleSignInClient != null) {
            mGoogleSignInClient.signOut().addOnSuccessListener(mActivity, new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    mGoogleSignOutCallback.googleSignOutSuccessResult("Google sign out successfully");
                }
            }).addOnFailureListener(mActivity, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mGoogleSignOutCallback.googleSignOutFailureResult("Error in google sign out");
                }
            });
        } else {
            Toast.makeText(mActivity, "Please Login First", Toast.LENGTH_SHORT).show();
        }

    }

}