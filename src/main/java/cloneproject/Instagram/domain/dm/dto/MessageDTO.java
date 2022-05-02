package cloneproject.Instagram.domain.dm.dto;

import cloneproject.Instagram.domain.dm.entity.MessageImage;
import cloneproject.Instagram.domain.dm.entity.MessagePost;
import cloneproject.Instagram.domain.dm.entity.MessageStory;
import cloneproject.Instagram.domain.dm.entity.MessageText;
import cloneproject.Instagram.domain.member.dto.MemberDTO;
import cloneproject.Instagram.domain.story.entity.Story;
import cloneproject.Instagram.global.vo.Image;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO {

    private Long roomId;
    private Long messageId;
    private MemberDTO sender;
    private Image senderImage;
    private Object content;
    private String messageType;
    private LocalDateTime messageDate;
    private List<MemberDTO> likeMembers = new ArrayList<>();

    public MessageDTO(MessageStory message) {
        this.roomId = message.getRoom().getId();
        this.messageId = message.getId();
        this.sender = new MemberDTO(message.getMember());
        this.senderImage = message.getMember().getImage();
        this.messageDate = message.getCreatedDate();
        this.messageType = message.getDtype();

        String status = "DELETED";
        try {
            final Story story = message.getStory();
            if (story.getUploadDate().isBefore(LocalDateTime.now().minusHours(24)))
                status = "DISABLED";
            else
                status = "ACTIVATE";
            this.content = MessageStoryDTO.builder()
                    .status(status)
                    .storyId(story.getId())
                    .storyImage(story.getImage())
                    .uploader(new MemberDTO(message.getMember()))
                    .build();
        } catch (Exception e) {
            this.content = MessageStoryDTO.builder()
                    .status(status)
                    .build();
        }
    }

    public MessageDTO(MessagePost message) {
        this.roomId = message.getRoom().getId();
        this.messageId = message.getId();
        this.sender = new MemberDTO(message.getMember());
        this.senderImage = message.getMember().getImage();
        this.messageDate = message.getCreatedDate();
        this.messageType = message.getDtype();
        try {
            this.content = MessagePostDTO.builder()
                    .status("UPLOADED")
                    .postId(message.getPost().getId())
                    .postImage(message.getPost().getPostImages().get(0).getImage())
                    .postImageCount(message.getPost().getPostImages().size())
                    .uploader(new MemberDTO(message.getMember()))
                    .content(message.getPost().getContent())
                    .build();
        } catch (Exception e) {
            this.content = MessagePostDTO.builder()
                    .status("DELETED")
                    .build();
        }
    }

    public MessageDTO(MessageText message) {
        this.roomId = message.getRoom().getId();
        this.messageId = message.getId();
        this.sender = new MemberDTO(message.getMember());
        this.senderImage = message.getMember().getImage();
        this.messageDate = message.getCreatedDate();
        this.messageType = message.getDtype();
        this.content = message.getContent();
    }

    public MessageDTO(MessageImage message) {
        this.roomId = message.getRoom().getId();
        this.messageId = message.getId();
        this.sender = new MemberDTO(message.getMember());
        this.senderImage = message.getMember().getImage();
        this.messageDate = message.getCreatedDate();
        this.messageType = message.getDtype();
        this.content = message.getImage();
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MessagePostDTO {

        private String status;
        private Long postId;
        private Image postImage;
        private Integer postImageCount;
        private String content;
        private MemberDTO uploader;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MessageStoryDTO {

        private String status;
        private Long storyId;
        private Image storyImage;
        private MemberDTO uploader;
    }
}
