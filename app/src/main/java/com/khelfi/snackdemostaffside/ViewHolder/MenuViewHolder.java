package com.khelfi.snackdemostaffside.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.khelfi.snackdemostaffside.Common.Common;
import com.khelfi.snackdemostaffside.Interfaces.ItemClickListener;
import com.khelfi.snackdemostaffside.R;

/**
 * Our RecyclerView's ViewHolder.
 * Created by norma on 23/12/2017.
 */

public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {

    public ImageView ivMenu;
    public TextView tvMenuName;
    private ItemClickListener itemClickListener;

    public MenuViewHolder(View itemView) {
        super(itemView);

        ivMenu = (ImageView) itemView.findViewById(R.id.ivMenuItem);
        tvMenuName = (TextView) itemView.findViewById(R.id.tvMealName);

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
        contextMenu.add(0, 0, getAdapterPosition(), Common.UPDATE);
        contextMenu.add(0, 0, getAdapterPosition(), Common.DELETE);
    }
}
