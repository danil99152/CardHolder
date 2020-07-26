package com.danilkomyshev.cardholder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class ViewHolder extends RecyclerView.ViewHolder {
    TextView message;
    ViewHolder(@NonNull View itemView) {
        super(itemView);
        message = itemView.findViewById(R.id.card_item);
    }
}