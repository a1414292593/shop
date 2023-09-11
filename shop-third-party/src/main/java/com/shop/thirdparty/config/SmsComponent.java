package com.shop.thirdparty.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;

@ConfigurationProperties(prefix = "spring.alicloud")
@Data
@Component
public class SmsComponent {

    private String from;

    @Resource
    MailSender mailSender;

    public void sendCode(String email, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(email);
        message.setSubject("您的验证邮件");
        message.setText("验证码是：" + code);
        try {
            mailSender.send(message);
        } catch (MailException e) {
            throw new RuntimeException("验证码邮件发送失败: {}", e.getCause());
        }
    }

}
