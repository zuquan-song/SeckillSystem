package top.imyzt.study.miaosha.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.domain.SeckillUser;
import org.seckill.service.SeckillUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.seckill.utils.MD5Util;

import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.IntStream;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class SeckillUserServiceTest {

    @Autowired
    private SeckillUserService userService;

    /**
     * 注册N个用户, 并处理登录.将token存入redis中.
     */
    @Test
    public void register() {

        long epochSecond = Instant.now().getEpochSecond();
        List<SeckillUser> seckillUsers = new CopyOnWriteArrayList<>();

        IntStream.range(1, 5000)
                .parallel().forEach(i -> {

            // 生成用户
            SeckillUser user = new SeckillUser();
            user.setId(13000000000L + i);
            user.setNickname("testUser_" + i);
            String formPass = MD5Util.inputPassToFormPass(RandomUtil.randomString(10));
            user.setPassword(formPass);
            user.setHead("head");
            user.setRegisterDate(Date.from(Instant.now()));
            user.setLoginCount(1);

            // 注册用户
            userService.register(user);

            // 保存用户
            seckillUsers.add(user);
            log.info("save user index ={}", user.getId());
        });

        CopyOnWriteArrayList <String> tokens = new CopyOnWriteArrayList <>();
        seckillUsers.forEach(user -> {

            HttpResponse execute = HttpRequest.post("http://localhost:8080/login/do_login")
                    .form("mobile", user.getId())
                    .form("password", user.getPassword())
                    .timeout(2000)
                    .execute();

            if (execute.getStatus() != HttpStatus.HTTP_OK) {
                throw new RuntimeException("请求错误");
            }
            String token = execute.getCookie("token").getValue();

            tokens.add(user.getId()+","+token);
            log.info("save token index ={}", user.getId());

        });

        FileUtil.writeUtf8Lines(tokens, "D:/tmp/tokens.txt");

        System.out.println(Instant.now().toEpochMilli() - epochSecond);
    }
}
