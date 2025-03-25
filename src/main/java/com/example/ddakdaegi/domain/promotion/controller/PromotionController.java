package com.example.ddakdaegi.domain.promotion.controller;

import com.example.ddakdaegi.domain.promotion.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api")
@RequiredArgsConstructor
public class PromotionController {

	private final PromotionService promotionService;
}
