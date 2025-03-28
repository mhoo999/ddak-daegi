package com.example.ddakdaegi.domain.order.service;

import com.example.ddakdaegi.domain.image.entity.Image;
import com.example.ddakdaegi.domain.image.enums.ImageType;
import com.example.ddakdaegi.domain.member.entity.Member;
import com.example.ddakdaegi.domain.member.enums.UserRole;
import com.example.ddakdaegi.domain.order.dto.request.PromotionProductRequest;
import com.example.ddakdaegi.domain.order.dto.response.StockResponse;
import com.example.ddakdaegi.domain.order.entity.Order;
import com.example.ddakdaegi.domain.product.entity.Product;
import com.example.ddakdaegi.domain.promotion.entity.Promotion;
import com.example.ddakdaegi.domain.promotion.entity.PromotionProduct;
import com.example.ddakdaegi.domain.promotion.enums.DiscountPolicy;
import com.example.ddakdaegi.domain.promotion.repository.PromotionProductRepository;
import com.example.ddakdaegi.domain.promotion.repository.PromotionRepository;
import com.example.ddakdaegi.global.common.exception.BaseException;
import com.example.ddakdaegi.global.common.exception.enums.ErrorCode;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.anyList;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

	@Mock
	private PromotionProductRepository promotionProductRepository;

	@Mock
	private PromotionRepository promotionRepository;

	@InjectMocks
	private StockService stockService;

	/* MOCK 객체 */
	private static final Member MOCK_MEMBER = Member.builder()
		.email("a@a.com")
		.password("password")
		.address("addressa")
		.phoneNumber("01022223333")
		.role(UserRole.ROLE_ADMIN)
		.build();

	private static final Image MOCK_PRODUCT_IMAGE = new Image("product", ImageType.PRODUCT, "product");
	private static final Image MOCK_PROMOTION_IMAGE = new Image("promotion", ImageType.PROMOTION, "promotion");

	private static final Product MOCK_PRODUCT =
		new Product(MOCK_MEMBER, "description", "name", MOCK_PRODUCT_IMAGE, 1000L, 21000L);

	private static final Promotion MOCK_PROMOTION =
		new Promotion("name", MOCK_PROMOTION_IMAGE, LocalDateTime.now(), LocalDateTime.now(), true);

	private static final PromotionProduct MOCK_PROMOTION_PRODUCT =
		new PromotionProduct(MOCK_PROMOTION, MOCK_PRODUCT, 100L, 3L, DiscountPolicy.FIXED, 1000L, 20000L);

	private static final Order MOCK_ORDER = Order.of(MOCK_MEMBER, 20000L);

	private static final List<PromotionProductRequest> MOCK_REQUEST = List.of(
		new PromotionProductRequest(MOCK_PROMOTION_PRODUCT.getId(), 1L));

	@BeforeEach
	public void setUp() {
		ReflectionTestUtils.setField(MOCK_MEMBER, "id", 1L);
		ReflectionTestUtils.setField(MOCK_ORDER, "id", 1L);
		ReflectionTestUtils.setField(MOCK_PROMOTION_PRODUCT, "id", 1L);
		ReflectionTestUtils.setField(MOCK_PROMOTION, "id", 1L);
	}

	@Test
	public void 프로모션이_시작하지_않아서_재고_감소_실패() throws Exception {
		// given
		ReflectionTestUtils.setField(MOCK_PROMOTION, "isActive", false);

		// when
		BaseException result = assertThrows(BaseException.class,
			() -> stockService.decreaseStockAndCalculateTotalPrice(MOCK_PROMOTION.getId(), MOCK_REQUEST));

		// then
		assertEquals(ErrorCode.PROMOTION_NOT_STARTED, result.getErrorCode());
		assertEquals(ErrorCode.PROMOTION_NOT_STARTED.getHttpStatus(), result.getErrorCode().getHttpStatus());
		assertEquals(ErrorCode.PROMOTION_NOT_STARTED.getMessage(), result.getErrorCode().getMessage());
	}

	@Test
	public void 프로모션_상품을_찾지_못해서_재고_감소_실패() throws Exception {
		// given
		given(promotionRepository.promotionIsActive(anyLong())).willReturn(true);
		given(promotionProductRepository.findAllByIdIn(anyList())).willReturn(List.of());

		// when
		BaseException result = assertThrows(BaseException.class,
			() -> stockService.decreaseStockAndCalculateTotalPrice(MOCK_PROMOTION.getId(), MOCK_REQUEST));

		// then
		assertEquals(ErrorCode.NOT_FOUND_PROMOTION_PRODUCT, result.getErrorCode());
		assertEquals(ErrorCode.NOT_FOUND_PROMOTION_PRODUCT.getHttpStatus(), result.getErrorCode().getHttpStatus());
		assertEquals(ErrorCode.NOT_FOUND_PROMOTION_PRODUCT.getMessage(), result.getErrorCode().getMessage());
	}

	@Test
	public void 구매하려는_수량보다_남은_수량이_더_적어서_실패() throws Exception {
		// given
		given(promotionRepository.promotionIsActive(anyLong())).willReturn(true);
		given(promotionProductRepository.findAllByIdIn(anyList())).willReturn(List.of(MOCK_PROMOTION_PRODUCT));
		ReflectionTestUtils.setField(MOCK_PROMOTION_PRODUCT, "stock", 1L);
		List<PromotionProductRequest> request = List.of(
			new PromotionProductRequest(MOCK_PROMOTION_PRODUCT.getId(), 2L));

		// when & then
		BaseException exception = assertThrows(BaseException.class, () ->
			stockService.decreaseStockAndCalculateTotalPrice(MOCK_PROMOTION.getId(), request)
		);

		assertEquals(ErrorCode.INSUFFICIENT_PROMOTION_PRODUCT_STOCK, exception.getErrorCode());
		assertEquals(ErrorCode.INSUFFICIENT_PROMOTION_PRODUCT_STOCK.getHttpStatus(),
			exception.getErrorCode().getHttpStatus());
		assertEquals(ErrorCode.INSUFFICIENT_PROMOTION_PRODUCT_STOCK.getMessage(),
			exception.getErrorCode().getMessage());
	}

	@Test
	public void 재고_감소에_성공한다() throws Exception {
		// given
		given(promotionRepository.promotionIsActive(anyLong())).willReturn(true);
		given(promotionProductRepository.findAllByIdIn(anyList())).willReturn(List.of(MOCK_PROMOTION_PRODUCT));
		List<PromotionProductRequest> request = List.of(
			new PromotionProductRequest(MOCK_PROMOTION_PRODUCT.getId(), 1L));

		// when
		StockResponse response = stockService.decreaseStockAndCalculateTotalPrice(MOCK_PROMOTION.getId(), request);

		// then
		assertEquals(MOCK_PROMOTION_PRODUCT.getPrice() * MOCK_REQUEST.get(0).getQuantity(), response.getTotalPrice());
	}

}