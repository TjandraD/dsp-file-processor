package com.tjandra.dspfileprocessor.controller;

import com.tjandra.dspfileprocessor.controller.dto.ProcessImagesRequestDTO;
import com.tjandra.dspfileprocessor.service.ImageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping(value = "/api/v1/process-images/")
    public ResponseEntity<String> processImages(@RequestBody ProcessImagesRequestDTO processImagesRequestDTO) {
        imageService.processImages(processImagesRequestDTO.getFolderPath());

        return ResponseEntity.noContent().build();
    }
}
