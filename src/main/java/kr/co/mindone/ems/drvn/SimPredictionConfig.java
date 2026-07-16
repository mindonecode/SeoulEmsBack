package kr.co.mindone.ems.drvn;
/**
 * packageName    : kr.co.mindone.ems.drvn
 * fileName       : SimPredictionConfig
 * author         : ch.song
 * date           : 26. 7. 15.
 * description    : 계측값 기반 예측 시뮬레이터 스케줄러.
 *                  외부 파이썬/ML 모듈이 TB_CTR_TNK_RST 에 적재하는 예측 결과와 동일한 세트 구조
 *                  (태그 N개 × horizon{10,60,120,180,360} × 태그별 모델 다중화 개수)의 데이터를,
 *                  TB_RAWDATA 실측값에 랜덤 오차(±errorRate)를 입혀 매 10분 정각에 생성하고
 *                  원본과 분리된 TB_CTR_TNK_RST_IMPROVED 테이블에 적재한다(없으면 첫 실행 시 자동 생성).
 *                  스케줄러 골격(cron 게이트 + @Async 위임 + 배치 멱등 마킹)은 DrvnConfig 관례를 따름.
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 7. 15.        ch.song       최초 생성
 */
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
@PropertySource("classpath:application-${spring.profiles.active}.properties")
@Profile("!gm & !hp & !ji & !hy & !ss & !gm2 & !hp2 & !hy2 & !ji2")
public class SimPredictionConfig {

	private static final DateTimeFormatter TS_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	@Autowired
	private SimPredictionMapper simPredictionMapper;

	// 대상 태그 → 모델 다중화 개수 맵(JSON). 원본 TB_CTR_TNK_RST 한 세트의 태그별 행수 구조를 재현:
	// 대부분 1, 북악 유출 2태그=5, 대표 유량·관압·공릉6지·북악유입 10태그=6.
	// 한 세트 = Σ(태그별 count) × horizon수. (예: (18×1+2×5+10×6) × 5 = 88×5 = 440행)
	@Value("${dstrb.prdct.sim.tagModelCount}")
	private String tagModelCountJson;
	private LinkedHashMap<String, Integer> tagModelCount;

	@Value("${dstrb.prdct.sim.enabled:false}")
	private boolean simEnabled;
	// white 모드에서만 쓰는 시점별 난수 진폭(±). smooth 모드는 base/spike 를 사용.
	@Value("${dstrb.prdct.sim.errorRate:0.05}")
	private double errorRate;
	// 편차 방식: smooth(구간 바이어스 — 6~12h 동안 ±2~4% 유지하다 완만히 반전, 요동 없음) | white(시점별 난수, 톱니).
	@Value("${dstrb.prdct.sim.deviationMode:smooth}")
	private String deviationMode;
	// [smooth] 바이어스 크기 하한/상한(±). 각 구간은 이 범위에서 랜덤 크기·랜덤 부호로 결정.
	@Value("${dstrb.prdct.sim.biasMinPct:0.02}")
	private double biasMinPct;
	@Value("${dstrb.prdct.sim.biasMaxPct:0.04}")
	private double biasMaxPct;
	// [smooth] 바이어스 레벨 유지 구간 길이(시간). 이 간격마다 새 레벨을 잡고 smoothstep 으로 이어 완만히 전환.
	@Value("${dstrb.prdct.sim.biasHoldHours:9}")
	private double biasHoldHours;
	// [블렌드] 유량 예측 = w·실측 + (1-w)·원본예측. w=1 이면 순수 실측, w=0 이면 순수 원본. 0.5 → 오차 절반.
	// 0 이면 블렌드 비활성(랜덤워크 방식 사용). 원본 예측이 없는 시점은 랜덤워크로 폴백.
	@Value("${dstrb.prdct.sim.blendWeight:0.5}")
	private double blendWeight;
	// [smooth] 선택적 스파이크(기본 0=미사용). 필요 시 특정 구간을 추가로 튀게. 0<p 면 slot 당 확률 p.
	@Value("${dstrb.prdct.sim.spikeErrorRate:0.05}")
	private double spikeErrorRate;
	@Value("${dstrb.prdct.sim.spikeProb:0.0}")
	private double spikeProb;
	// 스파이크 구간 길이(분).
	private static final int SPIKE_WINDOW_MIN = 30;
	// 예측 horizon(분) CSV. 각 RGSTR_TIME 당 이 시점들에 대해 예측 행을 생성.
	@Value("${dstrb.prdct.sim.horizons:10,60,120,180,360}")
	private String horizonsCsv;
	// model_name 컬럼 값 (기존 소비 SQL 이 'XGBoost' 로 필터하므로 기본 동일).
	@Value("${dstrb.prdct.sim.modelName:XGBoost}")
	private String modelName;
	// MODEL_VERSION 다중화 구분자 접두어. 태그별 count 만큼 {prefix}1..{prefix}N 으로 행 생성.
	@Value("${dstrb.prdct.sim.versionPrefix:v}")
	private String versionPrefix;

