package com.googlelibrary.interfaces;


import com.google.android.gms.auth.api.signin.GoogleSignInAccount;


public interface GoogleSignInCallback {
     void googleSignInSuccessResult(GoogleSignInAccount googleSignInAccount);
     void googleSignInFailureResult(String message);
}
