package com.example.ddakdaegi.domain.image.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.ddakdaegi.domain.image.dto.response.ImageResponse;
import com.example.ddakdaegi.domain.image.entity.Image;
import com.example.ddakdaegi.domain.image.enums.ImageType;
import com.example.ddakdaegi.domain.image.repository.ImageRepository;
import com.example.ddakdaegi.global.common.exception.BaseException;
import com.example.ddakdaegi.global.common.exception.enums.ErrorCode;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

@ExtendWith(MockitoExtension.class)
class S3ImageServiceTest {

	@InjectMocks
	private S3ImageService s3ImageService;

	@Mock
	private S3Client s3Client;

	@Mock
	private ImageRepository imageRepository;

	@Test
	void saveImage_성공() {
		//given
		MockMultipartFile mockFile = new MockMultipartFile(
			"file",
			"test.png",
			MediaType.IMAGE_PNG_VALUE,
			"test image content".getBytes()
		);

		ImageType imageType = ImageType.PRODUCT;
		String fileName = "PRODUCT/test.png";
		String imageUrl = "https://test-bucket.s3.ap-northeast-2.amazonaws.com/" + fileName;

		Image savedImage = new Image(imageUrl, imageType, fileName);
		ReflectionTestUtils.setField(savedImage, "id", 1L);

		given(imageRepository.save(any(Image.class))).willReturn(savedImage);

		// when
		ImageResponse response = s3ImageService.saveImage(mockFile, "product");

		// then
		assertEquals(1L, response.getImageId());
		assertEquals(imageUrl, response.getImageUrl());
		assertEquals(fileName, response.getImageName());
	}

	@Test
	void deleteImage_성공() {
		// given
		Long imageId = 1L;
		String fileName = "profile/123456789_test.png";
		Image image = new Image("url", ImageType.PRODUCT, fileName);
		ReflectionTestUtils.setField(image, "id", imageId);

		given(imageRepository.findById(imageId)).willReturn(Optional.of(image));

		// when
		s3ImageService.deleteImage(imageId);

		// then
		verify(s3Client, times(1)).deleteObject(any(DeleteObjectRequest.class));
		verify(imageRepository, times(1)).delete(image);
	}

	@Test
	void saveImage_이미지아닐경우_예외발생() {
		// given
		MockMultipartFile mockFile = new MockMultipartFile(
			"file",
			"test.txt",
			MediaType.TEXT_PLAIN_VALUE,
			"not an image".getBytes()
		);

		// when & then
		BaseException exception = assertThrows(BaseException.class,
			() -> s3ImageService.saveImage(mockFile, "product"));

		assertEquals(ErrorCode.INVALID_IMAGE_TYPE, exception.getErrorCode());
	}
}