	// 10분 배치 중복 산출 방지용 마지막 처리 배치 경계("yyyy-MM-dd HH:mm:ss").
	private volatile String lastProcessedBatch = null;
	// 적재 테이블 존재 보장(CREATE IF NOT EXISTS)을 1회만 수행하기 위한 가드.
	private volatile boolean tableEnsured = false;
	// 실시간(라이브) 경로의 태그별 편차 랜덤워크 상태(직전 dev). backfill 은 실행마다 별도 로컬 상태 사용.
	private final java.util.Map<String, Double> liveWalkState = new java.util.concurrent.ConcurrentHashMap<>();

	@PostConstruct
	public void init() {
		Type mapType = new TypeToken<LinkedHashMap<String, Integer>>() {}.getType();
		tagModelCount = new Gson().fromJson(tagModelCountJson, mapType);
		if (tagModelCount == null) tagModelCount = new LinkedHashMap<>();
	}

	/**
	 * 실시간 배치 스케줄러(매 분). 현재 10분 배치(:X0)를 아직 성공 처리하지 않았으면 시도:
	 *   - 원본 TB_CTR_TNK_RST(:X0 적재분)이 아직 없으면(복사 0행) → 마킹 안 함 → 다음 분에 재시도
	 *   - 원본이 도착해 복사되면 유량 시뮬도 생성하고 배치 완료 마킹 → 그 배치는 재시도 중단
	 * 한 10분 창 안에서 최대 ~9회 재시도(:X1~:X9), 다음 :X0 이 되면 새 배치로 넘어감(옛 배치 미도착이면 포기).
	 */
	@Scheduled(cron = "0 * * * * *")
	public void scheduleSimPrediction() {
		if (!simEnabled) return;
		try {
			LocalDateTime now = LocalDateTime.now();
			LocalDateTime batchStart = now.withMinute(now.getMinute() / 10 * 10).withSecond(0).withNano(0);
			String batchKey = batchStart.format(TS_FMT);
			if (batchKey.equals(lastProcessedBatch)) return;   // 이미 성공 처리한 배치 → skip
			int copied = generateAndInsert(batchStart);
			if (copied > 0) {                                  // 원본 도착·적재 성공 → 완료 마킹(재시도 중단)
				lastProcessedBatch = batchKey;
			}
			// copied==0 이면 미마킹 → 다음 분 재시도
		} catch (Exception e) {
			log.error("[SIM] 예측 시뮬레이터 배치 게이트 처리 실패: {}", e.getMessage(), e);
		}
	}

	/**
	 * 한 배치(batchStart) 처리: 원본 복사(비시뮬 태그) + 유량 시뮬 생성.
	 * 스케줄러(매 분 게이트)와 수동 트리거(triggerNow)가 공유.
	 * @return 원본에서 복사된 행 수. 0 이면 이 배치 원본이 아직 미도착(= 준비 안 됨) → 시뮬도 건너뜀.
	 */
	public int generateAndInsert(LocalDateTime batchStart) {
		try {
			ensureTable();
			String rgstrTime = batchStart.format(TS_FMT);
			// 1) 원본 복사(비시뮬 태그). 0행이면 이 배치 원본이 아직 안 들어온 것 → 준비 안 됨.
			int copied = copyOriginalExceptSimulated(rgstrTime, rgstrTime);
			if (copied <= 0) {
				return 0; // 스케줄러가 다음 분에 재시도
			}
			// 2) 원본 도착 → 유량(시뮬 대상) 생성.
			List<String> tags = targetTags();
			if (!tags.isEmpty()) {
				// 블렌드용 원본 예측(이 배치 시각) 로드 + 라이브 랜덤워크 폴백 상태(liveWalkState).
				Map<String, Double> origMap = loadOrigPredMap(tags, rgstrTime, rgstrTime);
				List<HashMap<String, Object>> rows = buildRowsForBatch(batchStart, tags, parseHorizons(), liveWalkState, origMap);
				if (!rows.isEmpty()) {
					simPredictionMapper.insertSimPrediction(rows);
				}
			}
			log.info("[SIM] 배치 {} 적재 완료: 원본복사 {}행 + 유량 시뮬", rgstrTime, copied);
			return copied;
		} catch (Exception e) {
			log.error("[SIM] 예측 시뮬레이터 적재 실패: {}", e.getMessage(), e);
			return 0;
		}
	}

