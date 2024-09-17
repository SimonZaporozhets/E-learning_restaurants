package com.example.e_learning_restaurants;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity {

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        progressBar = findViewById(R.id.progressBar);
        final EditText et_Login = findViewById(R.id.et_Login);
        final EditText et_Password = findViewById(R.id.et_Password);
        Button btn_Login = findViewById(R.id.btn_Login);
        TextView tv_Register = findViewById(R.id.tv_Register);
        final AsyncHttpClient client = new AsyncHttpClient();
        SharedPreferences preferences = getSharedPreferences("userPrefences", Activity.MODE_PRIVATE);

        tv_Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        btn_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String login = et_Login.getText().toString();
                String password = et_Password.getText().toString();

                if(login.isEmpty() || password.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setTitle(R.string.errorTitle)
                            .setMessage(R.string.errorEmptyFields)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    String url = "http://dev.imagit.pl/mobilne/api/login/"+login+"/"+password;
                    progressBar.setVisibility(View.VISIBLE);
                    client.get(url, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int i, Header[] headers, byte[] bytes) {
                            String response = new String(bytes);

                            if(android.text.TextUtils.isDigitsOnly(response)) {
                                SharedPreferences.Editor preferencesEditor = preferences.edit();
                                preferencesEditor.putString("userId", response);
                                preferencesEditor.commit();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class );
                                startActivity(intent);
                            } else {
                                Toast.makeText(LoginActivity.this, R.string.errorLoginIncorrect, Toast.LENGTH_LONG).show();
                            }
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });

    }
}