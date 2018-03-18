package com.my.backery.items;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.my.backery.R;
import com.my.backery.domain.BackeryMenu;

public class BackeryMenuItem {
    private ImageView thumb;
    private TextView itemName;
    private TextView price;
    private Button button;
    private Context context;
    private View view;
    private BackeryMenu menu;

    public BackeryMenuItem(Context context, ViewGroup parent, int layout, final BackeryMenu menu) {
        this.context = context;
        this.menu = menu;
        final Context c1 = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(layout, parent, false);

        itemName = (TextView) view.findViewById(R.id.menu_item_name);
        price = (TextView) view.findViewById(R.id.menu_item_price);
        button = (Button) view.findViewById(R.id.menu_item_btn);
        thumb = (ImageView) view.findViewById(R.id.menu_item_thumb);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(c1, "Button clicked " + itemName.getText(),
                        Toast.LENGTH_LONG).show();
                view.setVisibility(View.INVISIBLE);
                menu.setAmount(1);
            }
        });

        setItemName(menu.getItemName());
        setPrice(menu.getPrice());
    }

    public void setItemName(String name) {
        itemName.setText(name);
    }

    public void setPrice(double pr) {
        price.setText(""+pr);
    }

    public void setIcon(Drawable d) {
        thumb.setImageDrawable(d);
    }

    public View getConvertView() { return view;}

    public BackeryMenu getMenu() { return menu;}
}
