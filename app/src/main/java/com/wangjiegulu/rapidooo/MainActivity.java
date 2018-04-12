package com.wangjiegulu.rapidooo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.wangjiegulu.rapidooo.depmodule.bll.FakeInteractorImpl;
import com.wangjiegulu.rapidooo.depmodule.bll._bo.PetBO;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FakeInteractorImpl fakeInteractor = new FakeInteractorImpl();

        PetBO petBO = fakeInteractor.requestPet();

        Log.i(TAG, "petDTO_: " + petBO.getPetName());


    }
}
