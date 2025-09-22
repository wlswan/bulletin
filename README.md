# 📌 게시판 (Spring Boot Backend)

## 📖 프로젝트 소개
이 프로젝트는 **Spring Boot 학습을 위한 SSR 게시판 백엔드 애플리케이션**입니다.

주요 기능:
- 게시글 CRUD
- 댓글 CRUD & 실시간 알림 (WebSocket)
- 조회수, 좋아요 기능 (Redis 캐싱 + Scheduler 동기화)
- 회원 인증 (Form 로그인 + OAuth2 로그인: 구글, 카카오)

---

## ⚙️ 기술 스택
- **Backend**: Spring Boot, Spring MVC, Spring Security, Spring Data JPA
- **Frontend 템플릿**: Thymeleaf (뷰 렌더링)
- **Database**: H2 (개발용), MySQL (운영 환경)
- **Cache/Session**: Redis (좋아요 캐싱, 세션 저장소)
- **Realtime**: WebSocket + STOMP (실시간 알림/댓글 알림)
- **Infra**: AWS S3 + CloudFront (파일 업로드/조회/삭제)
- **Build Tool**: Gradle

---

## 🛠 주요 기능

### 🔑 인증 & 인가
- **폼 로그인 (Form Login)**
    - 사용자 이메일/비밀번호 기반 로그인 
    - BCrypt 기반 비밀번호 암호화
    - 관리자/사용자 권한 분리

- **OAuth2 소셜 로그인**
    - Google, Kakao 지원 
    - 최초 로그인 시 DB에 사용자 저장
    - 권한(Role) 적용

---

### 📄 게시판
- 게시글 작성 / 조회 / 수정 / 삭제
- 조회수, 좋아요에 따른 인기글
- 조회수 증가
- 권한 제어 (작성자 + 관리자만 수정/삭제 가능)
- Thymeleaf 

---

### 💬 댓글 시스템
- 댓글 작성 / 조회 / 삭제 (`CommentService`)
- 댓글 작성 시 게시글 작성자에게 **실시간 알림 발송** (`NotificationService`)
- 댓글 삭제 시 본인 또는 관리자만 허용

---

### ❤️ 좋아요 시스템
- 사용자가 게시글 좋아요 클릭 시 **Redis에 반영** (`post:view:{postId}`)
- `LikeCountScheduler`가 **10초마다 Redis → DB 동기화**
- 사용자 화면에는 Redis 값을 바로 가져와서 실시간 반영


---
### 👁 조회수 시스템
- 사용자가 게시글 상세 페이지 접속 시 **Redis에 반영** (`post:view:{postId}`)
- `ViewCountScheduler`가 **10초마다 Redis → DB 동기화**
- 사용자 화면에는 Redis 값을 바로 가져와서 실시간 반영
- 로그인 시 userId로 비로그인 시에는 guestKey를 쿠키로 발급해서 조회수 중복 제거


---

### 🔔 알림 시스템
- `Notification` 엔티티로 알림 저장
- 알림 타입: 댓글, 좋아요 
- WebSocket 기반 실시간 알림 발송
- 알림 읽음 처리


---
### 📂파일 업로드 
- AWS S3 버킷과 CloudFront 기반 파일 업로/조회/삭제
- 게시글 작성/수정  첨부파일 업로드 및 삭제 가능
- 이벤트 기반 비동기 처리-> 업로드와 DB 저장 분리 
- 멀티스레딩을 이용해서 파일 업로드 및 커넥션 풀을 고려해서 saveAll()로 DB 일괄 저장

---
## 🚀 실행 방법

```bash
git clone https://github.com/wlswan/bulletin.git
```

```
cd bulletin
```
```
docker run --name redis -p 6379:6379 -d redis
```

아래 파일을 복사한 뒤 OAuth2 Client ID/Secret, S3 버킷 이름, CloudFront 주소, AccessKey, SecretKey 등을 입력하세요.
``` 
cp src/main/resources/application-secret.yml.example src/main/resources/application-sercet.yml
```
```
./gradlew bootRun
```



## ⚡ 개발 중 겪은 문제들

### 📂 파일 업로드 관련
- **게시글 생성 지연 문제**  
  S3 업로드(IO 작업) 때문에 게시글 저장 응답이 늦어짐  
  -> DB 저장과 파일 업로드를 분리 → `@TransactionalEventListener(AFTER_COMMIT)` + `@Async` 로 비동기 이벤트 처리

- **MultipartFile 생명주기 문제**  
  HTTP 요청이 끝나면 MultipartFile이 소멸되어 비동기 이벤트에서 접근 불가  
  -> 업로드 요청 시 `Files.createTempFile` 로 임시 디스크에 저장하고, 이벤트에는 파일 경로(Path)만 전달  


