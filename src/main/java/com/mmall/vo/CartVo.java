package com.mmall.vo;

import com.sun.org.apache.xpath.internal.operations.Bool;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车 Vo对象 、下面包括购物车商品vo的一个集合
 *
 * @author yangshaojun
 * @create 2017-12-07 上午11:32
 **/

public class CartVo {
    // 购物车商品vo的一个集合
    private List<CartProductVo> cartProductVoList;
    //     购物车所有商品总价
    private BigDecimal cartTotalPrice;
    //     是否已经都勾选
    private Boolean allChecked;
    private String imageHost;

    public List<CartProductVo> getCartProductVoList() {
        return cartProductVoList;
    }

    public void setCartProductVoList(List<CartProductVo> cartProductVoList) {
        this.cartProductVoList = cartProductVoList;
    }

    public BigDecimal getCartTotalPrice() {
        return cartTotalPrice;
    }

    public void setCartTotalPrice(BigDecimal cartTotalPrice) {
        this.cartTotalPrice = cartTotalPrice;
    }

    public Boolean getAllChecked() {
        return allChecked;
    }

    public void setAllChecked(Boolean allChecked) {
        this.allChecked = allChecked;
    }

    public String getImageHost() {
        return imageHost;
    }

    public void setImageHost(String imageHost) {
        this.imageHost = imageHost;
    }
}
