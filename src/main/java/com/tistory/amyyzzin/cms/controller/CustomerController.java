package com.tistory.amyyzzin.cms.controller;

import static com.tistory.amyyzzin.cms.exception.ErrorCode.NOT_FOUND_USER;

import com.tistory.amyyzzin.cms.domain.customer.CustomerDto;
import com.tistory.amyyzzin.cms.domain.model.Customer;
import com.tistory.amyyzzin.cms.exception.CustomException;
import com.tistory.amyyzzin.cms.service.CustomerService;
import com.tistory.amyyzzin.config.JwtAuthenticationProvider;
import com.tistory.amyyzzin.domain.common.UserVo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final JwtAuthenticationProvider provider;
    private final CustomerService customerService;

    @GetMapping("/getInfo")
    public ResponseEntity<CustomerDto> getInfo(@RequestHeader(name = "X-AUTH-TOKEN") String token) {
        UserVo vo = provider.getUserVo(token);
        Customer c = customerService.findByIdAndEmail(vo.getId(), vo.getEmail())
            .orElseThrow(() -> new CustomException(NOT_FOUND_USER));
        return ResponseEntity.ok(CustomerDto.from(c));
    }
}
