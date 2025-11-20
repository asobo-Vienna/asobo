package at.msm.asobo.controllers;

import at.msm.asobo.dto.comment.UserCommentDTO;
import at.msm.asobo.services.UserCommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/comments")
public class UserCommentControllerAdmin {
    private UserCommentService userCommentService;

    public UserCommentControllerAdmin(UserCommentService userCommentService) {
        this.userCommentService = userCommentService;
    }

    @GetMapping
    public List<UserCommentDTO> getAllUserComments() {
        return this.userCommentService.getAllUserComments();
    }
}
