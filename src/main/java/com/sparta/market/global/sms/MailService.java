package com.sparta.market.global.sms;

import com.sparta.market.global.common.exception.CustomException;
import com.sparta.market.global.common.exception.ErrorCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender emailSender;

    /* 한글 인코딩이 안되서, setFrom에 직접 이메일 기입 */
    @Value("${spring.mail.from}")
    private String from;

    public void sendEmail(String toEmail,
                          String title,
                          String text) {
//        SimpleMailMessage emailForm = createEmailForm(toEmail, title, text);
        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
        try {
            helper.setTo(toEmail);
            helper.setSubject(title);
            helper.setText(text, true);

            helper.setFrom("바니마켓 <brandy193010@gmail.com>");

            emailSender.send(mimeMessage);
        } catch (RuntimeException e) {
            log.debug("MailService.sendEmail exception occur toEmail: {}, " +
                    "title: {}, text: {}", toEmail, title, text);
            throw new CustomException(ErrorCode.FAIL_TO_SEND_MAIL);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    // 발신할 이메일 데이터 세팅
    private SimpleMailMessage createEmailForm(String toEmail,
                                              String title,
                                              String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(title);
        message.setText(text);

        return message;
    }
}
