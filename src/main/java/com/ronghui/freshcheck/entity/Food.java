package com.ronghui.freshcheck.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author ansel
 * @since 2024-06-02
 */
public class Food implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userId;

    @TableId
    private String foodId;

    private String foodName;

    private String brand;

    private String foodCategory;

    private String createDate;

    private String purchaseDate;

    private String expireDate;

    private Integer ifExpire;

    private Integer quantity;

    private Double weight;

    private String unit;

    private Double price;

    private String storageLocation;

    private int ifTick;

    private int ifAddedWaste;

    public int getIfAddedWaste() {
        return ifAddedWaste;
    }

    public void setIfAddedWaste(int ifAddedWaste) {
        this.ifAddedWaste = ifAddedWaste;
    }

    public int getIfTick() {
        return ifTick;
    }

    public void setIfTick(int ifTick) {
        this.ifTick = ifTick;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }
    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }
    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }
    public String getFoodCategory() {
        return foodCategory;
    }

    public void setFoodCategory(String foodCategory) {
        this.foodCategory = foodCategory;
    }
    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }
    public Integer getIfExpire() {
        return ifExpire;
    }

    public void setIfExpire(Integer ifExpire) {
        this.ifExpire = ifExpire;
    }
    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }
    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Food{" +
                "userId='" + userId + '\'' +
                ", foodId='" + foodId + '\'' +
                ", foodName='" + foodName + '\'' +
                ", brand='" + brand + '\'' +
                ", foodCategory='" + foodCategory + '\'' +
                ", createDate='" + createDate + '\'' +
                ", purchaseDate='" + purchaseDate + '\'' +
                ", expireDate='" + expireDate + '\'' +
                ", ifExpire=" + ifExpire +
                ", quantity=" + quantity +
                ", weight=" + weight +
                ", unit='" + unit + '\'' +
                ", price=" + price +
                ", storageLocation='" + storageLocation + '\'' +
                ", ifTick=" + ifTick +
                ", ifAddedWaste=" + ifAddedWaste +
                '}';
    }
}
