package com.example.ddakdaegi.domain.order.service;

import com.example.ddakdaegi.domain.image.entity.Image;
import com.example.ddakdaegi.domain.image.enums.ImageType;
import com.example.ddakdaegi.domain.member.entity.Member;
import com.example.ddakdaegi.domain.member.enums.UserRole;
import com.example.ddakdaegi.domain.member.repository.MemberRepository;
import com.example.ddakdaegi.domain.order.dto.response.OrderDetailResponse;
import com.example.ddakdaegi.domain.order.dto.response.OrderResponse;
import com.example.ddakdaegi.domain.order.dto.response.StockResponse;
import com.example.ddakdaegi.domain.order.entity.Order;
import com.example.ddakdaegi.domain.order.entity.OrderPromotionProduct;
import com.example.ddakdaegi.domain.order.enums.OrderStatus;
import com.example.ddakdaegi.domain.order.repository.OrderPromotionProductRepository;
import com.example.ddakdaegi.domain.order.repository.OrderRepository;
import com.example.ddakdaegi.domain.product.entity.Product;
import com.example.ddakdaegi.domain.promotion.entity.Promotion;
import com.example.ddakdaegi.domain.promotion.entity.PromotionProduct;
import com.example.ddakdaegi.domain.promotion.enums.DiscountPolicy;
import com.example.ddakdaegi.global.common.dto.AuthUser;
import com.example.ddakdaegi.global.common.exception.BaseException;
import com.example.ddakdaegi.global.common.exception.enums.ErrorCode;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

	@Mock
	private OrderRepository orderRepository;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private OrderPromotionProductRepository orderPromotionProductRepository;

	@InjectMocks
	private OrderService orderService;

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

	private static final OrderPromotionProduct MOCK_ORDER_PROMOTION_PRODUCT =
		OrderPromotionProduct.of(MOCK_ORDER, MOCK_PROMOTION_PRODUCT, 1L);


	@BeforeEach
	public void setUp() {
		ReflectionTestUtils.setField(MOCK_MEMBER, "id", 1L);
		ReflectionTestUtils.setField(MOCK_ORDER, "id", 1L);
		ReflectionTestUtils.setField(MOCK_PROMOTION_PRODUCT, "id", 1L);
	}

	@Test
	public void 사용자를_찾을_수_없어서_주문_실패() throws Exception {
		// given
		AuthUser authUser = new AuthUser(MOCK_MEMBER.getId(), MOCK_MEMBER.getEmail(), MOCK_MEMBER.getRole());
//		StockResponse stockResponse = StockResponse.of(10000L, Map.of(1L, 1L), List.of());
		given(memberRepository.findById(anyLong())).willReturn(Optional.empty());

		// when
		BaseException result = assertThrows(BaseException.class,
			() -> orderService.createOrder(authUser, any(StockResponse.class)));

		// then
		assertEquals(ErrorCode.NOT_FOUND_MEMBER, result.getErrorCode());
		assertEquals(ErrorCode.NOT_FOUND_MEMBER.getHttpStatus(), result.getErrorCode().getHttpStatus());
		assertEquals(ErrorCode.NOT_FOUND_MEMBER.getMessage(), result.getErrorCode().getMessage());
	}

	@Test
	public void 주문_생성_성공() throws Exception {
		// given
		AuthUser authUser = new AuthUser(1L, MOCK_MEMBER.getEmail(), MOCK_MEMBER.getRole());
		StockResponse stockResponse = StockResponse.of(20000L, Map.of(1L, 2L), List.of(MOCK_PROMOTION_PRODUCT));

		given(memberRepository.findById(anyLong())).willReturn(Optional.of(MOCK_MEMBER));
		given(orderRepository.save(any(Order.class))).willAnswer(invocation -> {
			Order order = invocation.getArgument(0);
			ReflectionTestUtils.setField(order, "id", 1L);
			return order;
		});
		given(orderPromotionProductRepository.saveAll(anyList())).willReturn(List.of(MOCK_ORDER_PROMOTION_PRODUCT));

		// when
		OrderResponse result = orderService.createOrder(authUser, stockResponse);

		// then
		assertNotNull(result);
		assertEquals(1L, result.getOrderId());
		assertEquals(20000L, result.getTotalPrice());
		assertEquals(OrderStatus.COMPLETED, result.getStatus());
	}

	@Test
	public void 사용자를_찾을_수_없어서_주문_단건_실패() throws Exception {
		// given
		AuthUser authUser = new AuthUser(MOCK_MEMBER.getId(), MOCK_MEMBER.getEmail(), MOCK_MEMBER.getRole());

		given(memberRepository.findById(anyLong())).willReturn(Optional.empty());

		// when
		BaseException result = assertThrows(BaseException.class,
			() -> orderService.getOrder(MOCK_ORDER.getId(), authUser));

		// then
		assertEquals(ErrorCode.NOT_FOUND_MEMBER, result.getErrorCode());
		assertEquals(ErrorCode.NOT_FOUND_MEMBER.getHttpStatus(), result.getErrorCode().getHttpStatus());
		assertEquals(ErrorCode.NOT_FOUND_MEMBER.getMessage(), result.getErrorCode().getMessage());
	}

	@Test
	public void 사용자가_주문한_내역을_찾을_수_없어서_주문_단건_실패() throws Exception {
		// given
		AuthUser authUser = new AuthUser(MOCK_MEMBER.getId(), MOCK_MEMBER.getEmail(), MOCK_MEMBER.getRole());

		given(memberRepository.findById(anyLong())).willReturn(Optional.of(MOCK_MEMBER));
		given(orderRepository.findByOrderIdAndMemberId(anyLong(), anyLong())).willReturn(Optional.empty());

		// when
		BaseException result = assertThrows(BaseException.class,
			() -> orderService.getOrder(MOCK_ORDER.getId(), authUser));

		// then
		assertEquals(ErrorCode.NOT_FOUND_ORDER, result.getErrorCode());
		assertEquals(ErrorCode.NOT_FOUND_ORDER.getHttpStatus(), result.getErrorCode().getHttpStatus());
		assertEquals(ErrorCode.NOT_FOUND_ORDER.getMessage(), result.getErrorCode().getMessage());
	}

	@Test
	public void 주문_단건_성공() throws Exception {
		// given
		AuthUser authUser = new AuthUser(MOCK_MEMBER.getId(), MOCK_MEMBER.getEmail(), MOCK_MEMBER.getRole());

		given(memberRepository.findById(anyLong())).willReturn(Optional.of(MOCK_MEMBER));
		given(orderRepository.findByOrderIdAndMemberId(anyLong(), anyLong())).willReturn(Optional.of(MOCK_ORDER));

		// when
		OrderDetailResponse response = orderService.getOrder(MOCK_ORDER.getId(), authUser);

		// then
		assertEquals(MOCK_ORDER.getId(), response.getOrderId());
		assertEquals(MOCK_MEMBER.getEmail(), response.getEmail());
		assertEquals(MOCK_MEMBER.getAddress(), response.getAddress());
		assertEquals(MOCK_ORDER.getTotalPrice(), response.getTotalPrice());
		assertEquals(MOCK_ORDER.getStatus(), response.getStatus());
		assertEquals(MOCK_ORDER.getCreatedAt(), response.getOrderCompletionTime());
	}

	@Test
	public void 주문_전체_조회() throws Exception {
		// given
		AuthUser authUser = new AuthUser(MOCK_MEMBER.getId(), MOCK_MEMBER.getEmail(), MOCK_MEMBER.getRole());
		Pageable pageable = PageRequest.of(0, 5);

		when(orderRepository.findOrders(MOCK_MEMBER.getId(), pageable))
			.thenReturn(new PageImpl<>(Collections.emptyList(), pageable, 0));

		// when
		Page<OrderResponse> responsePage = orderService.getAllOrders(authUser, pageable);

		// then
		assertNotNull(responsePage);
		assertEquals(0, responsePage.getTotalElements());
		then(orderRepository).should(times(1)).findOrders(MOCK_MEMBER.getId(), pageable);
	}

	@Test
	public void 사용자가_주문한_내역을_찾을_수_없어서_주문_취소_실패() throws Exception {
		// given
		AuthUser authUser = new AuthUser(MOCK_MEMBER.getId(), MOCK_MEMBER.getEmail(), MOCK_MEMBER.getRole());
		given(orderRepository.findByOrderIdAndMemberId(anyLong(), anyLong())).willReturn(Optional.empty());

		// when
		BaseException result = assertThrows(BaseException.class,
			() -> orderService.cancelOrder(MOCK_ORDER.getId(), authUser));

		// then
		assertEquals(ErrorCode.NOT_FOUND_ORDER, result.getErrorCode());
		assertEquals(ErrorCode.NOT_FOUND_ORDER.getHttpStatus(), result.getErrorCode().getHttpStatus());
		assertEquals(ErrorCode.NOT_FOUND_ORDER.getMessage(), result.getErrorCode().getMessage());
	}

	@Test
	public void 진행_중인_이벤트가_있을_경우_주문_취소_시_프로모션_상품의_개수가_올라간다() throws Exception {
		// given
		AuthUser authUser = new AuthUser(MOCK_MEMBER.getId(), MOCK_MEMBER.getEmail(), MOCK_MEMBER.getRole());

		given(orderRepository.findByOrderIdAndMemberId(anyLong(), anyLong())).willReturn(Optional.of(MOCK_ORDER));
		given(orderPromotionProductRepository.findByOrderId(anyLong()))
			.willReturn(List.of(MOCK_ORDER_PROMOTION_PRODUCT));

		// when
		orderService.cancelOrder(MOCK_ORDER.getId(), authUser);

		// then
		assertEquals(101, MOCK_PROMOTION_PRODUCT.getStock());
	}

	@Test
	public void 이벤트가_종료된_경우_주문_취소_시_상품의_개수가_올라간다() throws Exception {
		// given
		ReflectionTestUtils.setField(MOCK_PROMOTION, "isActive", false);
		AuthUser authUser = new AuthUser(MOCK_MEMBER.getId(), MOCK_MEMBER.getEmail(), MOCK_MEMBER.getRole());

		given(orderRepository.findByOrderIdAndMemberId(anyLong(), anyLong())).willReturn(Optional.of(MOCK_ORDER));
		given(orderPromotionProductRepository.findByOrderId(anyLong()))
			.willReturn(List.of(MOCK_ORDER_PROMOTION_PRODUCT));

		// when
		orderService.cancelOrder(MOCK_ORDER.getId(), authUser);

		// then
		assertEquals(1001, MOCK_PRODUCT.getStock());
	}

}