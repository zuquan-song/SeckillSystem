package org.seckill.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.seckill.validator.IsMobile;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author Zuquan Song
 *
 * @description LoginVo
 */
@Data
public class LoginVo implements Serializable{

    @NotNull
    @IsMobile
    private String mobile;

    @NotNull
    @Length(min = 6)
    private String password;
}
