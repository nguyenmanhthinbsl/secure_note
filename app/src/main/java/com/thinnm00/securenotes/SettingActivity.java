package com.thinnm00.securenotes;

import android.content.SharedPreferences;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import android.widget.CompoundButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.concurrent.Executor;

public class SettingActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "BiometricsSetting";
    public String KEY_BIOMETRIC_FINGERPRINT = "isEnableFingerPrint";
    public String KEY_BIOMETRIC_FACEID = "isEnableFaceId";
    private BiometricPrompt biometricPrompt;

    boolean isEnableBiometric;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        isEnableBiometric = sharedPreferences.getBoolean(KEY_BIOMETRIC_FINGERPRINT, false);

        SwitchMaterial biometricFingerPrintSwitch = findViewById(R.id.biometric_fingerprint);
        if (isEnableBiometric) {
            biometricFingerPrintSwitch.setChecked(true);
        }

        Executor executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                // Biometric authentication succeeded, enable the setting
                enableSetting(true);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                // Biometric authentication failed, disable the setting
                enableSetting(false);
            }
        });

        biometricFingerPrintSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Show biometric prompt
                showBiometricPrompt();
                sharedPreferences.edit().putBoolean(KEY_BIOMETRIC_FINGERPRINT, true).apply();
            } else {
                // Disable the setting
                enableSetting(false);
                sharedPreferences.edit().putBoolean(KEY_BIOMETRIC_FINGERPRINT, false).apply();
            }
        });
    }

    private void enableSetting(boolean isEnabled) {
        // TODO: Implement the logic to enable or disable the setting in your app
        if (isEnabled) {
            Toast.makeText(this, "Biometric authentication enabled", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Biometric authentication disabled", Toast.LENGTH_SHORT).show();
        }
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