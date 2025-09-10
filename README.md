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


---

### 🔔 알림 시스템
- `Notification` 엔티티로 알림 저장
- 알림 타입: 댓글, 좋아요 등 (`NotificationType`)
- WebSocket 기반 실시간 알림 발송
- 알림 읽음 처리 (`isRead` 플래그)
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

파일 복사 후 client-id, client-secret 값을 본인 Google/Kakao API 키로 입력하세요.
``` 
cp src/main/resources/application-oauth.yml.example src/main/resources/application-oauth.yml
```
```
./gradlew bootRun
```





---
## 📌 향후 개선 계획

- **조회수 중복 막기**  
  같은 사람이 계속 새로고침해도 조회수가 무한히 늘어나지 않도록 방지

- **엔티티 Setter 줄이기**  
  엔티티의 값은 직접 바꾸지 않고, 생성자나 필요한 기능 메서드를 통해서만 바꾸도록 개선

- **알림 연결 항상 유지**  
  현재는 게시글 목록 화면에서만 알림이 동작 → 모든 화면에서도 알림을 받을 수 있도록 공통 스크립트로 연결

