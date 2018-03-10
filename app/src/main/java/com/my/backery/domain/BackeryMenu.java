package com.my.backery.domain;

public class BackeryMenu {
    public enum ItemState { MENU_STATE, ORDER_STATE}

    private ItemState state = ItemState.MENU_STATE;

    private Integer id;

    private String itemName;

    private Double price;

    private int amount = 0;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ItemState getState() {
        return state;
    }

    public void setState(ItemState state) {
        this.state = state;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public int getAmount() { return amount;}

    public void setAmount(int amount) { this.amount = amount;}
}
