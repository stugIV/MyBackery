package com.my.backery.items;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.my.backery.R;

public class BackeryMenuItem {
    private ImageView thumb;
    private TextView itemName;
    private TextView price;
    private Button button;
    private Context context;
    private View view;

    public BackeryMenuItem(Context context, ViewGroup parent, int layout) {
        this.context = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(layout, parent, false);

        itemName = (TextView) view.findViewById(R.id.menu_item_name);
        price = (TextView) view.findViewById(R.id.menu_item_price);
        button = (Button) view.findViewById(R.id.menu_item_btn);
//
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Toast.makeText(getContext(), "Button clicked " + position, Toast.LENGTH_LONG).show();
//            }
//        });

    }

    public void setItemName(String name) {
        itemName.setText(name);
    }

    public void setPrice(double pr) {
        price.setText(""+pr);
    }

    public View getConvertView() { return view;}
}
