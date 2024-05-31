package com.ronghui.freshcheck.service.impl;

import com.ronghui.freshcheck.entity.User;
import com.ronghui.freshcheck.mapper.UserMapper;
import com.ronghui.freshcheck.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ansel
 * @since 2024-05-26
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
