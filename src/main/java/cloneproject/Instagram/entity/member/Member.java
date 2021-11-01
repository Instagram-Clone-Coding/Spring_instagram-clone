package cloneproject.Instagram.entity.member;

import javax.persistence.*;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "members")
public class Member {

    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_userid", nullable = false, length = 20, unique = true)
    private String userid;

    @Column(name = "member_username", nullable = false, length = 20)
    private String username;

    @Column(name = "member_password", nullable = false)
    private String password;

    @Column(name = "member_website")
    private String website;

    @Lob
    @Column(name = "member_introduce")
    private String introduce;

    @Column(name = "member_email")
    private String email;

    @Column(name = "member_phone")
    private String phone;

    @Column(name = "member_gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "member_image_url")
    private String imageUrl;

    @Column(name = "member_image_type")
    @Enumerated(EnumType.STRING)
    private ImageType imageType;

    @Column(name = "member_image_name")
    private String imageName;

    @Column(name = "member_image_uuid")
    private String imageUUID;

    @Builder
    public Member(String userid, String username, String password, String phone){
        this.userid = userid;
        this.username = username;
        this.password = password;
        this.phone = phone;
        
        // 자동 초기화
        this.gender = Gender.PRIVATE;
        this.imageUrl = "https://drive.google.com/file/d/1Gu0DcGCJNs4Vo0bz2U9U6v01d_VwKijs/view?usp=sharing";
        this.imageName = "BaseImageName";
        this.imageType = ImageType.PNG;
        this.imageUUID = "BaseImageUUID";
    }
}
