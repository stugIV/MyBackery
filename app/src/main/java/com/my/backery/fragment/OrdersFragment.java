package com.my.backery.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.my.backery.R;
import com.my.backery.domain.BackeryMenu;
import com.my.backery.domain.Order;
import com.my.backery.domain.OrderItem;
import com.my.backery.items.BackeryMenuItem;
import com.my.backery.items.OrderViewItem;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.sql.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class OrdersFragment extends Fragment {

    private static final int CURRENT_ORDER_HEIGHT = 300;
    private List<BackeryMenu> items;
    private List<OrderItem> oItems = Collections.emptyList();
    private ListView orderItemsList;
    private ListView ordersList;
    private Context context;
    private Button orderButton;
    private MappingJackson2HttpMessageConverter converter;

    public void setConverter(MappingJackson2HttpMessageConverter converter) {
        this.converter = converter;
    }

    public void setContext(Context context) { this.context = context;}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);
        orderItemsList = view.findViewById(R.id.orderItemsList);
        orderButton = view.findViewById(R.id.orderButton);
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PostOrderTask(getString(R.string.service_base_url), items).execute();
            }
        });
        ordersList = view.findViewById(R.id.ordersList);

        updateOrders(getString(R.string.service_base_url));
        return view;
    }

    public void setSelectedMenuItems(List<BackeryMenu> selected) {
        items = selected;
        oItems = new LinkedList<>();
        for(BackeryMenu item : selected) {
            OrderItem oi = new OrderItem();
            oi.setMenuItem(item);
            oi.setAmount(item.getAmount());
            oItems.add(oi);
        }
    }

    public void addOrderItem(BackeryMenu menuItem, int amount) {
        OrderItem item = new OrderItem();
        item.setMenuItem(menuItem);
        item.setAmount(amount);
        oItems.add(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        buildOrder();
    }

    protected void buildOrder() {
        orderItemsList.setAdapter(
                new OrderItemsListAdapter(context, R.layout.menu_item, items));
        orderButton.setEnabled(items.isEmpty() ? false : true);
        orderButton.setText(items.isEmpty() ?
                getString(R.string.ORDER_BUTTON_NOTHING_TO_ORDER) :
                getString(R.string.ORDER_BUTTON_ORDER));
    }

    protected void updateOrders(String url) {
        new GetOrdersTask(url).execute();
    }

    private class OrderItemsListAdapter extends ArrayAdapter<BackeryMenu> {
        private int layout;

        private OrderItemsListAdapter(Context context, int resource, List<BackeryMenu> objects) {
            super(context, resource, objects);
            layout = resource;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            BackeryMenu menu = getItem(position);
            if (convertView == null) {
                BackeryMenuItem item =
                        new BackeryMenuItem(getContext(), parent, layout, menu);
                convertView = item.getConvertView();
                convertView.setTag(item);
            }
            BackeryMenuItem item = (BackeryMenuItem) convertView.getTag();
            new MenuFragment.GetIconTask(item)
                    .execute(getString(R.string.service_base_url)+menu.getIconPath());

            return convertView;
        }
    }

    public class PostOrderTask extends AsyncTask<Void, Void, BackeryMenu[]> {
        private static final String ORDER_URL = "order";

        private String baseUrl;
        private List<BackeryMenu> items;
        private List<Exception> exceptions = new LinkedList<>();

        public PostOrderTask(String baseUrl, List<BackeryMenu> items) {
            this.baseUrl = baseUrl;
            this.items = items;
        }

        @Override
        protected BackeryMenu[] doInBackground(Void... voids) {
            RestTemplate restTemplate = new RestTemplate();

            try {
                restTemplate.getMessageConverters().add(converter);

                Order order = new Order();

                order.setItems(oItems);

                restTemplate.postForObject(getUrl(), order, Integer.class);
            }catch(Exception e) {
                exceptions.add(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(BackeryMenu[] menuItems) {
            if (!exceptions.isEmpty())
                Toast.makeText(context, exceptions.get(0).getMessage(),Toast.LENGTH_LONG).show();
            else {
                ((ArrayAdapter) orderItemsList.getAdapter()).clear();
                updateOrders(baseUrl);
            }
        }


        private String getUrl() {
            return new StringBuilder().append(baseUrl).append("/").append(ORDER_URL).toString();
        }
    }

    private class OrdersListAdapter extends ArrayAdapter<Order> {
        private int layout;

        public OrdersListAdapter(Context context, int resource, Order[] objects) {
            super(context, resource, objects);
            layout = resource;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                OrderViewItem item =
                        new OrderViewItem(getContext(), parent, layout, getItem(position));
                convertView = item.getView();
                convertView.setTag(item);
            }

            return convertView;
        }

    }

    public class GetOrdersTask extends AsyncTask<Void, Void, Order[]> {
        private static final String ORDER_URL = "order";

        private String baseUrl;
        private List<Exception> exceptions = new LinkedList<>();

        public GetOrdersTask(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        @Override
        protected Order[] doInBackground(Void... voids) {
            RestTemplate restTemplate = new RestTemplate();

            try {
                restTemplate.getMessageConverters().add(converter);

                Order[] orders;

                orders = restTemplate.getForObject(getUrl(), Order[].class);
                return orders;
            }catch(Exception e) {
                exceptions.add(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Order[] orders) {
            if (!exceptions.isEmpty())
                Toast.makeText(context, getString(R.string.FAILED_TO_FETCH_ORDERS) + exceptions.get(0).getMessage(),Toast.LENGTH_LONG).show();
            else
                setOrders(orders);
        }


        private String getUrl() {
            return new StringBuilder().append(baseUrl).append("/").append(ORDER_URL).toString();
        }
    }

    protected void setOrders(Order[] orders) {
        ordersList.setAdapter(new OrdersListAdapter(context, R.layout.order_item, orders));
    }
}
