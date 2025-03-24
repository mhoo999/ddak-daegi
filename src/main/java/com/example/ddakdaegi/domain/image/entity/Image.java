package com.example.ddakdaegi.domain.image.entity;

import com.example.ddakdaegi.domain.image.enums.ImageType;
import com.example.ddakdaegi.global.common.entity.Timestamped;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private ImageType type;
}
