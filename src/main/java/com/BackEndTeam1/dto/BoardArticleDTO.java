package com.BackEndTeam1.dto;

import com.BackEndTeam1.entity.Board;
import com.BackEndTeam1.entity.BoardArticle;
import com.BackEndTeam1.entity.User;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardArticleDTO {

    private int id;
    private String title;
    private String content;
    private String boardName;
    private String createdAt;
    private String updatedAt;
    private String userName;
    private String userId; // 작성자 ID 추가
    private String trashDate;
    private String deletedBy;
    private String status = "active";
    private Boolean isImportant;
    private List<BoardFileDTO> files;

    private Boolean mustRead;
    private Boolean notification;


    private List<Long> ids;

    public BoardArticleDTO(Long id, String title, String content, String boardName, String createdAt, String updatedAt, String userName, String userId, String trashDate, User deletedBy, String status, Boolean isImportant, Boolean mustRead, Boolean notification) {
        this.id = Math.toIntExact(id);
        this.title = title;
        this.content = content;
        this.boardName = boardName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.userName = userName;
        this.userId = userId;
        this.trashDate = trashDate;
        this.deletedBy = deletedBy != null ? deletedBy.getUsername() : "Unknown"; // 적절히 변환
        this.status = status != null ? status : "active";
        this.isImportant = isImportant != null ? isImportant : true;
        this.mustRead = mustRead != null ? mustRead : true;
        this.notification = notification != null ? notification : true;
    }

    public BoardArticleDTO(BoardArticle article) {
        this.id = Math.toIntExact(article.getId());
        this.title = article.getTitle();
        this.content = article.getContent();
        this.boardName = article.getBoard().getBoardName();
        this.createdAt = article.getCreatedAt().toString();
        this.updatedAt = article.getUpdatedAt().toString();
        this.userName = article.getAuthor().getUsername();
        this.userId = article.getAuthor().getUserId().toString();
        this.trashDate = article.getTrashDate() != null ? article.getTrashDate().toString() : null;
        this.deletedBy = article.getDeletedBy() != null ? article.getDeletedBy().getUsername() : null;
        this.status = article.getStatus();
        this.isImportant = article.getMustRead(); // 필독 여부를 isImportant에 매핑
        this.mustRead = article.getMustRead();
        this.notification = article.getNotification();

    }
}