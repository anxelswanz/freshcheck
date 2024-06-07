package com.ronghui.freshcheck.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author ansel
 * @since 2024-06-03
 */
@TableName("item_alert")
public class ItemAlert implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    private String alertId;

    private String itemId;

    private String userId;

    private String itemName;

    private int alertThreshold;

    private int leftDay;

    private String createDay;

    private String expireDay;

    private String storageLocation;

    /**
     * 1 ticked , 0 not ticked
     */
    private Integer ifTick;

    /**
     * 1 expired, 0 not expired
     */
    private Integer ifExpire;

    public String getAlertId() {
        return alertId;
    }

    public void setAlertId(String alertId) {
        this.alertId = alertId;
    }
    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public int getAlertThreshold() {
        return alertThreshold;
    }

    public void setAlertThreshold(int alertThreshold) {
        this.alertThreshold = alertThreshold;
    }
    public int getLeftDay() {
        return leftDay;
    }

    public void setLeftDay(int leftDay) {
        this.leftDay = leftDay;
    }
    public String getCreateDay() {
        return createDay;
    }

    public void setCreateDay(String createDay) {
        this.createDay = createDay;
    }
    public String getExpireDay() {
        return expireDay;
    }

    public void setExpireDay(String expireDay) {
        this.expireDay = expireDay;
    }
    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }
    public Integer getIfTick() {
        return ifTick;
    }

    public void setIfTick(Integer ifTick) {
        this.ifTick = ifTick;
    }
    public Integer getIfExpire() {
        return ifExpire;
    }

    public void setIfExpire(Integer ifExpire) {
        this.ifExpire = ifExpire;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    @Override
    public String toString() {
        return "ItemAlert{" +
                "alertId='" + alertId + '\'' +
                ", itemId='" + itemId + '\'' +
                ", userId='" + userId + '\'' +
                ", itemName='" + itemName + '\'' +
                ", alertThreshold=" + alertThreshold +
                ", leftDay=" + leftDay +
                ", createDay='" + createDay + '\'' +
                ", expireDay='" + expireDay + '\'' +
                ", storageLocation='" + storageLocation + '\'' +
                ", ifTick=" + ifTick +
                ", ifExpire=" + ifExpire +
                '}';
    }

}
