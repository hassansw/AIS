package com.thewolves.ais;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
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
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    String[] Depart = {"B.S(C.S)", "B.E(S.E)", "B.E(E.E)", "B.E(E.L)", "B.E(T.E)"};
    String[] Desig = {"H.O.D", "Lecturer", "Asst. Prof"};

    private EditText mNameView;
    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mConfirmPassView;
    private EditText mContactView;
    private MaterialBetterSpinner mDepartment;
    private MaterialBetterSpinner mDesignation;

    RequestQueue requestQueue;


    private File file;
    private static final String FILENAME = "jsondata.json";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initialize();

        mNameView = (EditText) findViewById(R.id.txtName);
        mEmailView = (EditText) findViewById(R.id.txtMail);
        mPasswordView = (EditText) findViewById(R.id.txtPassword);
        mConfirmPassView = (EditText) findViewById(R.id.txtConfirmPassword);
        mContactView = (EditText) findViewById(R.id.txtContact);
        mDesignation = (MaterialBetterSpinner) findViewById(R.id.spDesignation);
        mDepartment = (MaterialBetterSpinner) findViewById(R.id.spDepartment);

        requestQueue = Volley.newRequestQueue(this);

        Button btnReg = (Button) findViewById(R.id.btn_Reg_SignUp);

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                attemptLogin();setContentView(R.layout.fragment_frag_profile);
                setContentView(R.layout.fragment_frag_feed);
                View l2 = findViewById(R.id.frag_feed);
                TextView edtSamp = (TextView)l2.findViewById(R.id.samptv);
                edtSamp.setText("Heck");
            }
        });

        File extFileDir = getExternalFilesDir(null);
        String path = extFileDir.getAbsolutePath();
        file = new File(extFileDir,FILENAME);


    }


    private void attemptLogin() {


        // Reset errors.
        mNameView.setError(null);
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mConfirmPassView.setError(null);
        mContactView.setError(null);
        boolean exp = true;

        // Store values at the time of the login attempt.
        String name = mNameView.getText().toString();
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String confirmpass = mConfirmPassView.getText().toString();
        String contact = mContactView.getText().toString();
        String depart = mDepartment.getText().toString();
        String desig = mDesignation.getText().toString();


        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(contact)) {
            mContactView.setError(getString(R.string.error_field_required));
            focusView = mContactView;
            cancel = true;
        }

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password) ) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }else if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }


        if (!TextUtils.isEmpty(confirmpass) && !isPasswordValid(confirmpass) ) {
            mConfirmPassView.setError(getString(R.string.error_invalid_password));
            focusView = mConfirmPassView;
            cancel = true;
        }else if (TextUtils.isEmpty(password)) {
            mConfirmPassView.setError(getString(R.string.error_field_required));
            focusView = mConfirmPassView;
            cancel = true;
        }

        if(!TextUtils.equals(password,confirmpass)){

            mPasswordView.setError("Passwords donot match...");
            focusView = mPasswordView;
            cancel = true;
        }


        if (TextUtils.equals(password,confirmpass)){
            exp = false;
        }

        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (TextUtils.isEmpty(name)) {
            mNameView.setError(getString(R.string.error_field_required));
            focusView = mNameView;
            cancel = true;
        }

        if (TextUtils.isEmpty(desig)) {
            Toast.makeText(this, "Please Select a Department", Toast.LENGTH_SHORT).show();
            focusView = mNameView;
            cancel = true;
        }

        if (TextUtils.isEmpty(depart)) {
            Toast.makeText(this, "Please Select a Designation", Toast.LENGTH_SHORT).show();
            focusView = mNameView;
            cancel = true;
        }



        if( exp == false && !TextUtils.isEmpty(name) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirmpass) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(contact)){
            if (isOnline()){ requestData("http://mmssatc.pk/main/test/sign_up.php"); }
            else {
                Toast.makeText(this, "No Network Connection", Toast.LENGTH_SHORT).show();
            }
        }
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


    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    private void requestData(String uri) {

        StringRequest request = new StringRequest(Request.Method.POST, uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.names().get(0).equals("success")){

                        Toast.makeText(SignUp.this, "Success", Toast.LENGTH_SHORT).show();

                        try { createFile(mNameView.getText().toString(), mEmailView.getText().toString(),mContactView.getText().toString());}
                        catch (IOException e) { Toast.makeText(SignUp.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();}

                    } else{ Toast.makeText(SignUp.this, "SignUp Unsuccessfull, try again later ", Toast.LENGTH_SHORT).show();}

                } catch (JSONException e) { Toast.makeText(SignUp.this, e.getMessage() , Toast.LENGTH_SHORT).show();}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SignUp.this, "Error", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<String,String>();
                hashMap.put("name",mNameView.getText().toString());
                hashMap.put("email",mEmailView.getText().toString());
                hashMap.put("pass",mPasswordView.getText().toString());
                hashMap.put("contact",mContactView.getText().toString());

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
    }

    public boolean checkedExternalStorageState(){
        String state = Environment.getExternalStorageState();

        if ( state.equals(Environment.MEDIA_MOUNTED)){ return true;}
        else if (state.equals(Environment.MEDIA_MOUNTED_READ_ONLY)){ return false; }
        else { Toast.makeText(this, "Storage Access Un-Available", Toast.LENGTH_SHORT).show();}

        return false;
    }

    public void initialize(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarSignUp);
        toolbar.setTitle("Sign Up");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, Depart);
        MaterialBetterSpinner spDepartment = (MaterialBetterSpinner) findViewById(R.id.spDepartment);
        spDepartment.setAdapter(arrayAdapter);

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, Desig);
        MaterialBetterSpinner spDesignation = (MaterialBetterSpinner) findViewById(R.id.spDesignation);
        spDesignation.setAdapter(arrayAdapter);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
