package cloneproject.Instagram.exception;

public class UseridAlreadyExistException extends RuntimeException{
        public UseridAlreadyExistException(){
            super("이미 존재하는 ID 입니다");
        }
}
