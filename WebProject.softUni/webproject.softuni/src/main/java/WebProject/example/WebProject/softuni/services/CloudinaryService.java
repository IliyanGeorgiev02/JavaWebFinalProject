package webproject.example.webproject.softuni.services;

import com.cloudinary.Cloudinary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }
    public String upload(MultipartFile file, String folder) {
        try {
            return upload(file.getBytes(), folder);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public String upload(byte[] file, String folder) {
        try {
            Map data = cloudinary.uploader().upload(file, new HashMap<>());

            return (String) data.get("secure_url");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
