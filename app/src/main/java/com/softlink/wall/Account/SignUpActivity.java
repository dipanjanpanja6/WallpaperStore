package com.softlink.wall.Account;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.softlink.wall.MainActivity;
import com.softlink.wall.R;

public class SignUpActivity extends AppCompatActivity {

    private EditText Email, Password, NAme;
    private Button SignUp;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().hide();

        Email = findViewById(R.id.Signup_email);
        Password = findViewById(R.id.Signup_password);
        NAme = findViewById(R.id.Signup_name);
        SignUp = findViewById(R.id.signup_button);
        mAuth = FirebaseAuth.getInstance();

        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = Email.getText().toString();
                String password = Password.getText().toString();
String name=NAme.getText().toString();
                if (email.isEmpty())
                {
                    Toast.makeText(SignUpActivity.this, "Enter Email address", Toast.LENGTH_SHORT).show();
                }
                else if (name.isEmpty()){
                    Toast.makeText(SignUpActivity.this, "Enter your name", Toast.LENGTH_SHORT).show();
                }
                else if (password.isEmpty())
                {
                    Toast.makeText(SignUpActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful())
                            {
                                Toast.makeText(SignUpActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignUpActivity.this,MainActivity.class);
                                startActivity(intent);
                            }
                            else
                            {
                                String m = task.getException().getMessage().toString();
                                Toast.makeText(SignUpActivity.this, m, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
