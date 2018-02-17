package com.my.backery;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.my.backery.domain.BackeryMenuItem;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private Button button;
    private TextView lv_response;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_menu:
//                    showMenu();
                    return true;
                case R.id.navigation_orders:
//                    mTextMessage.setText(R.string.title_orders);
                    mTextMessage.setText("amy text");
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        button = (Button) findViewById(R.id.button_get);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callList();
            }
        });
        lv_response = (TextView) findViewById(R.id.lv_response);
//        createMenu();

        new GetMenuRequestTask(getString(R.string.service_base_url), this).execute();
    }

    private void callList() {
        try {
            new GetMenuRequestTask(getString(R.string.service_base_url), this).execute();
        }catch (Throwable e) {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    protected void createMenu(List<BackeryMenuItem> items) {
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        ListView lv = (ListView) findViewById(R.id.list_view);
        lv.setAdapter(new BackeryMenuListAdapter(this, R.layout.menu_item, items));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "from OnCreate " + position, Toast.LENGTH_LONG).show();
            }
        });
    }

    private class BackeryMenuListAdapter extends ArrayAdapter<BackeryMenuItem> {
        private int layout;

        private BackeryMenuListAdapter(Context context, int resource, List<BackeryMenuItem> objects) {
            super(context, resource, objects);
            layout = resource;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);

                ViewHolder viewHolder = new ViewHolder();
                viewHolder.thumb = (ImageView) convertView.findViewById(R.id.menu_item_thumb);
                if (viewHolder.thumb != null)
                    viewHolder.thumb.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getContext(), "Item clicked " + position, Toast.LENGTH_LONG).show();
                        }
                    });
                viewHolder.itemName = (TextView) convertView.findViewById(R.id.menu_item_name);
                viewHolder.price = (TextView) convertView.findViewById(R.id.menu_item_price);
                viewHolder.button = (Button) convertView.findViewById(R.id.menu_item_btn);
                convertView.setTag(viewHolder);
            }
            ViewHolder mainViewHolder = (ViewHolder) convertView.getTag();
            mainViewHolder.itemName.setText(getItem(position).getItemName());
            mainViewHolder.price.setText(""+getItem(position).getPrice());
            mainViewHolder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "Button clicked " + position, Toast.LENGTH_LONG).show();
                }
            });

            return convertView;
        }
    }

    public class GetMenuRequestTask extends AsyncTask<Void, Void, BackeryMenuItem[]> {
        private static final String MENU_URL = "menu";

        private String baseUrl;
        private MainActivity activity;

        public GetMenuRequestTask(String baseUrl, MainActivity activity) {
            this.baseUrl = baseUrl;
            this.activity = activity;
        }

        @Override
        protected BackeryMenuItem[] doInBackground(Void... voids) {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            BackeryMenuItem[] menuItems;
            menuItems = restTemplate.getForObject(getUrl(), BackeryMenuItem[].class);
            return menuItems;
        }

        @Override
        protected void onPostExecute(BackeryMenuItem[] menuItems) {
            activity.createMenu(Arrays.asList(menuItems));
        }


        private String getUrl() {
            return new StringBuilder().append(baseUrl).append("/").append(MENU_URL).toString();
        }
    }

    public class ViewHolder {
        ImageView thumb;
        TextView itemName;
        TextView price;
        Button button;
    }
}
