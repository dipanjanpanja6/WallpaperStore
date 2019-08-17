package com.softlink.wall.Account;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.DeviceLoginButton;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.annotations.Nullable;
import com.softlink.wall.MainActivity;
import com.softlink.wall.R;

import java.util.Arrays;

public class LogInActivity extends AppCompatActivity {
    private EditText Email, Password;
    private Button Login,textView,fbbutton;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    CallbackManager mCallbackManager;
    String TAG ="Hey";
    LoginButton loginButton;
    private static final int RC_SIGN_IN = 234;
    GoogleSignInClient mGoogleSignInClient;

    public void onClick(View v) {
        if (v == fbbutton) {
            loginButton.performClick();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        getSupportActionBar().hide();

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

        mAuth = FirebaseAuth.getInstance();
        fbbutton=findViewById(R.id.login_button_facebook);
        loginButton=findViewById(R.id.fb);
        Email = findViewById(R.id.login_email);
        Password = findViewById(R.id.login_password);
        Login = findViewById(R.id.login_button);
        textView = findViewById(R.id.create_account);
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        findViewById(R.id.g_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });


        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogInActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = Email.getText().toString();
                String password = Password.getText().toString();

                if (email.isEmpty())
                {
                    Toast.makeText(LogInActivity.this, "Enter Email...", Toast.LENGTH_SHORT).show();
                }
                else if (password.isEmpty())
                {
                    Toast.makeText(LogInActivity.this, "Enter Password...", Toast.LENGTH_SHORT).show();
                }

                else
                {
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful())
                            {
                                LoginSuccess();
                            }
                            else
                            {
                                String m = task.getException().getMessage().toString();
                                Toast.makeText(LogInActivity.this, ""+m, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        mCallbackManager =CallbackManager.Factory.create();
        {

            loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(LogInActivity.this, Arrays.asList("email", "public_profile"));
                LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.i(TAG,"Hello"+loginResult.getAccessToken().getToken());
                       // Toast.makeText(LogInActivity.this, "Token:"+loginResult.getAccessToken(), Toast.LENGTH_SHORT).show();

                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(FacebookException error) {

                    }
                });
            }
        }); }
        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user!=null){
                   // name = user.getDisplayName();
                    //name=user.getEmail();
                    Toast.makeText(LogInActivity.this,""+user.getDisplayName(),Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(LogInActivity.this,"something went wrong",Toast.LENGTH_LONG).show();
                }


            }
        };

    }

    private void LoginSuccess() {
        Intent intent = new Intent(LogInActivity.this, MainActivity.class);
         intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
         startActivity(intent);
    }

    private void signIn() {

            //getting the google signin intent
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();

            //starting the activity for result
            startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {

            //Getting the GoogleSignIn Task
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                //Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                //authenticating with firebase
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(LogInActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        //getting the auth credential
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        //Now using firebase we are signing in the user here
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            LoginSuccess();
                        }
                        else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LogInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }


                    }
                });

    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {


                        if (task.isSuccessful())
                        {
                            LoginSuccess();

                        }
                        else
                        {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LogInActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }


                    }
        });
    }
}
