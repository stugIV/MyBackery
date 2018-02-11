package com.my.backery.requests;

import android.os.AsyncTask;

import com.my.backery.domain.MenuItem;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class GetMenuRequestTask extends AsyncTask<Void, Void, MenuItem[]>{
    private static final String MENU_URL = "menu";

    private String baseUrl;

    public GetMenuRequestTask(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    protected MenuItem[] doInBackground(Void... voids) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        MenuItem[] menuItems;
        menuItems = restTemplate.getForObject(getUrl(), MenuItem[].class);
        return menuItems;
    }

    @Override
    protected void onPostExecute(MenuItem[] menuItem) {

    }

    private String getUrl() {
        return new StringBuilder().append(baseUrl).append("/").append(MENU_URL).toString();
    }
}
