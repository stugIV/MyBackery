package com.my.backery.domain;

import lombok.Data;

@Data
public class BackeryMenu {
    public enum ItemState { MENU_STATE, ORDER_STATE}

    private ItemState state = ItemState.MENU_STATE;

    private Integer id;

    private String itemName;

    private Double price;

    private int amount = 0;

    private String iconPath;
}
