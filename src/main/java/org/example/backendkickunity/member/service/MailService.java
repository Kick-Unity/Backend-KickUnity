package org.example.backendkickunity.member.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private static String authNum;


    //인증 번호 생성
    public static void createNumber(){
        Random random = new Random();
        StringBuffer key = new StringBuffer();

        for(int i=0; i<6; i++) { // 6자리 인증 번호 생성
            int idx = random.nextInt(3); // 0~2 사이의 값을 랜덤하게 받아와 idx 집어넣기

            // 0,1,2 값을 switch case 분류 -> 숫자와 ASCII 코드 사용
            switch (idx) {
                case 0 :
                    // 0일 때, a~z 까지 랜덤 생성 후 key에 추가
                    key.append((char) (random.nextInt(26) + 97));
                    break;
                case 1:
                    // 1일 때, A~Z 까지 랜덤 생성 후 key에 추가
                    key.append((char) (random.nextInt(26) + 65));
                    break;
                case 2:
                    // 2일 때, 0~9 까지 랜덤 생성 후 key에 추가
                    key.append(random.nextInt(9));
                    break;
            }
        }
        authNum = key.toString();
    }

    //인증 메일 생성
    public MimeMessage createEmailForm(String email) throws MessagingException, UnsupportedEncodingException {

        createNumber();

        String fromEmail = "kickunity@gmail.com"; //보내는 사람
        String toEmail  = email; // 받는 사람
        String title = "Kick-Unity 회원가입 인증"; //메일 제목

        MimeMessage message = mailSender.createMimeMessage();
        message.addRecipients(MimeMessage.RecipientType.TO, toEmail); // 받는 사람 설정
        message.setSubject(title); // 제목 설정

        //메일 내용 설정
        String msgOfEmail="";
        msgOfEmail += "<div style='margin:20px;'>";
        msgOfEmail += "<h1> 안녕하세요 Kick-Unity 입니다. </h1>";
        msgOfEmail += "<br>";
        msgOfEmail += "<p>아래 코드를 입력해주세요<p>";
        msgOfEmail += "<br>";
        msgOfEmail += "<p>감사합니다.<p>";
        msgOfEmail += "<br>";
        msgOfEmail += "<div align='center' style='border:1px solid black; font-family:verdana';>";
        msgOfEmail += "<h3 style='color:blue;'>회원가입 인증 코드입니다.</h3>";
        msgOfEmail += "<div style='font-size:130%'>";
        msgOfEmail += "CODE : <strong>";
        msgOfEmail += authNum + "</strong><div><br/> ";
        msgOfEmail += "</div>";

        message.setFrom(fromEmail);		// 보내는 사람 설정
        message.setText(msgOfEmail, "utf-8", "html"); //내용 설정

        return message;
    }

    //인증 메일 발송
    public String sendMail(String email) throws MessagingException, UnsupportedEncodingException {

        //메일 전송 정보 설정
        MimeMessage emailForm = createEmailForm(email);

        mailSender.send(emailForm);

        return authNum; //인증 코드 반환
    }

    // 인증번호 확인
    public boolean checkAuthNumber(String inputAuthNum) {
        return authNum != null && authNum.equals(inputAuthNum);
    }
}
