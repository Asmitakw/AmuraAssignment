package com.asmita.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_layout);

        Thread timer=new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    sleep(2000);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }

                //Get Firebase auth instance
                FirebaseAuth auth = FirebaseAuth.getInstance();

                if (auth.getCurrentUser() != null)
                {
                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                    finish();
                }
                else
                {
                    Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };timer.start();
    }
}
