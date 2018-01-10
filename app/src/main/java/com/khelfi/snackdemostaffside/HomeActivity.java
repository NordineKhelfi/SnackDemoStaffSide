package com.khelfi.snackdemostaffside;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.khelfi.snackdemostaffside.Common.Common;
import com.khelfi.snackdemostaffside.Interfaces.ItemClickListener;
import com.khelfi.snackdemostaffside.Model.Category;
import com.khelfi.snackdemostaffside.ViewHolder.MenuViewHolder;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import dmax.dialog.SpotsDialog;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int PICK_IMAGE_REQUEST_CODE = 71;
    TextView tvUser;
    DatabaseReference category_table;
    RecyclerView recyclerView;


    //Add new category
    MaterialEditText etNewCategory;
    Button bSelect, bUpload;
    AlertDialog alertDialog;
    Category newCategory;
    Uri savedUri;

    //Firebase
    FirebaseRecyclerAdapter<Category, MenuViewHolder> recyclerAdapter;
    StorageReference storageReference;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Management Menu");
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showAddCategoryDialog();

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Set user name
        View headerView = navigationView.getHeaderView(0);
        tvUser = (TextView) headerView.findViewById(R.id.tvUser);
        tvUser.setText(Common.currentUser.getName());

        //Init firebase
        category_table = FirebaseDatabase.getInstance().getReference("Category");
        storageReference = FirebaseStorage.getInstance().getReference();

        //Load menus from DB
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        recyclerAdapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(Category.class, R.layout.menu_item, MenuViewHolder.class, category_table) {
            @Override
            protected void populateViewHolder(MenuViewHolder viewHolder, Category model, int position) {

                viewHolder.tvMenuName.setText( model.getName() );
                Picasso.with(getApplicationContext()).load(model.getLink()).into(viewHolder.ivMenu);

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //TODO

                    }
                });

            }
        };

        recyclerAdapter.notifyDataSetChanged();     // Refresh data when it changes
        recyclerView.setAdapter(recyclerAdapter);

    }

    private void showAddCategoryDialog() {

        LayoutInflater inflater = this.getLayoutInflater();
        View add_category_layout = inflater.inflate(R.layout.add_new_category, null);
        AlertDialog.Builder adBuilder = new AlertDialog.Builder(this);

        etNewCategory = (MaterialEditText) add_category_layout.findViewById(R.id.etNewMenu);
        bSelect = (Button) add_category_layout.findViewById(R.id.bSelect);
        bUpload = (Button) add_category_layout.findViewById(R.id.bUpload);

        bSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Let the user pick an image from the gallery, then save the image's Uri
                selectAnImage();
            }
        });

        bUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });

        adBuilder.setTitle("Add new category")
                .setMessage("Enter a name, then select a picture to represent your new category.")
                .setView(add_category_layout);

        alertDialog = adBuilder.create();
        alertDialog.show();

    }

    private void selectAnImage() {

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*"); // <-- MIME. Take a look here : https://fr.wikipedia.org/wiki/Type_MIME
        startActivityForResult(Intent.createChooser(intent, "Select an image"), PICK_IMAGE_REQUEST_CODE);
        //startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE);

    }

    private void uploadImage() {

        //Input check
        if(etNewCategory.getText().toString().matches("")){
            Toast.makeText(this, "Please enter a category name", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(HomeActivity.this, "Upload successfull !", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();

                //Now we instantiate the new Category
                newCategory = new Category(etNewCategory.getText().toString(), taskSnapshot.getDownloadUrl().toString());

                //Then finally add it to the dataBase
                category_table.push().setValue(newCategory);
                Snackbar.make(findViewById(R.id.drawer_layout), "New category added", Snackbar.LENGTH_SHORT).show();
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mDialog.dismiss();
                Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        })
        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

            }
        });

        //Clear savedUri
        savedUri = null;
    }

    // Update/Delete categories
    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if(item.getTitle().equals(Common.UPDATE)){
            showUpdateDialog(recyclerAdapter.getRef(item.getOrder()).getKey(), recyclerAdapter.getItem(item.getOrder()));

        } else if(item.getTitle().equals(Common.DELETE)){
            category_table.child(recyclerAdapter.getRef(item.getOrder()).getKey()).removeValue();
        }

        return super.onContextItemSelected(item);
    }

    private void showUpdateDialog(final String key, final Category categoryItem) {

        LayoutInflater inflater = this.getLayoutInflater();
        View add_category_layout = inflater.inflate(R.layout.add_new_category, null);
        AlertDialog.Builder adBuilder = new AlertDialog.Builder(this);

        etNewCategory = (MaterialEditText) add_category_layout.findViewById(R.id.etNewMenu);
        bSelect = (Button) add_category_layout.findViewById(R.id.bSelect);
        bUpload = (Button) add_category_layout.findViewById(R.id.bUpload);

        etNewCategory.setText(categoryItem.getName());

        bSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Let the user pick an image from the gallery, then save the image's Uri
                selectAnImage();
            }
        });

        bUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeImage(categoryItem, key);
            }
        });

        adBuilder.setTitle("Update category")
                .setMessage("Enter a name, then select a picture to represent your new category.")
                .setView(add_category_layout);

        alertDialog = adBuilder.create();
        alertDialog.show();
    }

    private void changeImage(final Category categoryItem, final String key) {

        //Input check
        if(etNewCategory.getText().toString().matches("")){
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
                    Toast.makeText(HomeActivity.this, "Upload successful !", Toast.LENGTH_SHORT).show();

                    //Now we update the Category here, then push it to the db
                    categoryItem.setLink(taskSnapshot.getDownloadUrl().toString());
                    categoryItem.setName(etNewCategory.getText().toString());
                    category_table.child(key).setValue(categoryItem);
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else {
            //We just change it's name, and we update it in the dataBase
            categoryItem.setName(etNewCategory.getText().toString());
            category_table.child(key).setValue(categoryItem);
        }

        alertDialog.dismiss();
        Snackbar.make(findViewById(R.id.drawer_layout), "Category updated", Snackbar.LENGTH_SHORT).show();

        //Clear savedUri
        savedUri = null;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            savedUri = data.getData();
            bSelect.setText("Selected !");
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement  <-- We removed that !
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {

        } else if (id == R.id.nav_cart) {

        } else if (id == R.id.nav_orders) {

        } else if (id == R.id.nav_exit) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
