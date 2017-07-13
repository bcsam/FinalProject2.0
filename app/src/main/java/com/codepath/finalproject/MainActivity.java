package com.codepath.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity { // TODO: 7/12/17 make the app work if the device is turned sideways 

    Button btCheck;
    EditText etBody;
    EditText etName;
    EditText etSubject;
    AnalyzerClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        InitializeViews();
        unwrapIntent();
        setListeners();



        /*
        etBody.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //btCheck.setVisibility(View.VISIBLE);
                //btCheck.setBackgroundColor(Color.parseColor("#D3D3D3")); // TODO: 7/12/17 abstract this
                if(etBody.getText().toString().trim().length()>0 && etName.getText().toString().trim().length()>0) {
                    btCheck.setBackgroundColor(Color.parseColor("#267326")); // TODO: 7/12/17 abstract this

                }else if(etBody.getText().toString().trim().length()>0 || etName.getText().toString().trim().length()>0){
                    btCheck.setBackgroundColor(Color.parseColor("#D3D3D3")); // TODO: 7/12/17 abstract this

                }else{
                    btCheck.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable str) {
                if(etBody.getText().toString().trim().length()>0 && etName.getText().toString().trim().length()>0) {
                    btCheck.setBackgroundColor(Color.parseColor("#267326")); // TODO: 7/12/17 abstract this

                }else if(etBody.getText().toString().trim().length()>0 || etName.getText().toString().trim().length()>0){
                    btCheck.setBackgroundColor(Color.parseColor("#D3D3D3")); // TODO: 7/12/17 abstract this

                }else{
                    btCheck.setVisibility(View.INVISIBLE);
                }
            }

        });*/
    }

    private void setListeners() {
        btCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubmit();
            }
        });
    }

    public void InitializeViews(){
        btCheck = (Button) findViewById(R.id.btCheck);
        etBody = (EditText) findViewById(R.id.etBody);
        etName = (EditText) findViewById(R.id.etName);
        etSubject = (EditText) findViewById(R.id.etSubject);
    }

    public void unwrapIntent(){
        String recipient = getIntent().getStringExtra("recipient");
        if (recipient != null){
            etName.setText(recipient);
        }

        String subject = getIntent().getStringExtra("subject");
        if (subject != null){
            etSubject.setText(subject);
        }

        String message = getIntent().getStringExtra("message");
        if (message != null){
            etBody.setText(message);
        }
    }

    /**
     *
     */
    public void onSubmit(){
        String message = etBody.getText().toString();
        String to = etName.getText().toString();
        String subject = etSubject.getText().toString();

        if(!message.equals("") && !to.equals("")){
            Intent intent = new Intent(MainActivity.this, PostCheckActivity.class);
            intent.putExtra("message", message);
            intent.putExtra("to", to);
            intent.putExtra("subject", subject);

            TextBody tb = new TextBody();
            tb.setMessage(message);
            client = new AnalyzerClient();
            client.getToneScores(tb);
            MainActivity.this.startActivity(intent);

        //makes the user enter a message before submitting
        }else if (message.equals("")){
            Toast.makeText(getApplicationContext(), "Please enter a message!",
                    Toast.LENGTH_LONG).show();

        //makes the user enter a recipient before submitting
        }else{
            Toast.makeText(getApplicationContext(), "Please enter a recipient!",
                    Toast.LENGTH_LONG).show();
        }
            
    }
}

