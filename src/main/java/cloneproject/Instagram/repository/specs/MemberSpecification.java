package cloneproject.Instagram.repository.specs;

import org.springframework.data.jpa.domain.Specification;

import cloneproject.Instagram.entity.member.Member;

public class MemberSpecification {
    
    public static Specification<Member> containsTextInUsernameOrName(String text){
        String finalText = "%" + text+ "%";
        return (root, query, builder) -> builder.or(
                builder.like(root.get("username"), finalText),
                builder.like(root.get("name"), finalText)
        );

    }

}
