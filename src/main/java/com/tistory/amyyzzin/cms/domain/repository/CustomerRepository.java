package com.tistory.amyyzzin.cms.domain.repository;

import com.tistory.amyyzzin.cms.domain.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

}
