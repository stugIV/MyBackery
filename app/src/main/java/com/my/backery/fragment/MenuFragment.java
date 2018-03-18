package com.my.backery.fragment;

import android.app.Fragment;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.my.backery.R;
import com.my.backery.domain.BackeryMenu;
import com.my.backery.items.BackeryMenuItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class MenuFragment extends Fragment {
    private ListView menuListView;
    private Context context;
    private MappingJackson2HttpMessageConverter converter
            = new MappingJackson2HttpMessageConverter();
    private List<BackeryMenu> menuItems = Collections.emptyList();
    private Logger logger = LoggerFactory.getLogger(getClass().getName());

    public void setMenuItems(List<BackeryMenu> menuItems) {
        this.menuItems = menuItems;
    }

    public void setConverter(MappingJackson2HttpMessageConverter converter) {
        this.converter = converter;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void updateMenuItems(String url) {
        logger.debug("MenuFragment.updateMenuItems");
        new GetMenuRequestTask(url).execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, null);
        menuListView = view.findViewById(R.id.menuListView);
        context = getActivity().getApplicationContext();

        try {
            createMenu(menuItems);
        } catch (Throwable e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return view;
    }

    public List<BackeryMenu> selectedItems() {
        if (menuListView == null)
            return Collections.emptyList();
        ListAdapter adapter = menuListView.getAdapter();
        List<BackeryMenu> selected = new LinkedList<>();
        for(int i = 0; i < adapter.getCount(); ++i) {
            BackeryMenu item = (BackeryMenu) adapter.getItem(i);
            if(item.getAmount() > 0)
                selected.add(item);
        }

        return selected;
    }

    public class GetMenuRequestTask extends AsyncTask<Void, Void, BackeryMenu[]> {
        private static final String MENU_URL = "menu";

        private String baseUrl;
        private List<Exception> exceptions = new LinkedList<>();

        public GetMenuRequestTask(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        @Override
        protected BackeryMenu[] doInBackground(Void... voids) {
            RestTemplate restTemplate = new RestTemplate();

            restTemplate.getMessageConverters().add(converter);

            BackeryMenu[] menuItems;
            try {
                menuItems = restTemplate.getForObject(getUrl(), BackeryMenu[].class);

                return menuItems;
            }catch (Exception e) {
                exceptions.add(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(BackeryMenu[] menuItems) {
            if (exceptions.isEmpty()) {
                setMenuItems(Arrays.asList(menuItems));
            }
            else {
                Toast.makeText(context, exceptions.get(0).getMessage(), Toast.LENGTH_LONG).show();
            }
        }


        private String getUrl() {
            return new StringBuilder().append(baseUrl).append("/").append(MENU_URL).toString();
        }
    }

    public static class GetIconTask extends AsyncTask<String, Void, Drawable> {

        private BackeryMenuItem item;
        public GetIconTask(BackeryMenuItem item) {
            this.item = item;
        }

        @Override
        protected Drawable doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream is = (InputStream) connection.getInputStream();
                return Drawable.createFromStream(is, "src");
            }catch(Exception e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Drawable result) {
            item.setIcon(result);
        }
    }

    protected void createMenu(final List<BackeryMenu> items) {
        menuListView.setAdapter(new BackeryMenuListAdapter(context, R.layout.menu_item, items));
        menuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                items.get(position).setState(BackeryMenu.ItemState.ORDER_STATE);
                items.get(position).setAmount(1);
                ((BaseAdapter) parent.getAdapter()).notifyDataSetChanged();
                Toast.makeText(context, "from OnCreate " + position, Toast.LENGTH_LONG).show();
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
            BackeryMenu menu = getItem(position);

            if (convertView == null) {
                BackeryMenuItem item =
                        new BackeryMenuItem(getContext(), parent, layout, menu);
                convertView = item.getConvertView();
                convertView.setTag(item);
            }
            BackeryMenuItem item = (BackeryMenuItem) convertView.getTag();
            new GetIconTask(item).execute(
                    getString(R.string.service_base_url)+menu.getIconPath());
            item.setItemName(getItem(position).getItemName());
            if (menu.getState().equals(BackeryMenu.ItemState.ORDER_STATE)) {
                convertView.setVisibility(View.GONE);
                convertView.setLayoutParams(
                        new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,1));
            }
            else {
                convertView.setVisibility(View.VISIBLE);
                convertView.setLayoutParams(
                        new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                AbsListView.LayoutParams.MATCH_PARENT));

            }

            return convertView;
        }
    }
}