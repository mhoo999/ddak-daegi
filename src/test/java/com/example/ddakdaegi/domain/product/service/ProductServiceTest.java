package com.example.ddakdaegi.domain.product.service;

import static org.junit.jupiter.api.Assertions.*;

import com.example.ddakdaegi.domain.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class ProductServiceTest {

	@Autowired
	private ProductRepository productRepository;


	@Test
	void findAllProduct() {
	}

	@Test
	void saveProduct() {
	}

	@Test
	void softDeleteProduct() {
	}

	@Test
	void setSoldOut() {
	}

	@Test
	void editProduct() {
	}
}