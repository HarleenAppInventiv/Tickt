package com.googlelibrary.interfaces;

public interface GoogleSignOutCallback {
    void googleSignOutSuccessResult(String message);

    void googleSignOutFailureResult(String message);
}
