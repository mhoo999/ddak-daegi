package com.example.ddakdaegi.domain.image.controller;

import com.example.ddakdaegi.domain.image.dto.response.ImageResponse;
import com.example.ddakdaegi.domain.image.service.ImageService;
import com.example.ddakdaegi.global.common.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ImageController {

	private final ImageService imageService;

	@PostMapping("/v1/images")
	public Response<ImageResponse> saveImage(@RequestPart("file") MultipartFile image,
		@RequestPart("type") String type) {
		ImageResponse imageResponse = imageService.saveImage(image, type);

		return Response.of(imageResponse);
	}

}
