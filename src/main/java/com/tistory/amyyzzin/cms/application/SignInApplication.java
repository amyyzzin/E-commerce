package com.tistory.amyyzzin.cms.application;

import static com.tistory.amyyzzin.cms.exception.ErrorCode.LOGIN_CHECK_FAIL;

import com.tistory.amyyzzin.cms.domain.SignInForm;
import com.tistory.amyyzzin.cms.domain.model.Customer;
import com.tistory.amyyzzin.cms.exception.CustomException;
import com.tistory.amyyzzin.cms.service.CustomerService;
import com.tistory.amyyzzin.config.JwtAuthenticationProvider;
import com.tistory.amyyzzin.domain.common.UserType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignInApplication {

    private final CustomerService customerService;
    private final JwtAuthenticationProvider provider;

    public String customerLoginToken(SignInForm form) {
        // 1. 로그인 가능 여부
        Customer c = customerService.findValidCustomer(form.getEmail(), form.getPassword())
            .orElseThrow(() -> new CustomException(LOGIN_CHECK_FAIL));

        // 2. 토큰을 발행하고

        // 3. 토큰을 response한다.

        return provider.createToken(c.getEmail(), c.getId(), UserType.CUSTOMER);
    }

}
