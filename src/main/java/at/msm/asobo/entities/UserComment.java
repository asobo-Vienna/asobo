package at.msm.asobo.entities;

import java.time.LocalDateTime;

public class UserComment {

    private String text;
    private User user;
    private LocalDateTime creationDate;
    private LocalDateTime modificationDate;
    // private File file;

    public UserComment(){

    }

    public UserComment(String text, User user) {
        this.text = text;
        this.user = user;
        this.creationDate = LocalDateTime.now();
        this.modificationDate = null;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(LocalDateTime modificationDate) {
        this.modificationDate = modificationDate;
    }
}
