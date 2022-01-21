<div id="top"></div>
<!--
*** Thanks for checking out the Best-README-Template. If you have a suggestion
*** that would make this better, please fork the repo and create a pull request
*** or simply open an issue with the tag "enhancement".
*** Don't forget to give the project a star!
*** Thanks again! Now go create something AMAZING! :D
-->

<!-- PROJECT SHIELDS -->
<!--
*** I'm using markdown "reference style" links for readability.
*** Reference links are enclosed in brackets [ ] instead of parentheses ( ).
*** See the bottom of this document for the declaration of the reference variables
*** for contributors-url, forks-url, etc. This is an optional, concise syntax you may use.
*** https://www.markdownguide.org/basic-syntax/#reference-style-links
-->
[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![Pull Requests][pr-shield]][pr-url]
[![MIT License][license-shield]][license-url]
<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/Instagram-Clone-Coding">
    <img src="https://avatars.githubusercontent.com/u/90607105?s=200&v=4" alt="Logo" width="80" height="80">
  </a>

  <h3 align="center">BE-Instagram-Clone</h3>

  <p align="center">
    인스타그램 클론코딩 프로젝트의 backend 부분 github입니다.
    <br />
    <a href="https://github.com/Instagram-Clone-Coding"><strong>Explore the Organization</strong></a>
    <br />
    <br />
    <!-- <a href="https://github.com/othneildrew/Best-README-Template">View Demo</a> -->
    <!-- · -->
    <a href="https://github.com/Instagram-Clone-Coding/Spring_instagram-clone/issues/new">Report Bug</a>
    ·
    <a href="https://github.com/Instagram-Clone-Coding/Spring_instagram-clone/issues/new">Request Feature</a>
  </p>
</div>

<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <!-- <a href="#about-the-project">About The Project</a> -->
      <a href="#built-with">Built With</a>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#convention">Convention</a></li>
        <li><a href="#commit-convention">Commit Convention</a></li>
      </ul>
    </li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
    <li><a href="#acknowledgments">Acknowledgments</a></li>
  </ol>
</details>

<!-- ABOUT THE PROJECT -->

<!-- ## About The Project

