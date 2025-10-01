package org.example.hirehub.service;

import com.cloudinary.utils.ObjectUtils;
import com.cloudinary.Cloudinary;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadAndGetUrl(MultipartFile file) throws IOException {
        Map result = cloudinary.uploader()
                .upload(file.getBytes(), ObjectUtils.emptyMap());

        return (String) result.get("secure_url");
     }
}
