package com.example.kushal.letschat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.Firebase;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import java.util.Date;

import java.util.HashMap;
import java.util.Map;
import java.util.Calendar;

public class NextActivity extends AppCompatActivity {

    private Firebase mFirebaseRef;
    private Firebase mMessageRef;
    private String mUsername;
    private FirebaseListAdapter<Message> mListAdapter;
    EditText mEditText;
    ListView mListView;
    private  String s,pos;
    private String option;
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       s= getIntent().getStringExtra("name");
       pos= getIntent().getStringExtra("position");

        mUsername  = s;

        //setting up Firebase context
        Firebase.setAndroidContext(this);

        //connection to root of our database
        mFirebaseRef = new Firebase("https://letschat-47c25.firebaseio.com/");
        mMessageRef = mFirebaseRef.child("messages"+pos);// connection to message object

        mEditText = (EditText)this.findViewById(R.id.message_text);
        mListView = (ListView)this.findViewById(R.id.listView);

        mListAdapter = new FirebaseListAdapter<Message>(mMessageRef,Message.class,R.layout.message_layout,this) {
            @Override
            protected void populateView(View v, Message model) {

                ((TextView)v.findViewById(R.id.username_text_view)).setText(model.getName()+ ": ");
                ((TextView)v.findViewById(R.id.message_text_view)).setText(model.getMessage());

            }
        };
        mListView.setAdapter(mListAdapter);
    }


    public void onSendButtonClick(View view) {
        String message = mEditText.getEditableText().toString();//message to be sent
        if (!message.isEmpty()) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            String s1 = dateFormat.format(date);
            mMessageRef.push().setValue(new Message(mUsername, message + " " + s1));
            mListView.smoothScrollToPosition(mListAdapter.getCount());

            mEditText.setText("");
        }
    }

        /*mFirebaseRef.addAuthStateListener(new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                if (authData != null) {
                    mUsername = ((String) authData.getProviderData().get("email"));
                    findViewById(R.id.loginButton).setVisibility(View.INVISIBLE);
                } else {
                    mUsername = null;
                    findViewById(R.id.loginButton).setVisibility(View.VISIBLE);
                }
            }
        });*/
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;
        }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            // Get text from email and passord field
            final String email = mUsername;
            option = "logout";
            // Initialize  AsyncLogin() class with email and password
            new NextActivity.AsyncLogin().execute(email);
            return true;
        }
        else if(id == R.id.online)
        {
            // Get text from email and passord field
            final String email = mUsername;
            option = "online";
            // Initialize  AsyncLogin() class with email and password
            new NextActivity.AsyncLogin().execute(email);
            return true;

        }


        return super.onOptionsItemSelected(item);
    }


    private class AsyncLogin extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(NextActivity.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tWorking...! Please Wait....");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }
        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides
                if(option.equalsIgnoreCase("online")) {
                    url = new URL("http://attendrollno.000webhostapp.com/ret.php");
                }
                else{
                    url = new URL("http://attendrollno.000webhostapp.com/delete.php");

                }
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception";
            }
            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("username", params[0]);
                String query = builder.build().getEncodedQuery();

                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return "exception";
            }

            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    return(result.toString());

                }else{

                    return("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            } finally {
                conn.disconnect();
            }


        }

        @Override
        protected void onPostExecute(String result) {

            //this method will be running on UI thread

            pdLoading.dismiss();

            if(!result.equalsIgnoreCase("false"))
            {
                /* Here launching another activity when login successful. If you persist login state
                use sharedPreferences of Android. and logout button to clear sharedPreferences.
                 */
                if(option.equalsIgnoreCase("online")) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(NextActivity.this);
                    builder1.setMessage(result)
                            .setTitle("Online Users:");

                    AlertDialog alert =builder1.create();
                    alert.show();

                    builder1.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK button
                        }
                    });

                }
                else
                {
                    Intent intent = new Intent(NextActivity.this,MainActivity.class);
                    startActivity(intent);
                    NextActivity.this.finish();

                }

            }else if (result.equalsIgnoreCase("false")){

                // If username and password does not match display a error message
             //   Toast.makeText(MainActivity.this, "Invalid email or password", Toast.LENGTH_LONG).show();

            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {

              //  Toast.makeText(MainActivity.this, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();

            }
        }

    }

}