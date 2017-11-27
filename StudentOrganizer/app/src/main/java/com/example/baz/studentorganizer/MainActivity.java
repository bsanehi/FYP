package com.example.baz.studentorganizer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;




// import fragments
import com.example.baz.studentorganizer.fragments.Login_Fragment;



public class MainActivity extends AppCompatActivity {

   //public static final String TAG_NAME = MainActivity.class.getSimpleName();

    private Login_Fragment loginFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState == null){
            loadLoginFragment();
        }

    }


    private void loadLoginFragment(){

        if (loginFragment == null) {
            loginFragment = new Login_Fragment();
        }
        getFragmentManager().beginTransaction().replace(R.id.fragmentFrame,loginFragment,Login_Fragment.TAG_NAME).commit();
    }

}
