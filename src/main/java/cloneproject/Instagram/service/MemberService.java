package cloneproject.Instagram.service;



import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cloneproject.Instagram.dto.JwtDto;
import cloneproject.Instagram.dto.LoginRequest;
import cloneproject.Instagram.dto.RegisterRequest;
import cloneproject.Instagram.dto.ReissueRequest;
import cloneproject.Instagram.entity.member.Member;
import cloneproject.Instagram.exception.InvalidJwtException;
import cloneproject.Instagram.exception.MemberDoesNotExistException;
import cloneproject.Instagram.exception.UseridAlreadyExistException;
import cloneproject.Instagram.repository.MemberRepository;
import cloneproject.Instagram.util.JwtUtil;
import cloneproject.Instagram.vo.RefreshToken;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtUtil jwtUtil;

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public void register(RegisterRequest registerRequest){
        if(memberRepository.findByUsername(registerRequest.getUsername()).isPresent()){
            throw new UseridAlreadyExistException();
        }
        Member member = registerRequest.convert();
        String encryptedPassword = bCryptPasswordEncoder.encode(member.getPassword());
        member.setEncryptedPassword(encryptedPassword);
        memberRepository.save(member);
    }

    @Transactional
    public JwtDto login(LoginRequest loginRequest){
        UsernamePasswordAuthenticationToken authenticationToken = 
            new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        JwtDto jwtDto = jwtUtil.generateTokenDto(authentication);
        RefreshToken refreshToken = RefreshToken.builder()
                                            .value(jwtDto.getRefreshToken())
                                            .build();
        Member member = memberRepository.findById(Long.valueOf(authentication.getName()))
                                        .orElseThrow(() -> new MemberDoesNotExistException());
        member.setRefreshToken(refreshToken);
        memberRepository.save(member);
        
        return jwtDto;
    }

    @Transactional
    public JwtDto reisuue(ReissueRequest reissueRequest){
        String accessTokenString = reissueRequest.getAccessToken();
        String refreshTokenString = reissueRequest.getRefreshToken();
        if(!jwtUtil.validateRefeshJwt(refreshTokenString)){
            throw new InvalidJwtException();
        }
        Authentication authentication = jwtUtil.getAuthentication(accessTokenString);
        Member member = memberRepository.findById(Long.valueOf(authentication.getName()))
                                        .orElseThrow(() -> new MemberDoesNotExistException());
        RefreshToken refreshToken = member.getRefreshToken();
        if(!refreshToken.getValue().equals(refreshTokenString)){
            throw new InvalidJwtException();
        }
        
        JwtDto jwtDto = jwtUtil.generateTokenDto(authentication);

        refreshToken.updateTokenValue(jwtDto.getRefreshToken());
        memberRepository.save(member);

        return jwtDto;
    }

    // ! login 권한 테스트를 위해 임시로 만든 메서드입니다. 추후에 삭제
    public Member info(String memberId){
        return memberRepository.getById(Long.valueOf(memberId));
    }

}
