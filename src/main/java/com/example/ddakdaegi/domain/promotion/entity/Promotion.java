package com.example.ddakdaegi.domain.promotion.entity;

import com.example.ddakdaegi.domain.image.entity.Image;
import com.example.ddakdaegi.domain.promotion.dto.request.UpdatePromotionRequest;
import com.example.ddakdaegi.global.common.entity.Timestamped;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Promotion extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToOne
    @JoinColumn(name = "banner_id")
    private Image banner;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private Boolean isActive;

    public Promotion(String name, Image banner, LocalDateTime startTime, LocalDateTime endTime, Boolean isActive) {
        this.name = name;
        this.banner = banner;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isActive = isActive;
    }

    public static Promotion create(String name, Image banner, LocalDateTime start, LocalDateTime end) {
        Promotion promotion = new Promotion(name, banner, start, end, false);
        promotion.updateIsActive();
        return promotion;
    }

    public void update(String name, Image newBannerImage, LocalDateTime startDate, LocalDateTime endDate) {
        if (name != null) {
            this.name = name;
        }
        if (newBannerImage != null) {
            this.banner = newBannerImage;
        }
        if (startDate != null) {
            this.startTime = startDate;
        }
        if (endDate != null) {
            this.endTime = endDate;
        }
        updateIsActive();
    }

    private void updateIsActive() {
        if (this.startTime != null && this.endTime != null) {
            this.isActive = !LocalDateTime.now().isBefore(this.startTime)
                && !LocalDateTime.now().isAfter(this.endTime);
        }
    }
}
