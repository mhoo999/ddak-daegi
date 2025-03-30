package com.example.ddakdaegi.domain.product.service;


import static org.junit.jupiter.api.Assertions.*;

import com.example.ddakdaegi.domain.image.entity.Image;
import com.example.ddakdaegi.domain.image.enums.ImageType;
import com.example.ddakdaegi.domain.image.repository.ImageRepository;
import com.example.ddakdaegi.domain.member.entity.Member;
import com.example.ddakdaegi.domain.member.enums.UserRole;
import com.example.ddakdaegi.domain.member.repository.MemberRepository;
import com.example.ddakdaegi.domain.product.dto.request.ProductRequest;
import com.example.ddakdaegi.domain.product.dto.response.ProductResponse;
import com.example.ddakdaegi.domain.product.entity.Product;
import com.example.ddakdaegi.domain.product.repository.ProductRepository;
import com.example.ddakdaegi.global.common.dto.AuthUser;
import com.example.ddakdaegi.global.common.exception.BaseException;
import com.example.ddakdaegi.global.common.exception.enums.ErrorCode;
import java.util.Collections;
import java.util.Optional;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@ExtendWith(SpringExtension.class)
class ProductServiceTest {

	@InjectMocks
	private ProductService productService;

	@Mock
	private ProductRepository productRepository;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private ImageRepository imageRepository;


	// 정보수정 테스트
	@Test
	@DisplayName("상품정보수정 전체 테스트")
	public void editProduct_editTest() {
		// given
		ProductRequest productRequest = new ProductRequest("상품수정하기", "상품수정하기", 2L, 100L, 2000L);

		AuthUser authUser = new AuthUser(1L, "abc@abc.com", UserRole.ROLE_USER);
		Member member = Member.fromAuthUser(authUser);

		Image image = new Image("aaa", ImageType.PRODUCT, "image_url");
		Mockito.when(imageRepository.findById(2L)).thenReturn(Optional.of(image));

		Product product = new Product(member, "현재 저장되어있는상품", "현재 저장되어있는상품", image, 50L, 1000L);
		Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(product));

		// when
		ProductResponse response = productService.editProduct(authUser, 1L, productRequest);

