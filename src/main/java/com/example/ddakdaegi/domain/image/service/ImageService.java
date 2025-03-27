package com.example.ddakdaegi.domain.image.service;

import com.example.ddakdaegi.domain.image.dto.response.ImageResponse;
import org.springframework.web.multipart.MultipartFile;


public interface ImageService {

	ImageResponse saveImage(MultipartFile file, String type);

	String upload(MultipartFile file, String type);
}
