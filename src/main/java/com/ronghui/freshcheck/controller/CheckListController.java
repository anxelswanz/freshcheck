package com.ronghui.freshcheck.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ronghui.freshcheck.entity.Checklist;
import com.ronghui.freshcheck.entity.Task;
import com.ronghui.freshcheck.mapper.ChecklistMapper;
import com.ronghui.freshcheck.mapper.TaskMapper;
import com.ronghui.freshcheck.vo.IndividualTask;
import com.ronghui.freshcheck.vo.RespBean;
import com.ronghui.freshcheck.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Ronghui Zhong
 * @description:
 * @date 2024/6/14 1:35
 * @ProjectName freshcheck
 **/
@RestController
@RequestMapping("/checklist")
public class CheckListController {


    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    @Autowired
    private ChecklistMapper checklistMapper;

    @Autowired
    private TaskMapper taskMapper;

    @PostMapping("/addCheckList")
    public RespBean addCheckList(@RequestBody HashMap<Object, Object> map){
        String userId = "";
        String title = "";
        Integer count = 0;
        String uuid = UUID.randomUUID().toString();
        String table = "";
        Set<Object> objects = map.keySet();
        for (Object object : objects) {
            String key = (String) object;
            if (key.equals("userId")) {
                userId = (String) map.get(key);
            }  else if (key.equals("title")) {
                title =  (String) map.get(key);
            } else if ( key.equals("count")){
                 count = (Integer) map.get(key);
            } else {
                table += key + "-";
                String value =(String) map.get(key);
                table += value;
                table += " ";
            }
        }
        
        System.out.println(table);
        System.out.println(userId);
        System.out.println(title);
        System.out.println(count);
        Checklist checklist = new Checklist();
        checklist.setCheckId(uuid);
        checklist.setCount(count);
        checklist.setTitle(title);
        checklist.setContent(table);
        checklist.setUserId(userId);
        checklistMapper.insert(checklist);
        return RespBean.success();
    }

    @GetMapping("/getTemplates/{userId}")
    public RespBean getTemplates(@PathVariable String userId) {
        QueryWrapper<Checklist> checklistQueryWrapper = new QueryWrapper<>();
        checklistQueryWrapper.eq("user_id", userId);
        List<Checklist> checklists = checklistMapper.selectList(checklistQueryWrapper);
        return RespBean.success(checklists);
    }


    @GetMapping("/getTasks/{userId}/{templateId}")
    public RespBean getTasks(@PathVariable String userId, @PathVariable String templateId) {
        if (templateId == null && userId == null)
            return  RespBean.error(RespBeanEnum.ERROR);
        QueryWrapper<Checklist> checklistQueryWrapper = new QueryWrapper<>();
            checklistQueryWrapper.eq("user_id", userId);

        checklistQueryWrapper.eq("check_id", templateId);
        Checklist checklist = checklistMapper.selectOne(checklistQueryWrapper);
        if(checklist == null) {
            return RespBean.error(RespBeanEnum.ERROR);
        }
        String content = checklist.getContent();
        String[] s = content.split("&");
        ArrayList<IndividualTask> individualTasks = new ArrayList<>();
        for (String s1 : s) {
            String[] split = s1.split("-");
            IndividualTask individualTask = new IndividualTask();
            individualTask.setName(split[0]);
            individualTask.setValue(split[1]);
            individualTasks.add(individualTask);
        }
        HashMap<Object, Object> map = new HashMap<>();
        map.put("object", checklist);
        map.put("tasks", individualTasks);

        /**
         *  add into task table
         */
        String uuid = UUID.randomUUID().toString();
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String format = simpleDateFormat.format(date);
        Task task = new Task();
        task.setChecklistId(templateId);
        task.setIfFinished(0);
        task.setUserId(userId);
        task.setCreateTime(format);
        task.setTaskId(uuid);
        taskMapper.insert(task);
        map.put("uuid", uuid);
        return RespBean.success(map);
    }

    @GetMapping("/submitTask/{taskId}/{unfinshedTask}")
    public RespBean submitTask(@PathVariable String taskId, @PathVariable String unfinshedTask) {

        if (taskId == null )
            return RespBean.error(RespBeanEnum.ERROR);

        System.out.println("unfinished task => " + unfinshedTask);
        String[] s = unfinshedTask.split("-");
        System.out.println(s.length);
        QueryWrapper<Task> taskQueryWrapper = new QueryWrapper<>();
        taskQueryWrapper.eq("task_id", taskId);
        Task task = taskMapper.selectOne(taskQueryWrapper);
        /**
         *  1-xxxx xxxx xxxx
         */
        if (s.length == 1) {
            task.setIfFinished(1);
        } else {
            task.setUnfinishedTask(s[1]);
        }
        taskMapper.updateById(task);
        return RespBean.success();
    }

