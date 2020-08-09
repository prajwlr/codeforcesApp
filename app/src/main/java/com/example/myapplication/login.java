package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.params.HttpParams;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public class login extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private Button loginbut;
    public String h;
    public static final String CREDENTIALS_PREF_FILENAME = "com.example.myapplication.Credentials";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username=findViewById(R.id.inputUser);
        password=findViewById(R.id.inputPassword);
        loginbut=findViewById(R.id.login);


        loginbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cfUsername=username.getText().toString();
                String cfPassword=password.getText().toString();

                String loginUrl = "https://codeforces.com/enter";
                new Multitask().execute(loginUrl,cfUsername,cfPassword);
            }
        });
        //Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }
    ProgressDialog pd;
    public class Multitask extends AsyncTask<String,String,String>
    {
        @Override
        protected void onPreExecute() {
            //super.onPreExecute();
            pd=ProgressDialog.show(login.this,"Trying to login...","",true,false);

        }

        @Override
        protected void onPostExecute(String s) {
            //super.onPostExecute(s);

            if(s=="True")
            {MainActivity.islogin=true;
            //pd.dismiss();
            Toast.makeText(login.this,"Login Successful",Toast.LENGTH_LONG).show();

            SharedPreferences.Editor credEditor = getSharedPreferences(CREDENTIALS_PREF_FILENAME, MODE_PRIVATE).edit();
            credEditor.putString("handle",username.getText().toString());
            credEditor.putString("password",password.getText().toString());
            credEditor.putBoolean("loginStatus",true);
            credEditor.commit();

            Intent intent = new Intent(login.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            }
            else if(s=="False")
            {   //pd.dismiss();
                Toast.makeText(login.this,"Incorrect Credential!!! Please Try Again",Toast.LENGTH_SHORT).show();}
            else
            {   //pd.dismiss();
                Toast.makeText(login.this,s,Toast.LENGTH_SHORT).show();
            }
            pd.dismiss();
        }

        @Override
        protected String doInBackground(String... strings) {
            String url=strings[0];
            String handle=strings[1];
            String password=strings[2];

            Connection.Response loginForm=null;
            try {
                loginForm= Jsoup.connect(url).method(Connection.Method.GET).timeout(10000).execute();
            } catch (IOException e) {
                return "Connecting Error";
            }

            Document doc=null;
            try {
                doc = loginForm.parse();
            } catch (IOException e) {
                return "Can't Fetch data";
            }
            String csrf_token=doc.select("input[name=csrf_token]").attr("value");

            try {
                doc = Jsoup.connect(url).data("csrf_token",csrf_token).data("action","enter").data("handleOrEmail",handle).data("password",password).cookies(loginForm.cookies()).post();
            } catch (IOException e) {
                return "Can't Post data";
            }

            Log.d("ahref","a[href=/profile/"+handle+"]");
            String check=doc.select("a[href=/profile/"+handle+"]").html();
            h=check;
            if(check.length()>0)
                return "True";
            else
                return "False";

        }
    }



//    public void get_cookie(HttpURLConnection conn) {
//        SharedPreferences sh_pref_cookie = getSharedPreferences("cookies", Context.MODE_PRIVATE);
//        String cook_new;
//        String COOKIES_HEADER;
//        if (conn.getHeaderField("Set-Cookie") != null) {
//            COOKIES_HEADER = "Set-Cookie";
//        }
//        else {
//            COOKIES_HEADER = "Cookie";
//        }
//        cook_new = conn.getHeaderField(COOKIES_HEADER);
//        if (cook_new.indexOf("sid", 0) >= 0) {
//            SharedPreferences.Editor editor = sh_pref_cookie.edit();
//            editor.putString("Cookie", cook_new);
//            editor.commit();
//        }
//    }
    public void set_cookie(HttpURLConnection conn) {
        SharedPreferences sh_pref_cookie = getSharedPreferences("cookies", Context.MODE_PRIVATE);
        String COOKIES_HEADER = "Cookie";
        String cook = sh_pref_cookie.getString(COOKIES_HEADER, "no_cookie");
        if (!cook.equals("no_cookie")) {
            conn.setRequestProperty(COOKIES_HEADER, cook);
        }
    }
}