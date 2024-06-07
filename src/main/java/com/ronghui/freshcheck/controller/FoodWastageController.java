package com.ronghui.freshcheck.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ronghui.freshcheck.entity.Food;
import com.ronghui.freshcheck.entity.FoodWastage;
import com.ronghui.freshcheck.mapper.FoodMapper;
import com.ronghui.freshcheck.mapper.FoodWastageMapper;
import com.ronghui.freshcheck.vo.RespBean;
import com.ronghui.freshcheck.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ansel
 * @since 2024-06-05
 */
@RestController
@RequestMapping("/food-wastage")
public class FoodWastageController {

    @Autowired
    private FoodWastageMapper foodWastageMapper;

    @Autowired
    private FoodMapper foodMapper;

    @PostMapping("/addWastage")
    public RespBean addWastage(@RequestBody FoodWastage foodWastage){

        String uuid = UUID.randomUUID().toString();
        foodWastage.setFoodId(uuid);
        String expireDate = foodWastage.getExpireDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date parse = simpleDateFormat.parse(expireDate);
            String format = simpleDateFormat.format(parse);
            foodWastage.setExpireDate(format);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        if (foodWastage == null)
            return RespBean.success(RespBeanEnum.ERROR);
        foodWastageMapper.insert(foodWastage);

        return RespBean.success();
    }

    @GetMapping("/getWastageList/{userId}/{date}")
    public RespBean getWastageList(@PathVariable String userId, @PathVariable String date) {
        List<FoodWastage> wastages = null;
        QueryWrapper<Food> foodQueryWrapper = new QueryWrapper<>();
        foodQueryWrapper.eq("user_id",userId);
        foodQueryWrapper.eq("if_tick", 0);
        foodQueryWrapper.eq("if_expire",1);
        foodQueryWrapper.eq("if_added_waste",0);
        List<Food> foods = foodMapper.selectList(foodQueryWrapper);
        if (ObjectUtil.isNotEmpty(foods)) {
            for (Food food : foods) {
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
                foodWastageMapper.insert(wastage);
                food.setIfAddedWaste(1);
                foodMapper.updateById(food);
            }
        }
        if (date == null|| "undefined".equals(date)) {
            QueryWrapper<FoodWastage> foodQueryWrapper2 = new QueryWrapper<>();
            foodQueryWrapper2.eq("user_id", userId);
             wastages = foodWastageMapper.selectList(foodQueryWrapper2);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            wastages.sort(Comparator.comparing(wastage -> {
                try {
                    return simpleDateFormat.parse(wastage.getExpireDate());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }));
        } else {
            QueryWrapper<FoodWastage> foodQueryWrapper2 = new QueryWrapper<>();
            foodQueryWrapper2.eq("user_id", userId).eq("expire_date", date);
            wastages = foodWastageMapper.selectList(foodQueryWrapper2);
        }

        return RespBean.success(wastages);

    }


}