    @GetMapping("/getTasksHistory/{userId}/{content}")
    public RespBean getTasksHistory(@PathVariable String userId, @PathVariable String content){
        if (userId == null)
            return RespBean.error(RespBeanEnum.ERROR);
        if (content.equals("null")) {
            System.out.println("yes");
            content = null;
        }
        QueryWrapper<Task> taskQueryWrapper = new QueryWrapper<>();
        taskQueryWrapper.eq("user_id", userId);
        List<Task> tasks = taskMapper.selectList(taskQueryWrapper);
        List<Task> newTasks1 = new ArrayList<>();
        for (Task task : tasks) {
            QueryWrapper<Checklist> checklistQueryWrapper = new QueryWrapper<>();
            checklistQueryWrapper.eq("check_id", task.getChecklistId() );
            Checklist checklist = checklistMapper.selectOne(checklistQueryWrapper);
            task.setTitle(checklist.getTitle());
            newTasks1.add(task);
        }
        String finalContent = content;
        System.out.println(finalContent);
        List<Task> collect = newTasks1.stream().filter(task -> {
            if (finalContent != null && !finalContent.isEmpty()) {
                return task.getTitle().contains(finalContent) || task.getCreateTime().contains(finalContent);
            }
            return true;
        }).collect(Collectors.toList());

        return RespBean.success(collect);
    }

    @GetMapping("/getStatistics/{userId}")
    public RespBean getStatistics(@PathVariable String userId) {


        Map<String, Object> map = new HashMap<>();

        /**
         *  Current Date
         */
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
        String format = simpleDateFormat.format(date);
        QueryWrapper<Task> taskQueryWrapper = new QueryWrapper<>();
        taskQueryWrapper.eq("user_id", userId);
        taskQueryWrapper.like("create_time", format);
        List<Task> tasks = taskMapper.selectList(taskQueryWrapper);
        /**
         * 1. Total Count
         */

        map.put("count", tasks.size());

        double checkedCount = 0.0;
        double uncheckedCOunt = 0.0;
        if (!tasks.isEmpty()) {
            List<Task> newTasks1 = new ArrayList<>();
            for (Task task : tasks) {
                QueryWrapper<Checklist> checklistQueryWrapper = new QueryWrapper<>();
                checklistQueryWrapper.eq("check_id", task.getChecklistId() );
                Checklist checklist = checklistMapper.selectOne(checklistQueryWrapper);
                task.setTitle(checklist.getTitle());
                newTasks1.add(task);
                if (task.getIfFinished() == 0) {
                    uncheckedCOunt++;
                } else {
                    checkedCount++;
                }
            }
            List<String> collect =
                    newTasks1.stream().map(Task::getTitle).collect(Collectors.toList());
            /**
             *  2. get three top checked checklist
             */
            //1. Use getOrDefault to get the most frequent strings
            HashMap<String, Integer> frequencyMap = new HashMap<>();
            for (String s : collect) {
                frequencyMap.put(s, frequencyMap.getOrDefault(s,0) + 1);
            }
            ArrayList<Map.Entry<String, Integer>> entries = new ArrayList<>(frequencyMap.entrySet());
            entries.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));
            ArrayList<String> strings = new ArrayList<>();
            ArrayList<Integer> integers = new ArrayList<>();
            int count = 0;
            for (Map.Entry<String, Integer> entry : entries) {
                if (count == 3) {
                    break;
                }
                strings.add(entry.getKey());
                integers.add(entry.getValue());
                count++;
            }
            map.put("topThreeStrings", strings);
            map.put("topThreeIntegers", integers);

            /**
             *  3. Unchecked list and checkedlist proportion
             */

            double checkedPercentage = checkedCount / (checkedCount + uncheckedCOunt);
            double uncheckedPercentage = uncheckedCOunt / (checkedCount + uncheckedCOunt);
            ArrayList<Map<String, Object>> list = new ArrayList<>();
            HashMap<String, Object> proportion1 = new HashMap<String, Object>();
            proportion1.put("name", "checkedPercentage " +(int) checkedCount);
            proportion1.put("value", checkedPercentage);

            HashMap<String, Object> proportion2 = new HashMap<String, Object>();
            proportion2.put("name", "uncheckedPercentage " + (int) uncheckedCOunt);
            proportion2.put("value", uncheckedPercentage);

            list.add(proportion1);
            list.add(proportion2);

            ArrayList<String> proportionNames = new ArrayList<>();
            proportionNames.add("checked tasks " + (int) checkedCount);
            proportionNames.add("unchecked tasks " + (int) uncheckedCOunt);
            map.put("proportion", list);
            map.put("proportionNames", proportionNames);
        }
        return RespBean.success(map);
    }

}
