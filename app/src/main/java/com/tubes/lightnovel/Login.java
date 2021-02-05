package com.tubes.lightnovel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class Login extends AppCompatActivity {

    private Button callSignUp,login_btn,continueNow;
    private ImageView image;
    private TextView logoText, sloganText;
    private TextInputLayout username,password;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        //hooks
        callSignUp = findViewById(R.id.go_signup_screen);
        image = findViewById(R.id.Logo_image);
        logoText = findViewById(R.id.Logo_name);
        sloganText = findViewById(R.id.slogan_name);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login_btn = findViewById(R.id.Login_btn);
        continueNow = findViewById(R.id.continue_now);
        //database reference
        mAuth = FirebaseAuth.getInstance();


        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        continueNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(Login.this, MainActivity.class));
            }
        });

        callSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this,SignUp.class);
                Pair[] pairs = new Pair[7];

                pairs[0] = new Pair<View,String>(image,"logo_image");
                pairs[1] = new Pair<View,String>(logoText,"logo_text");
                pairs[2] = new Pair<View,String>(sloganText,"logo_desc");
                pairs[3] = new Pair<View,String>(username,"username_tran");
                pairs[4] = new Pair<View,String>(password,"password_tran");
                pairs[5] = new Pair<View,String>(login_btn,"button_tran");
                pairs[6] = new Pair<View,String>(callSignUp,"login_signup_tran");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Login.this,pairs);
                startActivity(intent, options.toBundle());

            }
        });

        login_btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                userLogin();
            }
        });

    }

    private void userLogin() {
        String email = username.getEditText().getText().toString().trim();
        String pass = password.getEditText().getText().toString().trim();

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

        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent intent = new Intent(Login.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                    Toast.makeText(getApplicationContext(),"Welcome Fellow Daoist", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}
