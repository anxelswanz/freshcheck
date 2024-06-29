package com.ronghui.freshcheck.service.impl;

import com.ronghui.freshcheck.entity.Task;
import com.ronghui.freshcheck.mapper.TaskMapper;
import com.ronghui.freshcheck.service.ITaskService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;


@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements ITaskService {

}
