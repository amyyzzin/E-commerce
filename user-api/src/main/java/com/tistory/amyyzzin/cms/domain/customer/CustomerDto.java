package com.tistory.amyyzzin.cms.domain.customer;

import com.tistory.amyyzzin.cms.domain.model.Customer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CustomerDto {

    private Long id;
    private String name;
    private String email;
    private Integer balance;


    public static CustomerDto from(Customer customer) {
        return new CustomerDto(customer.getId(), customer.getName(), customer.getEmail(),
            customer.getBalance() == null ? 0 : customer.getBalance());
    }

}
