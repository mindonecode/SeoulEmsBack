package kr.co.mindone.ems.drvn;
/**
 * packageName    : kr.co.mindone.ems.drvn
 * fileName       : SimPredictionMapper
 * author         : ch.song
 * date           : 26. 7. 15.
 * description    : 계측값 기반 예측 시뮬레이터(TB_CTR_TNK_RST_SIM) 적재용 매퍼.
 *                  파이썬/ML 예측기가 TB_CTR_TNK_RST 에 넣는 것과 동일한 형태의 데이터를
 *                  TB_RAWDATA 실측값 + 랜덤 오차로 생성해 별도 테이블에 적재한다.
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 7. 15.        ch.song       최초 생성
 */
import org.apache.ibatis.annotations.Mapper;
import org.springframework.context.annotation.Profile;

import java.util.HashMap;
import java.util.List;

@Mapper
@Profile("!gm & !hp & !ji & !hy & !ss & !gm2 & !hp2 & !hy2 & !ji2")
public interface SimPredictionMapper {

	/** 적재 대상 테이블(TB_CTR_TNK_RST_IMPROVED)이 없으면 생성. 첫 배치 실행 시 1회 호출. */
	void createImprovedTableIfNotExists();

	/**
	 * 태그 목록을 받아 각 태그의 기준시각(ref_ts) 이하 최신 실측값 1건씩을 반환.
	 * @param param tagList(List&lt;String&gt;), ref_ts("yyyy-MM-dd HH:mm:ss")
	 * @return [{ tag, value }] 태그별 최신 실측값 (윈도우 내 데이터 없는 태그는 미포함)
	 */
	List<HashMap<String, Object>> selectLatestRawByTags(HashMap<String, Object> param);

	/**
	 * 시뮬레이션 예측 행들을 TB_CTR_TNK_RST_IMPROVED 에 배치 upsert.
	 * @param list [{ DSTRB_ID, MODEL_NAME, RGSTR_TIME, PRDCT_TIME, PRDCT_VALUE }]
	 */
	void insertSimPrediction(List<HashMap<String, Object>> list);

	/**
	 * 시뮬레이션 대상이 아닌 DSTRB_ID 의 예측을 원본 TB_CTR_TNK_RST → IMPROVED 로 복사.
	 * @param param fromTs, toTs("yyyy-MM-dd HH:mm:ss"), excludeTags(List&lt;String&gt; = 시뮬레이션 태그)
	 * @return 복사된 행 수
	 */
	int copyOriginalPredictions(HashMap<String, Object> param);

	/**
	 * 블렌드용: 시뮬 대상 태그의 원본 XGBoost 예측을 (RGSTR_TIME,PRDCT_TIME)별로 조회.
	 * @param param tagList(List&lt;String&gt;), fromTs, toTs
	 * @return [{ tag, rgstr_time, prdct_time, value }]
	 */
	List<HashMap<String, Object>> selectOriginalPredRange(HashMap<String, Object> param);
}