	/**
	 * 지정 기간 [from, to] 을 10분 배치로 순회하며 각 시점의 실측값 기반 예측 행을 생성해
	 * TB_CTR_TNK_RST_IMPROVED 에 일괄 적재(backfill). 청크 단위(CHUNK)로 upsert.
	 * 실행량이 클 수 있어 컨트롤러에서 프록시 경유 비동기 실행(fire-and-forget, 진행상황은 로그).
	 */
	@Async("taskExecutor")
	public void backfill(LocalDateTime from, LocalDateTime to) {
		backfillSync(from, to);
	}

	/** backfill 의 동기 코어. 오케스트레이터(순차 실행)에서 직접 호출해 완료까지 블록. */
	public void backfillSync(LocalDateTime from, LocalDateTime to) {
		final int CHUNK = 1000;
		try {
			ensureTable();
			List<String> tags = targetTags();
			if (tags.isEmpty()) {
				log.warn("[SIM] 대상 태그가 비어 있어 backfill 스킵 (dstrb.prdct.sim.tagModelCount 확인)");
				return;
			}
			int[] horizons = parseHorizons();
			// 시작을 10분 경계로 내림. 종료 시각(포함)까지 10분씩 진행.
			LocalDateTime cur = from.withMinute(from.getMinute() / 10 * 10).withSecond(0).withNano(0);
			log.info("[SIM] backfill 시작: {} ~ {} (10분 배치, 시뮬 태그 {}개 × horizon {}개)",
					cur.format(TS_FMT), to.format(TS_FMT), tags.size(), horizons.length);

			// 시뮬레이션 대상이 아닌 태그는 원본 TB_CTR_TNK_RST 값을 구간 전체 한 번에 복사.
			int copied = copyOriginalExceptSimulated(cur.format(TS_FMT), to.format(TS_FMT));
			log.info("[SIM] backfill 원본복사(비시뮬 태그): {}행", copied);

			List<HashMap<String, Object>> buffer = new ArrayList<>();
			// backfill 전용 랜덤워크 상태(폴백용) + 블렌드용 원본 예측을 구간 전체 한 번에 로드.
			java.util.Map<String, Double> walkState = new HashMap<>();
			Map<String, Double> origMap = loadOrigPredMap(tags, cur.format(TS_FMT), to.format(TS_FMT));
			log.info("[SIM] backfill 블렌드용 원본 예측 로드: {}건", origMap.size());
			int okBatches = 0, emptyBatches = 0, totalRows = 0;
			while (!cur.isAfter(to)) {
				List<HashMap<String, Object>> rows = buildRowsForBatch(cur, tags, horizons, walkState, origMap);
				if (rows.isEmpty()) {
					emptyBatches++;
				} else {
					buffer.addAll(rows);
					okBatches++;
				}
				if (buffer.size() >= CHUNK) {
					simPredictionMapper.insertSimPrediction(buffer);
					totalRows += buffer.size();
					buffer.clear();
				}
				cur = cur.plusMinutes(10);
			}
			if (!buffer.isEmpty()) {
				simPredictionMapper.insertSimPrediction(buffer);
				totalRows += buffer.size();
			}
			log.info("[SIM] backfill 완료: 적재배치 {}개 / 무데이터배치 {}개 / 총 {}행",
					okBatches, emptyBatches, totalRows);
		} catch (Exception e) {
			log.error("[SIM] backfill 실패: {}", e.getMessage(), e);
		}
	}

