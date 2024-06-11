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
import java.time.Month;
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

    @GetMapping("/getThreeMonthsWastagePrice/{userId}")
    public RespBean getStatistics(@PathVariable String userId){
        if (!ObjectUtil.isNotEmpty(userId)) {
            return RespBean.error(RespBeanEnum.ERROR);
        }
        /**
         *  1. Get the total wastage price of recent three months
         */
        HashMap<String, Object> returnMap = new HashMap<>();
        // 获取当前日期
        LocalDate currentDate = LocalDate.now();

        // 创建一个格式化对象，用于输出月份和年份
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        // 存储最近三个月的月份和年份
        List<String> lastThreeMonths = new ArrayList<>();

        // 获取当前月份及过去两个月的月份和年份
        String previousMonth = "";
        String currentMonth = "";
        for (int i = 0; i < 3; i++) {
            LocalDate date = currentDate.minusMonths(i);
            String format = date.format(formatter);
            lastThreeMonths.add(format);
            if (i == 0) {
                currentMonth = format;
            } else if (i == 1) {
                previousMonth = format;
            }
        }
        Collections.reverse(lastThreeMonths);
        returnMap.put("numberList", lastThreeMonths);

        double currentSum = 0;
        double previousSum = 0;

        List<Double> wastePriceList = new ArrayList<>();
        int size = 0;
        for (String lastThreeMonth : lastThreeMonths) {

            QueryWrapper<FoodWastage> foodWastageQueryWrapper = new QueryWrapper<>();
            foodWastageQueryWrapper.like("expire_date",lastThreeMonth);
            List<FoodWastage> wastages = foodWastageMapper.selectList(foodWastageQueryWrapper);
            if (wastages != null) {
                Double totalNum = wastages.stream().map(FoodWastage::getPrice).reduce(0.0, Double::sum);
                wastePriceList.add(totalNum);
                if (currentMonth.equals(lastThreeMonth)) {
                    returnMap.put("currentAmount", totalNum);
                    size = wastages.size();
                    currentSum = totalNum;
                } else if (previousMonth.equals(lastThreeMonth)) {
                    previousSum = totalNum;
                }
                returnMap.put(lastThreeMonth,totalNum);
            } else {
                wastePriceList.add(0.0);
                returnMap.put(lastThreeMonth, 0.0);
            }
        }


        returnMap.put("wasteSize", size);
        returnMap.put("priceList", wastePriceList);

        /**
         *  2. Compare this month and last month price decrease or increase
         */
        double saveFood = currentSum - previousSum;
        returnMap.put("compareMoneyDifference", saveFood);

        /**
         *  3. Determine the magnitude
         */
        double percentage;
        if (previousSum != 0) {
            if (previousSum > currentSum) {
                 returnMap.put("magnitude","decrease");
                 percentage = (previousSum - currentSum) / previousSum;
                 returnMap.put("percentage", percentage);
            } else {
                returnMap.put("magnitude","increase");
                percentage = (currentSum - previousSum) / previousSum;
                returnMap.put("percentage", percentage);
            }
        } else {
            returnMap.put("magnitude", null);
            returnMap.put("percentage", null);
        }

        /**
         *  Inventory amount
         */
        QueryWrapper<Food> foodQueryWrapper = new QueryWrapper<>();
        foodQueryWrapper.like("create_date", currentMonth);
        foodQueryWrapper.eq("user_id", userId);
        Integer count = foodMapper.selectCount(foodQueryWrapper);
        returnMap.put("inventoryCount", count);

        List<Map<String, Object>> topThreeFoodCategories = foodWastageMapper.getTopThreeFoodCategories(currentMonth, userId);
        List<Map<String, Object>> topThree = new ArrayList<>();
        List<Object> keys = new ArrayList<>();
        for (Map<String, Object> topThreeFoodCategory : topThreeFoodCategories) {
            HashMap<String, Object> key = new HashMap<>();
            key.put("value", topThreeFoodCategory.get("count"));
            key.put("name", topThreeFoodCategory.get("food_category"));
            keys.add(topThreeFoodCategory.get("food_category"));
            topThree.add(key);
        }
        System.out.println(topThreeFoodCategories);
        returnMap.put("topThree", topThree);
        returnMap.put("topThreeList", keys);
        return RespBean.success(returnMap);
    }


   // @GetMapping("/get")
}
