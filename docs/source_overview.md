# EMS Backend Source Overview

본 문서는 `/Users/chitaho/SmartEmsBack/ems_back` 기준으로 소스 전체를 빠르게 파악할 수 있도록 정리한 요약입니다. 클래스 단위 설명과 용도(기능) 단위 분류를 함께 제공합니다.

**프로젝트 개요**
- Spring Boot 기반 EMS(에너지 관리/정수장) 백엔드
- 주요 구성: REST API, MyBatis SQL Mapper, 스케줄러, Kafka 연동, JWT 인증
- 프로파일별 설정: `application-*.properties`로 정수장(사이트)별 환경 분기

**엔트리/부트스트랩**
- `src/main/java/kr/co/mindone/ems/EmsApplication.java` Spring Boot 실행 진입점, 스케줄링/비동기 활성화
- `src/main/java/kr/co/mindone/ems/ServletInitializer.java` 외부 WAS 배포 시 초기화

**설정/공통 인프라**
- `src/main/java/kr/co/mindone/ems/config/WebConfig.java` CORS 설정
- `src/main/java/kr/co/mindone/ems/config/SwaggerConfig.java` Swagger 문서 설정
- `src/main/java/kr/co/mindone/ems/config/base/BaseController.java` 공통 응답 생성 헬퍼
- `src/main/java/kr/co/mindone/ems/config/response/ResponseObject.java` 표준 응답 DTO
- `src/main/java/kr/co/mindone/ems/config/response/ResponseMessage.java` 응답 메시지 상수
- `src/main/java/kr/co/mindone/ems/common/AsyncConfig.java` 비동기 스레드풀 설정
- `src/main/java/kr/co/mindone/ems/common/SchedulerConfig.java` 스케줄러 스레드풀 설정

**로그인/인증**
- `src/main/java/kr/co/mindone/ems/login/LoginController.java` 토큰 검증/재발급, AI 상태 업데이트 트리거
- `src/main/java/kr/co/mindone/ems/login/LoginService.java` 사용자 조회/토큰 업데이트, UserDetailsService 구현
- `src/main/java/kr/co/mindone/ems/login/LoginMapper.java` 로그인/토큰 관련 MyBatis 매퍼
- `src/main/java/kr/co/mindone/ems/login/SecurityConfig.java` Spring Security 설정, JWT 필터 등록
- `src/main/java/kr/co/mindone/ems/login/JwtTokenProvider.java` JWT 생성/검증/파싱
- `src/main/java/kr/co/mindone/ems/login/JwtAuthenticationFilter.java` 요청마다 JWT 검증
- `src/main/java/kr/co/mindone/ems/login/dto/*.java` 로그인 요청/응답, 사용자, 권한 관련 DTO

**AI/예측(펌프/탱크/피크 등)**
- `src/main/java/kr/co/mindone/ems/ai/AiController.java` AI 예측/펌프/탱크 조회 API
- `src/main/java/kr/co/mindone/ems/ai/AiService.java` AI 예측 로직, 펌프/탱크 데이터 조합
- `src/main/java/kr/co/mindone/ems/ai/AiMapper.java` AI 관련 SQL 매퍼

**알람**
- `src/main/java/kr/co/mindone/ems/alarm/AlarmController.java` 알람 조회/확인/생성 API
- `src/main/java/kr/co/mindone/ems/alarm/AlarmService.java` 피크 알람 생성 스케줄, 알람 중복 제거
- `src/main/java/kr/co/mindone/ems/alarm/AlarmMapper.java` 알람 SQL 매퍼

**운전현황/펌프 조합(Drvn)**
- `src/main/java/kr/co/mindone/ems/drvn/DrvnController.java` 운전현황 API 및 엑셀 다운로드
- `src/main/java/kr/co/mindone/ems/drvn/DrvnService.java` 성능곡선/수두손실/조합 계산 로직
- `src/main/java/kr/co/mindone/ems/drvn/DrvnMapper.java` 운전현황 관련 SQL 매퍼
- `src/main/java/kr/co/mindone/ems/drvn/DrvnConfig.java` 프로퍼티 기반 운전현황/조합 설정 로드, 스케줄 실행

**에너지 사용/절감**
- `src/main/java/kr/co/mindone/ems/energy/EnerSpendController.java` 전력 사용/절감 조회 API
- `src/main/java/kr/co/mindone/ems/energy/EnerSpendService.java` 전력/요금/피크 계산 서비스
- `src/main/java/kr/co/mindone/ems/energy/EnerSpendMapper.java` 전력/요금 SQL 매퍼
- `src/main/java/kr/co/mindone/ems/common/SavingService.java` 절감량 계산 및 DB 적재

**펌프 제어**
- `src/main/java/kr/co/mindone/ems/pump/PumpService.java` 펌프 제어/상태/명령 로직
- `src/main/java/kr/co/mindone/ems/pump/PumpScheduler.java` AI 운전/추천/상태 확인 스케줄
- `src/main/java/kr/co/mindone/ems/pump/PumpMapper.java` 펌프 제어 SQL 매퍼

