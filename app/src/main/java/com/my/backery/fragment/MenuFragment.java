package com.my.backery.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class MenuFragment extends Fragment {
    private ListView menuListView;
    private Context context;
    private MappingJackson2HttpMessageConverter converter
            = new MappingJackson2HttpMessageConverter();
    private List<BackeryMenu> menuItems = Collections.emptyList();
    private Logger logger = Logger.getLogger(getClass().getName());

    public void setMenuItems(List<BackeryMenu> menuItems) {
        this.menuItems = menuItems;
    }

    public void setConverter(MappingJackson2HttpMessageConverter converter) {
        this.converter = converter;
    }

    public void updateMenuItems(String url) {
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

            logger.info("doInBackground " + converter);
            restTemplate.getMessageConverters().add(converter);

            BackeryMenu[] menuItems;
            try {
                logger.info("getForObject");
                menuItems = restTemplate.getForObject(getUrl(), BackeryMenu[].class);
                return menuItems;
            }catch (Exception e) {
                logger.info("exception: " + e);
                exceptions.add(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(BackeryMenu[] menuItems) {
            if (exceptions.isEmpty()) {
                logger.info("stMenuItems");
                setMenuItems(Arrays.asList(menuItems));
            }
            else
                Toast.makeText(context, exceptions.get(0).getMessage(),Toast.LENGTH_LONG).show();
        }


        private String getUrl() {
            return new StringBuilder().append(baseUrl).append("/").append(MENU_URL).toString();
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