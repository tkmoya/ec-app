package com.tkmoya.springgradle.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderConfirmModel {
    @NotEmpty(message = "個数を入力してください")
    @Pattern(regexp = "^[0-9]*$", message = "半角英数字で入力してください")
    private String productCnt;

    private String productCode;
    private String productName;
    private String maker;
    private String unitPrice;
    private String memo;
}