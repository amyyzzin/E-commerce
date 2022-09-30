package com.tistory.amyyzzin.order.service;

import com.tistory.amyyzzin.order.Repository.ProductRepository;
import com.tistory.amyyzzin.order.domain.model.Product;
import com.tistory.amyyzzin.order.domain.product.AddProductForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public Product addProduct(Long sellerId, AddProductForm form) {
        return productRepository.save(Product.of(sellerId, form));
    }
}
