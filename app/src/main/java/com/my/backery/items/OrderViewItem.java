package com.my.backery.items;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.my.backery.R;
import com.my.backery.domain.Order;

import java.text.SimpleDateFormat;

public class OrderViewItem {
    private Context context;
    private View view;
    private TextView created;
    private TextView cost;
    private TextView status;
    private TextView amount;
    private Order order;

    public OrderViewItem(Context context, ViewGroup parent, int layout, Order order) {
        this.context = context;
        this.order = order;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(layout, parent, false);

        created = (TextView) view.findViewById(R.id.order_item_created);
        cost = (TextView) view.findViewById(R.id.order_item_cost);
        status = (TextView) view.findViewById(R.id.order_item_status);
        amount = (TextView) view.findViewById(R.id.order_item_amount); 

        SimpleDateFormat f = new SimpleDateFormat("hh:mm");
        created.setText(f.format(order.getCreated()));
        status.setText(statusToText(context, order.getStatus()));
        cost.setText("" + order.getCost() + " " + context.getString(R.string.CURRENCY_RUB));
        amount.setText("" + order.getItems().size()+ " " + context.getString(R.string.ITEMS));
    }

    public View getView() {
        return view;
    }

    private static String statusToText(Context context, Order.Status status) {
        switch (status) {
            case NEW: return context.getString(R.string.STATUS_NEW);
            case READY: return context.getString(R.string.STATUS_READY);
            case ISSUED: return context.getString(R.string.STATUS_ISSUED);
            case APPROVED: return context.getString(R.string.STATUS_APPROVED);
        }
        
        return context.getString(R.string.STATUS_UNKNOWN);
    }
}
