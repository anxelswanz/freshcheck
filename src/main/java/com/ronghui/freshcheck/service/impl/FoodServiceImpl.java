package com.ronghui.freshcheck.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ronghui.freshcheck.entity.Food;
import com.ronghui.freshcheck.mapper.FoodMapper;
import com.ronghui.freshcheck.service.IFoodService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ansel
 * @since 2024-06-02
 */
@Service
public class FoodServiceImpl extends ServiceImpl<FoodMapper, Food> implements IFoodService {

}
