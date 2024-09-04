package webproject.example.webproject.softuni.services;

import org.springframework.stereotype.Service;
import webproject.example.webproject.softuni.model.Like;
import webproject.example.webproject.softuni.model.User;

import java.util.Set;

@Service
public class LikeService {

    public boolean containsUser(Set<Like> likeSet, User user){
        return likeSet.stream().anyMatch(like -> like.getLiked().equals(user));
    }
}
