package com.khelfi.snackdemostaffside;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button bSignIn;
    TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bSignIn = (Button) findViewById(R.id.bSignIn);
        tvTitle = (TextView) findViewById(R.id.tvTitle);

        /*Typeface ttf = Typeface.createFromAsset(getAssets(), "fonts/Landliebe.ttf");
        tvTitle.setTypeface(ttf);*/

        bSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });
    }
}
