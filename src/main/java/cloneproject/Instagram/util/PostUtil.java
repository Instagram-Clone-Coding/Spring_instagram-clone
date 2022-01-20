package cloneproject.Instagram.util;

import cloneproject.Instagram.dto.post.MemberPostDTO;
import cloneproject.Instagram.entity.post.Post;

public class PostUtil {
    
    public static MemberPostDTO convertPostToMemberPostDTO(Post post){
        return MemberPostDTO.builder()
                            .postId(post.getId())
                            .postImage(post.getPostImages().get(0).getImage())
                            .hasManyPosts((post.getPostImages().size() > 1 ? true : false))
                            .postLikesCount(post.getPostLikes().size())
                            .postCommentsCount(post.getComments().size())
                            .build();
    }
    
}
