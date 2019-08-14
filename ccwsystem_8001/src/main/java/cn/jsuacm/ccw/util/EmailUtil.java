package cn.jsuacm.ccw.util;

import cn.jsuacm.ccw.pojo.enity.MessageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.MimeMessage;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @ClassName EmailUtils
 * @Description TODO
 * @Author h4795
 * @Date 2019/08/13 19:58
 */
@Component
public class EmailUtil {


    /**
     * 发送邮箱
     */
    private static String EMAIL_FROM = "God_4795@163.com";

    /**
     * 标题名称
     */
    private static String EMAIL_SUBJECT = "JSU_ADMIN";




    private static JavaMailSender javaMailSender;

    private static TemplateEngine templateEngine;

    @Autowired
    public EmailUtil(JavaMailSender javaMailSender, TemplateEngine templateEngine){
        EmailUtil.javaMailSender = javaMailSender;
        EmailUtil.templateEngine = templateEngine;
    }


    public static MessageResult sendEmail(String toEmail, String name, Date date, String address, String msg){

        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(EmailUtil.EMAIL_FROM);
            helper.setTo(toEmail);
            helper.setSubject(EmailUtil.EMAIL_SUBJECT);
            Context context = new Context();
            context.setVariable("name", name);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            context.setVariable("date", formatter.format(date));
            context.setVariable("address", address);
            context.setVariable("msg", msg);
            String emailContext = templateEngine.process("OpenRecruitment", context);
            helper.setText(emailContext, true);
            javaMailSender.send(mimeMessage);
            return new MessageResult(true, "邮件发送成功");

        }catch (Exception e){
            e.printStackTrace();
            return new MessageResult(false, "邮件发送失败");
        }
    }
}
