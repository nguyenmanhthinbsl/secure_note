package com.thinnm00.securenotes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import java.util.concurrent.Executor;

import static androidx.biometric.BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE;
import static androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS;
import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

public class AuthActivity extends AppCompatActivity {
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "BiometricsSetting";
    private final static String KEY_BIOMETRIC_FINGERPRINT = "isEnableFingerPrint";
    public String KEY_BIOMETRIC_FACEID = "isEnableFaceId";
    boolean isEnableBiometric;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        //if not setting
        if (!sharedPreferences.contains(KEY_BIOMETRIC_FINGERPRINT)) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return;
        }

        isEnableBiometric = sharedPreferences.getBoolean(KEY_BIOMETRIC_FINGERPRINT, false);
        //if setting but not enable
        if (!isEnableBiometric) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return;
        }
        //  else enable
        else {
            BiometricManager biometricManager = BiometricManager.from(this);
            switch (biometricManager.canAuthenticate(BIOMETRIC_STRONG | BIOMETRIC_WEAK)) {
                case BiometricManager.BIOMETRIC_SUCCESS:
                    break;
                case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                    Toast.makeText(this, "Hardware device not support biometric!", Toast.LENGTH_SHORT).show();
                    break;
                case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                    Toast.makeText(this, "Biometrics not available!", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(this, "Biometrics not available!", Toast.LENGTH_SHORT).show();
                    break;
            }

        }

        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(AuthActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(), "Something went wrong! : " + errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                startActivity(new Intent(AuthActivity.this, MainActivity.class));
                Toast.makeText(getApplicationContext(),
                        "Authentication success!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication failed!", Toast.LENGTH_SHORT).show();
            }
        });

//        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
//                .setTitle("please authenticate to access app!")
//                .setConfirmationRequired(true)
//                .setAllowedAuthenticators(BIOMETRIC_STRONG | BIOMETRIC_WEAK | DEVICE_CREDENTIAL)
//                .build();

        showBiometricPrompt();

    }

    private void showBiometricPrompt() {
        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Authentication")
                .setSubtitle("Use fingerprint or face recognition")
                .setNegativeButtonText("Cancel")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

}