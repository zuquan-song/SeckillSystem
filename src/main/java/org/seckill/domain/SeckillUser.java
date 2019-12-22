package org.seckill.domain;

import lombok.Data;

import java.util.Date;

/**
 * @author Zuquan Song
 *
 * @description SeckillUser
 */
@Data
public class SeckillUser {

    private Long id;
    private String nickname;
    private String password;
    private String salt;
    private String head;
    private Date registerDate;
    private Date lastLoginDate;
    private Integer loginCount;

}
