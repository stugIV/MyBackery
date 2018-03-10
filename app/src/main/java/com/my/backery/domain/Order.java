package com.my.backery.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class Order {

    public enum Status {NEW, APPROVED, READY, ISSUED}
    private int id;
    private List<OrderItem> items = Collections.emptyList();
    private Date created = new Date();
    private Status status = Status.NEW;
    private int cost;
}
