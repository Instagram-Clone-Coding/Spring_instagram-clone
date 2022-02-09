package cloneproject.Instagram.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import cloneproject.Instagram.entity.member.Member;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@DisplayName("Member Repository")
public class MemberRepositoryTest {
    

    @Autowired
    private MemberRepository memberRepository;
    
    @Nested
    @DisplayName("save 메서드는")
    class Describe_save{

        // TODO querydsl 테스트 추가
        @BeforeEach
        void prepare(){
            memberRepository.deleteAll();
        }

        @Nested
        @DisplayName("멤버 객체가 주어지면")
        class Context_given_member{
            final Member givenMember =  Member.builder()
                                            .username("testusername")
                                            .name("testname")
                                            .password("1234")
                                            .email("123@gmail.com")
                                            .build();
            
            @Test
            @DisplayName("주어진 멤버를 저장한 뒤, 저장된 멤버를 반환한다.")
            void it_save_member_and_return_saved_member(){
                assertNull(givenMember.getId(), "저장전 id는 없어야 한다");
                final Member savedMember = memberRepository.save(givenMember);
                
                assertNotNull(savedMember.getId(), "저장후 id는 존재해야 한다");
                assertEquals(savedMember.getUsername(), givenMember.getUsername(), "저장된 정보가 일치해야 한다");
            }
        }

    }
    
}
