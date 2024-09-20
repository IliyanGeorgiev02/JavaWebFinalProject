package webproject.example.webproject.softuni.model;

import jakarta.persistence.*;
import webproject.example.webproject.softuni.model.enums.UserRoleEnum;

import java.util.*;

@Entity
@Table(name = "users")
public class User extends BaseEntity {
    @Column(unique = true, nullable = false)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(name = "first_name", nullable = false)
    private String firstName;
    @Column(name = "last_name", nullable = false)
    private String lastName;
    @Column(nullable = false)
    private String email;
    private String bio;
    @Column(name = "profile_Picture")
    private String profilePicture;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRoleEnum role;
    @OneToMany(mappedBy = "user")
    private List<CustomList> lists;
    @OneToMany(mappedBy = "user")
    private List<Comment> comments;
    @Column
    @ElementCollection
    private Set<Long> review_ids;
    @OneToMany(mappedBy = "liked")
    private Set<Like> likes;

    public User() {
        this.lists = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.review_ids = new HashSet<>();
        this.likes = new HashSet<>();
    }

    public Set<Like> getLikes() {
        return likes;
    }

    public void setLikes(Set<Like> likes) {
        this.likes = likes;
    }

    public List<CustomList> getLists() {
        return lists;
    }

    public void setLists(List<CustomList> lists) {
        this.lists = lists;
    }

    public UserRoleEnum getRole() {
        return role;
    }

    public void setRole(UserRoleEnum role) {
        this.role = role;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public Set<Long> getReview_ids() {
        return review_ids;
    }

    public void setReview_ids(Set<Long> review_ids) {
        this.review_ids = review_ids;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        User user = (User) o;
        return Objects.equals(username, user.username) && Objects.equals(password, user.password) && Objects.equals(firstName, user.firstName) && Objects.equals(lastName, user.lastName) && Objects.equals(email, user.email) && Objects.equals(bio, user.bio) && Objects.equals(profilePicture, user.profilePicture) && role == user.role && Objects.equals(lists, user.lists) && Objects.equals(comments, user.comments) && Objects.equals(review_ids, user.review_ids) && Objects.equals(likes, user.likes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), username, password, firstName, lastName, email, bio, profilePicture, role, lists, comments, review_ids, likes);
    }
}
