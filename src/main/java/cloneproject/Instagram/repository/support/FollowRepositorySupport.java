// package cloneproject.Instagram.repository.support;

// import java.util.List;

// import com.querydsl.core.types.Projections;
// import com.querydsl.jpa.impl.JPAQueryFactory;

// import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

// import cloneproject.Instagram.entity.member.Follow;
// import lombok.RequiredArgsConstructor;

// public class FollowRepositorySupport extends QuerydslRepositorySupport{

//     private final JPAQueryFactory jpaQueryFactory;

//     public FollowRepositorySupport(JPAQueryFactory jpaQueryFactory){
//         super(Follow.class);
//         this.jpaQueryFactory = jpaQueryFactory;
//     }
    
//     public List<String> findFollowsById(String id){
//         return jpaQueryFactory
//                     .select(Projections.fields(Follow.class, ))
//     }
// }