[![Product Name Screen Shot][product-screenshot]](https://example.com)

There are many great README templates available on GitHub; however, I didn't find one that really suited my needs so I created this enhanced one. I want to create a README template so amazing that it'll be the last one you ever need -- I think this is it.

Here's why:

-   Your time should be focused on creating something amazing. A project that solves a problem and helps others
-   You shouldn't be doing the same tasks over and over like creating a README from scratch
-   You should implement DRY principles to the rest of your life :smile:

Of course, no one template will serve all projects since your needs may be different. So I'll be adding more in the near future. You may also suggest changes by forking this repo and creating a pull request or opening an issue. Thanks to all the people have contributed to expanding this template!

Use the `BLANK_README.md` to get started.

<p align="right">(<a href="#top">back to top</a>)</p> -->

### Built With

<b>Backend</b>

-   [Spring Boot](https://spring.io/projects/spring-boot)
-   [Spring Security](https://spring.io/projects/spring-security)
-   [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
-   [Spring Data Redis](https://spring.io/projects/spring-data-redis)
-   [Spring WebSocket](https://spring.io/guides/gs/messaging-stomp-websocket/)
-   [Springfox Swagger UI](http://springfox.github.io/springfox/docs/current/)
-   [JSON Web Token](https://jwt.io/)
-   [Querydsl](http://querydsl.com/)
-   [MySQL](https://www.mysql.com/)
-   [Amazon Web Services](https://aws.amazon.com/)

<p align="right">(<a href="#top">back to top</a>)</p>

<!-- GETTING STARTED -->

## Getting Started

### Convention

-   Conding Convention

1. 통일된 Error Response 객체
    - Error Response JSON
        ```json
        {
          "message": "Invalid Input Value",
          "status": 400,
          "errors": [
          {
            "field": "name.last",
            "value": "",
            "reason": "must not be empty"
          },
          {
              "field": "name.first",
              "value": "",
              "reason": "must not be empty"
            }
          ],
          "code": "C001"
        }
        ```
        - message : 에러에 대한 message를 작성합니다.
        - status : http status code를 작성합니다.
        - errors : 요청 값에 대한 field, value, reason 작성합니다. 일반적으로 @Validated 어노테이션으로 Bean Validation에 대한 검증을 진행 합니다.
          - 만약 errors에 binding된 결과가 없을 경우 null이 아니라 빈 배열 []을 응답합니다.
        - code : 에러에 할당되는 유니크한 코드 값입니다.
    - Error Response 객체
        ```java
        @Getter
        @NoArgsConstructor(access = AccessLevel.PROTECTED)
        public class ErrorResponse {

            private String message;
            private int status;
            private List<FieldError> errors;
            private String code;
            ...

            @Getter
            @NoArgsConstructor(access = AccessLevel.PROTECTED)
            public static class FieldError {
                private String field;
                private String value;
                private String reason;
                ...
            }
        }
        ```
2. Error Code 정의
    ```java
    public enum ErrorCode {

        // Common
        INVALID_INPUT_VALUE(400, "C001", " Invalid Input Value"),
        METHOD_NOT_ALLOWED(405, "C002", " Invalid Input Value"),
        ....
        HANDLE_ACCESS_DENIED(403, "C006", "Access is Denied"),

        // Member
        EMAIL_DUPLICATION(400, "M001", "Email is Duplication"),
        LOGIN_INPUT_INVALID(400, "M002", "Login input is invalid"),

        ;
        private final String code;
        private final String message;
        private int status;

        ErrorCode(final int status, final String code, final String message) {
            this.status = status;
            this.message = message;
            this.code = code;
        }
    }
    ```
3. 비즈니스 예외를 위한 최상위 BusinessException 클래스

    ```java
    @Getter
    public class BusinessException extends RuntimeException {

        private ErrorCode errorCode;
        private List<ErrorResponse.FieldError> errors = new ArrayList<>();

        public BusinessException(String message, ErrorCode errorCode) {
            super(message);
            this.errorCode = errorCode;
        }

        public BusinessException(ErrorCode errorCode) {
            super(errorCode.getMessage());
            this.errorCode = errorCode;
        }

        public BusinessException(ErrorCode errorCode, List<ErrorResponse.FieldError> errors) {
            super(errorCode.getMessage());
            this.errors = errors;
            this.errorCode = errorCode;
        }
    }
    ```
    - 모든 비지니스 예외는 BusinessException을 상속 받고, 하나의 BusinessException handler 메소드로 한 번에 처리합니다.

4. @RestControllerAdvice로 모든 예외를 핸들링

    ```java
    @RestControllerAdvice
    public class GlobalExceptionHandler {

        @ExceptionHandler
        protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
            final ErrorResponse response = ErrorResponse.of(INVALID_INPUT_VALUE, e.getBindingResult());
            return new ResponseEntity<>(response, BAD_REQUEST);
        }

        @ExceptionHandler
        protected ResponseEntity<ErrorResponse> handleBindException(BindException e) {
            final ErrorResponse response = ErrorResponse.of(INVALID_INPUT_VALUE, e.getBindingResult());
            return new ResponseEntity<>(response, BAD_REQUEST);
        }

        @ExceptionHandler
        protected ResponseEntity<ErrorResponse> handleMissingServletRequestPartException(MissingServletRequestPartException e) {
            final ErrorResponse response = ErrorResponse.of(NO_POST_IMAGE);
            return new ResponseEntity<>(response, BAD_REQUEST);
        }

        @ExceptionHandler
        protected ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
            final ErrorResponse response = ErrorResponse.of(e);
            return new ResponseEntity<>(response, BAD_REQUEST);
        }

        @ExceptionHandler
        protected ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
            final ErrorResponse response = ErrorResponse.of(INVALID_INPUT_VALUE);
            return new ResponseEntity<>(response, BAD_REQUEST);
        }

        @ExceptionHandler
        protected ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
            final ErrorResponse response = ErrorResponse.of(METHOD_NOT_ALLOWED);
            return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
        }

        @ExceptionHandler
        protected ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
            final ErrorCode errorCode = e.getErrorCode();
            final ErrorResponse response = ErrorResponse.of(errorCode, e.getErrors());
            return new ResponseEntity<>(response, HttpStatus.valueOf(errorCode.getStatus()));
        }

        @ExceptionHandler
        protected ResponseEntity<ErrorResponse> handleException(Exception e) {
            final ErrorResponse response = ErrorResponse.of(INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    ```

5. 통일된 Result Response 객체
    - Result Response JSON
        ```json
        {
            "status": 200,
            "code": "M109",
            "message": "회원 이미지 변경에 성공하였습니다.",
            "data": {
                "status": "success",
                "imageUrl": "https://xxx.com/A.jpg"
            }
        }
        ```
         - message : 결과에 대한 message를 작성합니다.
         - status : http status code를 작성합니다.
         - data : 결과 객체를 JSON 형태로 나타냅니다.
         - code : 결과에 할당되는 유니크한 코드 값입니다.
    - Result Respone 객체
      ```java
      @Getter
      public class ResultResponse {

          private int status;
          private String code;
          private String message;
          private Object data;

          public static ResultResponse of(ResultCode resultCode, Object data) {
              return new ResultResponse(resultCode, data);
          }
      
          public ResultResponse(ResultCode resultCode, Object data) {
              this.status = resultCode.getStatus();
              this.code = resultCode.getCode();
              this.message = resultCode.getMessage();
              this.data = data;
          }
      }
      ```
6.  @RestController에서 통일된 응답 사용
    ```java
    @RestController
    @RequiredArgsConstructor
    public class PostController {

        private final PostService postService;

        @ApiOperation(value = "게시물 업로드", consumes = MULTIPART_FORM_DATA_VALUE)
        @PostMapping("/posts")
        public ResponseEntity<ResultResponse> createPost(@Validated @ModelAttribute PostUploadRequest request) {
            ...
    
            return ResponseEntity.ok(ResultResponse.of(CREATE_POST_SUCCESS, response));
        }
        ...
    }
    ```
7.  자바 네이밍 전략

    - 패키지: 소문자
    - 변수: Camel Case + 명사
      ```java
      private String imageName;
      ```
    - 상수: 대문자 + underscore
      ```java
      private final static String BEARER_TYPE = "Bearer";
      ```
    - 메소드: Camel Case + 동사
      ```java
      public ResponseEntity<ResultResponse> createPost(@Validated @ModelAttribute PostUploadRequest request) { ... }
      ```
8.  데이터베이스 명명 전략
    - Common
      1. 소문자 사용
      2. 단어를 임의로 축약 x
      3. 동사는 능동태 사용
    - Table
      1. 복수형 사용
      2. 교차 테이블(Many to Many): 각 테이블 이름을 _(underscore)로 연결 -> Snake case
        > ex) vip_members
    - Column
      1. PK, FK는 해당 테이블의 단수명_id으로 사용
      2. boolean 유형은 _flag 접미어 사용
      3. datetime 유형은 _date 접미어 사용
9.  프로젝트 폴더 구조

    ```txt
    /src.main.java.cloneproject.instagram
    ├── /advice
    │ ├── GlobalExceptionHandler.java
    ├── /config
    ├── /controller
    ├── /dto
    │ ├── /error
    │ │ ├── ErrorCode.java
    │ │ └── ErrorResponse.java
    │ ├── /result
    │ │ ├── ResultCode.java
    │ │ └── ResultResponse.java
    ├── /entity
    ├── /exception
    ├── /repository
    ├── /service
    ├── /util
    ├── /vo
    │ InstagramApplication.java
    ```

### Commit Convention

```txt
Type: Subject
ex) Feat: 회원가입 API 추가

Description

Footer 
ex) Resolves: #1, #2
```
- <b>Type</b>
  - Feat: 기능 추가, 삭제, 변경
  - Fix: 버그 수정
  - Refactor: 코드 리팩토링
  - Style: 코드 형식, 정렬 등의 변경. 동작에 영향 x
  - Test: 테스트 코드 추가, 삭제 변경
  - Docs: 문서 추가 삭제 변경. 코드 수정 x
  - Etc: 위에 해당하지 않는 모든 변경
- <b>Description</b>
  - 한 줄당 72자 이내로 작성
  - 최대한 상세히 작성(why - what)
- <b>Footer</b>
  - Resolve(s): Issue 해결 시 사용
  - See Also: 참고할 Issue 있을 시 사용
- <b>Rules</b>
  - 관련된 코드끼리 나누어 Commit
  - 불필요한 Commit 지양
  - 제목은 명령조로 작성

<p align="right">(<a href="#top">back to top</a>)</p>

<!-- CONTRIBUTING -->

## Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

If you have a suggestion that would make this better, please fork the repo and create a pull request. You can also simply open an issue with the tag "enhancement".
Don't forget to give the project a star! Thanks again!

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Contributors

<table>
  <tr>
    <td align="center">
      <a href="https://github.com/seonpilKim">
        <img src="https://avatars.githubusercontent.com/u/68049320?v=4" width="110px;" alt=""/><br />
        <sub><b>seonpilKim</b></sub></a><br />
        <a href="https://github.com/seonpilKim" title="Code">💻</a>
    </td>
    <td align="center">
      <a href="https://github.com/bluetifulc">
        <img src="https://avatars.githubusercontent.com/u/58378676?v=4" width="110px;" alt=""/><br />
        <sub><b>bluetifulc</b></sub></a><br />
        <a href="https://github.com/bluetifulc" title="Code">💻</a>
    </td>
  </tr>
</table>  

<p align="right">(<a href="#top">back to top</a>)</p>

<!-- LICENSE -->

## License

Distributed under the MIT License. See `LICENSE.txt` for more information.

<p align="right">(<a href="#top">back to top</a>)</p>

<!-- CONTACT -->

## Contact

SeonPil Kim - [Email](ksp970306@gmail.com) - ksp970306@gmail.com

<p align="right">(<a href="#top">back to top</a>)</p>

<!-- ACKNOWLEDGMENTS -->

## Acknowledgments

Use this space to list resources you find helpful and would like to give credit to. I've included a few of my favorites to kick things off!

-   [Choose an Open Source License](https://choosealicense.com)
-   [GitHub Emoji Cheat Sheet](https://www.webpagefx.com/tools/emoji-cheat-sheet)
-   [Malven's Flexbox Cheatsheet](https://flexbox.malven.co/)
-   [Malven's Grid Cheatsheet](https://grid.malven.co/)
-   [Img Shields](https://shields.io)
-   [Issue Trcking](https://shields.io/category/issue-tracking)
-   [GitHub Pages](https://pages.github.com)
-   [Font Awesome](https://fontawesome.com)
-   [React Icons](https://react-icons.github.io/react-icons/search)

<p align="right">(<a href="#top">back to top</a>)</p>

<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->

[contributors-shield]: https://img.shields.io/github/contributors/Instagram-Clone-Coding/Spring_instagram-clone.svg?style=for-the-badge
[contributors-url]: https://github.com/Instagram-Clone-Coding/Spring_instagram-clone/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/Instagram-Clone-Coding/Spring_instagram-clone.svg?style=for-the-badge
[forks-url]: https://github.com/Instagram-Clone-Coding/Spring_instagram-clone/network/members
[stars-shield]: https://img.shields.io/github/stars/Instagram-Clone-Coding/Spring_instagram-clone.svg?style=for-the-badge
[stars-url]: https://github.com/Instagram-Clone-Coding/Spring_instagram-clone/stargazers
[issues-shield]: https://img.shields.io/github/issues/Instagram-Clone-Coding/Spring_instagram-clone.svg?style=for-the-badge
[issues-url]: https://github.com/Instagram-Clone-Coding/Spring_instagram-clone/issues
[license-shield]: https://img.shields.io/github/license/Instagram-Clone-Coding/Spring_instagram-clone?style=for-the-badge
[license-url]: https://github.com/Instagram-Clone-Coding/Spring_instagram-clone/blob/develop/LICENSE.txt
[pr-shield]: https://img.shields.io/github/issues-pr/Instagram-Clone-Coding/Spring_instagram-clone?style=for-the-badge
[pr-url]: https://github.com/Instagram-Clone-Coding/Spring_instagram-clone/pulls
[product-screenshot]: images/screenshot.png
