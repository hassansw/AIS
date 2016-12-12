package com.thewolves.ais;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SignIn extends AppCompatActivity {

    private EditText edtMail,edtPass;
    Button btnSignIn, btnSignUp;
    RequestQueue requestQueue;

    String mName, mEmail, mContact;


    private File file;
    private static final String FILENAME = "jsondata.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        initialize();

        File extFileDir = getExternalFilesDir(null);
        String path = extFileDir.getAbsolutePath();

        file = new File(extFileDir,FILENAME);

        requestQueue = Volley.newRequestQueue(this);


        edtMail = (EditText) findViewById(R.id.txtEmail);
        edtPass = (EditText) findViewById(R.id.txtPass);

        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(edtMail.getText().toString()) && !TextUtils.isEmpty(edtPass.getText().toString())){
                    if (isOnline()) {
                        requestData("http://mmssatc.pk/main/test/sign_in.php");
                    } else {
                        Toast.makeText(SignIn.this, "Network isn't available", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(SignIn.this, "Please Enter email and password", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignIn.this, SignUp.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//                Animation slideRight = AnimationUtils.loadAnimation(getApplicationContext(),
//                        R.anim.slide_right);






            }
        });

    }

    protected boolean isOnline() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    private void requestData(String uri) {

        StringRequest request = new StringRequest(Request.Method.POST, uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Toast.makeText(SignIn.this, response, Toast.LENGTH_LONG).show();
                    JSONArray jsonObject = new JSONArray(response);
                    if (!TextUtils.isEmpty(response)){

                        Toast.makeText(SignIn.this, "Success", Toast.LENGTH_SHORT).show();

                        try { createFile(jsonObject.getJSONObject(0).getString("name"), jsonObject.getJSONObject(0).getString("email"), jsonObject.getJSONObject(0).getString("contact"));}
                        catch (IOException e) { Toast.makeText(SignIn.this, e.getMessage().toString() + " IO", Toast.LENGTH_SHORT).show();}

                    } else{ Toast.makeText(SignIn.this, "SignIn Unsuccessfull, try again later ", Toast.LENGTH_SHORT).show();}

                } catch (JSONException e) { Toast.makeText(SignIn.this, e.getMessage() + " JSON", Toast.LENGTH_LONG).show();}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SignIn.this, "Error", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<String,String>();
                hashMap.put("email",edtMail.getText().toString());
                hashMap.put("pass",edtPass.getText().toString());

                return hashMap;
            }
        };
        requestQueue.add(request);
    }


    public void createFile(String name, String email, String number) throws IOException, JSONException {

        if (!checkedExternalStorageState()){
            return;
        }

        JSONArray data = new JSONArray();
        JSONObject user;

        user = new JSONObject();
        user.put("name",name);
        user.put("email",email);
        user.put("contact",number);
        data.put(user);

        String text = data.toString();

        //FileOutputStream fos = openFileOutput("userdata.json", MODE_PRIVATE);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(text.getBytes());
        fos.close();

        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
    }

    public boolean checkedExternalStorageState(){
        String state = Environment.getExternalStorageState();

        if ( state.equals(Environment.MEDIA_MOUNTED)){
            return true;
        } else if (state.equals(Environment.MEDIA_MOUNTED_READ_ONLY)){
            return false;
        } else {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public void initialize(){

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarSignIn);
        toolbar.setTitle("Sign In");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

}
