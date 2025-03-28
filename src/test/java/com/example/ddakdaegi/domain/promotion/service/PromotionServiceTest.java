package com.example.ddakdaegi.domain.promotion.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.ddakdaegi.domain.image.entity.Image;
import com.example.ddakdaegi.domain.image.repository.ImageRepository;
import com.example.ddakdaegi.domain.product.entity.Product;
import com.example.ddakdaegi.domain.product.repository.ProductRepository;
import com.example.ddakdaegi.domain.promotion.dto.request.CreatePromotionProductRequest;
import com.example.ddakdaegi.domain.promotion.dto.request.CreatePromotionRequest;
import com.example.ddakdaegi.domain.promotion.dto.request.UpdatePromotionRequest;
import com.example.ddakdaegi.domain.promotion.dto.response.PromotionResponse;
import com.example.ddakdaegi.domain.promotion.entity.Promotion;
import com.example.ddakdaegi.domain.promotion.enums.DiscountPolicy;
import com.example.ddakdaegi.domain.promotion.repository.PromotionProductRepository;
import com.example.ddakdaegi.domain.promotion.repository.PromotionRepository;
import com.example.ddakdaegi.global.common.exception.BaseException;
import com.example.ddakdaegi.global.common.exception.enums.ErrorCode;
import com.example.ddakdaegi.global.scheduler.PromotionSchedulerService;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class PromotionServiceTest {

	@InjectMocks
	private PromotionService promotionService;

	@Mock
	private PromotionRepository promotionRepository;

	@Mock
	private ProductRepository productRepository;

	@Mock
	private PromotionProductRepository promotionProductRepository;

	@Mock
	private PromotionSchedulerService promotionSchedulerService;

	@Mock
	private ImageRepository imageRepository;

	private CreatePromotionRequest createPromotionRequest;
	private UpdatePromotionRequest updatePromotionRequest;

	@BeforeEach
	void setUp() {
		createPromotionRequest = new CreatePromotionRequest(
			"Test promotion",
			1L,
			LocalDateTime.now().plusDays(1),
			LocalDateTime.now().plusDays(10),
			Collections.singletonList(new CreatePromotionProductRequest(1L, 20L, 5L, DiscountPolicy.RATE, 20L))
		);
		updatePromotionRequest = new UpdatePromotionRequest(
			"Updated Promotion",
			1L,
			LocalDateTime.now().plusDays(1),
			LocalDateTime.now().plusDays(10),
			false
		);
	}

	@Test
	void 재고보다_수량이_많을_경우_예외를_던진다() {
		// given
		Image image = mock(Image.class);
		when(imageRepository.findById(anyLong())).thenReturn(Optional.of(image));

		Product product = mock(Product.class);
		when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

		// when & then
		BaseException exception = assertThrows(BaseException.class, () -> {
			promotionService.createPromotion(createPromotionRequest);
		});

		assertEquals(ErrorCode.OVER_STOCK, exception.getErrorCode());
	}

	@Test
	public void 존재하지_않는_이미지_ID일_경우_예외를_던진다() {
		// given
		when(imageRepository.findById(anyLong())).thenReturn(Optional.empty());

		// when & then
		BaseException exception = assertThrows(BaseException.class, () -> {
			promotionService.createPromotion(createPromotionRequest);
		});

		assertEquals(ErrorCode.NOT_FOUND_IMAGE, exception.getErrorCode());
	}

	@Test
	public void 존재하지_않는_제품_ID일_경우_예외를_던진다() {
		// given
		Image image = mock(Image.class);
		when(imageRepository.findById(anyLong())).thenReturn(Optional.of(image));

		when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

		// when & then
		BaseException exception = assertThrows(BaseException.class, () -> {
			promotionService.createPromotion(createPromotionRequest);
		});

		assertEquals(ErrorCode.NOT_FOUND_PRODUCT, exception.getErrorCode());
	}

	@Test
	public void 시작_날짜가_종료_날짜_이후일_경우_예외를_던진다() {
		// given
		CreatePromotionRequest invalidRequest = new CreatePromotionRequest(
			"Invalid Promotion",
			1L,
			LocalDateTime.now().plusDays(10), // 종료 날짜가 시작 날짜보다 이전
			LocalDateTime.now().plusDays(1),
			Collections.emptyList()
		);

		// when & then
		BaseException exception = assertThrows(BaseException.class, () -> {
			promotionService.createPromotion(invalidRequest);
		});

		assertEquals(ErrorCode.INVALID_DATE_RANGE, exception.getErrorCode());
	}

	@Test
	public void 존재하지_않는_프로모션_ID일_경우_예외를_던진다() {
		// given
		when(promotionRepository.findById(anyLong())).thenReturn(Optional.empty());

		// when & then
		BaseException exception = assertThrows(BaseException.class, () -> {
			promotionService.findPromotionById(1L);
		});

		assertEquals(ErrorCode.NOT_FOUND_PROMOTION, exception.getErrorCode());
	}

	@Test
	public void 잘못된_할인_정책일_경우_예외를_던진다() {
		// given
		Image image = mock(Image.class);
		when(imageRepository.findById(anyLong())).thenReturn(Optional.of(image));

		Product product = spy(new Product(null, "test", "product", image, 100L, 1000L)); // 충분한 재고 설정
		when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

		CreatePromotionProductRequest invalidProductRequest = new CreatePromotionProductRequest(
			1L, 10L, 5L, null, 20L // 잘못된 할인 정책 (null)
		);

		CreatePromotionRequest invalidRequest = new CreatePromotionRequest(
			"Test Promotion",
			1L,
			LocalDateTime.now().plusDays(1),
			LocalDateTime.now().plusDays(10),
			Collections.singletonList(invalidProductRequest)
		);

		// when & then
		BaseException exception = assertThrows(BaseException.class, () -> {
			promotionService.createPromotion(invalidRequest);
		});

		assertEquals(ErrorCode.WRONG_POLICY, exception.getErrorCode());
	}

	@Test
	void 할인_정책_적용된_프로모션_생성_테스트() {
		// given
		Image image = mock(Image.class);
		when(imageRepository.findById(anyLong())).thenReturn(Optional.of(image));

		Product product = spy(new Product(null, "test", "product", image, 100L, 10000L));
		when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

		CreatePromotionProductRequest promotionProductRequest = new CreatePromotionProductRequest(
			1L, 10L, 5L, DiscountPolicy.RATE, 20L // 20% 할인 적용
		);

		CreatePromotionRequest createPromotionRequest = new CreatePromotionRequest(
			"Discounted Promotion",
			1L,
			LocalDateTime.now().plusDays(1),
			LocalDateTime.now().plusDays(10),
			Collections.singletonList(promotionProductRequest)
		);

		Promotion promotion = mock(Promotion.class);
		when(promotion.getId()).thenReturn(1L);
		when(promotion.getBanner()).thenReturn(image);
		when(promotionRepository.save(any(Promotion.class))).thenReturn(promotion);

		// when
		promotionService.createPromotion(createPromotionRequest);

		// then
		verify(promotionProductRepository, times(1)).save(argThat(promotionProduct ->
			promotionProduct.getPrice().equals(8000L) // 20% 할인 적용 확인
		));
	}

	@Test
	void 모든_프로모션_조회_테스트() {
		// given
		Pageable pageable = PageRequest.of(0, 10);

		Image image = mock(Image.class);
		when(image.getId()).thenReturn(1L);

		Promotion promotion1 = mock(Promotion.class);
		Promotion promotion2 = mock(Promotion.class);

		when(promotion1.getBanner()).thenReturn(image);
		when(promotion2.getBanner()).thenReturn(image);

		List<Promotion> promotions = List.of(promotion1, promotion2);
		Page<Promotion> promotionPage = new PageImpl<>(promotions, pageable, promotions.size());

		when(promotionRepository.findAll(pageable)).thenReturn(promotionPage);

		List<PromotionResponse> promotionResponses = promotions.stream()
			.map(PromotionResponse::new)
			.toList();

		// when
		Page<PromotionResponse> result = promotionService.findAllPromotion(pageable);

		// then
		assertEquals(promotionResponses.size(), result.getTotalElements());
		verify(promotionRepository, times(1)).findAll(pageable);
	}

	@Test
	void 존재하지_않는_프로모션_ID_예외_발생() {
		// given
		when(promotionRepository.findById(anyLong())).thenReturn(Optional.empty());

		// when & then
		BaseException exception = assertThrows(BaseException.class, () ->
			promotionService.updatePromotion(1L, updatePromotionRequest)
		);
		assertEquals(ErrorCode.NOT_FOUND_PROMOTION, exception.getErrorCode());
	}

	@Test
	void 프로모션_정상_업데이트() {
		// given
		Promotion promotion = mock(Promotion.class);
		Image image = mock(Image.class);
		when(image.getId()).thenReturn(1L); // 배너 이미지의 ID 반환 설정
		when(promotion.getBanner()).thenReturn(image); // 배너 이미지가 null이 아니도록 설정

		when(promotionRepository.findById(anyLong())).thenReturn(Optional.of(promotion));
		when(imageRepository.findById(anyLong())).thenReturn(Optional.of(image));

		// when
		PromotionResponse response = promotionService.updatePromotion(1L, updatePromotionRequest);

		// then
		assertNotNull(response);
		verify(promotion, times(1)).update(
			eq(updatePromotionRequest.getName()),
			eq(image),
			eq(updatePromotionRequest.getStartDate()),
			eq(updatePromotionRequest.getEndDate()),
			eq(updatePromotionRequest.getIsTerminate())
		);
	}
}