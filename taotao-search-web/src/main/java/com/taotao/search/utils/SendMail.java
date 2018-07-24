package com.taotao.search.utils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * 发送邮件工具类
 */
public class SendMail {

    public static void sendMail(String subject,String text) throws MessagingException {
        //获取系统的属性
        Properties properties = new Properties();

        //设置服务器访问的协议
        properties.setProperty("mail.transport.protocol", "smtp");
        // 主机名
        properties.put("mail.smtp.host", "smtp.qq.com");
        // 端口号
        properties.put("mail.smtp.port", 465);
        //访问smtp服务其需要验证
        properties.setProperty("mail.smtp.auth","true");
        //设置是否使用ssl安全连接  ---一般都使用
        properties.put("mail.smtp.ssl.enable", "true");
        //获取session对象
        Session session = Session.getDefaultInstance(properties);
        //打开debug功能
        session.setDebug(true);
        //创建一个默认的MimeMessage对象
        Message msg = new MimeMessage(session);
        //这里填你登录163邮箱所用的用户名
        msg.setFrom(new InternetAddress("121601585@qq.com")); //设置发件人，163邮箱要求发件人与登录用户必须一致（必填），其它邮箱不了解

        msg.setSubject(subject);
        //设置邮件内容
        msg.setText(text);

        Transport trans = session.getTransport();
        //下面四个参数，前两个可以认为是固定的，不用变，后两个参数分别是登录163邮箱的用户名以及客户端授权密码（注意，不是登录密码）
        //连接邮箱smtp服务器，25为默认端口
        trans.connect("121601585@qq.com","yjcdtcjdzjwhbigg");
        //要发送到哪个邮箱，这里以qq邮箱为例
        trans.sendMessage(msg, new Address[]{new InternetAddress("a121601585@163.com")}); //发送邮件

        trans.close(); //关闭连接

    }
}
