package org.seckill.service;

import org.seckill.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.seckill.mapper.UserMapper;

/**
 * @author Zuquan Song
 *
 * @description UserService
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User getById(Integer id) {
        return userMapper.findById(id);
    }
}
