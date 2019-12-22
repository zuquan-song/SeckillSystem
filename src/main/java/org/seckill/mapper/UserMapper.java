package org.seckill.mapper;

import org.apache.ibatis.annotations.Select;
import org.seckill.domain.User;

/**
 * @author Zuquan Song
 *
 * @description UserMapper
 */
public interface UserMapper {

    @Select("SELECT * FROM user WHERE id = #{id}")
    User findById(Integer id);
}
