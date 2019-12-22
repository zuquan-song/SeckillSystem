package org.seckill.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.seckill.domain.SeckillUser;

/**
 * @author Zuquan Song
 *
 * @description SeckillUserMapper
 */
public interface SeckillUserMapper {

    @Select("SELECT * FROM miaosha.miaosha_user WHERE id = #{id} ")
    SeckillUser getById(@Param("id") long id);

    @Update("UPDATE miaosha_user SET password = #{password} WHERE id = #{id} ")
    void update(SeckillUser updateUser);

    @Select("INSERT INTO miaosha_user (id, nickname, password, salt, head, redister_date, login_count) " +
            "VALUES (#{id}, #{nickname}, #{password}, #{salt}, #{head}, #{registerDate}, #{loginCount} )")
    SeckillUser insert(SeckillUser user);
}
