package com.ronghui.freshcheck.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author ansel
 * @since 2024-06-05
 */
@TableName("food_wastage")
public class FoodWastage implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userId;

    private String foodId;

    private String foodName;

    private String brand;

    private int quantity;

    private Double weight;

    private String unit;

    private Double price;

    private String expireDate;

    private String reason;

    private String foodCategory;

    public String getFoodCategory() {
        return foodCategory;
    }

    public void setFoodCategory(String foodCategory) {
        this.foodCategory = foodCategory;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
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
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
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
    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    @Override
    public String toString() {
        return "FoodWastage{" +
                "userId='" + userId + '\'' +
                ", foodId='" + foodId + '\'' +
                ", foodName='" + foodName + '\'' +
                ", brand='" + brand + '\'' +
                ", quantity=" + quantity +
                ", weight=" + weight +
                ", unit='" + unit + '\'' +
                ", price=" + price +
                ", expireDate='" + expireDate + '\'' +
                ", reason='" + reason + '\'' +
                ", foodCategory='" + foodCategory + '\'' +
                '}';
    }
}
