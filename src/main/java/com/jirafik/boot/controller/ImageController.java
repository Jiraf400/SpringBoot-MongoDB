package com.jirafik.boot.controller;

import com.jirafik.boot.collection.Photo;
import com.jirafik.boot.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM;


@RestController
@RequestMapping("/image")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService service;

    @PostMapping
    public String addImage(@RequestParam("image") MultipartFile file) throws IOException {

        String id = service.addPhoto(file.getOriginalFilename(), file);
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> downloadImage(@PathVariable String id) {
        Photo photo = service.getPhoto(id);
        Resource resource = new ByteArrayResource(photo.getPhoto().getData());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + photo.getTitle() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}













