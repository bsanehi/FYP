package com.example.baz.studentorganizer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;


// import constant class
import com.example.baz.studentorganizer.models.Response;
import com.example.baz.studentorganizer.network.ServerRequests;
import com.example.baz.studentorganizer.utilities.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import com.example.baz.studentorganizer.models.User;



public class ProfileActivity extends AppCompatActivity {

 //   public static final String TAG_NAME = ProfileActivity.class.getSimpleName();

    private SharedPreferences sharedPreferences;
    private CompositeSubscription subscriptions;


    private String token;
    private String email;

    private TextView tV_Username;
    private TextView tV_Email;
    private TextView tV_Date;

    private Button bt_Logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        subscriptions = new CompositeSubscription();

        initializeSharedPreferences();
        initializeViews();
        loadUserProfile();
    }

    private void initializeViews() {

        tV_Username = (TextView) findViewById(R.id.tv_username);
        tV_Email = (TextView) findViewById(R.id.tv_email);
        tV_Date = (TextView) findViewById(R.id.tv_date);

        bt_Logout = (Button) findViewById(R.id.btn_logout);

        bt_Logout.setOnClickListener(view -> logout());
    }

    private void initializeSharedPreferences() {

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        token = sharedPreferences.getString(Constants.TOKEN,"");
        email = sharedPreferences.getString(Constants.EMAIL,"");
    }




    private void loadUserProfile() {

        subscriptions.add(ServerRequests.getRetrofit(token).getProfile(email)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }





    private void handleResponse(User user) {

       // mProgressbar.setVisibility(View.GONE);
        tV_Username.setText(user.getUsername());
        tV_Email.setText(user.getEmail());
        tV_Date.setText(user.getCreated_at());

    }

    private void handleError(Throwable error) {

       // mProgressbar.setVisibility(View.GONE);

        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();


                Log.e("Logging into timetables", errorBody);

                Response response = gson.fromJson(errorBody,Response.class);
                showSnackBarMessage(response.getMessage());

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            showSnackBarMessage("Network Error !");
        }
    }






    // show snack bar message
    private void showSnackBarMessage(String message) {
       Snackbar.make(findViewById(R.id.activity_main),message,Snackbar.LENGTH_SHORT).show();
    }






    private void logout() {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.EMAIL,"");
        editor.putString(Constants.TOKEN,"");
        editor.apply();
        finish();


        // start main activity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        subscriptions.unsubscribe();
    }



}
