package cloneproject.Instagram.dto;

import com.mysql.cj.protocol.Message;

import lombok.Getter;

@Getter
public class MessageResponse {
    
    private String message;

    public MessageResponse(String message){
        this.message = message;
    }

}
