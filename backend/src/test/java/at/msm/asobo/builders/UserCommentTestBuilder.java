package at.msm.asobo.builders;

import at.msm.asobo.dto.comment.UserCommentDTO;
import at.msm.asobo.entities.Event;
import at.msm.asobo.entities.User;
import at.msm.asobo.entities.UserComment;
import at.msm.asobo.mappers.UserCommentDTOUserCommentMapper;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.UUID;

public class UserCommentTestBuilder {
    private static final UUID FIXED_COMMENT_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final LocalDateTime FIXED_DATE =  LocalDateTime.of(2026, 1, 1, 12, 0);

    private UUID id;
    private String text;
    private User author;
    private Event event;
    private LocalDateTime creationDate;
    private LocalDateTime modificationDate;

    private final UserCommentDTOUserCommentMapper userCommentDTOUserCommentMapper;

    public UserCommentTestBuilder() {
        this.id = FIXED_COMMENT_ID;
        this.text = "Best comment ever!";
        this.author = defaultAuthor();
        this.event = defaultEvent();
        this.creationDate = FIXED_DATE;
        this.modificationDate = FIXED_DATE.plusDays(1);

        this.userCommentDTOUserCommentMapper = new UserCommentDTOUserCommentMapper();
    }

    public UserCommentTestBuilder withId(UUID id) {
        this.id = id;
        return this;
    }

    public UserCommentTestBuilder withText(String text) {
        this.text = text;
        return this;
    }

    public UserCommentTestBuilder withAuthor(User author) {
        this.author = author;
        return this;
    }

    public UserCommentTestBuilder withEvent(Event event) {
        this.event = event;
        return this;
    }

    public UserCommentTestBuilder withCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    public UserCommentTestBuilder withModificationDate(LocalDateTime modificationDate) {
        this.modificationDate = modificationDate;
        return this;
    }

    public UserComment buildUserComment() {
        UserComment userComment = new UserComment();
        userComment.setId(this.id);
        userComment.setText(this.text);
        userComment.setAuthor(this.author);
        userComment.setEvent(this.event);
        userComment.setCreationDate(this.creationDate);
        userComment.setModificationDate(this.modificationDate);

        return userComment;
    }

    public UserCommentDTO buildUserCommentDTO() {
        UserCommentDTO userCommentDTO = new UserCommentDTO();
        userCommentDTO.setId(this.id);
        userCommentDTO.setText(this.text);
        userCommentDTO.setAuthorId(this.author.getId());
        userCommentDTO.setUsername(this.author.getUsername());
        userCommentDTO.setEventId(this.event.getId());
        userCommentDTO.setCreationDate(this.creationDate);
        userCommentDTO.setModificationDate(this.modificationDate);

        return userCommentDTO;
    }

    public UserCommentTestBuilder fromUserComment(UserComment userComment) {
        this.id = userComment.getId();
        this.text = userComment.getText();
        this.author = userComment.getAuthor();
        this.event = userComment.getEvent();
        this.creationDate = userComment.getCreationDate();
        this.modificationDate = userComment.getModificationDate();
        return this;
    }

    public User defaultAuthor() {
        return new UserTestBuilder()
                .withId(UUID.fromString("00000000-0000-0000-0000-000000000002"))
                .withUsernameAndEmail("author")
                .buildUserEntity();
    }

    public Event defaultEvent() {
        return new EventTestBuilder()
                .withId(UUID.randomUUID())
                .withCreator(defaultAuthor())
                .withEventAdmins(new HashSet<>())
                .buildEventEntity();
    }
}