	/**
	 * 한 배치(batchStart) 기준 예측 행 생성.
	 * 앵커: 생성시각(RGSTR_TIME=batchStart)의 실측값 × (1+dev) — 모든 horizon 공통.
	 * dev 는 태그별 평균회귀 랜덤워크로 '배치당 1회' 진행(주기 없이 완만히 변화). walkState 로 연속성 유지.
	 * 태그 × horizon 마다 1행(모델 다중화 없음, MODEL_VERSION=NULL). 실측 전무면 빈 리스트.
	 */
	private List<HashMap<String, Object>> buildRowsForBatch(LocalDateTime batchStart, List<String> tags,
			int[] horizons, java.util.Map<String, Double> walkState, Map<String, Double> origMap) {
		String rgstrTime = batchStart.format(TS_FMT);
		List<HashMap<String, Object>> rows = new ArrayList<>();
		Map<String, Double> baseMap = fetchRawMap(tags, rgstrTime); // 생성시각 실측 (앵커/블렌드 입력)
		if (baseMap.isEmpty()) return rows;

		long tMin = batchStart.toEpochSecond(java.time.ZoneOffset.UTC) / 60L;
		// 태그별 랜덤워크 편차(블렌드 불가 시 폴백)를 배치당 1회 진행.
		Map<String, Double> tagDev = new HashMap<>();
		for (String tag : tags) {
			if (baseMap.get(tag) == null) continue;
			tagDev.put(tag, nextWalkDev(tag, tMin, walkState));
		}

		boolean useBlend = blendWeight > 0 && origMap != null;
		for (int h : horizons) {
			String prdctTime = batchStart.plusMinutes(h).format(TS_FMT);
			for (String tag : tags) {
				Double base = baseMap.get(tag);
				if (base == null) continue;
				double prdctVal;
				Double orig = useBlend ? origMap.get(tag + "|" + rgstrTime + "|" + prdctTime) : null;
				if (orig != null) {
					// 블렌드: w·실측 + (1-w)·원본예측 → 원본 오차의 (1-w)배
					prdctVal = blendWeight * base + (1.0 - blendWeight) * orig;
				} else {
					// 폴백: 실측 앵커 + 랜덤워크 편차 (원본 예측 없거나 블렌드 비활성)
					Double dev = tagDev.get(tag);
					prdctVal = base * (1.0 + (dev == null ? 0.0 : dev));
				}
				double prdct = BigDecimal.valueOf(prdctVal).setScale(2, RoundingMode.HALF_UP).doubleValue();
				HashMap<String, Object> row = new HashMap<>();
				row.put("DSTRB_ID", tag);
				row.put("MODEL_NAME", modelName);
				row.put("RGSTR_TIME", rgstrTime);
				row.put("PRDCT_TIME", prdctTime);
				row.put("PRDCT_VALUE", prdct);
				rows.add(row);
			}
		}
		return rows;
	}

	/** 시뮬 대상 태그의 원본 XGBoost 예측을 [fromTs,toTs] 구간에서 로드. 키 = "tag|rgstr|prdct". */
	private Map<String, Double> loadOrigPredMap(List<String> tags, String fromTs, String toTs) {
		Map<String, Double> m = new HashMap<>();
		if (tags == null || tags.isEmpty()) return m;
		HashMap<String, Object> param = new HashMap<>();
		param.put("tagList", tags);
		param.put("fromTs", fromTs);
		param.put("toTs", toTs);
		List<HashMap<String, Object>> raw = simPredictionMapper.selectOriginalPredRange(param);
		if (raw != null) {
			for (HashMap<String, Object> r : raw) {
				Object val = r.get("value");
				if (val == null) continue;
				try {
					m.put(r.get("tag") + "|" + r.get("rgstr_time") + "|" + r.get("prdct_time"),
							Double.parseDouble(String.valueOf(val)));
				} catch (NumberFormatException ignore) {
				}
			}
		}
		return m;
	}

	/**
	 * 편차(dev, = PRDCT_VALUE/실측 − 1)를 태그별 '평균회귀 랜덤워크'로 한 스텝 진행해 반환.
	 * 매 10분 아주 작은 랜덤 스텝(±step)으로 서서히 표류하고, θ 로 0 쪽으로 약하게 당겨 경계 고착/발산을 막는다.
	 *  - 주기(파형) 성분이 전혀 없음 → 규칙적 움직임 없이 자연스럽게 완만히 변함.
	 *  - step 은 작아(≈biasMaxPct×0.12) 급변하지 않음. θ 는 biasHoldHours 로 상관시간(방향 유지 길이) 조절.
	 *  - 스텝 난수는 (tag,tMin) 해시로 결정적 → 같은 시작상태면 재실행 재현. clamp: ±biasMaxPct.
	 * state: 태그별 직전 dev. backfill=실행별 로컬맵(0에서 시작), 라이브=liveWalkState(연속).
	 */
	private double nextWalkDev(String tag, long tMin, java.util.Map<String, Double> state) {
		if (!"smooth".equalsIgnoreCase(deviationMode)) {
			return errorRate > 0 ? ThreadLocalRandom.current().nextDouble(-errorRate, errorRate) : 0.0;
		}
		double theta = Math.min(0.2, Math.max(0.005, 10.0 / Math.max(60.0, biasHoldHours * 60.0)));
		double step = biasMaxPct * 0.12;
		double cap = biasMaxPct;
		double prev = state.getOrDefault(tag, 0.0);
		double noise = 2.0 * hash01(tag + "|w|" + tMin) - 1.0;   // [-1,1) 결정적
		double dev = prev * (1.0 - theta) + step * noise;
		if (dev > cap) dev = cap;
		else if (dev < -cap) dev = -cap;
		state.put(tag, dev);
		return dev;
	}

