package cn.jsuacm.gateway.util;

import cn.jsuacm.gateway.pojo.enity.MessageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.MimeMessage;

/**
 * @author h4795
 * @date 2019/02/19
 */
@Component
public class EmailUtil {

    /**
     * 发送邮箱
     */
    private static String EMAIL_FROM;

    @Value("${spring.mail.username}")
    public  void setUsername(String username) {
        EmailUtil.EMAIL_FROM = username;
    }

    /**
     * 标题名称
     */
    private static String EMAIL_SUBJECT = "JSU_ADMIN";

    /**
     * email有效时间
     */
    public static long EMAIL_TIME = 5*60;

    /**
     * 标记绑定邮箱时的前缀
     */
    public static String BINDING_EMAIL_PREFIX = "BINDING_SIGN_";

    /**
     * 标记验证邮箱时的前缀
     */
    public static String VALIDATION_EMAIL_PREFIX = "VALIDATION_SIGN_";

    /**
     * 标记发送邮箱的时间
     */
    public static long SIGN_TIME = 60L;


    private static JavaMailSender javaMailSender;

    private static TemplateEngine templateEngine;

    @Autowired
    public EmailUtil(JavaMailSender javaMailSender, TemplateEngine templateEngine){
        EmailUtil.javaMailSender = javaMailSender;
        EmailUtil.templateEngine = templateEngine;
    }

    /**
     * 更新邮箱操作
     * @param toEmail 发送的用户
     * @param code 验证码
     * @return
     */
    public static MessageResult sendUpdataEmail(String toEmail, String code){
        MessageResult result = sendEmail("UpdataEmail", toEmail, code);
        return result;
    }

    /**
     * 绑定邮箱操作
     * @param toEmail 发送的用户
     * @param code 验证码
     * @return
     */
    public static MessageResult sendBindingMail(String toEmail, String code){
        MessageResult result = sendEmail("BindingEmail", toEmail, code);
        return result;
    }


    /**
     * 发送邮件
     * @param templateName 使用的邮件模板
     * @param toEmail 发送的用户
     * @param code 验证码
     * @return
     */
    private static MessageResult sendEmail(String templateName, String toEmail, String code){

        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(EmailUtil.EMAIL_FROM);
            helper.setTo(toEmail);
            helper.setSubject(EmailUtil.EMAIL_SUBJECT);
            Context context = new Context();
            context.setVariable("code", code);
            String emailContext = templateEngine.process(templateName, context);
            helper.setText(emailContext, true);
            javaMailSender.send(mimeMessage);
            return new MessageResult(true, "邮件发送成功");

        }catch (Exception e){
            e.printStackTrace();
            return new MessageResult(false, "邮件发送失败");
        }
    }

}
