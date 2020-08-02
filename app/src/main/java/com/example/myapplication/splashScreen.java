package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class splashScreen extends AppCompatActivity {
    int SLEEP_IN_SECS =1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();
        LogoLauncher logo_launcher = new LogoLauncher();
        logo_launcher.start();
    }
    private class LogoLauncher extends Thread{
        public void run(){
            try{
                sleep(1000 * SLEEP_IN_SECS);

            }
            catch(InterruptedException e){
                e.printStackTrace();
            }
            Intent intent = new Intent(splashScreen.this, MainActivity.class);
            startActivity(intent);
            splashScreen.this.finish();
        }
    }
}