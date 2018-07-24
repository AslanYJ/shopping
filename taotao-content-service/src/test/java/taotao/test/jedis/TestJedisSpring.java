package taotao.test.jedis;

import com.taotao.jedis.JedisClient;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestJedisSpring {
    @Test
    public void TestJedisClient(){
        //初始化容器
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-redis.xml");
        //从容器中获得JedisClient对象
        JedisClient jedisClient =  applicationContext.getBean(JedisClient.class);
        jedisClient.set("testSpringJedisClient","test-spring-jedisClient");
        String value = jedisClient.get("testSpringJedisClient");
        System.out.println(value);
        //对redis进行操作
    }


}
