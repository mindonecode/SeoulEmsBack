# 정수장별 제어 설정 공통 기능 요약

정수장별 `application-*.properties`에서 제어에 직접 관여하는 `dstrb.*` 설정은 대부분 동일한 구조이며, **공통적으로 아래 기능을 담당**합니다.

- 펌프 조합 계산에 쓰이는 **펌프 가중치/레벨 기준** 설정
- 전력 예측에 쓰이는 **계수(인덱스)와 보정값** 설정
- 펌프 조합의 **목표 범위(최소/최대) 및 수위 목표** 설정
- 수두손실(Head Loss) 계산을 위한 **그룹 구성과 상수** 설정
- 운전현황/예측에 필요한 **유량·압력·수위 태그와 예측 변수(dstrb_id) 매핑**
- 펌프 그룹별 **예측 유량/압력 매핑**
- 제어 결과 기록을 위한 **OPT_IDX 태그 prefix**
- 리포트(엑셀)에서 제외할 **EPA 관련 dstrb_id**

즉, 각 정수장별 파일은 **제어/예측 계산에 필요한 입력 값과 태그 매핑을 하드코딩으로 제공**하는 역할을 합니다.

---

**관련 소스 파일 목록 (`application-*.properties`)**
- `src/main/resources/application-ba.properties`
- `src/main/resources/application-dev.properties`
- `src/main/resources/application-gm2.properties`
- `src/main/resources/application-gr.properties`
- `src/main/resources/application-gr1.properties`
- `src/main/resources/application-gs.properties`
- `src/main/resources/application-gu.properties`
- `src/main/resources/application-hp2.properties`
- `src/main/resources/application-hy2.properties`
- `src/main/resources/application-ji2.properties`
- `src/main/resources/application-ss.properties`
- `src/main/resources/application-wm.properties`
