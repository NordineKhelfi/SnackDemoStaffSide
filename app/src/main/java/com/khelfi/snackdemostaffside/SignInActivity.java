package com.khelfi.snackdemostaffside;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khelfi.snackdemostaffside.Common.Common;
import com.khelfi.snackdemostaffside.Model.User;
import com.rengwuxian.materialedittext.MaterialEditText;

import dmax.dialog.SpotsDialog;

public class SignInActivity extends AppCompatActivity {

    MaterialEditText etPhone, etPass;
    Button bSignIn;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        etPhone = (MaterialEditText) findViewById(R.id.etPhone);
        etPass = (MaterialEditText) findViewById(R.id.etPass);
        bSignIn = (Button) findViewById(R.id.bSignIn);
        dialog = new SpotsDialog(this);

        //Firebase Init
        final DatabaseReference user_table = FirebaseDatabase.getInstance().getReference("User");

        bSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Input control
                if(etPhone.getText().toString().matches("") || etPass.getText().toString().matches(""))
                    return;

                dialog.show();

                user_table.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.child(etPhone.getText().toString()).exists()){

                            User user = dataSnapshot.child(etPhone.getText().toString()).getValue(User.class);
                            //We manually add phone number because it's not present in the Firebase User table (we're going to need it later)
                            user.setPhone(etPhone.getText().toString());

                            if(Boolean.parseBoolean(user.getIsStaff())){

                                if(etPass.getText().toString().equals(user.getPassword())){
                                    Common.currentUser = user;
                                    Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else
                                    Toast.makeText(SignInActivity.this, "Wrong password.", Toast.LENGTH_SHORT).show();

                            }
                            else Toast.makeText(SignInActivity.this, "Please sign in with a Staff account.", Toast.LENGTH_SHORT).show();
                        }
                        else Toast.makeText(SignInActivity.this, "User doesn't exist", Toast.LENGTH_SHORT).show();

                        dialog.dismiss();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });

    }
}