		// then
		assertNotNull(response);
		assertEquals("상품수정하기", response.getName());
		assertEquals("상품수정하기", response.getDescription());
		assertEquals(100, response.getStock());
		assertEquals(2000, response.getPrice());
	}


	@Test
	@DisplayName("상품 정보수정시 수정하려는 상품이 없는 경우")
	public void editProduct_ProductNotFoundTest() {
		// given
		AuthUser authUser = new AuthUser(1L, "abc@abc.com", UserRole.ROLE_USER);

		ProductRequest productRequest = new ProductRequest("상품내용", "상품이름", 2L, 100L, 2000L);

		Mockito.when(productRepository.findById(1L)).thenReturn(Optional.empty());

		// when then
		assertThrows(BaseException.class,
			() -> productService.editProduct(authUser, 1L, productRequest),
			ErrorCode.NOT_FOUND_PRODUCT.getMessage());
	}


	@Test
	@DisplayName("상품 정보수정시 상품 주인이 아닐경우")
	public void editProduct_NotOwnerTest() {
		// given
		AuthUser authUser = new AuthUser(1L, "abc@abc.com", UserRole.ROLE_USER);

		AuthUser ownerAuthUser = new AuthUser(2L, "abcd@abcd.com", UserRole.ROLE_USER);
		Member owner = Member.fromAuthUser(ownerAuthUser);

		ProductRequest productRequest = new ProductRequest("변경상품내용", "변경상품이름", 2L, 100L, 2000L);

		Image image = new Image("2", ImageType.PRODUCT, "image_url");

		Product product = new Product(owner, "원래상품내용", "원래상품이름", image, 50L, 1000L);
		Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(product));

		// when then
		assertThrows(BaseException.class,
			() -> productService.editProduct(authUser, 1L, productRequest),
			ErrorCode.IS_NOT_YOUR_PRODUCT.getMessage());
	}


	@Test
	@DisplayName("상품 정보수정시 이미지를 찾지 못했을 경우")
	public void editProduct_ImageNotFoundTest() {
		// given
		AuthUser authUser = new AuthUser(1L, "abc@abc.com", UserRole.ROLE_USER);
		Member member = Member.fromAuthUser(authUser);

		ProductRequest productRequest = new ProductRequest("변경상품", "변경상품이름", 2L, 100L, 2000L);

		Image image = new Image("aaa", ImageType.PRODUCT, "image_url");
		Mockito.when(imageRepository.findById(1L)).thenReturn(Optional.of(image));

		Product product = new Product(member, "원래상품", "원래상품이름", image, 50L, 1000L);
		Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(product));

		Mockito.when(imageRepository.findById(2L)).thenReturn(Optional.empty()); // id 2 이미지 조회 (존재하지않는 이미지id)

		// when then
		assertThrows(BaseException.class,
			() -> productService.editProduct(authUser, 1L, productRequest),
			ErrorCode.NOT_FOUND_IMAGE.getMessage());
	}


	// 상품 저장 테스트
	@Test
	@DisplayName("상품 저장 테스트")
	void findAllProduct_Test() {
		// given
		Pageable pageable = PageRequest.of(0, 10);

		AuthUser authUser1 = new AuthUser(1L, "abc@abc.com", UserRole.ROLE_USER);
		Member member1 = Member.fromAuthUser(authUser1);

		AuthUser authUser2 = new AuthUser(1L, "abc@abc.com", UserRole.ROLE_USER);
		Member member2 = Member.fromAuthUser(authUser2);

		Image image1 = new Image("aaa", ImageType.PRODUCT, "image_url");
		Mockito.when(imageRepository.findById(1L)).thenReturn(Optional.of(image1));

		Image image2 = new Image("aaa", ImageType.PRODUCT, "image_url");
		Mockito.when(imageRepository.findById(2L)).thenReturn(Optional.of(image2));

		List<Product> products = List.of(
			new Product(member1, "등록한 상품", "등록 상품1", image1, 10L, 1000L),
			new Product(member2, "등록한 상품", "등록 상품2", image2, 20L, 2000L)
		);
		Page<Product> productPage = new PageImpl<>(products, pageable, products.size());

		Mockito.when(productRepository.findByDeletedAtNull(pageable)).thenReturn(productPage);

		// when
		Page<ProductResponse> responsePage = productService.findAllProduct(pageable);

		// then
		assertNotNull(responsePage);
		assertEquals(2, responsePage.getTotalElements());
		assertEquals("등록 상품1", responsePage.getContent().get(0).getName());
		assertEquals("등록 상품2", responsePage.getContent().get(1).getName());
	}


	@Test
	@DisplayName("등록한 상품이 없는대 조회하는 경우")
	void findAllProduct_EmptyPageTest() {
		// given
		Pageable pageable = PageRequest.of(0, 10);
		Page<Product> emptyProductPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

		Mockito.when(productRepository.findByDeletedAtNull(pageable)).thenReturn(emptyProductPage);

		// when
		Page<ProductResponse> responsePage = productService.findAllProduct(pageable);

		// then
		assertNotNull(responsePage);
		assertTrue(responsePage.getContent().isEmpty());
		assertEquals(0, responsePage.getTotalElements());
	}


	@Test
	@DisplayName("새로운 상품 저장 테스트")
	void saveProduct_SuccessTest() {
		// given
		AuthUser authUser1 = new AuthUser(1L, "abc@abc.com", UserRole.ROLE_USER);

		Member member1 = Member.fromAuthUser(authUser1);
		Mockito.when(memberRepository.findById(1L)).thenReturn(Optional.of(member1));

		Image image1 = new Image("aaa", ImageType.PRODUCT, "image_url");
		Mockito.when(imageRepository.findById(1L)).thenReturn(Optional.of(image1));

		ProductRequest productRequest = new ProductRequest("새로운상품", "새로운상품이름", 1L, 100L, 2000L);

		// when
		ProductResponse response = productService.saveProduct(authUser1, productRequest);

		// then
		assertNotNull(response);
		assertEquals("새로운상품이름", response.getName());
		assertEquals("새로운상품", response.getDescription());
		assertEquals(100, response.getStock());
		assertEquals(2000, response.getPrice());
	}


	@Test
	@DisplayName("새로운 상품 저장시 멤버정보를 찾지 못하였을 경우")
	void saveProduct_MemberNotFoundTest() {
		// given
		AuthUser authUser1 = new AuthUser(1L, "abc@abc.com", UserRole.ROLE_USER);

		ProductRequest productRequest = new ProductRequest("새로운상품", "새로운상품이름", 1L, 100L, 2000L);

		Mockito.when(memberRepository.findById(1L)).thenReturn(Optional.empty());

		// when then
		assertThrows(BaseException.class,
			() -> productService.saveProduct(authUser1, productRequest),
			ErrorCode.NOT_FOUND_MEMBER.getMessage());
	}


	@Test
	@DisplayName("새로운 상품 저장시 이미지를 찾지 못하였을경우")
	void saveProduct_ImageNotFoundTest() {
		// given
		AuthUser authUser1 = new AuthUser(1L, "abc@abc.com", UserRole.ROLE_USER);

		Member member1 = Member.fromAuthUser(authUser1);
		Mockito.when(memberRepository.findById(1L)).thenReturn(Optional.of(member1));

		ProductRequest productRequest = new ProductRequest("새로운상품", "새로운상품이름", 1L, 100L, 2000L);

		Mockito.when(imageRepository.findById(2L)).thenReturn(Optional.empty());

		// when then
		assertThrows(BaseException.class,
			() -> productService.saveProduct(authUser1, productRequest),
			ErrorCode.NOT_FOUND_IMAGE.getMessage());
	}


	@Test
	@DisplayName("상품 판매상태 정보수정 테스트")
	void setSoldOut_SuccessTest() {
		// given
		AuthUser authUser1 = new AuthUser(1L, "abc@abc.com", UserRole.ROLE_USER);

		Member member1 = Member.fromAuthUser(authUser1);
		Mockito.when(memberRepository.findById(1L)).thenReturn(Optional.of(member1));

		Image image1 = new Image("aaa", ImageType.PRODUCT, "image_url");
		Mockito.when(imageRepository.findById(1L)).thenReturn(Optional.of(image1));

		Product product = new Product(member1, "원래상품", "원래상품이름", image1, 50L, 1000L);
		product.setSoldOut(false);
		Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(product));

		// when
		ProductResponse response = productService.setSoldOut(authUser1, 1L, true);

		// then
		assertNotNull(response);
		assertTrue(response.getSoldOut());
	}

	@Test
	@DisplayName("상품을 찾기 못하였을 경우")
	void setSoldOut_ProductNotFoundTest() {
		// given
		AuthUser authUser1 = new AuthUser(1L, "abc@abc.com", UserRole.ROLE_USER);

		Mockito.when(productRepository.findById(1L)).thenReturn(Optional.empty());

		// when then
		assertThrows(BaseException.class,
			() -> productService.setSoldOut(authUser1, 1L, true),
			ErrorCode.NOT_FOUND_PRODUCT.getMessage());
	}

	@Test // authuser id 다시 보기
	@DisplayName("상품 판매상태 수정시 내 상품이 아닌경우")
	void setSoldOut_NotOwnerTest() {
		// given
		AuthUser authUser1 = new AuthUser(1L, "abc@abc.com", UserRole.ROLE_USER);
		AuthUser ownerAuthUser = new AuthUser(2L, "abcd@abcd.com", UserRole.ROLE_USER);

		Member owner = Member.fromAuthUser(ownerAuthUser);

		Image image1 = new Image("aaa", ImageType.PRODUCT, "image_url");
		Mockito.when(imageRepository.findById(1L)).thenReturn(Optional.of(image1));

		Product product = new Product(owner, "원래상품", "원래상품이름", image1, 50L, 1000L);

		Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(product));

		// when then
		assertThrows(BaseException.class,
			() -> productService.setSoldOut(authUser1, 1L, true),
			ErrorCode.IS_NOT_YOUR_PRODUCT.getMessage());
	}


	@Test
	@DisplayName("상품수정시 판매상태값이 동일할 경우")
	void setSoldOut_SameFlagTest() {
		// given
		AuthUser authUser1 = new AuthUser(1L, "abc@abc.com", UserRole.ROLE_USER);
		Member member1 = Member.fromAuthUser(authUser1);
		Product product = new Product(member1, "원래상품", "원래상품이름", null, 50L, 1000L);
		product.setSoldOut(true);

		Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(product));

		// when then
		assertThrows(BaseException.class,
			() -> productService.setSoldOut(authUser1, 1L, true),
			ErrorCode.SOLD_OUT_SAME_FLAG.getMessage());
	}


	@Test
	@DisplayName("상품 소프트 딜리트 테스트")
	void softDeleteProduct_SuccessTest() {
		// given

		AuthUser authUser1 = new AuthUser(1L, "abc@abc.com", UserRole.ROLE_USER);

		Member member1 = Member.fromAuthUser(authUser1);
		Mockito.when(memberRepository.findById(1L)).thenReturn(Optional.of(member1));

		Image image1 = new Image("aaa", ImageType.PRODUCT, "image_url");
		Mockito.when(imageRepository.findById(1L)).thenReturn(Optional.of(image1));

		Product product = new Product(member1, "원래상품", "원래상품이름", image1, 50L, 1000L);
		//product.setDeletedAt(null);
		Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(product));

		// when
		ProductResponse response = productService.softDeleteProduct(1L);

		// then
		assertNotNull(response);
		assertNotNull(product.getDeletedAt());
	}


	@Test
	@DisplayName("상품 소프트 딜리트시 해당 상품을 찾지 못하였을 경우")
	void softDeleteProduct_ProductNotFoundTest() {
		// given
		Mockito.when(productRepository.findById(1L)).thenReturn(Optional.empty());

		// when then
		assertThrows(BaseException.class,
			() -> productService.softDeleteProduct(1L),
			ErrorCode.NOT_FOUND_PRODUCT.getMessage());
	}

}