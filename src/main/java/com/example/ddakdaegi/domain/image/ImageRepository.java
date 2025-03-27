package com.example.ddakdaegi.domain.image;

import com.example.ddakdaegi.domain.image.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {

}
