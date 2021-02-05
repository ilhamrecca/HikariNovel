package com.tubes.lightnovel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.util.Log;
import android.util.Pair;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class SignUp extends AppCompatActivity {
    Button callSignUp,signUp;
    ImageView image;
    TextView logoText, sloganText;
    TextInputLayout username,password,repassword;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        callSignUp = findViewById(R.id.go_login_screen);
        image = findViewById(R.id.Logo_image);
        logoText = findViewById(R.id.Logo_name);
        sloganText = findViewById(R.id.slogan_name);
        username = findViewById(R.id.email_s);
        password = findViewById(R.id.password_s);
        repassword = findViewById(R.id.re_password_s);
        signUp = findViewById(R.id.Sign_up);

        //firebase auth
        mAuth = FirebaseAuth.getInstance();

        callSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        
        signUp.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        String email = username.getEditText().getText().toString().trim();
        String pass = password.getEditText().getText().toString().trim();
        String pass2 = repassword.getEditText().getText().toString().trim();
        if(email.isEmpty()){
            username.setError("Email is required");
            username.requestFocus();
            return;
        }
        Log.i("Email", "fsdafsd"+email);
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            username.setError("Please enter a valid email address");
            username.requestFocus();
            return;
        }

        if(pass.isEmpty()){
            password.setError("Password is required");
            password.requestFocus();
            return;
        }

        if(pass.length()<6){
            password.setError("Minimum length of password is 6");
            password.requestFocus();
            return;
        }

        if(!pass.equals(pass2)){
            password.setError("Password did not match");
            password.requestFocus();
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    onBackPressed();
                    Toast.makeText(getApplicationContext(),"Sign Up Succesful", Toast.LENGTH_LONG).show();
                }
                else{

                    if(task.getException() instanceof FirebaseAuthUserCollisionException){
                        Toast.makeText(getApplicationContext(),"You're Already Registered", Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(getApplicationContext(),task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }

                }
            }
        });

    }
}
