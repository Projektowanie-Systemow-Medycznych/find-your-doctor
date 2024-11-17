package com.example.controller;

import com.example.model.Picture;
import com.example.repository.PictureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.UUID;

@Controller
public class PictureController {
    @Autowired
    private PictureRepository pictureRepository;

    @GetMapping("/pictures/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getPicture(@PathVariable("id") UUID id) {
        Picture picture = pictureRepository.findById(id).orElse(null);
        if (picture != null) {
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(picture.getData());
        }
        return ResponseEntity.notFound().build();
    }

}