**설정/리포트**
- `src/main/java/kr/co/mindone/ems/setting/SettingController.java` 요금/목표/태그/리포트 API
- `src/main/java/kr/co/mindone/ems/setting/SettingService.java` 설정/리포트 계산 로직
- `src/main/java/kr/co/mindone/ems/setting/SettingMapper.java` 설정/리포트 SQL 매퍼
- `src/main/java/kr/co/mindone/ems/common/ExcelService.java` 리포트 엑셀 생성

**EPA 연동**
- `src/main/java/kr/co/mindone/ems/epa/EpaController.java` EPA 모드 조회/변경 API
- `src/main/java/kr/co/mindone/ems/epa/EpaService.java` EPA 모드/유량/압력 계산
- `src/main/java/kr/co/mindone/ems/epa/EpaMapper.java` EPA SQL 매퍼

**공통 서비스**
- `src/main/java/kr/co/mindone/ems/common/CommonController.java` 공통 조회/다운로드/절감 계산 API
- `src/main/java/kr/co/mindone/ems/common/CommonService.java` 공통 데이터 조회/가공
- `src/main/java/kr/co/mindone/ems/common/CommonMapper.java` 공통 SQL 매퍼
- `src/main/java/kr/co/mindone/ems/common/SchedulerService.java` 절감/AI/데이터 삭제 등 스케줄러

**휴일 계산**
- `src/main/java/kr/co/mindone/ems/common/holiday/CalendarType.java` 양력/음력 구분
- `src/main/java/kr/co/mindone/ems/common/holiday/Holiday.java` 공휴일 정의
- `src/main/java/kr/co/mindone/ems/common/holiday/HolidayChecker.java` 공휴일 판별

**Kafka**
- `src/main/java/kr/co/mindone/ems/kafka/KafkaProperties.java` Kafka 설정 바인딩
- `src/main/java/kr/co/mindone/ems/kafka/KafkaConfig.java` 단일 Kafka 설정(특정 프로필에서 사용 안 함)
- `src/main/java/kr/co/mindone/ems/kafka/KafkaConsumerService.java` 단일 Kafka 소비자(구성 전환 전 버전)
- `src/main/java/kr/co/mindone/ems/kafka/KafkaProducerTasks.java` 단일 Kafka 생산 스케줄(구성 전환 전 버전)
- `src/main/java/kr/co/mindone/ems/kafka/consumer/KafkaConfig.java` 이중 Kafka 설정
- `src/main/java/kr/co/mindone/ems/kafka/consumer/KafkaConsumerService.java` 이중 Kafka 소비자
- `src/main/java/kr/co/mindone/ems/kafka/producer/KafkaProducerTasks.java` 이중 Kafka 생산 스케줄

**테스트**
- `src/test/java/kr/co/mindone/ems/EmsApplicationTests.java` 스프링 부트 기본 테스트

**SQL Mapper 리소스**
- `src/main/resources/sqlmapper/mysql/*.xml` 도메인별 SQL (ai, alarm, common, drvn, enerSpend, epa, login, pump, setting)
- `src/main/resources/sqlmapper/config/config.xml` MyBatis 설정

**환경설정**
- `src/main/resources/application-*.properties` 사이트/프로파일별 설정
- `src/main/resources/application.properties` 기본 설정

---

# 용도(기능)별 분류

**인증/보안**
- `SecurityConfig`, `JwtTokenProvider`, `JwtAuthenticationFilter`, `LoginController`, `LoginService`, `LoginMapper`, `login/dto/*`

**AI 예측/최적화**
- `AiController`, `AiService`, `AiMapper`

**운전현황/성능곡선/펌프조합**
- `DrvnController`, `DrvnService`, `DrvnMapper`, `DrvnConfig`

**펌프 제어/운전**
- `PumpService`, `PumpScheduler`, `PumpMapper`

**에너지 사용/피크/요금/절감**
- `EnerSpendController`, `EnerSpendService`, `EnerSpendMapper`, `SavingService`

**알람**
- `AlarmController`, `AlarmService`, `AlarmMapper`

**설정/리포트**
- `SettingController`, `SettingService`, `SettingMapper`, `ExcelService`

**공통 기능/API**
- `CommonController`, `CommonService`, `CommonMapper`, `ResponseObject`, `ResponseMessage`, `BaseController`

**스케줄링/비동기**
- `SchedulerService`, `SchedulerConfig`, `AsyncConfig`

**Kafka 연동**
- `KafkaProperties`, `KafkaConfig`, `KafkaConsumerService`, `KafkaProducerTasks` 및 `kafka/consumer`, `kafka/producer` 패키지

**EPA 연동**
- `EpaController`, `EpaService`, `EpaMapper`

**휴일 계산**
- `HolidayChecker`, `Holiday`, `CalendarType`

---

# 프로파일 관련 메모

- `@Profile`로 사이트별 활성 클래스가 달라짐. 예: `kafka/consumer` 및 `kafka/producer`는 특정 프로파일에서만 동작.
- `SchedulerService`, `PumpScheduler`, `DrvnConfig`, `DrvnService` 등은 프로파일 조건에 따라 활성화/비활성화됨.

---

# 다음 단계 제안

원하시면 아래 형태로 확장 가능합니다.
- 클래스별 상세 API 목록과 파라미터 요약
- SQL 매퍼별 쿼리 요약(입력/출력 스키마)
- 프로파일별 활성 구성표
