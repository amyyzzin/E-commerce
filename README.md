## 개요

간단한 커머스 프로젝트

Use : Spring, Jpa, MySql, Redis, Docker, AWS

Goal : 셀러와 구매자 사이를 중개해주는 커머스 서버를 구축한다

## 회원
### 공통
- [x] 이메일을 통해서 인증번호를 통한 회원가입

### 고객

- [x] 회원 가입
- [x] 인증 (이메일)
- [x] 로그인 토큰 발행
- [x] 로그인 토큰을 통한 제어 확인(JWT, Filter를 사용해서 간략하게)
- [ ] 예치금 관리


## 주문 서버
### 셀러
- [ ]  상품 CRUD(Create, Read, Update, Delete)
- [ ]  판매내역 조회
- [ ]  정산

## 구매자

- [ ]  장바구니를 위한 Redis 연동
- [ ]  고객이 주문