package com.ronghui.freshcheck.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ronghui.freshcheck.entity.Food;
import com.ronghui.freshcheck.entity.ItemAlert;
import com.ronghui.freshcheck.mapper.FoodMapper;
import com.ronghui.freshcheck.mapper.ItemAlertMapper;
import com.ronghui.freshcheck.vo.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ansel
 * @since 2024-06-03
 */
@RestController
@RequestMapping("/item-alert")
public class ItemAlertController {

    @Autowired
    private ItemAlertMapper itemAlertMapper;

    @Autowired
    private FoodMapper foodMapper;

    @GetMapping("/getAlerts/{userId}")
    public RespBean getAlerts(@PathVariable String userId){
        QueryWrapper<ItemAlert> itemAlertQueryWrapper = new QueryWrapper<>();
        itemAlertQueryWrapper.eq("user_id", userId);
        List<ItemAlert> itemAlerts = itemAlertMapper.selectList(itemAlertQueryWrapper);

        List<ItemAlert> collect = itemAlerts.stream()
                .filter(itemAlert -> itemAlert.getAlertThreshold() != -1)
                .filter(itemAlert -> itemAlert.getIfTick() != 1).collect(Collectors.toList());
        // sort according to left day
        collect.sort(Comparator.comparingInt(e -> e.getLeftDay()));
        for (ItemAlert itemAlert : collect) {
            int threshold = -1;
            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String format = simpleDateFormat.format(date);
            String expireDay = itemAlert.getExpireDay();
            int daysBetween = daysBetween(format, expireDay);
            if (daysBetween <= 3 && daysBetween >= 0) {
                threshold = 0;
            }
            else if(daysBetween <0) {
                threshold = -1;
            }
            else if (daysBetween <= 7) {
                threshold = 1;
            } else {
                threshold = 2;
            }
            itemAlert.setLeftDay(daysBetween);
            itemAlert.setAlertThreshold(threshold);
            itemAlertMapper.updateById(itemAlert);
        }

        return RespBean.success(collect);
    }

    @GetMapping("/sortByAddDate/{userId}")
    public RespBean sortByAddDate(@PathVariable String userId){
        QueryWrapper<ItemAlert> itemAlertQueryWrapper = new QueryWrapper<>();
        itemAlertQueryWrapper.eq("user_id", userId);
        List<ItemAlert> itemAlerts = itemAlertMapper.selectList(itemAlertQueryWrapper);

        List<ItemAlert> collect = itemAlerts.stream()
                .filter(itemAlert -> itemAlert.getAlertThreshold() != -1)
                .filter(itemAlert -> itemAlert.getIfTick() != 1).collect(Collectors.toList());
        List<ItemAlert> itemAlerts1 = sortByDate(itemAlerts);
        return RespBean.success(itemAlerts1);
    }

    public List<ItemAlert> sortByDate(List<ItemAlert> list){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        list.sort(Comparator.comparing(itemAlert -> {
            try {
                return simpleDateFormat.parse(itemAlert.getCreateDay());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }));
        return list;
    }

    @GetMapping("/tickItem/{alertId}")
    public RespBean tickItem(@PathVariable String alertId) {
        QueryWrapper<ItemAlert> itemAlertQueryWrapper = new QueryWrapper<>();
        itemAlertQueryWrapper.eq("alert_id", alertId);
        ItemAlert itemAlert = itemAlertMapper.selectOne(itemAlertQueryWrapper);
        String itemId = itemAlert.getItemId();
        QueryWrapper<Food> foodQueryWrapper = new QueryWrapper<>();
        foodQueryWrapper.eq("food_id", itemId);
        Food food = foodMapper.selectOne(foodQueryWrapper);
        food.setIfTick(1);
        foodMapper.updateById(food);
        if (itemAlert != null) {
            itemAlert.setIfTick(1);
        }
        itemAlertMapper.updateById(itemAlert);
        return RespBean.success();
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
        System.out.println("相差天数: " + daysBetween);
        return days;
    }
}
