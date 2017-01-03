package au.com.stripysock.utiltest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import au.com.stripysock.util.Helper;
import au.com.stripysock.util.Logger;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Logger.d("Test Log: " + Helper.convertDpToPixel(10));
        Logger.e("Test Error");
    }
}
