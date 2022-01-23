package cloneproject.Instagram.util;

import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtil {
    
    public static Long getLoginedMemberIdOrNull(){
        try{
            final String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
            return Long.valueOf(memberId);
        }catch(Exception e){
            return -1L;
        }
    }

}
