package com.tistory.amyyzzin.order.service;

import static com.tistory.amyyzzin.order.exception.ErrorCode.NOT_FOUND_PRODUCT;
import static com.tistory.amyyzzin.order.exception.ErrorCode.SAME_ITEM_NAME;

import com.tistory.amyyzzin.order.Repository.ProductItemRepository;
import com.tistory.amyyzzin.order.Repository.ProductRepository;
import com.tistory.amyyzzin.order.domain.model.Product;
import com.tistory.amyyzzin.order.domain.model.ProductItem;
import com.tistory.amyyzzin.order.domain.product.AddProductItemForm;
import com.tistory.amyyzzin.order.exception.CustomException;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductItemService {

    private final ProductRepository productRepository;
    private final ProductItemRepository productItemRepository;

    @Transactional
    public Product addProductItem(Long sellerId, AddProductItemForm form) {

        Product product = productRepository.findBySellerIdAndId(sellerId, form.getProductId())
            .orElseThrow(() -> new CustomException(NOT_FOUND_PRODUCT));

        if (product.getProductItems().stream()
            .anyMatch(item -> item.getName().equals(form.getName()))) {
            throw new CustomException(SAME_ITEM_NAME);
        }

        ProductItem productItem = ProductItem.of(sellerId, form);
        product.getProductItems().add(productItem);
        return product;
    }

}
