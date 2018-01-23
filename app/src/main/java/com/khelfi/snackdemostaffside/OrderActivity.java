package com.khelfi.snackdemostaffside;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.khelfi.snackdemostaffside.Common.Common;
import com.khelfi.snackdemostaffside.Interfaces.ItemClickListener;
import com.khelfi.snackdemostaffside.Model.Message;
import com.khelfi.snackdemostaffside.Model.MyResponse;
import com.khelfi.snackdemostaffside.Model.Notification;
import com.khelfi.snackdemostaffside.Model.Request;
import com.khelfi.snackdemostaffside.Model.Token;
import com.khelfi.snackdemostaffside.RemoteWebServer.APIService;
import com.khelfi.snackdemostaffside.ViewHolder.OrderViewHolder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderActivity extends AppCompatActivity {

    public RecyclerView recyclerView;

    DatabaseReference requests;
    FirebaseRecyclerAdapter<Request, OrderViewHolder> recyclerAdapter;

    //FCM Servicce
    APIService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_order);
        recyclerView.setHasFixedSize(true);

        //Firebase
        requests = FirebaseDatabase.getInstance().getReference("Requests");

        //Init FCM Service
        mService = Common.getFCMService();

        loadOrders();
    }

    private void loadOrders() {

        recyclerAdapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(Request.class,
                R.layout.order_item,
                OrderViewHolder.class,
                requests) {    // <-- " SELECT * FROM Requests "


            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, Request model, int position) {

                viewHolder.tvId.setText(recyclerAdapter.getRef(position).getKey());
                viewHolder.tvStatus.setText(codeToStatus(model.getStatus()));
                viewHolder.tvPhone.setText(model.getPhone());
                viewHolder.tvAddress.setText(model.getAddress());

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                    }
                });
            }
        };

        recyclerAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(recyclerAdapter);
    }

    private String codeToStatus(String code) {

        switch (code){

            case "0" :
                return "Placed";

            case "1" :
                return "On my way..";

            case "2" :
                return "Delivered";

            default:
                return "Placed";
        }

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if(item.getTitle().equals(Common.UPDATE)){
            showUpdateDialog(recyclerAdapter.getRef(item.getOrder()).getKey(), recyclerAdapter.getItem(item.getOrder()));

        } else if(item.getTitle().equals(Common.DELETE)){
            requests.child(recyclerAdapter.getRef(item.getOrder()).getKey()).removeValue();
        }

        return super.onContextItemSelected(item);
    }

    private void showUpdateDialog(final String key, final Request requestItem) {

        LayoutInflater inflater = this.getLayoutInflater();
        View updateOrderView = inflater.inflate(R.layout.update_order, null);
        final MaterialSpinner spinner = (MaterialSpinner) updateOrderView.findViewById(R.id.statusSpinner);

        spinner.setItems("Placed", "On my way", "Delivered");

        final AlertDialog.Builder adBuilder = new AlertDialog.Builder(this);

        adBuilder.setTitle("Update status")
                .setView(updateOrderView)
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //We update the request Status, then refresh it in the DB
                        requestItem.setStatus(String.valueOf(spinner.getSelectedIndex()));
                        requests.child(key).setValue(requestItem);

                        //Send notification to client via FCM remote webserver
                        sendOrderStatusNotificationToClient(requestItem, key);
                    }
                }).create().show();

    }

    private void sendOrderStatusNotificationToClient(final Request requestItem, final String key) {

        /*
        We construct our message payload, like this :
        --> {
                to : "Token...",
                notification : {
                    title : "our title",
                    body : "Lorem ipsum blablabla..."
                }
            }
        */

        DatabaseReference token_table = FirebaseDatabase.getInstance().getReference("Tokens");
        token_table.orderByKey().equalTo(requestItem.getPhone()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for( DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);

                    Notification notification = new Notification("Snack Demo", "Your order #" + key + " is " + Common.codeToStatus(requestItem.getStatus()));
                    Message message = new Message(token.getToken(), notification);

                    mService.sendNotification(message)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if(response.body().success == 1)
                                        Toast.makeText(OrderActivity.this, "Order updated and notified !", Toast.LENGTH_SHORT).show();
                                    else
                                        Toast.makeText(OrderActivity.this, "Order updated, but unable to notify ..", Toast.LENGTH_SHORT).show();

                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {
                                    Log.e("ERROR", t.getMessage());
                                    Log.d("DEBUG", "onFailure: " + t.getMessage());
                                }
                            });
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
