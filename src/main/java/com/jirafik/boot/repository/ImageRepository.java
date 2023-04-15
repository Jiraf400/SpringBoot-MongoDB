package com.jirafik.boot.repository;

import com.jirafik.boot.collection.Photo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends MongoRepository<Photo, String> {
}
