package com.tistory.amyyzzin.order.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.tistory.amyyzzin.order.Repository.ProductRepository;
import com.tistory.amyyzzin.order.domain.model.Product;
import com.tistory.amyyzzin.order.domain.product.AddProductCartForm;
import com.tistory.amyyzzin.order.domain.product.AddProductForm;
import com.tistory.amyyzzin.order.domain.product.AddProductItemForm;
import com.tistory.amyyzzin.order.domain.redis.Cart;
import com.tistory.amyyzzin.order.service.ProductService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CartApplicationTest {

    @Autowired
    private CartApplication cartApplication;
    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void ADD_TEST() {
        Product p = add_product();

        Product result = productRepository.findWithProductItemsById(p.getId()).get();

        assertNotNull(result);

        //나머지 필드들에 대한 검증?
        assertEquals(result.getName(), "유메꼬까옷");
        assertEquals(result.getDescription(), "유메가을옷");

        assertEquals(result.getProductItems().size(), 3);
        assertEquals(result.getProductItems().get(0).getName(), "유메꼬까옷0");
        assertEquals(result.getProductItems().get(0).getPrice(), 10000);
//        assertEquals(result.getProductItems().get(2).getCount(), 1);

        Long customerId = 100L;

        Cart cart = cartApplication.addCart(customerId, makeAddForm(result));

        //데이터가 잘 들어가는지
        assertEquals(cart.getMessages().size(), 0);

    }

    @Test
    void ADD_TEST_MODIFY() {

        Long customerId = 100L;

        cartApplication.clearCart(customerId);


        Product p = add_product();
        Product result = productRepository.findWithProductItemsById(p.getId()).get();

        assertNotNull(result);

        //나머지 필드들에 대한 검증?
        assertEquals(result.getName(), "유메꼬까옷");
        assertEquals(result.getDescription(), "유메가을옷");

        assertEquals(result.getProductItems().size(), 3);
        assertEquals(result.getProductItems().get(0).getName(), "유메꼬까옷0");
        assertEquals(result.getProductItems().get(0).getPrice(), 10000);
//        assertEquals(result.getProductItems().get(2).getCount(), 1);

        Cart cart = cartApplication.addCart(customerId, makeAddForm(result));

        //데이터가 잘 들어가는지
        assertEquals(cart.getMessages().size(), 0);

        cart = cartApplication.getCart(customerId);
        assertEquals(cart.getMessages().size(), 1);

    }

    AddProductCartForm makeAddForm(Product p) {
        AddProductCartForm.ProductItem productItem =
            AddProductCartForm.ProductItem.builder()
                .id(p.getProductItems().get(0).getId())
                .name(p.getProductItems().get(0).getName())
                .count(5)
                .price(20000)
                .build();
        return AddProductCartForm.builder()
            .id(p.getId())
            .sellerId(p.getSellerId())
            .name(p.getName())
            .description(p.getDescription())
            .items(List.of(productItem))
            .build();
    }

    Product add_product() {
        Long sellerId = 1L;

        AddProductForm form = makeProductForm("유메꼬까옷", "유메가을옷", 3);
        return productService.addProduct(sellerId, form);
    }

    private static AddProductForm makeProductForm(String name, String description, int itemCount) {
        List<AddProductItemForm> itemForms = new ArrayList<>();
        for (int i = 0; i < itemCount; i++) {
            itemForms.add(makeProductItemForm(null, name + i));
        }
        return AddProductForm.builder()
            .name(name)
            .description(description)
            .items(itemForms)
            .build();

    }

    private static AddProductItemForm makeProductItemForm(Long productId, String name) {
        return AddProductItemForm.builder()
            .productId(productId)
            .name(name)
            .price(10000)
            .count(10)
            .build();
    }

}