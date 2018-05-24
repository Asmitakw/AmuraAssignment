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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener
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
    }

    @Override//Click listener for SignUp Button
    @OnClick({R.id.btn_login,R.id.btn_signup})
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_login:

                attemptLogin();

                break;

            case R.id.btn_signup:

                Intent intent = new Intent(this,SignUpActivity.class);
                startActivity(intent);
                finish();

                break;
        }
    }

    private void attemptLogin()
    {
        String email = edtxtEmail.getText().toString();
        final String password = edtxtPassword.getText().toString();

        Utility.hideKeyboard(this);

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), R.string.enterEmail, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), R.string.enterPwd, Toast.LENGTH_SHORT).show();
            return;
        }

        progress.show();

        //authenticate user
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        dismissProgress();

                        if (task.isSuccessful())
                        {
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();

                        }
                        else
                        {
                            // there was an error
                            if (password.length() < 6)
                            {
                                edtxtPassword.setError(getString(R.string.minimum_password));
                            }
                            else
                            {
                                Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
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
