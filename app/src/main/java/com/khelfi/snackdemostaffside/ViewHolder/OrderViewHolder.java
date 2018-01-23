package com.khelfi.snackdemostaffside.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.TextView;

import com.khelfi.snackdemostaffside.Common.Common;
import com.khelfi.snackdemostaffside.Interfaces.ItemClickListener;
import com.khelfi.snackdemostaffside.R;

/**
 * Created by norma on 06/01/2018.
 *
 */

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener{

    public TextView tvId, tvStatus, tvPhone, tvAddress;

    private ItemClickListener itemClickListener;

    public OrderViewHolder(View itemView) {
        super(itemView);

        tvId = (TextView) itemView.findViewById(R.id.tvOrderId);
        tvStatus = (TextView) itemView.findViewById(R.id.tvOrderStatus);
        tvPhone = (TextView) itemView.findViewById(R.id.tvOrderPhone);
        tvAddress = (TextView) itemView.findViewById(R.id.tvOrderAddress);

        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.setHeaderTitle("Choose an action");
        contextMenu.add(0, 0,getAdapterPosition() , Common.UPDATE);
        contextMenu.add(0, 0,getAdapterPosition() , Common.DELETE);
    }
}
