package com.jirafik.boot.service;

import com.jirafik.boot.collection.Photo;
import com.jirafik.boot.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.bson.BsonBinarySubType.BINARY;

@RequiredArgsConstructor
@Service
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;

    @Override
    public String addPhoto(String originalFilename, MultipartFile image) throws IOException {
        Photo photo = new Photo();
        photo.setTitle(originalFilename);
        photo.setPhoto(new Binary(BINARY, image.getBytes()));
        return imageRepository.save(photo).getId();
    }

    @Override
    public Photo getPhoto(String id) {
        if (imageRepository.findById(id).isPresent()) return imageRepository.findById(id).get();
        else return null;
    }
}
