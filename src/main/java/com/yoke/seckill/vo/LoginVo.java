package com.yoke.seckill.vo;

import com.yoke.seckill.validator.IsMobile;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * @Author A_xiba
 * @Date 2022/7/2 14:30
 * @Version 1.0
 */
@Data
public class LoginVo {
    @NotNull
    @IsMobile
    private String mobile;

    @NotNull
    @Length(min = 32)
    private String password;
}
