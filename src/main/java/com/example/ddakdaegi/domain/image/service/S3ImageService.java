package com.example.ddakdaegi.domain.image.service;

import static com.example.ddakdaegi.global.common.exception.enums.ErrorCode.FAIL_UPLOAD_IMAGE;
import static com.example.ddakdaegi.global.common.exception.enums.ErrorCode.INVALID_IMAGE_TYPE;
import static org.springframework.util.MimeTypeUtils.IMAGE_JPEG_VALUE;
import static org.springframework.util.MimeTypeUtils.IMAGE_PNG_VALUE;

import com.example.ddakdaegi.domain.image.dto.response.ImageResponse;
import com.example.ddakdaegi.domain.image.entity.Image;
import com.example.ddakdaegi.domain.image.enums.ImageType;
import com.example.ddakdaegi.domain.image.repository.ImageRepository;
import com.example.ddakdaegi.global.common.exception.BaseException;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3ImageService implements ImageService {

	private final S3Client s3Client;
	private final ImageRepository imageRepository;

	@Value("${cloud.aws.s3.bucket:}")
	private String bucket;

	@Value("${cloud.aws.region.static}")
	private String region;

	@Override
	public ImageResponse saveImage(MultipartFile file, String type) {

		ImageType imageType = ImageType.valueOf(type.toUpperCase());
		String fileName = upload(file, type);
		String imageUrl = convertToUrl(fileName);

		Image image = new Image(imageUrl, imageType, fileName);
		Image savedImage = imageRepository.save(image);

		return new ImageResponse(savedImage.getId(), savedImage.getImageUrl());
	}

	@Override
	public void deleteImage(Long imageId) {
		Image image = imageRepository.findById(imageId).orElseGet(null);

		deleteImage(image.getFileName());

		imageRepository.delete(image);
	}

	private void deleteImage(String s3ImageKey) {
		DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
			.bucket(bucket)
			.key(s3ImageKey)
			.build();
		s3Client.deleteObject(deleteObjectRequest);
	}

	private String upload(MultipartFile file, String type) {
		String s3ImageKey = generateKey(file.getOriginalFilename(), type);
		String contentType = file.getContentType();

		if (!isImageType(contentType)) {
			log.info("=== 이미지 파일이 아님 ===");
			throw new BaseException(INVALID_IMAGE_TYPE);
		}

		PutObjectRequest request = PutObjectRequest.builder()
			.bucket(bucket)
			.key(s3ImageKey)
			.contentType(contentType)
			.build();

		try {
			s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));
			return s3ImageKey;
		} catch (IOException e) {
			log.error("Failed to upload file", e);
			throw new BaseException(FAIL_UPLOAD_IMAGE);
		}
	}

	private String generateKey(String fileName, String type) {
		return String.format("%s/%d_%s", type, System.currentTimeMillis(), fileName);
	}

	private boolean isImageType(String type) {
		return type.equals(IMAGE_PNG_VALUE) || type.equals(IMAGE_JPEG_VALUE);
	}

	private String convertToUrl(String s3ImageKey) {
		return String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, s3ImageKey);
	}
}
