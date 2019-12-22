package org.seckill.service;

import cn.hutool.core.util.RandomUtil;
import org.apache.commons.lang3.StringUtils;
import org.seckill.common.redis.SeckillUserKey;
import org.seckill.domain.SeckillUser;
import org.seckill.mapper.SeckillUserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.seckill.exception.GlobalException;
import org.seckill.result.CodeMsg;
import org.seckill.utils.MD5Util;
import org.seckill.utils.UUIDUtil;
import org.seckill.vo.LoginVo;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * @author Zuquan Song
 *
 * @description MiaoshaUserService
 */
@Service
public class SeckillUserService {

    private final SeckillUserMapper userMapper;
    private final RedisService redisService;

    public static final String COOKIE_NAME_TOKEN = "token";

    @Autowired
    public SeckillUserService(SeckillUserMapper userMapper, RedisService redisService) {
        this.userMapper = userMapper;
        this.redisService = redisService;
    }

    public SeckillUser getById(long id) {

        SeckillUser seckillUser = redisService.get(SeckillUserKey.getById, "" + id, SeckillUser.class);
        if (null != seckillUser) {
            return seckillUser;
        }

        seckillUser = userMapper.getById(id);

        if (null != seckillUser) {
            redisService.set(SeckillUserKey.getById, "" + id, seckillUser);
        }

        return seckillUser;
    }

    public SeckillUser register (SeckillUser user) {

        if (null == user) {
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }

        SeckillUser registerUser = new SeckillUser();
        BeanUtils.copyProperties(user, registerUser);

        String formPass = user.getPassword();
        String salt = RandomUtil.randomString(10);
        String dbPass = MD5Util.formPassToDBPass(formPass, salt);
        registerUser.setPassword(dbPass);
        registerUser.setSalt(salt);

        return userMapper.insert(registerUser);
    }

    public SeckillUser updatePassword(String token, long id, String form) {

        SeckillUser seckillUser = this.getById(id);
        if (null == seckillUser) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }

        SeckillUser updateUser = new SeckillUser();
        updateUser.setId(id);
        updateUser.setPassword(MD5Util.formPassToDBPass(form, seckillUser.getSalt()));
        userMapper.update(updateUser);

        seckillUser.setPassword(updateUser.getPassword());

        redisService.delete(SeckillUserKey.getById, ""+id);
        redisService.set(SeckillUserKey.TOKEN, token, seckillUser);

        return seckillUser;
    }


    public boolean  login(HttpServletResponse response, LoginVo loginVo) {

        if (null == loginVo) {
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }

        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword();

        SeckillUser user = this.getById(Long.parseLong(mobile));

        if (null == user) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }

        String dbSalt = user.getSalt();
        String dbPass = user.getPassword();

        String formPassToDBPass = MD5Util.formPassToDBPass(formPass, dbSalt);

        if (!Objects.equals(dbPass, formPassToDBPass)) {
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }

        String token = UUIDUtil.uuid();
        writeCookie(response, token, user);

        return true;
    }

    private void writeCookie(HttpServletResponse response, String token, SeckillUser user) {

        redisService.set(SeckillUserKey.TOKEN, token, user);

        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
        cookie.setMaxAge(SeckillUserKey.TOKEN.getExpireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public SeckillUser getByToken(HttpServletResponse response, String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        SeckillUser seckillUser = redisService.get(SeckillUserKey.TOKEN, token, SeckillUser.class);

        if (null != seckillUser) {
            writeCookie(response, token, seckillUser);
        }
        return seckillUser;
    }
}
