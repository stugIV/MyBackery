package com.my.backery.activities;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.my.backery.R;
import com.my.backery.domain.BackeryMenu;
import com.my.backery.items.BackeryMenuItem;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

public class MenuActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private Button btnGet;
    private ListView menuList;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_menu:
                    showMenu();
                    return true;
                case R.id.navigation_orders:
                    showOrders();
                    return true;
                case R.id.navigation_notifications:
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mTextMessage = (TextView) findViewById(R.id.message);
        btnGet = (Button) findViewById(R.id.button);
        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildMenuList();
            }
        });
        menuList = (ListView) findViewById(R.id.menu_view);

        buildMenuList();
    }

    protected void showMenu() {
        menuList.setVisibility(View.VISIBLE);
        buildMenuList();
    }

    protected void showOrders() {
        menuList.setVisibility(View.INVISIBLE);
    }

    private void buildMenuList() {
        try {
            new GetMenuRequestTask(getString(R.string.service_base_url), this).execute();
        }catch (Throwable e) {
            Toast.makeText(MenuActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    protected void createMenu(List<BackeryMenu> items) {
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        ListView lv = (ListView) findViewById(R.id.menu_view);
        lv.setAdapter(new BackeryMenuListAdapter(this, R.layout.menu_item, items));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MenuActivity.this, "from OnCreate " + position, Toast.LENGTH_LONG).show();
            }
        });
    }

    private class BackeryMenuListAdapter extends ArrayAdapter<BackeryMenu> {
        private int layout;

        private BackeryMenuListAdapter(Context context, int resource, List<BackeryMenu> objects) {
            super(context, resource, objects);
            layout = resource;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                BackeryMenuItem item = new BackeryMenuItem(getContext(), parent, layout);
                convertView = item.getConvertView();
                convertView.setTag(item);
            }
            BackeryMenuItem item = (BackeryMenuItem) convertView.getTag();
            item.setItemName(getItem(position).getItemName());

            return convertView;
        }
    }

    public class GetMenuRequestTask extends AsyncTask<Void, Void, BackeryMenu[]> {
        private static final String MENU_URL = "menu";

        private String baseUrl;
        private MenuActivity activity;

        public GetMenuRequestTask(String baseUrl, MenuActivity activity) {
            this.baseUrl = baseUrl;
            this.activity = activity;
        }

        @Override
        protected BackeryMenu[] doInBackground(Void... voids) {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            BackeryMenu[] menuItems;
            menuItems = restTemplate.getForObject(getUrl(), BackeryMenu[].class);
            return menuItems;
        }

        @Override
        protected void onPostExecute(BackeryMenu[] menuItems) {
            activity.createMenu(Arrays.asList(menuItems));
        }


        private String getUrl() {
            return new StringBuilder().append(baseUrl).append("/").append(MENU_URL).toString();
        }
    }
}
