package taotao.test.jedis;

import org.junit.Test;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

import java.util.HashSet;
import java.util.Set;

public class TestJedis {
    @Test
    public void TestJedis(){
        //创建jedis对象，需要指定Redis服务的IP和端口号
        Jedis jedis = new Jedis("192.168.208.50",6379);
        //操作数据库
        jedis.set("name","lan");
        //获取数据
        String name = jedis.get("name");
        System.out.println(name);
        //关闭jedisq
        jedis.close();
    }

    //连接池处理
    @Test
    public void TestJedisPool(){
        //创建一个连接池
        JedisPool jedisPool = new JedisPool("192.168.208.50",6379);
        //获取连接
        Jedis jedis = jedisPool.getResource();
        //使用jedis操作数据库（方法级别，就是说只是在该方法中使用，用完就关闭）
        jedis.set("user","test");
        String value = jedis.get("user");
        System.out.println(value);
        //用完之后关闭jedis连接
        jedis.close();
        //系统关闭前先关闭数据库连接池
        jedisPool.close();
    }

    //jedisCluster集群的测试
    @Test
    public void TestJedisCluster() {
        //Ctrl+Q查看方法说明
        //创建一个Set集合，里面保存集群的连接的地址和端口
        Set<HostAndPort> setNodes = new HashSet<>();
        setNodes.add(new HostAndPort("192.168.208.50",7001));
        setNodes.add(new HostAndPort("192.168.208.50",7002));
        setNodes.add(new HostAndPort("192.168.208.50",7003));
        setNodes.add(new HostAndPort("192.168.208.50",7004));
        setNodes.add(new HostAndPort("192.168.208.50",7005));
        setNodes.add(new HostAndPort("192.168.208.50",7006));
        //创建一个JedisCluster对象
        JedisCluster jedisCluster = new JedisCluster(setNodes);
        //直接使用jedisCluster操作数据库，自带连接池而且是单例的
        jedisCluster.set("clusterTest","hello jeids cluster");
        String value = jedisCluster.get("clusterTest");
        System.out.println(value);
        //关闭jedisCluster
        jedisCluster.close();
    }

}
