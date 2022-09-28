package com.tistory.amyyzzin.cms.service;

import static org.junit.jupiter.api.Assertions.*;

import com.tistory.amyyzzin.cms.domain.SignUpForm;
import com.tistory.amyyzzin.cms.domain.model.Customer;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

@SpringBootTest
class SignUpCustomerServiceTest {

    @Autowired
    private SignUpCustomerService service;

    @Test
    void signUp() {
        SignUpForm form = SignUpForm.builder()
            .name("name")
            .birth(LocalDate.now())
            .email("amyyzzin@naver.com")
            .password("1")
            .phone("010-0000-0000")
            .build();
        Customer c = service.signUp(form);
        assertNotNull(c.getId());
        assertNotNull(c.getCreatedAt());



    }

}