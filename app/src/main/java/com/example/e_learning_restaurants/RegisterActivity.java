package com.example.e_learning_restaurants;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText et_RegisterLogin = findViewById(R.id.et_RegisterLogin);
        EditText et_RegisterPassword = findViewById(R.id.et_RegisterPassword);
        EditText et_RegisterEmail = findViewById(R.id.et_RegisterEmail);
        Button btn_RegisterSubmit = findViewById(R.id.btn_RegisterSubmit);
        AsyncHttpClient client = new AsyncHttpClient();

        btn_RegisterSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String login = et_RegisterLogin.getText().toString();
                String password = et_RegisterPassword.getText().toString();
                String email = et_RegisterEmail.getText().toString();

                if(login.isEmpty() || password.isEmpty() || email.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
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
                    String url = "http://dev.imagit.pl/mobilne/api/register";
                    RequestParams params = new RequestParams();
                    params.put("login", login);
                    params.put("pass", password);
                    params.put("email", email);

                    client.post(url, params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int i, Header[] headers, byte[] bytes) {
                            String response = new String(bytes);
                            if(response.equals("OK")) {
                                Toast.makeText(RegisterActivity.this, R.string.infoRegisterSuccessful, Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(RegisterActivity.this, R.string.errorAccountExist, Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

                        }
                    });
                }
            }
        });

    }
}