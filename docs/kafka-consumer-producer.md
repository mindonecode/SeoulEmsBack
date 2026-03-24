# Kafka Consumer/Producer 분석 (발표용)

이 문서는 현재 소스에서 Kafka Consumer/Producer 흐름을 발표하기 쉽게 정리한 요약입니다. 코드는 Spring Boot + Spring Kafka 기반입니다.

## 1. 전체 구조 요약

- Consumer
  - 프로파일별(`gm2`, `hy2`, `hp2`, `ji2`, `gr`, `wm`, `gs`, `gu`, `ba`, `ss`)로 활성화되는 Kafka Consumer가 있습니다.
  - 두 개의 Kafka 클러스터(1/2)에 대해 각각 리스너를 분리하여 수신합니다.
  - 수신 메시지는 JSON → `HashMap`으로 파싱 후, 태그 검증 및 시간 규칙에 따라 DB에 저장합니다.

- Producer
  - 주된 Producer는 `PumpService`에서 직접 `KafkaProducer`를 생성해 제어 명령/상태 데이터를 `ems_result` 토픽으로 송신합니다.
  - `KafkaProducerTasks`는 주기적 전송 로직이 있으나 “2중화 구성변경으로 미사용” 주석과 프로파일 조건으로 현재 환경에서는 비활성화된 것으로 보입니다.

## 1.1 Consumer/Producer 역할 설명 (발표용)

- Consumer의 역할
  - 외부 시스템(예: SCADA)에서 들어오는 실시간 데이터를 수신한다.
  - 메시지를 표준화(JSON 파싱, 시간 변환)하고, 유효한 태그인지 검증한다.
  - 운영 로직(비상 정지, 펌프 동기화, 전력 합산 등)을 적용한 뒤 DB에 저장한다.
  - 즉, “현장 데이터 수집 및 정제 → 저장”이 핵심 기능이다.

- Producer의 역할
  - 내부 분석 결과/제어 명령을 외부 시스템으로 송신한다.
  - Pump 제어(RUN/STOP), 모드 변경, 상태 확인 명령 등을 Kafka로 발행한다.
  - 일부 사이트는 안정성을 위해 동일 메시지를 재전송한다.
  - 즉, “내부 판단/제어 결과를 현장 시스템에 전달”하는 것이 핵심 기능이다.

## 2. Kafka 설정 (Consumer 중심)

### 2.1 다중 클러스터 Consumer 설정
- 파일: `src/main/java/kr/co/mindone/ems/kafka/consumer/KafkaConfig.java`
- 핵심 포인트
  - `bootstrap-servers-1`, `bootstrap-servers-2`로 클러스터 1/2 분리
  - `AdminClient`와 `ConsumerFactory`를 클러스터별로 생성
  - `ConcurrentKafkaListenerContainerFactory`도 1/2 분리

요약 흐름
1. `adminClient1/2` 생성
2. `consumerFactory1/2` 생성 (auto-offset-reset 적용)
3. `kafkaListenerContainerFactory1/2` 생성

### 2.2 프로파일별 설정 값
- 파일: `src/main/resources/application-*.properties`
- 주요 설정
  - `kafka.topic.scada1.name`, `kafka.topic.scada2.name`
  - `kafka.group.scada1_1.id`, `kafka.group.scada1_2.id`
  - `kafka.group.scada2_1.id`, `kafka.group.scada2_2.id`
  - `spring.kafka.bootstrap-servers-1/2`
  - `spring.kafka.consumer.auto-offset-reset`

## 3. Consumer 로직 상세

### 3.1 리스너 구성
- 파일: `src/main/java/kr/co/mindone/ems/kafka/consumer/KafkaConsumerService.java`
- 리스너 4개
  - 클러스터 1 + scada1: `scadaFirstListen`
  - 클러스터 1 + scada2: `scadaSecondListen`
  - 클러스터 2 + scada1: `scadaFirstListen2`
  - 클러스터 2 + scada2: `scadaSecondListen2`

리스너마다 공통 흐름
1. `ConsumerRecord`의 `value`를 JSON으로 파싱
2. `server` 필드에 토픽명 추가
3. `insertMsgHashMap` 호출

### 3.2 애플리케이션 기동 시 작업
- `onApplicationEvent(ApplicationReadyEvent)`
  - `AdminClient`로 클러스터 연결 상태 확인
  - DB에서 태그 목록 로딩: `selectWppTagList`, `selectEMSConsumerTag`

### 3.3 메시지 처리 흐름 (핵심)
- 메서드: `insertMsgHashMap`

핵심 단계
1. 시간 변환
   - 입력 시간 문자열을 `Asia/Seoul` 기준으로 재포맷