	/** 문자열 → [0,1) 결정적 의사난수. */
	private double hash01(String s) {
		return (s.hashCode() & 0x7fffffff) / (double) Integer.MAX_VALUE;
	}

	/**
	 * 시뮬레이션 대상(tagModelCount 키)이 아닌 DSTRB_ID 의 예측을 원본 TB_CTR_TNK_RST → IMPROVED 로 복사.
	 * 즉 IMPROVED = (설정 태그는 시뮬레이션 생성) + (그 외 태그는 원본 그대로).
	 * @return 복사 행 수
	 */
	private int copyOriginalExceptSimulated(String fromTs, String toTs) {
		List<String> simTags = new ArrayList<>(tagModelCount.keySet());
		HashMap<String, Object> param = new HashMap<>();
		param.put("fromTs", fromTs);
		param.put("toTs", toTs);
		param.put("excludeTags", simTags); // 시뮬레이션 태그는 복사 제외(생성으로 채움)
		try {
			return simPredictionMapper.copyOriginalPredictions(param);
		} catch (Exception e) {
			log.error("[SIM] 원본 복사 실패({}~{}): {}", fromTs, toTs, e.getMessage());
			return 0;
		}
	}

	/** 태그 목록의 ref_ts(기준시각) 이하 최신 실측값을 tag→value 맵으로. (selectLatestRawByTags 래핑) */
	private Map<String, Double> fetchRawMap(List<String> tags, String refTs) {
		HashMap<String, Object> param = new HashMap<>();
		param.put("tagList", tags);
		param.put("ref_ts", refTs);
		List<HashMap<String, Object>> raw = simPredictionMapper.selectLatestRawByTags(param);
		Map<String, Double> m = new HashMap<>();
		if (raw != null) {
			for (HashMap<String, Object> r : raw) {
				Object val = r.get("value");
				if (val == null) continue;
				try {
					m.put(String.valueOf(r.get("tag")), Double.parseDouble(String.valueOf(val)));
				} catch (NumberFormatException ignore) {
				}
			}
		}
		return m;
	}

	/** 적재 테이블을 없으면 생성(프로세스 생애 1회). */
	private void ensureTable() {
		if (tableEnsured) return;
		synchronized (this) {
			if (tableEnsured) return;
			simPredictionMapper.createImprovedTableIfNotExists();
			tableEnsured = true;
		}
	}

	/** 대상 태그 = dstrb.prdct.sim.tagModelCount 의 키 목록. */
	private List<String> targetTags() {
		return new ArrayList<>(tagModelCount.keySet());
	}

	/** 태그의 모델 다중화 개수(설정 없으면 1). */
	private int modelCountOf(String tag) {
		Integer c = tagModelCount.get(tag);
		return (c == null || c < 1) ? 1 : c;
	}

	private int[] parseHorizons() {
		String[] parts = horizonsCsv.split(",");
		List<Integer> out = new ArrayList<>();
		for (String p : parts) {
			String t = p.trim();
			if (t.isEmpty()) continue;
			try {
				out.add(Integer.parseInt(t));
			} catch (NumberFormatException ignore) {
			}
		}
		int[] arr = new int[out.size()];
		for (int i = 0; i < out.size(); i++) arr[i] = out.get(i);
		return arr;
	}

	/** 수동 트리거용(테스트) — 현재 시각의 10분 배치 경계로 즉시 1배치 생성. 컨트롤러에서 프록시 경유 비동기 실행. */
	@Async("taskExecutor")
	public void triggerNow() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime batchStart = now.withMinute(now.getMinute() / 10 * 10).withSecond(0).withNano(0);
		generateAndInsert(batchStart);
	}
}
