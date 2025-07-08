package com.digiquad.dealkaro.utility;

import com.digiquad.dealkaro.exceptions.customExceptions.ImageNotSavedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Component("imageHandler")
public class ImageHandler {

    @Value("${upload.dir}")
    private String imageStoragePath;

    public String handleImage(MultipartFile MultipartFile) throws ImageNotSavedException {
        try {
            if (MultipartFile.isEmpty()) {
                throw new IllegalArgumentException("Profile image is required");
            }

            // Get the original file name
            String fileName = MultipartFile.getOriginalFilename();
            if (fileName == null) {
                throw new IllegalArgumentException("Invalid file name");
            }
            fileName = System.currentTimeMillis() + "_" + fileName;
            Path path = Paths.get(imageStoragePath, fileName);
            Files.copy(MultipartFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ImageNotSavedException("Error while saving the image");
        }
    }
}
