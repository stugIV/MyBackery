package com.my.backery.domain;


import android.view.MenuItem;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {
    @JsonIgnore
    private int id;

    private int quantity;

    private BackeryMenu menuItem;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BackeryMenu getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(BackeryMenu menuItem) {
        this.menuItem = menuItem;
    }
}
