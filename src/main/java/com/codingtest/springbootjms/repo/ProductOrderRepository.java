package com.codingtest.springbootjms.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codingtest.springbootjms.entity.ProductOrder;


public interface ProductOrderRepository extends JpaRepository<ProductOrder, String> {

}
