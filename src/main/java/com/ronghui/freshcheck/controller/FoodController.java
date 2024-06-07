package com.ronghui.freshcheck.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ronghui.freshcheck.entity.Food;
import com.ronghui.freshcheck.entity.FoodWastage;
import com.ronghui.freshcheck.entity.ItemAlert;
import com.ronghui.freshcheck.mapper.FoodMapper;
import com.ronghui.freshcheck.mapper.FoodWastageMapper;
import com.ronghui.freshcheck.mapper.ItemAlertMapper;
import com.ronghui.freshcheck.vo.RespBean;
import com.ronghui.freshcheck.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;


@RestController
@RequestMapping("/food")

public class FoodController {

    @Autowired
    private FoodMapper foodMapper;

    @Autowired
    private ItemAlertMapper itemAlertMapper;

    @Autowired
    private FoodWastageMapper foodWastageMapper;

    @PostMapping("/addFood")
    public RespBean addFood(@RequestBody Food food){
        String purchaseDate = food.getPurchaseDate().substring(0, 10);
        String expireDate = food.getExpireDate().substring(0, 10);
        food.setPurchaseDate(purchaseDate);
        food.setExpireDate(expireDate);
        boolean b = ifExpire(purchaseDate, expireDate);
        if (b) {
            food.setIfExpire(1);
        } else {
            food.setIfExpire(0);
        }
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String format = simpleDateFormat.format(date);
        food.setCreateDate(format);
        food.setIfTick(0);
        System.out.println(food);
        if (ObjectUtil.isEmpty(food))
            return RespBean.error(RespBeanEnum.ERROR);
        String uuid = UUID.randomUUID().toString();
        food.setFoodId(uuid);
        food.setUnit(food.getUnit().toLowerCase());
        int insert = foodMapper.insert(food);

        // insert to alert
        int daysBetween = daysBetween(format, expireDate);
        int threshold = -1;
        ItemAlert itemAlert = new ItemAlert();
        itemAlert.setAlertId(UUID.randomUUID().toString());
        itemAlert.setCreateDay(format);
        itemAlert.setExpireDay(expireDate);
        itemAlert.setUserId(food.getUserId());
        itemAlert.setStorageLocation(food.getStorageLocation());
        itemAlert.setItemId(food.getFoodId());
        itemAlert.setUserId(food.getUserId());
        itemAlert.setItemName(food.getFoodName());
        if (!b){
            itemAlert.setIfTick(0);
            itemAlert.setIfExpire(0);
            if (daysBetween <= 3) {
                threshold = 0;
            } else if (daysBetween <= 7) {
                threshold = 1;
            } else {
                threshold = 2;
            }
            itemAlert.setAlertThreshold(threshold);
            itemAlert.setLeftDay(daysBetween);
        } else {
            itemAlert.setAlertThreshold(threshold);
            itemAlert.setIfExpire(1);
            itemAlert.setLeftDay(0);
        }
        int insert1 = itemAlertMapper.insert(itemAlert);

        return RespBean.success();
    }


    @GetMapping("/getFoodList/{userId}")
    public RespBean getFoodList(@PathVariable String userId) {

        QueryWrapper<Food> foodQueryWrapper = new QueryWrapper<>();
        foodQueryWrapper.eq("user_id", userId);
        foodQueryWrapper.eq("if_expire", 0);
        List<Food> foods = foodMapper.selectList(foodQueryWrapper);

        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String format = simpleDateFormat.format(date);


        // Check If Expired
        List<Food> newList = new ArrayList<>();
        List<FoodWastage> wastages = new ArrayList<>();
        for (Food food : foods) {
            boolean b = ifExpire(format, food.getExpireDate());
            if (!b) {
                newList.add(food);
            } else {
                food.setIfExpire(1);
                if (food.getIfTick() != 1) {
                    FoodWastage wastage = new FoodWastage();
                    wastage.setFoodName(food.getFoodName());
                    wastage.setBrand(food.getBrand());
                    wastage.setReason("OUT OF DATE");
                    wastage.setQuantity(food.getQuantity());
                    wastage.setUnit(food.getUnit());
                    wastage.setPrice(food.getPrice());
                    wastage.setExpireDate(food.getExpireDate());
                    wastage.setFoodId(food.getFoodId());
                    wastage.setUserId(food.getUserId());
                    wastage.setWeight(food.getWeight());
                    wastage.setFoodCategory(food.getFoodCategory());
                    wastages.add(wastage);
                }
                food.setIfAddedWaste(1);
                foodMapper.updateById(food);
            }

        }
        if (ObjectUtil.isNotEmpty(wastages)) {
            for (FoodWastage wastage : wastages) {
                foodWastageMapper.insert(wastage);
            }
        }
        newList.sort(Comparator.comparing(food -> {
            try {
                return simpleDateFormat.parse(food.getCreateDate());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }));


        return RespBean.success(newList);
    }

    @GetMapping("/getFoodListByExpirationDate/{userId}")
    public RespBean getFoodListByExpirationDate(@PathVariable String userId) {

        QueryWrapper<Food> foodQueryWrapper = new QueryWrapper<>();
        foodQueryWrapper.eq("user_id", userId);
        foodQueryWrapper.eq("if_expire", 0);
        List<Food> foods = foodMapper.selectList(foodQueryWrapper);

        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String format = simpleDateFormat.format(date);


        // Check If Expired
        List<Food> newList = new ArrayList<>();
        for (Food food : foods) {
            boolean b = ifExpire(format, food.getExpireDate());
            if (!b) {
                newList.add(food);
            } else {
                food.setIfExpire(1);
                foodMapper.updateById(food);
            }
        }

        newList.sort(Comparator.comparing(food -> {
            try {
                return simpleDateFormat.parse(food.getExpireDate());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }));

        return RespBean.success(newList);
    }


    public boolean ifExpire(String s1, String s2) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date1 = LocalDate.parse(s1, formatter);
            LocalDate date2 = LocalDate.parse(s2, formatter);

            // 比较日期
            if (date1.isBefore(date2)) {
                return false;
            } else if (date1.isAfter(date2)) {
               return true;
            } else {
                return false;
            }
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format: " + e.getMessage());
        }
        return false;
    }

    public int daysBetween(String s1, String s2) {
        // 定义日期格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 解析日期字符串
        LocalDate date1 = LocalDate.parse(s1, formatter);
        LocalDate date2 = LocalDate.parse(s2, formatter);

        // 计算日期差异
        long daysBetween = ChronoUnit.DAYS.between(date1, date2);
        int days = (int) daysBetween;
        // 输出结果
        return days;
    }
}
