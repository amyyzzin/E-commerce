package com.tistory.amyyzzin.order.controller;

import com.tistory.amyyzzin.config.JwtAuthenticationProvider;
import com.tistory.amyyzzin.order.application.CartApplication;
import com.tistory.amyyzzin.order.domain.product.AddProductCartForm;
import com.tistory.amyyzzin.order.domain.redis.Cart;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customer/cart")
@RequiredArgsConstructor
public class CustomerCartController {

    //임시코드
    private final CartApplication cartApplication;
    private final JwtAuthenticationProvider provider;

    @PostMapping
    public ResponseEntity<Cart> addCart(
        @RequestHeader(name = "X-AUTH-TOKEN") String token,
        @RequestBody AddProductCartForm form) {
        return ResponseEntity.ok(cartApplication.addCart(provider.getUserVo(token).getId(), form));
    }

    @GetMapping
    public ResponseEntity<Cart> shopCart(
        @RequestHeader(name = "X-AUTH-TOKEN") String token) {
        return ResponseEntity.ok(cartApplication.getCart(provider.getUserVo(token).getId()));
    }

    @PutMapping
    public ResponseEntity<Cart> updateCart(
        @RequestHeader(name = "X-AUTH-TOKEN") String token,
        @RequestBody Cart cart) {
        return ResponseEntity.ok(
            cartApplication.updateCart(provider.getUserVo(token).getId(), cart));
    }


}
