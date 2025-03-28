package com.example.ddakdaegi.domain.image.dto.response;

import com.example.ddakdaegi.domain.image.entity.Image;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ImageResponse {

	private final Long imageId;
	private final String imageUrl;
	private final String imageName;

	public static ImageResponse from(Image image) {
		return new ImageResponse(image.getId(), image.getImageUrl(), image.getFileName());
	}
}
