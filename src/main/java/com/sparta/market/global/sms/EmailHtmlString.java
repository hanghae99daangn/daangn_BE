package com.sparta.market.global.sms;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class EmailHtmlString {

    public String generateEmailHtmlContent(String verificationCode) {
        String shopName = "바니마켓";

        String htmlContent = "<html>" +
                "<head>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; }" +
                ".container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                ".header { background-color: #f8f9fa; padding: 10px; text-align: center; }" +
                ".content { padding: 20px; }" +
                ".footer { background-color: #f8f9fa; padding: 10px; text-align: center; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'>" +
                "<h2>인증코드 발송 안내</h2>" +
                "</div>" +
                "<div class='content'>" +
                "<p>" + shopName + "에 가입하신 것을 환영합니다!</p>" +
                "<p>아래 인증코드를 입력하여 가입을 완료해주세요.</p>" +
                "<h3>인증코드: " + verificationCode + "</h3>" +
                "</div>" +
                "<div class='footer'>" +
                "<p>이 메일은 자동으로 생성되었습니다. 궁금한 점이 있으시면 바니마켓에 문의해주세요.</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";

        return htmlContent;
    }
}
