package com.example.swathivarsha.tedx1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.swathivarsha.tedx1.SPHelper;


public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "LoginActivity";
     GoogleApiClient mGoogleApiClient;
    public static final int RC_SIGN_IN = 1000;
    private EditText mUserEmail, mPassword;
    private Button mLoginButton, mSignUpButton;
    private TextView mResetPasswordButton;
    private FirebaseAuth auth;
    private SharedPreferences sharedPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        //Get FirebaseAuth instance
        auth = FirebaseAuth.getInstance();

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });


        mUserEmail = (EditText) findViewById(R.id.login_user_email);
        mPassword = (EditText) findViewById(R.id.login_user_password);
        mLoginButton = (Button) findViewById(R.id.login_button);
        mSignUpButton = (Button) findViewById(R.id.sign_up_button);
        mResetPasswordButton = (TextView) findViewById(R.id.forgot_reset_password);
        mResetPasswordButton.setPaintFlags(mResetPasswordButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        mResetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //    startActivity( new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                LoginActivity.this.finish();
            }
        });
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = mUserEmail.getText().toString();
                final String password = mPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                //progressBar.setVisibility(View.VISIBLE);

                //authenticate user
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                //progressBar.setVisibility(View.GONE);
                                if (!task.isSuccessful()) {
                                    // there was an error
                                    if (password.length() < 6) {
                                        mPassword.setError(getString(R.string.minimum_password));
                                    } else {
                                        Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(LoginActivity.this, "Logged in as " + email + ".", Toast.LENGTH_SHORT).show();
                                    SPHelper.setSP(getApplicationContext(), "user_registered", "true");

                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                    //SPHelper.setSP(getApplicationContext(), "user_name", "");
                                    SPHelper.setSP(getApplicationContext(), "user_email", email);
                                    //SPHelper.setSP(getApplicationContext(), "user_phone", "");
                                    startActivity(intent);
                                    LoginActivity.this.finish();
                                }
                            }
                        });
            }
        });

    }




    //auth =FirebaseAuth.getInstance();
    FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            if (firebaseUser != null) {
                String userId = firebaseUser.getUid();
                String userEmail = firebaseUser.getEmail();
                sharedPref = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("firebasekey", userId);
                editor.commit();
            }
        }
    };


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount account = result.getSignInAccount();
            updateData(account);

        } else {
            // Signed out, show unauthenticated UI.
            Toast.makeText(this, "Turn on Internet.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateData(GoogleSignInAccount account) {
        //Toast.makeText(this, "Logged in as " + account.getDisplayName() + ".", Toast.LENGTH_SHORT).show();
        //String photoUrl = account.getPhotoUrl().toString();
        //SPHelper.setSP(this, "user_photo", photoUrl);
        //Alert dialog to get phone number
        SPHelper.setSP(this, "user_name", account.getDisplayName());
        SPHelper.setSP(this, "user_email", account.getEmail());
        //Log.e("Varsha", SPHelper.getSP(this , "user_name") + " " + SPHelper.getSP(this , "user_email"));
        startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
        this.finish();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Unable to get your information. Please try again later or try to Sign Up.", Toast.LENGTH_SHORT).show();
    }
}

