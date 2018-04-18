package com.wangjiegulu.rapidooo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.wangjiegulu.rapidooo.depmodule.bll.FakeInteractorImpl;
import com.wangjiegulu.rapidooo.depmodule.bll.xbo.parcelable.PetBO;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FakeInteractorImpl fakeInteractor = new FakeInteractorImpl();

        for (int i = 0; i < 13; i++) {
            PetBO petBO1 = fakeInteractor.requestPet();
            PetBO petBO2 = fakeInteractor.requestPet();
            PetBO petBO3 = fakeInteractor.requestPet();
            PetBO petBO4 = fakeInteractor.requestPet();
            Log.i(TAG, "petBO1: " + petBO1);
            Log.i(TAG, "petBO2: " + petBO2);
            Log.i(TAG, "petBO3: " + petBO3);
            Log.i(TAG, "petBO4: " + petBO4);
            petBO1.release();
            petBO2.release();
            petBO3.release();
            petBO4.release();
        }


    }


}
