package com.example.swathivarsha.tedx1;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private Button mSignUp;
    private EditText mUserEmail, mUserName, mUserPhone, mPassword, mConfirmPassword;
    private DatabaseReference mDatabase;
    private FirebaseAuth auth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        getSupportActionBar().setTitle("Sign Up");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //Get instance
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mUserEmail = (EditText) findViewById(R.id.user_email);
        mUserName = (EditText) findViewById(R.id.user_name);
        mUserPhone = (EditText) findViewById(R.id.user_phone);
        mPassword = (EditText) findViewById(R.id.user_password);
        mConfirmPassword = (EditText) findViewById(R.id.user_confirm_password);

        if(!SPHelper.getSP(this , "user_name").equals("none") && !SPHelper.getSP(this , "user_email").equals("none")) {
            mUserName.setText(SPHelper.getSP(this, "user_name"));
            mUserEmail.setText(SPHelper.getSP(this, "user_email"));
        }

        mSignUp = (Button)findViewById(R.id.screen_sign_up_button);
        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = mUserEmail.getText().toString().trim();
                final String name = mUserName.getText().toString().trim();
                final String password = mPassword.getText().toString().trim();
                String confirmpassword = mConfirmPassword.getText().toString().trim();
                final String phone = mUserPhone.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(getApplicationContext(), "Enter phone number!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (phone.length() < 10) {
                    Toast.makeText(getApplicationContext(), "Enter a 10 digit mobile number!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(confirmpassword)) {
                    Toast.makeText(getApplicationContext(), "Enter confirm password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(confirmpassword)) {
                    Toast.makeText(getApplicationContext(), "Passwords do not match!", Toast.LENGTH_SHORT).show();
                    return;
                }

                //progressBar.setVisibility(View.VISIBLE);
                Toast.makeText(SignUpActivity.this, "Signing Up. Please wait. . .", Toast.LENGTH_SHORT).show();

                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.e("Varsha", "createUserWithEmail:onComplete:" + task.isSuccessful());
                                //progressBar.setVisibility(View.GONE);
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(SignUpActivity.this, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    SPHelper.setSP(getApplicationContext(), "user_registered", "true");
                                    SPHelper.setSP(getApplicationContext(), "user_name", name);
                                    SPHelper.setSP(getApplicationContext(), "user_email", email);
                                    SPHelper.setSP(getApplicationContext(), "user_phone", phone);

                                    FirebaseUser user = auth.getCurrentUser();
                                    String userId = user.getUid();
                                    Person person = new Person();
                                    person.setName(name);
                                    person.setEmail(email);
                                    person.setPhone(phone);
                                    DatabaseReference root = mDatabase.child("tedxdsce_users").push();
                                    root.setValue(person);

                                    startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
                                    Toast.makeText(SignUpActivity.this, "Logged in as " + email, Toast.LENGTH_SHORT).show();
                                    SignUpActivity.this.finish();
                                }
                            }
                        });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

