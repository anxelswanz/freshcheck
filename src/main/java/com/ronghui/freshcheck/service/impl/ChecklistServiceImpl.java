package com.ronghui.freshcheck.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ronghui.freshcheck.entity.Checklist;
import com.ronghui.freshcheck.mapper.ChecklistMapper;
import com.ronghui.freshcheck.service.IChecklistService;
import org.springframework.stereotype.Service;


@Service
public class ChecklistServiceImpl extends ServiceImpl<ChecklistMapper, Checklist> implements IChecklistService {

}
