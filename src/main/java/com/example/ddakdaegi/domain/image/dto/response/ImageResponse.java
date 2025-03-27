package com.example.ddakdaegi.domain.image.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ImageResponse {

	private final Long imageId;
	private final String imageUrl;
}
