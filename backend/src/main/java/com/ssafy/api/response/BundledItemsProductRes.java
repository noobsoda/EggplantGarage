package com.ssafy.api.response;

import lombok.Data;

@Data
public class BundledItemsProductRes {
    private String name;
    private int soldPrice;
    private boolean isPaid;
    private int leftTopX;
    private int leftTopY;
    private int rightBottomX;
    private int rightBottomY;
    private String imageUrl;
    private Long buyerId;
    private String nickname;

    public BundledItemsProductRes(String name, int soldPrice, boolean isPaid, int leftTopX, int leftTopY, int rightBottomX, int rightBottomY, String imageUrl, Long buyerId, String nickname) {
        this.name = name;
        this.soldPrice = soldPrice;
        this.isPaid = isPaid;
        this.leftTopX = leftTopX;
        this.leftTopY = leftTopY;
        this.rightBottomX = rightBottomX;
        this.rightBottomY = rightBottomY;
        this.imageUrl = imageUrl;
        this.buyerId = buyerId;
        this.nickname = nickname;
    }
}
