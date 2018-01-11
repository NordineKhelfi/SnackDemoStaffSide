package com.khelfi.snackdemostaffside;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.khelfi.snackdemostaffside.Common.Common;
import com.khelfi.snackdemostaffside.Interfaces.ItemClickListener;
import com.khelfi.snackdemostaffside.Model.Food;
import com.khelfi.snackdemostaffside.ViewHolder.FoodViewHolder;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import dmax.dialog.SpotsDialog;

public class FoodListActivity extends AppCompatActivity {

    private static final int PICK_FOOD_IMAGE_REQUEST_CODE = 72;
    String categoryId;

    RecyclerView recyclerView;

    //Firebase
    DatabaseReference food_table;
    FirebaseRecyclerAdapter<Food, FoodViewHolder> recyclerAdapter;
    StorageReference storageReference;


    //Add new food
    FloatingActionButton fab;
    AlertDialog dialog;

    MaterialEditText etFoodName, etDescription, etPrice;
    Button bSelect, bUpload;

    Uri savedUri;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);


        //Firebase
        food_table = FirebaseDatabase.getInstance().getReference("Food");
        storageReference = FirebaseStorage.getInstance().getReference();


        recyclerView = (RecyclerView) findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);

        if(getIntent() != null) {
            categoryId = getIntent().getStringExtra("CategoryId");
        }

        if(!categoryId.isEmpty() && categoryId != null){
            loadFoodList(categoryId);
        }

        //Add a new food
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddFoodDialog();
            }
        });
    }

    private void loadFoodList(String categoryId) {

        //Fill the recyclerView
        recyclerAdapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(Food.class,
                R.layout.food_item,
                FoodViewHolder.class,
                food_table.orderByChild("menuId").equalTo(categoryId)) {    // <-- " SELECT * FROM food_table WHERE menuId = 'categoryId' " in SQL

            @Override
            protected void populateViewHolder(FoodViewHolder viewHolder, final Food model, int position) {
                viewHolder.tvFood.setText(model.getName());
                Picasso.with(getApplicationContext()).load(model.getImageLink()).into(viewHolder.ivFood);

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                    }
                });

            }
        };

        //finally Set the adapter
        recyclerView.setAdapter(recyclerAdapter);

    }

    private void showAddFoodDialog() {
        LayoutInflater inflater = this.getLayoutInflater();
        View add_new_food = inflater.inflate(R.layout.add_new_food, null);

        etFoodName = (MaterialEditText) add_new_food.findViewById(R.id.etNewFood);
        etDescription = (MaterialEditText) add_new_food.findViewById(R.id.etDescription);
        etPrice = (MaterialEditText) add_new_food.findViewById(R.id.etPrice);

        bSelect = (Button) add_new_food.findViewById(R.id.bSelect);
        bUpload = (Button) add_new_food.findViewById(R.id.bUpload);

        bSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_FOOD_IMAGE_REQUEST_CODE);
            }
        });

        bUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadFood();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add new food")
                .setView(add_new_food);

        dialog = builder.create();
        dialog.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_FOOD_IMAGE_REQUEST_CODE && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            savedUri = data.getData();
            bSelect.setText("Selected !");
        }

    }

    private void uploadFood() {

        //Input check
        if(etFoodName.getText().toString().matches("") || etDescription.getText().toString().matches("") || etPrice.getText().toString().matches("") ){
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if(savedUri == null){
            Toast.makeText(this, "Please select an image to upload", Toast.LENGTH_SHORT).show();
            return;
        }

        final ProgressDialog mDialog = new ProgressDialog(this);
        mDialog.setMessage("Uploading");
        mDialog.show();

        final String imageName = UUID.randomUUID().toString();
        StorageReference imageReference = storageReference.child("images/" + imageName);

        imageReference.putFile(savedUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mDialog.dismiss();
                Toast.makeText(FoodListActivity.this, "Upload successful !", Toast.LENGTH_SHORT).show();
                dialog.dismiss();

                //Create new Food then push it to the DB
                Food newFood = new Food(etFoodName.getText().toString(), taskSnapshot.getDownloadUrl().toString(), etDescription.getText().toString(), etPrice.getText().toString(), categoryId);
                food_table.push().setValue(newFood);
                Snackbar.make(findViewById(R.id.rlFoodList), "New food added", Snackbar.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(FoodListActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //Clear sa&vedUri
        savedUri = null;

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if(item.getTitle().equals(Common.UPDATE)){
            showUpdateFoodDialog(recyclerAdapter.getRef(item.getOrder()).getKey(), recyclerAdapter.getItem(item.getOrder()));
        }
        else if(item.getTitle().equals(Common.DELETE)){
            food_table.child(recyclerAdapter.getRef(item.getOrder()).getKey()).removeValue();
        }

        return super.onContextItemSelected(item);
    }

    private void showUpdateFoodDialog(final String key, final Food foodItem) {

        LayoutInflater inflater = this.getLayoutInflater();
        View add_new_food = inflater.inflate(R.layout.add_new_food, null);

        etFoodName = (MaterialEditText) add_new_food.findViewById(R.id.etNewFood);
        etDescription = (MaterialEditText) add_new_food.findViewById(R.id.etDescription);
        etPrice = (MaterialEditText) add_new_food.findViewById(R.id.etPrice);

        bSelect = (Button) add_new_food.findViewById(R.id.bSelect);
        bUpload = (Button) add_new_food.findViewById(R.id.bUpload);

        etFoodName.setText(foodItem.getName());
        etDescription.setText(foodItem.getDescription());
        etPrice.setText(foodItem.getPrice());

        bSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_FOOD_IMAGE_REQUEST_CODE);
            }
        });

        bUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateFood(key, foodItem);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update food")
                .setView(add_new_food);

        dialog = builder.create();
        dialog.show();

    }

    private void updateFood(final String key, final Food foodItem) {
        //Input check
        if(etFoodName.getText().toString().matches("") || etDescription.getText().toString().matches("") || etPrice.getText().toString().matches("") ){
            Toast.makeText(this, "Please enter a category name", Toast.LENGTH_SHORT).show();
            return;
        }

        if(savedUri != null){
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Uploading");
            mDialog.show();

            final String imageName = UUID.randomUUID().toString();
            StorageReference imageReference = storageReference.child("images/" + imageName);

            imageReference.putFile(savedUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mDialog.dismiss();
                    Toast.makeText(FoodListActivity.this, "Upload successful !", Toast.LENGTH_SHORT).show();

                    //Now we update the Food here, then push it to the db
                    foodItem.setName(etFoodName.getText().toString());
                    foodItem.setDescription(etDescription.getText().toString());
                    foodItem.setPrice(etPrice.getText().toString());
                    foodItem.setImageLink(taskSnapshot.getDownloadUrl().toString());
                    food_table.child(key).setValue(foodItem);
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(FoodListActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else {
            //We just change it's name etc.., and we update it in the dataBase
            foodItem.setName(etFoodName.getText().toString());
            foodItem.setDescription(etDescription.getText().toString());
            foodItem.setPrice(etPrice.getText().toString());
            food_table.child(key).setValue(foodItem);
            Toast.makeText(this, "Food updated", Toast.LENGTH_SHORT).show();
        }

        dialog.dismiss();
        Snackbar.make(findViewById(R.id.rlFoodList), "Food updated", Snackbar.LENGTH_SHORT).show();

        //Clear savedUri
        savedUri = null;
    }
}
