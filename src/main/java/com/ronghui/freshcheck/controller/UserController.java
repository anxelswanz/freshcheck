package com.ronghui.freshcheck.controller;


import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.FastByteArrayOutputStream;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.code.kaptcha.Producer;
import com.ronghui.freshcheck.entity.User;
import com.ronghui.freshcheck.mapper.UserMapper;
import com.ronghui.freshcheck.utils.MD5Utils;
import com.ronghui.freshcheck.utils.RedisUtils;
import com.ronghui.freshcheck.vo.KaptchVo;
import com.ronghui.freshcheck.vo.RespBean;
import com.ronghui.freshcheck.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.ronghui.freshcheck.vo.RespBeanEnum.WRONG_KAPTCHA;


@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {


        @Autowired
        private RedisTemplate<String, Object> redisTemplate;
        @Autowired
        private Producer kaptchaProduer;

        @Autowired
        private UserMapper userMapper;

        @GetMapping("/kaptcha")
        public RespBean getKaptcha(HttpServletResponse response, HttpSession session) {

            String imagecode = kaptchaProduer.createText();
            // 生成图片
            BufferedImage image = kaptchaProduer.createImage(imagecode);
            // 将验证码存入Session
            session.setAttribute("kaptcha", imagecode);
            //将图片输出给浏览器
            String uuid = UUID.randomUUID().toString();//uuid-->验证码唯一标识
            FastByteArrayOutputStream os = new FastByteArrayOutputStream();
            try {
                response.setContentType("image/png");

                ImageIO.write(image, "png", os);

                //验证码实现redis缓存，过期时间2分钟

                session.setAttribute("uuid", imagecode);

                redisTemplate.opsForValue().set(uuid, imagecode, 2, TimeUnit.MINUTES);

            } catch (IOException e) {

                return RespBean.error(RespBeanEnum.ERROR);
            }
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("uuid", uuid);
            hashMap.put("img", Base64.encode(os.toByteArray()));
            return RespBean.success(hashMap);
    }

    @PostMapping("/verifyKaptcha")
    public RespBean verifyKaptcha(@RequestBody KaptchVo kaptchVo){
            if (ObjectUtil.isEmpty(kaptchVo.getUuid()) || ObjectUtil.isEmpty(kaptchVo.getKaptchaText()))
                return RespBean.error(RespBeanEnum.ERROR);

            System.out.println(kaptchVo);
            String value = (String) redisTemplate.opsForValue().get(kaptchVo.getUuid());
            System.out.println(value);
            if (value != null && value.equals(kaptchVo.getKaptchaText())) {
                return RespBean.success();
            } else {
                return RespBean.error(WRONG_KAPTCHA);
            }

    }


    @PostMapping("/login")
    public RespBean login(@RequestBody User user){
        System.out.println(user);
        if (ObjectUtil.isEmpty(user))
             return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        String password = user.getPassword();
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        QueryWrapper<User> eq =
                userQueryWrapper.eq("user_name", user.getUserName()).or().eq("email", user.getUserName());
        User one = userMapper.selectOne(eq);
        if (one == null)
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        System.out.println("one " +one);
        if (!MD5Utils.inputPassToFormPass(password).equals(one.getPassword())) {
            System.out.println(MD5Utils.inputPassToFormPass(password));
            System.out.println(one.getPassword());
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }

        one.setPassword("");
        return RespBean.success(one);
    }


    @PostMapping("/register")
    public RespBean register(@RequestBody User user){
        if (ObjectUtil.isEmpty(user))
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        String email = user.getEmail();
        String uuid = UUID.randomUUID().toString();
        user.setUserId(uuid);
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("email",email);
        User one = userMapper.selectOne(userQueryWrapper);
        if (one != null)
            return RespBean.error(RespBeanEnum.EMAIL_ERROR);
        int insert = userMapper.insert(user);
        if (insert>0)
            return RespBean.success();
        else
            return RespBean.error(RespBeanEnum.ERROR);
    }
}
