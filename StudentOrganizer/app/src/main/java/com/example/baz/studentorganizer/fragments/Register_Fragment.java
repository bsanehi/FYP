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

import static com.example.baz.studentorganizer.utilities.Validation.validateEmail;
import static com.example.baz.studentorganizer.utilities.Validation.validateFields;

import com.example.baz.studentorganizer.models.Response;
import com.example.baz.studentorganizer.models.User;
import com.example.baz.studentorganizer.network.ServerRequests;
import com.example.baz.studentorganizer.utilities.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;


/**
 * Created by Baz on 24/11/2017.
 */

public class Register_Fragment extends Fragment{

    public static final String TAG_NAME = Register_Fragment.class.getSimpleName();


    private EditText eT_Username;
    private EditText eT_Email;
    private EditText eT_Password;

    private TextInputLayout tI_Username;
    private TextInputLayout tI_Email;
    private TextInputLayout tI_Password;

    private TextView tV_Login;

    private Button bt_Register;

    private ProgressBar progressBar;


    private CompositeSubscription subscriptions;
    private SharedPreferences sharedPreferences;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_register,container,false);
        subscriptions = new CompositeSubscription();
        initializeViews(view);
        initializeSharedPreferences();
        return view;

    }


    private void initializeViews(View v) {

        eT_Username = (EditText) v.findViewById(R.id.et_username);
        eT_Email = (EditText) v.findViewById(R.id.et_email);
        eT_Password = (EditText) v.findViewById(R.id.et_password);

        bt_Register = (Button) v.findViewById(R.id.btn_register);


        tI_Username = (TextInputLayout) v.findViewById(R.id.ti_username);
        tI_Email = (TextInputLayout) v.findViewById(R.id.ti_email);
        tI_Password = (TextInputLayout) v.findViewById(R.id.ti_password);


        progressBar = (ProgressBar) v.findViewById(R.id.progress);

        tV_Login = (TextView) v.findViewById(R.id.tv_login);

        bt_Register.setOnClickListener(view -> register());
        tV_Login.setOnClickListener(view -> goToLoginPage());

    }


    private void initializeSharedPreferences() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }


    private void setError() {

        tI_Username.setError(null);
        tI_Email.setError(null);
        tI_Password.setError(null);

        tI_Username.setErrorEnabled(false);
        tI_Email.setErrorEnabled(false);
        tI_Password.setErrorEnabled(false);

    }


    private void register() {

        setError();

        String username = eT_Username.getText().toString();
        String email = eT_Email.getText().toString();
        String password = eT_Password.getText().toString();

        int invalid_input = 0;

        if (!validateFields(username)) {
            invalid_input++;
            tI_Username.setError("Enter a username.");
        }

        if (!validateEmail(email)) {
            invalid_input++;
            tI_Email.setError("Enter a valid email address.");
        }

        if (!validateFields(password)) {
            invalid_input++;
            tI_Password.setError("Enter a password.");
        }

        if (invalid_input == 0) {

            tV_Login.setVisibility(View.GONE);

            bt_Register.setEnabled(false);


            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(password);


            registerClient(user);
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




    private void registerClient(User user) {

        subscriptions.add(ServerRequests.getRetrofit().register(user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }



    private void handleResponse(Response response) {

        progressBar.setVisibility(View.GONE);


        Log.e("Student_TTT", response.getMessage().toString());

        // save token and email into device sharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.TOKEN,response.getToken());
        editor.putString(Constants.EMAIL,response.getMessage());
        editor.apply();

        eT_Username.setText(null);
        eT_Email.setText(null);
        eT_Password.setText(null);

        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        startActivity(intent);

    }

    private void handleError(Throwable error) {

        bt_Register.setEnabled(true);

        progressBar.setVisibility(View.GONE);

        tV_Login.setVisibility(View.VISIBLE);

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

            Log.e("StudentYOOOO", error.toString());
            showSnackBarMessage("Failed to connect to server. " + error);
        }
    }



    private void goToLoginPage(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Login_Fragment fragment = new Login_Fragment();
        ft.replace(R.id.fragmentFrame, fragment, Login_Fragment.TAG_NAME);
        ft.commit();
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        subscriptions.unsubscribe();
    }

}
