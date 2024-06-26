package com.example.campwild;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonLogin;
    private TextView textViewToggleMode;
    private boolean isSignInMode = true;

    private CheckBox checkBoxTerms;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewToggleMode = findViewById(R.id.textViewToggleMode);
        checkBoxTerms = findViewById(R.id.checkBoxTerms);
        updateToggleButtonText();

        TextView textViewTermsLink = findViewById(R.id.textViewTermsLink);
        checkBoxTerms.setVisibility(isSignInMode ? View.GONE : View.VISIBLE);
        textViewTermsLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, TermsActivity.class);
                startActivity(intent);
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextUsername.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    editTextUsername.setError("Email is required");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    editTextPassword.setError("Password is required");
                    return;
                }

                if (!isSignInMode && !checkBoxTerms.isChecked()) {
                    showSnackbar("Please agree to the terms and conditions.");
                    return;
                }

                if (isSignInMode) {
                    signIn(email, password);
                } else {
                    signUp(email, password);
                }
            }
        });

        textViewToggleMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSignInMode = !isSignInMode;
                updateToggleButtonText();
                checkBoxTerms.setVisibility(isSignInMode ? View.GONE : View.VISIBLE);
            }
        });

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean stayLoggedIn = preferences.getBoolean("stayLoggedIn", false);
        if (stayLoggedIn) {
            String email = preferences.getString("email", "");
            String password = preferences.getString("password", "");
            openMapsActivity();
            if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                signIn(email, password);
                return;
            }
        }
    }

    private void updateToggleButtonText() {
        textViewToggleMode.setText(isSignInMode ? R.string.sign_up_mode : R.string.sign_in_mode);
        buttonLogin.setText(isSignInMode ? R.string.sign_in : R.string.register);
    }

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
//                            showRemainLoggedInDialog(email, password);
                            openMapsActivity();
                        } else {
                            showSnackbar("Authentication failed. Please try again.");
                        }
                    }
                });
    }


    private void signUp(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
//                            showRemainLoggedInDialog(email, password);
                            openMapsActivity();
                        } else {
                            showSnackbar("Registration failed. Please try again.");
                        }
                    }
                });
    }
    private void showSnackbar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
    }

    private void openMapsActivity() {
        Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
        startActivity(intent);
        finish();
    }
}
