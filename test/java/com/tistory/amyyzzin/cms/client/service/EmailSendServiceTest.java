package com.tistory.amyyzzin.cms.client.service;

import com.tistory.amyyzzin.cms.client.MailgunClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EmailSendServiceTest {

    @Autowired
    private MailgunClient mailgunClient;

    @Test
    public void EmailTest(){
        //needTestCode
        mailgunClient.sendEmail(null);
//        String response = mailgunClient.sendEmail();
//        System.out.println(response);
    }

}