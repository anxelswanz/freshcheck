package com.ronghui.freshcheck.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ansel Zhong
 * @description:
 * @date 2024/6/1 0:40
 * @ProjectName freshcheck
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KaptchVo {
    private String uuid;
    private String kaptchaText;
}
