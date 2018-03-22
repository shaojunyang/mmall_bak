package com.mmall.vo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Formatter;

/**
 * 购物车中商品的vo对象
 *结合产品 和 购物车的 一个抽象对象
 * @author
 * @create 2017-12-07 上午11:20
 **/

public class CartProductVo {
    // 购物车 id
    private Integer id;
    private Integer userId;
    private Integer productId;
    // 商品数量
    private Integer quantity;
    // 商品名字
    private String productName;
    //商品 副标题
    private String productSubtitle;
    // 商品主图
    private String productMainImage;
    // 商品价格
    private BigDecimal productPrice;
    // 商品状态
    private Integer productStatus;
    // 商品总价
    private BigDecimal productTotalPrice;
    // 商品库存
    private Integer productStock;
    // 商品是否勾选
    private Integer productChecked;
    // 限制数量的一个返回结果
    private String limitQuantity;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductSubtitle() {
        return productSubtitle;
    }

    public void setProductSubtitle(String productSubtitle) {
        this.productSubtitle = productSubtitle;
    }

    public String getProductMainImage() {
        return productMainImage;
    }

    public void setProductMainImage(String productMainImage) {
        this.productMainImage = productMainImage;
    }

    public BigDecimal getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(BigDecimal productPrice) {
        this.productPrice = productPrice;
    }

    public Integer getProductStatus() {
        return productStatus;
    }

    public void setProductStatus(Integer productStatus) {
        this.productStatus = productStatus;
    }

    public BigDecimal getProductTotalPrice() {
        return productTotalPrice;
    }

    public void setProductTotalPrice(BigDecimal productTotalPrice) {
        this.productTotalPrice = productTotalPrice;
    }

    public Integer getProductStock() {
        return productStock;
    }

    public void setProductStock(Integer productStock) {
        this.productStock = productStock;
    }

    public Integer getProductChecked() {
        return productChecked;
    }

    public void setProductChecked(Integer productChecked) {
        this.productChecked = productChecked;
    }

    public String getLimitQuantity() {
        return limitQuantity;
    }

    public void setLimitQuantity(String limitQuantity) {
        this.limitQuantity = limitQuantity;
    }
}
