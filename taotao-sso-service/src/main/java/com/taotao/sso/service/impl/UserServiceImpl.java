package com.taotao.sso.service.impl;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.jedis.JedisClient;
import com.taotao.mapper.TbUserMapper;
import com.taotao.pojo.TbUser;
import com.taotao.pojo.TbUserExample;
import com.taotao.sso.service.UserService;
import com.taotao.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private TbUserMapper tbUserMapper;
    @Autowired
    private JedisClient jedisClient;
    @Value("${USER_SESSION}")
    private String USER_SESSION;
    @Value("${SESSION_EXPIRE}")
    private Integer SESSION_EXPIRE;
    //校验数据
    @Override
    public TaotaoResult check(String param, Integer type) {
        TbUserExample example = new TbUserExample();
        TbUserExample.Criteria criteria = example.createCriteria();
        if(type == 1) {
            //校验登录名
          criteria.andUsernameEqualTo(param);
        } else if(type == 2) {
            //校验电话
            criteria.andPhoneEqualTo(param);
        }else if(type == 3) {
            //校验邮箱
            criteria.andEmailEqualTo(param);
        }else {
            return TaotaoResult.build(400, "所传参数非法！");
        }

        List<TbUser> user = tbUserMapper.selectByExample(example);
        if(user != null && user.size() > 0) {
            //如果有数据，证明能查到，就是数据库里面已经有了
            return TaotaoResult.build(400,"sorry",false);
        }
        //校验成功
        return TaotaoResult.ok(true);
    }
    //注册
    @Override
    public TaotaoResult register(TbUser user) {
        //注册要先校验，要看数据库里面是不是已经有了对应的数据，如果有的话直接返回给用户
//        if(user.getUsername() != null) {
//            criteria.andUsernameEqualTo(user.getUsername());
//            List<TbUser> users = tbUserMapper.selectByExample(example);
//            if(users != null && users.size() > 0) {
//                return TaotaoResult.build(400,"用户名已存在");
//            }
//        }else {
//            return TaotaoResult.build(400,"用户名不能为空");
//        }
        //校验userName
        if(StringUtils.isBlank(user.getUsername())) {
            return TaotaoResult.build(400,"用户名不能为空");
        }
        TaotaoResult taotaoResult = this.check(user.getUsername(),1);
        if(!(Boolean)taotaoResult.getData()) {
            return TaotaoResult.build(400,"用户名不能重复");
        }
        //password
        if(StringUtils.isBlank(user.getPassword())) {
            return TaotaoResult.build(400,"密码不能为空");
        }
        //phone
        if(StringUtils.isBlank(user.getPhone())) {
            return TaotaoResult.build(400,"电话为空");
        } else {
            taotaoResult = this.check(user.getPhone(),2);
            if(!(Boolean) taotaoResult.getData())
                return TaotaoResult.build(400,"电话已存在");
        }

        //email
//        if(StringUtils.isBlank(user.getEmail())) {
//            return TaotaoResult.build(400,"邮箱不能为空");
//        }else {
//            taotaoResult = this.check(user.getEmail(),3);
//            if(!(Boolean) taotaoResult.getData())
//                return TaotaoResult.build(400,"邮箱已经存在");
//        }
        //填充属性
        user.setCreated(new Date());
        user.setUpdated(new Date());
        //密码要进行Md5加密，我们不用添加额外的jar包，只需要使用Spring自带的包就可以了
        String md5 = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
        user.setPassword(md5);
        tbUserMapper.insert(user);
        return TaotaoResult.ok();
    }

    @Override
    public TaotaoResult login(String username, String password) {
        //1.判断用户的用户名和密码是否正确
        //判断用户的用户名
        TbUserExample example = new TbUserExample();
        TbUserExample.Criteria criteria = example.createCriteria();
        criteria.andUsernameEqualTo(username);
        List<TbUser> users = tbUserMapper.selectByExample(example);
        if (users == null && users.size() == 0 ) {
            return TaotaoResult.build(400,"用户名或者密码不正确！");
        }
        //用户名正确
        TbUser user = users.get(0);
        //判断用户的密码是否正确
        String mdStr = DigestUtils.md5DigestAsHex(password.getBytes());
        if(!mdStr.equals(user.getPassword())) {
            return TaotaoResult.build(400,"用户名或者密码不正确！");
        }
        //2.生产token,使用uuid
        String token = UUID.randomUUID().toString();
        //3.把用户信息保存到redis当中，key就是token，value就是用户信息
        //我们在redis中存放用户信息不要存密码，因为这样太危险了，因此我们先把密码置空
        user.setPassword(null);
        jedisClient.set(USER_SESSION + ":" + token, JsonUtils.objectToJson(user));
        //4.设置过期时间
        jedisClient.expire(USER_SESSION + ":" + token,SESSION_EXPIRE);
        //5.返回登录成功，要记得带回token信息
        return TaotaoResult.ok(token);
    }
    //查询用户信息
    @Override
    public TaotaoResult getUserMessageByToken(String token) {
        String key = USER_SESSION + ":" + token;
        String value = jedisClient.get(key);
        if(StringUtils.isBlank(value)) {
            //为空，没登录直接返回
            return TaotaoResult.build(400,"用户登录已经过期，请重新登录！");
        }
        //从jedis中获取
        TbUser user = JsonUtils.jsonToPojo(value,TbUser.class);
        //重新登录，要设置过期时间
        jedisClient.expire(key,SESSION_EXPIRE);
        //返回结果
        return TaotaoResult.ok(user);
    }

    @Override
    public TaotaoResult logout(String token) {
        String key = USER_SESSION + ":" + token;
        //根据key删除redis中token中的数据（直接设置为过期）
        jedisClient.expire(key,0);
        return TaotaoResult.ok();
    }


}
