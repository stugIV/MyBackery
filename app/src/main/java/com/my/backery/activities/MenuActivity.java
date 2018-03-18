package com.my.backery.activities;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.backery.R;
import com.my.backery.fragment.MenuFragment;
import com.my.backery.fragment.OrdersFragment;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

public class MenuActivity extends AppCompatActivity {

    private MenuFragment menuFragment;
    private OrdersFragment ordersFragment;

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

        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        jsonConverter.setObjectMapper(objectMapper);

        menuFragment = new MenuFragment();
        menuFragment.setConverter(jsonConverter);
        menuFragment.setContext(this);
        menuFragment.updateMenuItems(getString(R.string.service_base_url));
        ordersFragment = new OrdersFragment();
        ordersFragment.setConverter(jsonConverter);
        ordersFragment.setContext(this);
        showMenu();
    }

    protected void showMenu() {
        menuFragment.updateMenuItems(getString(R.string.service_base_url));
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content, menuFragment)
                .commit();
    }

    protected void showOrders() {
        ordersFragment.setSelectedMenuItems(menuFragment.selectedItems());
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content, ordersFragment)
                .commit();

    }
}