2. 태그 유효성 확인
   - DB에 정의된 태그(`wppTagList`)인지 검사
3. 특수 로직
   - EMS 태그(`-EMS-`)는 기능 타입으로 비상 정지 처리
   - `wm` 프로파일: 펌프 동기화/제어 모드 체크
   - `gs` 프로파일: 전력/전력량 합산 태그 생성
4. 시간 규칙에 따른 저장
   - `checkMsgTime` 결과에 따라 `all/min/hour` 타입으로 DB 저장
   - `hour`일 때는 1시간 전 데이터와 차이를 계산해 `sum` 저장

시간 규칙 (`checkMsgTime`)
- 초가 `00`이고 분이 `00`이면 `hour`
- 초가 `00`이고 분이 `15/30/45`이면 `min`
- 그 외 `all` 또는 `sec` 처리

### 3.4 GS 전력 합산 로직
- `processTagData`
  - 특정 태그 세트가 동일 타임스탬프에 모두 수신되면 합산
  - PWI: 단위 변환 후 합산 값 저장
  - PWQ: 직전 1시간 PWI 평균을 산출해 저장

## 4. Producer 로직 상세

### 4.1 PumpService 기반 Producer (주요 사용)
- 파일: `src/main/java/kr/co/mindone/ems/pump/PumpService.java`
- 사용 방식
  - 필요한 시점에 `KafkaProducer` 생성
  - `ProducerRecord<>("ems_result", json)` 형태로 송신
  - JSON은 `makeProducerJsonValue`로 생성

주요 메서드
- `sendCtrTagItem`
  - RUN/STOP 제어 명령 전송
  - 특정 프로파일(`ba`, `gs`)은 재전송 로직 포함
- `sendCtrModeTagItem`
  - 제어 모드 변경 명령
  - gs 프로파일은 추가 재전송
- `sendCtrTagVVKItem`
  - 특수 케이스 제어 명령

특징
- 전송 후 일정 시간 대기(`Thread.sleep`)와 상태 업데이트 로직이 포함
- 일부 사이트는 동일 메시지를 2~3회 재전송

### 4.2 KafkaProducerTasks (비활성)
- 파일: `src/main/java/kr/co/mindone/ems/kafka/KafkaProducerTasks.java`
- 상태
  - “2중화 구성변경으로 미사용” 주석
  - 프로파일 조건으로 현재 운영 프로파일에서는 실행되지 않음
- 기능 요약
  - 매분 스케줄링으로 AI 분석 결과를 `ems_result` 토픽에 전송

## 5. 메시지 포맷

Producer 전송 메시지 (예시 구조)
```json
{
  "tag": "TAG_NAME",
  "value": 123.45,
  "time": "2026-03-13 12:00:00"
}
```
- 생성 메서드
  - `CommonService.makeProducerJsonValue`
  - `CommonService.makeProducerJsonStringValue`
- 파일: `src/main/java/kr/co/mindone/ems/common/CommonService.java`

## 6. 발표용 요약 포인트

1. Consumer는 두 개의 Kafka 클러스터에서 동일 주제(scada1/2)를 동시에 수신한다.
2. 메시지 수신 후 태그 검증, 시간 변환, 특수 로직을 거친 뒤 DB에 저장한다.
3. Producer는 PumpService 중심으로 제어/상태 메시지를 `ems_result` 토픽에 송신한다.
4. 일부 사이트는 신뢰성 확보를 위해 동일 메시지를 여러 번 재전송한다.
5. 과거에 존재하던 주기적 Producer(`KafkaProducerTasks`)는 현재 사용하지 않는다.

## 7. 발표 시 그림용 흐름 (텍스트 다이어그램)

Consumer Flow
```
Kafka Topic(scada1/2)
   -> KafkaListener(클러스터1/2)
   -> JSON 파싱
   -> 태그 검증/시간 변환
   -> 특수 로직
   -> DB 저장
```

Producer Flow
```
PumpService 제어 이벤트
   -> KafkaProducer 생성
   -> JSON 생성
   -> Topic: ems_result 송신
   -> (필요 시 재전송)
```

## 8. 참고 코드 위치

- Consumer 설정: `src/main/java/kr/co/mindone/ems/kafka/consumer/KafkaConfig.java`
- Consumer 처리: `src/main/java/kr/co/mindone/ems/kafka/consumer/KafkaConsumerService.java`
- Producer (주요): `src/main/java/kr/co/mindone/ems/pump/PumpService.java`
- Producer (미사용): `src/main/java/kr/co/mindone/ems/kafka/KafkaProducerTasks.java`
- JSON 생성: `src/main/java/kr/co/mindone/ems/common/CommonService.java`
- 환경 설정: `src/main/resources/application-*.properties`
