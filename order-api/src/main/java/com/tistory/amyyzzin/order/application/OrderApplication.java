package com.tistory.amyyzzin.order.application;

import static com.tistory.amyyzzin.order.exception.ErrorCode.ORDER_FAIL_CHECK_CART;
import static com.tistory.amyyzzin.order.exception.ErrorCode.ORDER_FAIL_NO_MONEY;

import com.tistory.amyyzzin.order.client.MailgunClient;
import com.tistory.amyyzzin.order.client.UserClient;
import com.tistory.amyyzzin.order.client.user.ChangeBalanceForm;
import com.tistory.amyyzzin.order.client.user.CustomerDto;
import com.tistory.amyyzzin.order.client.user.SendMailForm;
import com.tistory.amyyzzin.order.domain.model.Product;
import com.tistory.amyyzzin.order.domain.model.ProductItem;
import com.tistory.amyyzzin.order.domain.redis.Cart;
import com.tistory.amyyzzin.order.exception.CustomException;
import com.tistory.amyyzzin.order.service.ProductItemService;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderApplication {

    private final CartApplication cartApplication;
    private final UserClient userClient;
    private final ProductItemService productItemService;
    private final MailgunClient mailgunClient;

    @Transactional
    public String order(String token, Cart cart) {

        // 주문 시 기존 카트 버림
        Cart orderCart = cartApplication.refreshCart(cart);

        if (orderCart.getMessages().size() > 0) {
            throw new CustomException(ORDER_FAIL_CHECK_CART);
        }

        CustomerDto customerDto = userClient.getCustomerInfo(token).getBody();

        int totalPrice = getTotalPrice(cart);

        if (customerDto.getBalance() < getTotalPrice(cart)) {
            throw new CustomException(ORDER_FAIL_NO_MONEY);
        }

        userClient.changeBalance(token, ChangeBalanceForm.builder()
            .from("USER")
            .message("Order")
            .money(-totalPrice)
            .build());

        for (Cart.Product product : orderCart.getProducts()) {
            for (Cart.ProductItem cartItem : product.getItems()) {
                ProductItem productItem = productItemService.getProductItem(cartItem.getId());
                productItem.setCount(productItem.getCount() - cartItem.getCount());
            }
        }

        //주문 사항 이메일로 전송
        SendMailForm sendMailForm = SendMailForm.builder()
            .from("tester@amyyzzin.com")
            .to(customerDto.getEmail())
            .subject("order details Email")
            .text(getOrderEmailBody(customerDto.getName(), orderCart))
            .build();

        mailgunClient.sendEmail(sendMailForm);

        return "주문이 성공하였습니다.";
    }

    public Integer getTotalPrice(Cart cart) {
        return cart.getProducts().stream().flatMapToInt(product ->
                product.getItems().stream().flatMapToInt(
                    productItem -> IntStream.of(productItem.getPrice() * productItem.getCount())))
            .sum();
    }

    private String getOrderEmailBody(String name, Cart orderCart) {
        StringBuilder builder = new StringBuilder();
        return builder.append("Hello ")
            .append(name)
            .append("! Here is your order details.\n\n ")
            .append(orderCart.getProducts().toString())
            .append("상품명 :" + orderCart.getProducts())
            .append("\n\n Your order will be shipped within a week.\n Thank you for joining us.\n\n")
            .append("BestRegards, Amy's pet supplies.")
            .toString();
    }


}
