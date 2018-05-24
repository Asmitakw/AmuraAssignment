package com.asmita.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.asmita.utils.Utility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener
{
    // UI Widgets.
    @BindView(R.id.btn_login) Button btn_login;
    @BindView(R.id.btn_signup) Button btn_signup;
    @BindView(R.id.edtxtEmail) EditText edtxtEmail;
    @BindView(R.id.edtxtPassword) EditText edtxtPassword;

    // Labels
    private FirebaseAuth auth;
    private ProgressDialog progress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

         /*  Intialization of ButterKnife */
        ButterKnife.bind(this);

         /* Intialization of ProgressDialog */
        progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.loading));
        progress.setCancelable(false);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        btn_login.setText(getString(R.string.action_sign_in_short));
        btn_signup.setText(getString(R.string.btn_link_to_login));
    }

    @Override//Click listener for Login Button
    @OnClick({R.id.btn_login,R.id.btn_signup})
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_login:

                createUser();

                break;

            case R.id.btn_signup:

               movetoPreviousScreen();

                break;
        }
    }

    private void createUser()
    {
        String email = edtxtEmail.getText().toString().trim();
        String password = edtxtPassword.getText().toString().trim();

        Utility.hideKeyboard(this);

        if (TextUtils.isEmpty(email))
        {
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password))
        {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6)
        {
            Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
            return;
        }

        progress.show();

        //create user
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        dismissProgress();

                        Toast.makeText(SignUpActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful())
                        {
                            Toast.makeText(SignUpActivity.this, "Authentication failed." + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
                            finish();
                        }
                    }
                });
    }

    private void movetoPreviousScreen()
    {
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed()
    {
        movetoPreviousScreen();
    }

    private void dismissProgress()
    {
        if(progress != null)
        {
            progress.dismiss();
            progress.cancel();
        }
    }
}
