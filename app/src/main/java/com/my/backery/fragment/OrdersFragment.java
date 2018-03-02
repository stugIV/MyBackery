package com.my.backery.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.my.backery.R;
import com.my.backery.domain.BackeryMenu;
import com.my.backery.domain.Order;
import com.my.backery.domain.OrderItem;
import com.my.backery.items.BackeryMenuItem;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class OrdersFragment extends Fragment {
    private List<BackeryMenu> items;
    private List<OrderItem> oItems = new LinkedList<>();
    private ListView orderItemsList;
    private Context context;
    private Button orderButton;
    private MappingJackson2HttpMessageConverter converter;

    public void setConverter(MappingJackson2HttpMessageConverter converter) {
        this.converter = converter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);
        orderItemsList = view.findViewById(R.id.orderItemsList);
        context = getActivity().getApplicationContext();
        orderButton = view.findViewById(R.id.orderButton);
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PostOrderTask(getString(R.string.service_base_url), items).execute();
            }
        });
        return view;
    }

    public void setSelectedMenuItems(List<BackeryMenu> selected) {
        items = selected;
        for(BackeryMenu item : selected) {
            OrderItem oi = new OrderItem();
            oi.setMenuItem(item);
            oi.setQuantity(item.getAmount());
            oItems.add(oi);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        buildOrder();
    }

    protected void buildOrder() {
        orderItemsList.setAdapter(
                new OrderItemsListAdapter(context, R.layout.menu_item, items));
    }

    private class OrderItemsListAdapter extends ArrayAdapter<BackeryMenu> {
        private int layout;

        private OrderItemsListAdapter(Context context, int resource, List<BackeryMenu> objects) {
            super(context, resource, objects);
            layout = resource;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                BackeryMenuItem item =
                        new BackeryMenuItem(getContext(), parent, layout, getItem(position));
                convertView = item.getConvertView();
                convertView.setTag(item);
            }
            BackeryMenuItem item = (BackeryMenuItem) convertView.getTag();
            item.setItemName(getItem(position).getItemName());

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

            restTemplate.getMessageConverters().add(converter);

            Order order = new Order();

            order.setItems(oItems);
            try {
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
        }


        private String getUrl() {
            return new StringBuilder().append(baseUrl).append("/").append(ORDER_URL).toString();
        }
    }
}
