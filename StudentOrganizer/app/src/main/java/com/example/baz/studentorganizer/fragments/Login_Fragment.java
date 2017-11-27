package com.example.baz.studentorganizer.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.baz.studentorganizer.ProfileActivity;
import com.example.baz.studentorganizer.R;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


// import utilities
import static com.example.baz.studentorganizer.utilities.Validation.validateFields;

// import server request class
import com.example.baz.studentorganizer.models.Response;
import com.example.baz.studentorganizer.network.ServerRequests;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

// import constant class
import com.example.baz.studentorganizer.utilities.Constants;


/**
 * Created by Baz on 25/11/2017.
 */

public class Login_Fragment extends Fragment {

    public static final String TAG_NAME = Login_Fragment.class.getSimpleName();

    private EditText eT_usernameEmail;
    private EditText eT_Password;

    private TextInputLayout tI_usernameEmail;
    private TextInputLayout tI_Password;

    private TextView tV_Register;
    private TextView tV_Forgot_Password;

    private Button bt_Login;

    private ProgressBar progressBar;


    private CompositeSubscription subscriptions;
    private SharedPreferences sharedPreferences;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login,container,false);
        subscriptions = new CompositeSubscription();
        initializeViews(view);
        initializeSharedPreferences();
        return view;
    }



    private void initializeViews(View v) {

        eT_usernameEmail = (EditText) v.findViewById(R.id.et_usernameEmail);
        eT_Password = (EditText) v.findViewById(R.id.et_password);

        bt_Login = (Button) v.findViewById(R.id.btn_register);

        tI_usernameEmail = (TextInputLayout) v.findViewById(R.id.ti_email);
        tI_Password = (TextInputLayout) v.findViewById(R.id.ti_password);
        progressBar = (ProgressBar) v.findViewById(R.id.progress);

        tV_Register = (TextView) v.findViewById(R.id.tv_login);
        tV_Forgot_Password = (TextView) v.findViewById(R.id.tv_forgot_password);

        bt_Login.setOnClickListener(view -> login());
        tV_Register.setOnClickListener(view -> goToRegisterPage());
      //  tV_Forgot_Password.setOnClickListener(view -> showDialog());

    }


    private void initializeSharedPreferences() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }


    private void setError() {

        tI_usernameEmail.setError(null);
        tI_Password.setError(null);

        tI_usernameEmail.setErrorEnabled(false);
        tI_Password.setErrorEnabled(false);

    }


    private void login() {

        setError();

        String usernameEmail = eT_usernameEmail.getText().toString();
        String password = eT_Password.getText().toString();

        int invalid_input = 0;

        if (!validateFields(usernameEmail)) {
            invalid_input++;
            tI_usernameEmail.setError("Enter a username or an email address.");
        }

        if (!validateFields(password)) {
            invalid_input++;
            tI_Password.setError("Enter a password.");
        }

        if (invalid_input == 0) {

            tV_Register.setVisibility(View.GONE);
            tV_Forgot_Password.setVisibility(View.GONE);

            bt_Login.setEnabled(false);

            loginClient(usernameEmail,password);
            progressBar.setVisibility(View.VISIBLE);

        } else {
            showSnackBarMessage("Please enter required fields.");
        }

    }



    // show snack bar message
    private void showSnackBarMessage(String message) {
        if (getView() != null) {
            Snackbar.make(getView(),message,Snackbar.LENGTH_SHORT).show();
        }
    }



    private void loginClient(String usernameEmail, String password) {

        subscriptions.add(ServerRequests.getRetrofit(usernameEmail, password).login()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }


    private void handleResponse(Response response) {


        Log.e("NOO!!!", response.toString());

        progressBar.setVisibility(View.GONE);

        // save token and email into device sharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.TOKEN,response.getToken());
        editor.putString(Constants.EMAIL,response.getMessage());
        editor.apply();

        eT_usernameEmail.setText(null);
        eT_Password.setText(null);


        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        startActivity(intent);

        getActivity().getFragmentManager().beginTransaction().remove(this).commit();
        getActivity().finish();

    }

    private void handleError(Throwable error) {

        bt_Login.setEnabled(true);

        progressBar.setVisibility(View.GONE);

        tV_Register.setVisibility(View.VISIBLE);
        tV_Forgot_Password.setVisibility(View.VISIBLE);

        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                Response response = gson.fromJson(errorBody,Response.class);
                showSnackBarMessage(response.getMessage());

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            showSnackBarMessage("Failed to connect to server.");
        }
    }




    private void goToRegisterPage(){

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Register_Fragment fragment = new Register_Fragment();
        ft.replace(R.id.fragmentFrame, fragment, Register_Fragment.TAG_NAME);
        ft.commit();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        subscriptions.unsubscribe();
    }

}