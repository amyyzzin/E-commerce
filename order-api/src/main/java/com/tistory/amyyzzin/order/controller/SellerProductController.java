package com.tistory.amyyzzin.order.controller;

import com.tistory.amyyzzin.config.JwtAuthenticationProvider;
import com.tistory.amyyzzin.order.domain.product.AddProductForm;
import com.tistory.amyyzzin.order.domain.product.AddProductItemForm;
import com.tistory.amyyzzin.order.domain.product.ProductDto;
import com.tistory.amyyzzin.order.domain.product.ProductItemDto;
import com.tistory.amyyzzin.order.domain.product.UpdateProductForm;
import com.tistory.amyyzzin.order.domain.product.UpdateProductItemForm;
import com.tistory.amyyzzin.order.service.ProductItemService;
import com.tistory.amyyzzin.order.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/seller/product")
public class SellerProductController {

    private final ProductService productService;
    private final ProductItemService productItemService;
    private final JwtAuthenticationProvider provider;

    @PostMapping
    public ResponseEntity<ProductDto> addProduct(
        @RequestHeader(name = "X-AUTH-TOKEN") String token,
        @RequestBody AddProductForm form) {

        return ResponseEntity.ok(
            ProductDto.from(productService.addProduct(provider.getUserVo(token).getId(), form)));
    }

    @PostMapping("/item")
    public ResponseEntity<ProductDto> addProductItem(
        @RequestHeader(name = "X-AUTH-TOKEN") String token,
        @RequestBody AddProductItemForm form) {

        return ResponseEntity.ok(ProductDto.from(
            productItemService.addProductItem(provider.getUserVo(token).getId(), form)));
    }

    @PutMapping
    public ResponseEntity<ProductDto> updateProduct(
        @RequestHeader(name = "X-AUTH-TOKEN") String token,
        @RequestBody UpdateProductForm form) {

        return ResponseEntity.ok(
            ProductDto.from(productService.updateProduct(provider.getUserVo(token).getId(), form)));
    }

    @PutMapping("/item")
    public ResponseEntity<ProductItemDto> updateProductItem(
        @RequestHeader(name = "X-AUTH-TOKEN") String token,
        @RequestBody UpdateProductItemForm form) {

        return ResponseEntity.ok(ProductItemDto.from(
            productItemService.updateProductItem(provider.getUserVo(token).getId(), form)));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteProduct(
        @RequestHeader(name = "X-AUTH-TOKEN") String token, @RequestParam Long id) {
        productService.deleteProduct(provider.getUserVo(token).getId(), id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/item")
    public ResponseEntity<Void> deleteProductItem(
        @RequestHeader(name = "X-AUTH-TOKEN") String token,
        @RequestParam Long id) {
        productItemService.deleteProductItem(provider.getUserVo(token).getId(), id);

        return ResponseEntity.ok().build();
    }
}
