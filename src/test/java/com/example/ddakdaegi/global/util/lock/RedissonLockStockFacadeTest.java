package com.example.ddakdaegi.global.util.lock;

import com.example.ddakdaegi.domain.image.entity.Image;
import com.example.ddakdaegi.domain.image.enums.ImageType;
import com.example.ddakdaegi.domain.image.repository.ImageRepository;
import com.example.ddakdaegi.domain.member.entity.Member;
import com.example.ddakdaegi.domain.member.enums.UserRole;
import com.example.ddakdaegi.domain.member.repository.MemberRepository;
import com.example.ddakdaegi.domain.order.dto.request.PromotionProductRequest;
import com.example.ddakdaegi.domain.product.entity.Product;
import com.example.ddakdaegi.domain.product.repository.ProductRepository;
import com.example.ddakdaegi.domain.promotion.entity.Promotion;
import com.example.ddakdaegi.domain.promotion.entity.PromotionProduct;
import com.example.ddakdaegi.domain.promotion.enums.DiscountPolicy;
import com.example.ddakdaegi.domain.promotion.repository.PromotionProductRepository;
import com.example.ddakdaegi.domain.promotion.repository.PromotionRepository;
import com.example.ddakdaegi.global.common.exception.BaseException;
import com.example.ddakdaegi.global.common.exception.enums.ErrorCode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@Rollback
@SpringBootTest
class RedissonLockStockFacadeTest {

	private static final Logger log = LoggerFactory.getLogger(RedissonLockStockFacadeTest.class);

	@Autowired
	private RedissonLockStockFacade redissonLockStockFacade;

	@Autowired
	private PromotionProductRepository promotionProductRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private ImageRepository imageRepository;

	@Autowired
	private PromotionRepository promotionRepository;

	@Autowired
	private ProductRepository productRepository;

	@BeforeEach
	@Transactional
	void setUp() {
		Member member = Member.builder().email("a@a.com").password("password").address("addressa")
			.phoneNumber("01022223333").role(UserRole.ROLE_ADMIN).build();
		memberRepository.save(member);

		Image url = new Image("url", ImageType.PRODUCT, "abcd");
		imageRepository.save(url);
		Promotion promotion = new Promotion("name", url, LocalDateTime.now(), LocalDateTime.now(), true);
		promotionRepository.save(promotion);

		Image urlas = new Image("urlas", ImageType.PROMOTION, "abcd");
		imageRepository.save(urlas);
		Product product = new Product(member, "des", "name", urlas, 1000L, 21000L);
		productRepository.save(product);

		Image urlas1 = new Image("urlas1", ImageType.PROMOTION, "abcd1");
		imageRepository.save(urlas1);
		Product product1 = new Product(member, "des1", "name1", urlas1, 1000L, 21000L);
		productRepository.save(product1);

		PromotionProduct promotionProduct = new PromotionProduct(promotion, product, 100L, 3L,
			DiscountPolicy.FIXED,
			1000L, 20000L);
		promotionProductRepository.save(promotionProduct);

		PromotionProduct promotionProduct1 = new PromotionProduct(promotion, product1, 100L, 3L,
			DiscountPolicy.FIXED,
			1000L, 20000L);
		promotionProductRepository.save(promotionProduct1);
	}

	@Test
	@Disabled
	@DisplayName("동시에 여러 주문이 들어올 때 재고 관리 동시성 테스트")
	void 동시_주문_재고관리_테스트() throws InterruptedException {

		PromotionProduct promotionProduct = promotionProductRepository.findById(1L)
			.orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_PROMOTION_PRODUCT));

		PromotionProduct promotionProduct1 = promotionProductRepository.findById(2L)
			.orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_PROMOTION_PRODUCT));

		Promotion promotion = promotionRepository.findById(1L)
			.orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_PROMOTION));

		int threadCount = 50;
		ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
		CountDownLatch doneLatch = new CountDownLatch(threadCount);

		AtomicInteger successCount = new AtomicInteger(0);
		AtomicInteger failedCount = new AtomicInteger(0);

		List<PromotionProductRequest> request = List.of(
			new PromotionProductRequest(promotionProduct.getId(), 1L),
			new PromotionProductRequest(promotionProduct1.getId(), 1L));

		for (int i = 0; i < threadCount; i++) {
			int num = 1 + i;
			executorService.execute(() -> {
				try {
					Thread.sleep(100 % num * 100);
					redissonLockStockFacade.lockAndDecreaseStock(promotion.getId(), request);
					successCount.incrementAndGet();
				} catch (Exception e) {
					failedCount.incrementAndGet();
					log.error("exception: ", e);
					System.out.println(e.getClass().getSimpleName());
				} finally {
					doneLatch.countDown();
				}
			});
		}

		doneLatch.await();
		executorService.shutdown();

		log.info("successCount: {}", successCount);
		log.info("failedCount: {}", failedCount);
	}
}