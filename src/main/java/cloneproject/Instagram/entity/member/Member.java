package cloneproject.Instagram.entity.member;

import javax.persistence.*;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import cloneproject.Instagram.vo.Image;
import cloneproject.Instagram.vo.ImageType;
import cloneproject.Instagram.vo.RefreshToken;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "members")
public class Member {

    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_username", nullable = false, length = 20, unique = true)
    private String username;
    
    @Column(name = "member_role")
    @Enumerated(EnumType.STRING)
    private MemberRole role;

    @Column(name = "member_password", nullable = false)
    private String password;
    
    @Column(name = "member_name", nullable = false, length = 20)
    private String name;

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

    @OneToMany(mappedBy = "member")
    private List<Follow> followings = new ArrayList<>();
    
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "member_refresh_token_value")),
            @AttributeOverride(name = "createdAt", column = @Column(name = "member_refresh_token_created_At")),
    })
    private RefreshToken refreshToken;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "imageUrl", column = @Column(name = "member_image_url")),
            @AttributeOverride(name = "imageType", column = @Column(name = "member_image_type")),
            @AttributeOverride(name = "imageName", column = @Column(name = "member_image_name")),
            @AttributeOverride(name = "imageUUID", column = @Column(name = "member_image_uuid"))
    })
    private Image image;

    public void updateUsername(String username){
        this.username = username;
    }

    public void updateName(String name){
        this.name = name;
    }

    public void updateWebsite(String website){
        this.website = website;
    }

    public void updateIntroduce(String introduce){
        this.introduce = introduce;
    }

    public void updatePhone(String phone){
        this.phone = phone;
    }

    public void updateEmail(String email){
        this.email = email;
    }

    public void updateGender(Gender gender){
        this.gender = gender;
    }

    public void setRefreshToken(RefreshToken refreshToken){
        this.refreshToken = refreshToken;
    }

    public void setEncryptedPassword(String encryptedPassword){
        this.password = encryptedPassword;
    }

    public void uploadImage(Image image){
        deleteImage();
        this.image = image;
    }

    public void deleteImage(){
        if(this.image.getImageUUID().equals("base-UUID"))
            return;
        File file = new File(this.image.getImageUrl() + "\\" + this.image.getImageUUID());

        if(file.exists()){
            file.delete();
        }

        this.image = Image.builder()
                .imageName("base")
                .imageType(ImageType.PNG)
                .imageUrl("https://drive.google.com/file/d/1Gu0DcGCJNs4Vo0bz2U9U6v01d_VwKijs/view?usp=sharing")
                .imageUUID("base-UUID")
                .build();
    }

    @Builder
    public Member(String username, String name, String password, String phone){
        this.username = username;
        this.name = name;
        this.password = password;
        this.phone = phone;
        
        // 자동 초기화
        this.role = MemberRole.ROLE_USER;
        this.gender = Gender.PRIVATE;
        this.image = Image.builder()
                .imageName("base")
                .imageType(ImageType.PNG)
                .imageUrl("https://drive.google.com/file/d/1Gu0DcGCJNs4Vo0bz2U9U6v01d_VwKijs/view?usp=sharing")
                .imageUUID("base-UUID")
                .build();
    }
    
}
