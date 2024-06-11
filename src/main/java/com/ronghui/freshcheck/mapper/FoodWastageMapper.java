package com.ronghui.freshcheck.mapper;

import com.ronghui.freshcheck.entity.FoodWastage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @author ansel
 * @since 2024-06-05
 */
@Mapper
public interface FoodWastageMapper extends BaseMapper<FoodWastage> {

    @Select("SELECT food_category, COUNT(*) as count " +
            "FROM food_wastage " +
            "WHERE user_id = #{userId} " +
            "AND expire_date LIKE CONCAT(#{currentDate}, '%') " +
            "GROUP BY food_category " +
            "ORDER BY count DESC " +
            "LIMIT 3")
    List<Map<String, Object>> getTopThreeFoodCategories(@Param("currentDate") String currentDate, @Param("userId") String userId);
}
