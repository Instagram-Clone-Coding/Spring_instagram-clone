package cloneproject.Instagram.global.util;

import cloneproject.Instagram.domain.feed.dto.MemberPostDTO;
import cloneproject.Instagram.domain.feed.entity.Post;

public class PostUtil {
    
    public static MemberPostDTO convertPostToMemberPostDTO(Post post){
        return MemberPostDTO.builder()
                            .postId(post.getId())
                            // .postImage(post.getPostImages().get(0).getImage())
                            .hasManyPosts((post.getPostImages().size() > 1 ? true : false))
                            .postLikesCount(post.getPostLikes().size())
                            .postCommentsCount(post.getComments().size())
                            .build();
    }
    
}
