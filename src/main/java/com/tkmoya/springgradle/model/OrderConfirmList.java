package com.tkmoya.springgradle.model;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class OrderConfirmList {
    @Valid
    List<OrderConfirmModel> productList = new ArrayList<>();
}