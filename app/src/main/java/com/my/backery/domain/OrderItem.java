package com.my.backery.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {
    @JsonIgnore
    private int id;

    private int amount;

    private BackeryMenu menuItem;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public BackeryMenu getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(BackeryMenu menuItem) {
        this.menuItem = menuItem;
    }
}
