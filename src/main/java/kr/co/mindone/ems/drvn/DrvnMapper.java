package kr.co.mindone.ems.drvn;
/**
 * packageName    : kr.co.mindone.ems.drvn
 * fileName       : DrvnMapper
 * author         : geunwon
 * date           : 24. 9. 23.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 9. 23.        geunwon       최초 생성
 */
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.context.annotation.Profile;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Mapper
@Profile("!gm & !hp & !ji & !hy & !ss & !gm2 & !hp2 & !hy2 & !ji2" )
public interface DrvnMapper {
	/**
	 * 펌프 압력 데이터를 조회하는 메서드
	 * @param param 조회 조건을 담은 파라미터
	 * @return 펌프 압력 데이터를 반환
	 */
	List<HashMap<String, Object>> selectPumpPressure(HashMap<String, Object> param);
	/**
	 * 수두손실 데이터를 조회하는 메서드
	 * @param param 조회 조건을 담은 파라미터
	 * @return 수두손실 데이터를 반환
	 */
	Double selectHeadLoss(HashMap<String, Object> param);

	/**
	 * 펌프 유량 데이터를 조회하는 메서드
	 * @param param 조회 조건을 담은 파라미터
	 * @return 펌프 유량 데이터를 반환
	 */
	List<HashMap<String, Object>> selectPumpFlow(HashMap<String, Object> param);

	/**
	 * 현재 펌프 사용 데이터를 조회하는 메서드
	 * @param param 조회 조건을 담은 파라미터
	 * @return 현재 펌프 사용 데이터를 반환
	 */
	List<HashMap<String, Object>> selectNowPumpUse(HashMap<String, Object> param);

	/**
	 * 현재 펌프 전력 사용 데이터를 조회하는 메서드
	 * @param param 조회 조건을 담은 파라미터
	 * @return 현재 펌프 전력 사용 데이터를 반환
	 */
	List<HashMap<String, Object>> selectNowPumpPwrUse(HashMap<String, Object> param);

	/**
	 * 수두손실 계산을 위한 데이터를 조회하는 메서드
	 * @param param 조회 조건을 담은 파라미터
	 * @return 수두손실 계산 데이터를 반환
	 */
	List<HashMap<String, Object>> selectForHeadLoss(HashMap<String, Object> param);

	/**
	 * 시스템 소개 데이터를 조회하는 메서드
	 * @param param 조회 조건을 담은 파라미터
	 * @return 시스템 소개 데이터를 반환
	 */
	List<HashMap<String, Object>> selectIntradotion(HashMap<String, Object> param);

	/**
	 * 성능 곡선 유량 및 압력 데이터를 조회하는 메서드
	 * @param param 조회 조건을 담은 파라미터
	 * @return 성능 곡선 유량 및 압력 데이터를 반환
	 */
	List<HashMap<String, Object>> selectPrdctFlowPressure(HashMap<String, Object> param);

	/**
	 * 현재 펌프 유량 데이터를 조회하는 메서드
	 * @param param 조회 조건을 담은 파라미터
	 * @return 현재 펌프 유량 데이터를 반환
	 */
	List<HashMap<String, Object>> selectCurFlowData(HashMap<String, Object> param);

	/**
	 * 펌프 조합 계산 데이터를 조회하는 메서드
	 * @return 펌프 조합 계산 데이터를 반환
	 */
	List<HashMap<String, Object>> selectPumpCombCal();

	/**
	 * 성능 곡선 유량 및 압력 데이터를 조회하는 메서드
	 * @param map 조회 조건을 담은 맵
	 * @return 성능 곡선 유량 및 압력 데이터를 반환
	 */
	List<HashMap<String, Object>> prdctFlowPressure(HashMap<String, Object> map);

	/**
	 * 이번 10분 예측 배치 도착 여부 판정용.
	 * 대상 DSTRB_ID 중 RGSTR_TIME 이 [batchStart, batchEnd) 범위에 적재된 distinct DSTRB_ID 개수.
	 * @param param dstrbIds(List&lt;String&gt;), batchStart(String), batchEnd(String)
	 * @return 적재 완료된 distinct DSTRB_ID 개수
	 */
	Integer countPredictionReady(HashMap<String, Object> param);

	/**
	 * 태그·시간별 목표수위(TB_TARGET_LEVEL) 조회. 주어진 태그들의 HOURS 0~23 전체 행.
	 * @param tags 수조 태그 목록
	 * @return TAG, HOURS, MIN_VL, MAX_VL, USER_MIN_VL, USER_MAX_VL
	 */
	List<HashMap<String, Object>> selectTargetLevelByTags(@Param("tags") List<String> tags);

	/**
	 * 탱크 유량 및 압력 데이터를 조회하는 메서드
	 * @param map 조회 조건을 담은 맵
	 * @return 탱크 유량 및 압력 데이터를 반환
	 */
	List<HashMap<String, Object>> selectPreTnkFlowPressure(HashMap<String, Object> map);

	/**
	 * 현재 탱크 유량 및 압력 데이터를 조회하는 메서드
	 * @param map 조회 조건을 담은 맵
	 * @return 현재 탱크 유량 및 압력 데이터를 반환
	 */
	List<HashMap<String, Object>> selectCurTnkFlowPressure(HashMap<String, Object> map);

	/**
	 * EPANET 탱크 유량 및 압력 데이터를 조회하는 메서드
	 * @param map 조회 조건을 담은 맵
	 * @return EPANET 탱크 유량 및 압력 데이터를 반환
	 */
	List<HashMap<String, Object>> selectPreEPANETTnkFlowPressure(HashMap<String, Object> map);

	/**
	 * 수두손실 대상 이전 유량 데이터를 조회하는 메서드
	 * @param param 조회 조건을 담은 파라미터
	 * @return 수두손실 대상 이전 유량 데이터를 반환
	 */
	List<Double> selectHeadLossTargetPreFlow(HashMap<String, Object> param);

	/**
	 * 수두손실 대상 현재 유량 데이터를 조회하는 메서드
	 * @param param 조회 조건을 담은 파라미터
	 * @return 수두손실 대상 현재 유량 데이터를 반환
	 */
	List<Double> selectHeadLossTargetCurFlow(HashMap<String, Object> param);

	/**
	 * Drvn 펌프 사용 여부 데이터를 삽입하는 메서드
	 * @param map 삽입할 데이터를 담은 맵
	 */
	void insertDrvnPumpYnData(HashMap<String, Object> map);

	/**
	 * 펌프 사용 데이터를 조회하는 메서드
	 * @param param 조회 조건을 담은 파라미터
	 * @return 펌프 사용 데이터를 반환
	 */
	List<HashMap<String, Object>> getPumpUse(HashMap<String, Object> param);

	/**
	 * 부하 조회 데이터를 조회하는 메서드
	 * @return 부하 조회 데이터를 반환
	 */
	List<HashMap<String, String>> getLoadInquiry();

	/**
	 * 펌프 사용 여부 데이터를 삽입하는 메서드
	 * @param map 삽입할 데이터를 담은 맵
	 */
	void insertInQuiryPumpYnData(HashMap<String, Object> map);

	/**
	 * 생산 유량 및 압력 데이터를 삽입하는 메서드
	 * @param map 삽입할 데이터를 담은 맵
	 * @return 삽입된 생산 유량 및 압력 데이터를 반환
	 */
	List<HashMap<String, Object>> getInsertUsePrdctFlowPressure(HashMap<String, Object> map);
	/**
	 * 펌프 조합 여부 데이터를 조회하는 메서드
	 * @param param 조회 조건을 담은 파라미터
	 * @return 펌프 조합 여부 데이터를 반환
	 */
	List<HashMap<String, Object>> selectPrdctPumpCombYn(HashMap<String, Object> param);

	/**
	 * 그룹 펌프 조합 여부 데이터를 조회하는 메서드
	 * @param param 조회 조건을 담은 파라미터
	 * @return 그룹 펌프 조합 여부 데이터를 반환
	 */
	List<HashMap<String, Object>> selectGrpPrdctPumpCombYn(HashMap<String, Object> param);

	/**
	 * 현재 유량 및 압력 데이터를 조회하는 메서드
	 * @param param 조회 조건을 담은 파라미터
	 * @return 현재 유량 및 압력 데이터를 반환
	 */
	List<HashMap<String, Object>> curFlowPressure(HashMap<String, Object> param);

	/**
	 * 이전 유량 및 압력 데이터를 조회하는 메서드
	 * @param param 조회 조건을 담은 파라미터
	 * @return 이전 유량 및 압력 데이터를 반환
	 */
	List<HashMap<String, Object>> preFlowPressure(HashMap<String, Object> param);

	/**
	 * 최적 이전 펌프 사용 데이터를 조회하는 메서드
	 * @param pumpMap 펌프 데이터를 담은 파라미터
	 * @return 최적 이전 펌프 사용 데이터를 반환
	 */
	List<Integer> optPrePumpUse(HashMap<String, Object> pumpMap);

	/**
	 * 조회 데이터를 삭제하는 메서드
	 * @param map 삭제할 데이터를 담은 맵
	 */
	void deleteInquiryData(HashMap<String, Object> map);

	/**
	 * 현재 펌프 전력 데이터를 엑셀로 조회하는 메서드
	 * @param param 조회 조건을 담은 파라미터
	 * @return 현재 펌프 전력 데이터를 반환
	 */
	List<HashMap<String, Object>> excelCurPumpPower(HashMap<String, Object> param);

	/**
	 * 이전 펌프 데이터를 엑셀로 조회하는 메서드
	 * @param param 조회 조건을 담은 파라미터
	 * @return 이전 펌프 데이터를 반환
	 */
	List<HashMap<String, Object>> excelPrePumpData(HashMap<String, Object> param);

	/**
	 * 현재 펌프 유량 데이터를 엑셀로 조회하는 메서드
	 * @param param 조회 조건을 담은 파라미터
	 * @return 현재 펌프 유량 데이터를 반환
	 */
	List<HashMap<String, Object>> excelCurPumpFlow(HashMap<String, Object> param);

	/**
	 * 현재 펌프 데이터를 엑셀로 조회하는 메서드
	 * @param param 조회 조건을 담은 파라미터
	 * @return 현재 펌프 데이터를 반환
	 */
	List<HashMap<String, Object>> excelCurPumpPri(HashMap<String, Object> param);

	/**
	 * 현재 펌프 사용 여부 데이터를 엑셀로 조회하는 메서드
	 * @param param 조회 조건을 담은 파라미터
	 * @return 현재 펌프 사용 여부 데이터를 반환
	 */
	List<HashMap<String, Object>> excelCurPumpYn(HashMap<String, Object> param);

	/**
	 * 이전 펌프 사용 여부 데이터를 엑셀로 조회하는 메서드
	 * @param param 조회 조건을 담은 파라미터
	 * @return 이전 펌프 사용 여부 데이터를 반환
	 */
	List<HashMap<String, Object>> excelPrePumpYn(HashMap<String, Object> param);

	/**
	 * 이전 사용 펌프 문자열 데이터를 조회하는 메서드
	 * @param pumpUseParam 조회 조건을 담은 파라미터
	 * @return 이전 사용 펌프 문자열 데이터를 반환
	 */
	HashMap<String, String> getPreUsePumpString(HashMap<String, Object> pumpUseParam);
	/**
	 * 이전 사용 펌프 문자열 데이터를 조회하는 메서드
	 * @param pumpUseParam 조회 조건을 담은 파라미터
	 * @return 이전 사용 펌프 문자열 데이터를 반환
	 */
	HashMap<String, String> getCurUsePumpString(HashMap<String, Object> pumpUseParam);

	/**
	 * 실제 측정된 펌프 조합 데이터를 조회하는 메서드
	 * @param nowMap 조회 조건을 담은 파라미터
	 * @return 실제 측정된 펌프 조합 데이터를 반환
	 */
	List<HashMap<String, Object>> pumpActlMsrmCmbn(HashMap<String, Object> nowMap);

	/**
	 * 생성된 펌프 조합 데이터를 조회하는 메서드
	 * @param nowMap 조회 조건을 담은 파라미터
	 * @return 생성된 펌프 조합 데이터를 반환
	 */
	List<HashMap<String, Object>> pumpPrdcCmbn(HashMap<String, Object> nowMap);

	/**
	 * 펌프 전력 데이터를 조회하는 메서드
	 * @param nowMap 조회 조건을 담은 파라미터
	 * @return 펌프 전력 데이터를 반환
	 */
	List<Double> pumpInstPwr(HashMap<String, Object> nowMap);

	/**
	 * 펌프 유량 데이터를 조회하는 메서드
	 * @param nowMap 조회 조건을 담은 파라미터
	 * @return 펌프 유량 데이터를 반환
	 */
	List<Double> pumpInstFlowRate(HashMap<String, Object> nowMap);

	/**
	 * 펌프 조합 시간을 조회하는 메서드
	 * @param dateTime 조회할 날짜와 시간
	 * @return 펌프 조합 시간을 반환
	 */
	String getPumpCombTime();

	/**
	 * 12시간 내의 현재 펌프 유량 데이터를 조회하는 메서드
	 * @param param 조회 조건을 담은 파라미터
	 * @return 12시간 내의 현재 펌프 유량 데이터를 반환
	 */
	List<HashMap<String, Object>> select12HourCurPumpFlow(HashMap<String, Object> param);

	/**
	 * 12시간 내의 현재 펌프 압력 데이터를 조회하는 메서드
	 * @param param 조회 조건을 담은 파라미터
	 * @return 12시간 내의 현재 펌프 압력 데이터를 반환
	 */
	List<HashMap<String, Object>> select12HourCurPumpPressure(HashMap<String, Object> param);

	/**
	 * 이전 압력 데이터를 조회하는 메서드
	 * @param param 조회 조건을 담은 파라미터
	 * @return 이전 압력 데이터를 반환
	 */
	List<HashMap<String, Object>> selectPrePri(HashMap<String, Object> param);

	/**
	 * 이전 유량 데이터를 조회하는 메서드
	 * @param param 조회 조건을 담은 파라미터
	 * @return 이전 유량 데이터를 반환
	 */
	List<HashMap<String, Object>> selectPreFlow(HashMap<String, Object> param);

	/**
	 * 이전 예측 데이터를 조회하는 메서드
	 * @param param 조회 조건을 담은 파라미터
	 * @return 이전 예측 데이터를 반환
	 */
	List<HashMap<String, Object>> selectPreWithDstrb(HashMap<String, Object> param);

	/**
	 * 펌프 주파수 사용 데이터를 조회하는 메서드
	 * @param param 조회 조건을 담은 파라미터
	 * @return 펌프 주파수 사용 데이터를 반환
	 */
	List<HashMap<String, Object>> getPumpFreqUse(HashMap<String, Object> param);

	/**
	 * 운문 주파수 인덱스를 조회하는 메서드
	 * @param ts 조회할 타임스탬프
	 * @return 운문 주파수 인덱스를 반환
	 */
	Integer getWMFreqIdx(String ts);

	/**
	 * 마지막 현재 주파수를 조회하는 메서드
	 * @param pump_idx 펌프 인덱스가 담긴 파라미터
	 * @return 마지막 현재 주파수를 반환
	 */
	Double getLastCurFreq(HashMap<String, Object> pump_idx);

	/**
	 * Raw 데이터를 조회하는 메서드
	 * @param rawParam 조회 조건을 담은 파라미터
	 * @return Raw 데이터를 반환
	 */
	Double selectRawData(HashMap<String, Object> rawParam);

	/**
	 * [Seoul/Dev] 여러 태그 × 시간 범위 raw 데이터를 단일 쿼리로 조회.
	 * 행 형식: {tagname, ts("yyyy-MM-dd HH:mm:ss"), value}. TAGNAME, TS DESC 정렬.
	 *
	 * @param param  tags(List&lt;String&gt;), startDateTime(String), endDateTime(String)
	 * @return       TB_RAWDATA 행 리스트
	 */
	List<HashMap<String, Object>> selectRawDataRangeForTags(HashMap<String, Object> param);

	/**
	 * 운문 인버터 펌프 주파수 확인 메서드
	 * @param pumpMap 조회할 펌프 맵
	 * @return 펌프 주파수 데이터를 포함한 집합을 반환
	 */
	Set<Integer> wmInverterPumpFreqCheck(HashMap<String, Object> pumpMap);

	/**
	 * 성생된 펌프 조합 엑셀 데이터를 조회하는 메서드
	 * @param param 조회 조건을 담은 파라미터
	 * @return 성생된 펌프 조합 엑셀 데이터를 반환
	 */
	List<HashMap<String, String>> pumpDrvnCombExcelData(HashMap<String, Object> param);

	/**
	 * 고령 압력제어 압력데이터 반환
	 * @return 고령 압력제어 압력데이터 반환
	 */
	HashMap<String, Double> getGrLifePre();

	/**
	 * 고령 공업정수지 이전 예측 압력 데이터 반환
	 * @param combParam
	 * @return
	 */
	List<Double> selectPumpCombPressure(HashMap<String, Object> combParam);

	List<HashMap<String, Object>> grPreesureList(HashMap<String, Object> param);

	List<HashMap<String, Object>> grPrePressure(HashMap<String, Object> param);

	int checkManualOperLog(HashMap<String, Object> checkLogParam);

	List<String> guPumpStatusChange(HashMap<String, Object> statusParam);

	HashMap<String, String> getPreUsePumpStatus(HashMap<String, Object> pumpUseParam);

	void insertManualOperLog(HashMap<String, Object> logInsParma);

	/**
	 * PUMP_GRP 별 가장 최근 제어 명령 시각 조회 (TB_MNL_CHN_LOG).
	 * AI 자동/AI 추천/수동 모든 제어 명령 발사 후 기록되는 통합 lock 시각.
	 * @param param pump_grp
	 * @return 'yyyy-MM-dd HH:mm' 또는 null
	 */
	String selectLastCtrlTime(HashMap<String, Object> param);

	/**
	 * TB_HMI_CTR_TAG 의 가장 최근 실제 명령 발사 시각 (ANLY_CD IN 'RUN', 'STOP', 'FREQ').
	 * 멀티 인스턴스(로컬 백엔드 동시 가동) 환경에서 노드 간 공유 가능한 단일 진실 원천.
	 * PUMP_GRP 컬럼이 직접 없어 그룹 무관 전체 락 의도.
	 * @return 'yyyy-MM-dd HH:mm:ss' 또는 null
	 */
	String selectLastHmiCtrTime();

	/**
	 * 펌프 제어 쿨다운(분) 전역 설정값 조회 (TB_WPP_TAG_CODE, FUNC_TYP='CTRL_LOCK_MIN').
	 * @return 분 단위 Integer. 행이 없으면 null.
	 */
	Integer selectCtrlLockMin();

	/**
	 * 펌프 제어주기(분) 전역 설정값 조회 (tb_ctr_cycle, 전역 단일 행).
	 * @return 분 단위 Integer. 행이 없으면 null.
	 */
	Integer selectCtrlCycleMin();

	/**
	 * 펌프 제어주기(분) 전역 설정값 변경 (tb_ctr_cycle upsert, CYCLE_IDX=1 고정).
	 * @param param lockMin(Integer), updtUser(String|null)
	 */
	void upsertCtrlCycleMin(HashMap<String, Object> param);

	/**
	 * 펌프 예측 가동이력 타임라인 조회 (예측·실측 비교 차트용).
	 * @param param date(yyyy-MM-dd), pump_grp
	 * @return [{ts, PUMP_IDX, PUMP_GRP_IDX, name, PUMP_YN}]
	 */
	List<HashMap<String, Object>> selectPumpForecastTimeline(HashMap<String, Object> param);

	/**
	 * 펌프 실측 가동이력 타임라인 조회 (PMB_TAG 전용, 예측·실측 비교 차트용).
	 * @param param date(yyyy-MM-dd), pump_grp
	 * @return [{ts, PUMP_GRP_IDX, name, value}] (value>0=가동)
	 */
	List<HashMap<String, Object>> selectPumpActualTimeline(HashMap<String, Object> param);

	/**
	 * 펌프 실측 가동이력 조회 (분단위, 최근 24시간, PMB_TAG 전용, 단일차트 비교용).
	 * @param param pump_grp
	 * @return [{ts, PUMP_GRP_IDX, name, value}] (value>0=가동)
	 */
	List<HashMap<String, Object>> selectPumpActualMinute(HashMap<String, Object> param);

	/**
	 * 펌프 예측 가동이력 조회 (RGSTR_TIME 10분단위, 최근 24시간, 단일차트 비교용).
	 * 예측이 10분마다 생성된 시점(RGSTR_TIME) 기준.
	 * @param param pump_grp
	 * @return [{ts, PUMP_IDX, PUMP_GRP_IDX, name, PUMP_YN}]
	 */
	List<HashMap<String, Object>> selectPumpForecastByRgstr(HashMap<String, Object> param);

	/**
	 * 그룹 내 전체 펌프 명단(roster). 비교 차트 y축 고정용(데이터 없는 펌프도 표시).
	 * @param param pump_grp
	 * @return [{PUMP_GRP_IDX, name}]
	 */
	List<HashMap<String, Object>> selectPumpRosterByGrp(HashMap<String, Object> param);

	String getPumpCombLogTime();

	Set<Integer> inverterPumpFreqCheck(HashMap<String, Object> pumpMap);

	Double select5MinuteAvgRawData(HashMap<String, Object> rawParam);

	Double getBaWppValveData(String func_typ);

	void setBaWppValveData(HashMap<String, Object> valveUpdateMap);

	void insertManualOperLogNew(HashMap<String, Object> logInsParma);

	HashMap<String, String> checkManualOperLogPump(HashMap<String, Object> manualOperLogParam);

	HashMap<String, String> getWppData(String func_typ);

	void setWppOnlyDate(HashMap<String, String> param);

	void setWppCombAndStatus(HashMap<String, String> param);

	Double select10MinuteAvgRawData(HashMap<String, Object> rawParam);

	List<HashMap<String, Integer>> getPumpCombination(HashMap<String, Object> combParam);

	List<HashMap<String, Object>> getInsertGsFlowPressure(HashMap<String, Object> map);

	Double getGosanOptLevel(int hour);

	List<HashMap<String, Object>> selectPumpCombCalGosan();

	List<HashMap<String, Object>> getPumpCombinationItem(HashMap<String, Object> map);

	List<HashMap<String, Object>> getGroupPumpCal(HashMap<String, Object> pumpGrp);

	void setPumpListYn(HashMap<String, Object> item);

	void setPumpUseYn(HashMap<String, Object> map);

	void disableGroupPumpCal(HashMap<String, Object> changeCal);

	void enableGroupPumpCal(HashMap<String, Object> changeCal);

	void updatePumpComb(HashMap<String,Object> changeCal);

	List<HashMap<String, Object>> getGrpFlPreTag(int pump_grp);

	List<HashMap<String, Object>> selectPumpCombList(HashMap<String, Object> params);

	void savePumpComb (HashMap<String, Object> params);

	void updatePumpComb(@Param("pumpComb") String pumpComb, @Param("countIdx") int countIdx);

	void updatePumpCombItem (HashMap<String, Object> params);

	Double getHujaOptLevel(int hour);

	// drvnMapper.java
	Double selectAverageValueLast10Minutes(@Param("DSTRB_ID") String dstrbId, @Param("nowDateTime") String nowDateTime);

	List<HashMap<String, Object>> getGrpPumpComb(int grp);

	void insertCombPwrUnit(List<HashMap<String, Object>> insertArr);

	List<HashMap<String, Object>> selectGsAllLinkRange(HashMap<String, Object> param);

	List<HashMap<String, Object>> selectGsAllNodeRange(HashMap<String, Object> param);

	Double selectGsAllLinkFirst(HashMap<String, Object> param);

	Double selectGsAllNodeFirst(HashMap<String, Object> param);

	List<HashMap<String, Object>> selectGsAllCurLinkRange(HashMap<String, Object> param);

	List<HashMap<String, Object>> selectGsAllCurNodeRange(HashMap<String, Object> param);

	Double selectGsAllCurLinkFirst(HashMap<String, Object> param);

	Double selectGsAllCurNodeFirst(HashMap<String, Object> param);
	List<HashMap<String,Object>> selectWaterLevel();

	List<HashMap<String,Object>> selectWaterLevelByRange(HashMap<String, Object> param);

	/**
	 * 수위 5개 태그를 1분 단위 BETWEEN 범위로 조회.
	 * @param param startDate, endDate (yyyy-MM-dd HH:mm:00)
	 */
	List<HashMap<String,Object>> selectWaterLevelByMinuteRange(HashMap<String, Object> param);

	List<HashMap<String,Object>> selectPumpRunHistoryByGrp(HashMap<String, Object> param);

	/**
	 * 윈도 시작(startDate) 직전 각 펌프(PMB_TAG)의 마지막 ON/OFF 상태 시드 조회.
	 * @param param PUMP_GRP, startDate(Timestamp)
	 * @return [{TAGNAME, VALUE, PUMP_IDX}]
	 */
	List<HashMap<String,Object>> selectPumpLastStateBeforeGrp(HashMap<String, Object> param);

	/**
	 * 임의 horizon 예측 유량/수압 조회 (TB_CTR_TNK_RST).
	 * @param param DSTRB_ID, nowDateTime, horizonMin (10/60/120/180/360)
	 */
	List<HashMap<String,Object>> prdctFlowPressureByHorizon(HashMap<String, Object> param);

	/**
	 * 5단계 horizon 펌프 예측 조합 INSERT (TB_CTR_PUMPYN_PRDCT_RST).
	 */
	void insertDrvnPumpYnPrdctData(HashMap<String, Object> param);

	/**
	 * 펌프대수차트 미래 segment 용. 각 horizon 별 최근 산출 결과 반환.
	 * @param param PUMP_GRP, nowDateTime
	 */
	List<HashMap<String,Object>> selectPumpRunCountForecast(HashMap<String, Object> param);

	/**
	 * 다중 태그 × 다중 시점 실측값 조회 (TB_RAWDATA, 10분 정렬).
	 * @param param tagList(List<String>), startDate(yyyy-MM-dd HH:mm:00), endDate(yyyy-MM-dd HH:mm:00)
	 * @return [{tag, ts(yyyy-MM-dd HH:mm), value(Object)}]
	 */
	List<HashMap<String, Object>> selectMultiTagRawRange(HashMap<String, Object> param);

	/**
	 * 다중 태그 × 다중 시점 실측값 조회 (TB_RAWDATA, 1분 단위).
	 * selectMultiTagRawRange 와 동일 파라미터, MINUTE % 10 = 0 필터 미적용.
	 */
	List<HashMap<String, Object>> selectMultiTagRawRangeMinutely(HashMap<String, Object> param);

	/**
	 * 다중 태그 × 다중 PRDCT_TIME 예측값 조회 (TB_CTR_TNK_RST).
	 * 각 (tag, prdctTime) 조합에 대해 nowDateTime 이하의 최신 RGSTR_TIME 행을 반환.
	 * @param param tagList(List<String>), prdctTimeList(List<String>), nowDateTime
	 * @return [{tag, ts(yyyy-MM-dd HH:mm), value(Object)}]
	 */
	List<HashMap<String, Object>> selectMultiTagPrdctRange(HashMap<String, Object> param);

	/**
	 * 다중 태그 × 다중 PRDCT_TIME 의 "10분 horizon" 예측값 조회 (TB_CTR_TNK_RST).
	 * PRDCT_TIME = RGSTR_TIME + 10분 조건을 만족하는 행만 반환 → 실측과의 정확도 비교용.
	 * @param param tagList(List<String>), prdctTimeList(List<String>)
	 * @return [{tag, ts(yyyy-MM-dd HH:mm), value(Object)}]
	 */
	List<HashMap<String, Object>> selectMultiTagPrdct10minHorizon(HashMap<String, Object> param);

	/**
	 * 시점별 펌프 가동 여부 실측 조회 (PMB_TAG via TB_RAWDATA).
	 * @param param pumpGrp, startDate, endDate
	 * @return [{ts, pumpIdx, pumpYn}]
	 */
	List<HashMap<String, Object>> selectMultiTimePumpYnRaw(HashMap<String, Object> param);

	/**
	 * 시점별 펌프 가동 여부 실측 조회 (1분 단위).
	 * selectMultiTimePumpYnRaw 와 동일 파라미터, MINUTE % 10 = 0 필터 미적용.
	 */
	List<HashMap<String, Object>> selectMultiTimePumpYnRawMinutely(HashMap<String, Object> param);

	/**
	 * 가장 최근 RGSTR_TIME 기준 펌프별 예측 가동 여부 (TB_CTR_PUMPYN_RST).
	 * @param param pumpGrp, nowDateTime
	 * @return [{pumpIdx, pumpYn}]
	 */
	List<HashMap<String, Object>> selectLatestPumpYnPrdct(HashMap<String, Object> param);

	/**
	 * (10분 스냅샷) 예측 유량 조회 - TB_CTR_TNK_RST.
	 * RGSTR_TIME / PRDCT_TIME 이 snapshot 과 정확 동치인 행 1건. 없으면 null.
	 * 대시보드 성능점/예측점 유량과 동일 행을 보장.
	 * @param param dstrb_id, rgstr_time, prdct_time
	 * @return {prdct_flow:Double, prdct_time:String}
	 */
	HashMap<String, Object> selectLatestPredictedFlowTnk(HashMap<String, Object> param);

	/**
	 * (10분 스냅샷) 예측 펌프 조합 CSV + 결정 시각 조회 - TB_CTR_PUMPYN_RST 최신 RGSTR_TIME (≤ rgstr_time).
	 * @param param pump_grp, rgstr_time
	 * @return { prdct_comb: "1,3,4,6"(모두 OFF면 null), actl_ref_ts: "yyyy-MM-dd HH:mm:ss" (예측이 결정된 시각) }
	 */
	HashMap<String, Object> selectLatestPredictedComb(HashMap<String, Object> param);

	/**
	 * (10분 스냅샷) 실측 유량 조회 - TB_CTR_TNK_INF.DSTRB_Q_ID 로 매칭한 FRI_TAG 의 TB_RAWDATA 값.
	 * TS = snapshot.rgstr_time 정확 동치 행 1건. 없으면 null.
	 * 예측 유량(tnk_rst) 와 동일 지점·동일 시각 보장 → 대시보드 성능점/예측점과 일치.
	 * @param param dstrb_id, rgstr_time
	 * @return {ts, value}
	 */
	HashMap<String, Object> selectLatestActualFlow(HashMap<String, Object> param);

	/**
	 * (10분 스냅샷) 최신 실측 펌프 조합 CSV 조회 (PMB_TAG, TB_RAWDATA).
	 * @param param pump_grp, snapshot_time
	 * @return "1,3,4,6" (펌프 모두 OFF면 null)
	 */
	String selectLatestActualComb(HashMap<String, Object> param);

	/**
	 * (10분 스냅샷) TB_PUMP_FLOW_COMB_SNAPSHOT 적재 (멱등).
	 * 컬럼: rgstr_time, prdct_time, pump_grp,
	 *       prdct_flow, actl_flow, prdct_comb, actl_comb,
	 *       prdct_prsr, actl_prsr,
	 *       prdct_wl_gn|ba|gr|wg|wgok, actl_wl_gn|ba|gr|wg|wgok
	 * 새 컬럼은 null 허용. ON DUPLICATE KEY UPDATE 에서 COALESCE 로 기존 값 보존.
	 */
	int insertFlowCombSnapshot(HashMap<String, Object> param);

	/**
	 * (10분 스냅샷) 예측 압력: TB_CTR_TNK_RST 에서 압력 DSTRB_ID 의 RGSTR_TIME / PRDCT_TIME 정확 동치 PRDCT_VALUE.
	 * @param param dstrb_id, rgstr_time, prdct_time
	 * @return {prdct_prsr, prdct_time} 없으면 null
	 */
	HashMap<String, Object> selectLatestPredictedPressureTnk(HashMap<String, Object> param);

	/**
	 * (10분 스냅샷) 실측 압력: TB_RAWDATA 에서 압력 태그(=DSTRB_ID) 의 RGSTR_TIME 동일 시각 VALUE.
	 * @param param tag, rgstr_time
	 * @return {ts, value} 없으면 null
	 */
	HashMap<String, Object> selectLatestActualPressure(HashMap<String, Object> param);

	/**
	 * (10분 스냅샷) 배수지 예측 수위 평균: TB_CTR_TNK_RST 에서 tagList 의 RGSTR_TIME / PRDCT_TIME 정확 동치 PRDCT_VALUE 평균.
	 * @param param tagList(List&lt;String&gt;), rgstr_time, prdct_time
	 * @return {avg_value, tank_count}
	 */
	HashMap<String, Object> selectLatestPredictedReservoirAvg(HashMap<String, Object> param);

	/**
	 * (10분 스냅샷) 배수지 실측 수위 평균: TB_RAWDATA 에서 tagList 의 RGSTR_TIME 동일 시각 VALUE 평균.
	 * @param param tagList(List&lt;String&gt;), rgstr_time
	 * @return {avg_value, tank_count}
	 */
	HashMap<String, Object> selectLatestActualReservoirAvg(HashMap<String, Object> param);

	/**
	 * (10분 스냅샷) 실측 전력: 설정(dstrb.prdct.pumpActivePwrTag) 유효전력 IKW 태그를 TB_RAWDATA
	 * actl_ref_ts 윈도우 태그별 최신 1건으로 단순 SUM. IKW 는 순시 kW → 차분/단위환산 없음. 시스템 전체값.
	 * @param param actl_ref_ts(String), pwrTagList(List&lt;String&gt;)
	 * @return {value, tag_count} — tag_count=0 이면 매칭 raw 없음.
	 */
	HashMap<String, Object> selectLatestActualPowerSum(HashMap<String, Object> param);

	/**
	 * (10분 스냅샷) 예측 전력: TB_PEAK_PWR_PRDCT_RST.PRDCT_PWR.
	 * CNFRM_TIME∈[rgstr_time, prdct_time), ANLY_TIME∈[prdct_time, prdct_time+10m) 의 최신 1건.
	 * @param param rgstr_time(String), prdct_time(String)
	 * @return {prdct_pwr, cnfrm_time, anly_time} 없으면 null.
	 */
	HashMap<String, Object> selectLatestPredictedPower(HashMap<String, Object> param);

	/**
	 * (10분 스냅샷 backfill) 최근 N 시간 내 한 컬럼이라도 null 인 snapshot 행 조회.
	 * @param param nowDateTime(String), hoursBack(int)
	 * @return rgstr_time, prdct_time, pump_grp, 측정 컬럼들(전력 포함) (null/값).
	 */
	java.util.List<HashMap<String, Object>> selectRecentSnapshotsWithNulls(HashMap<String, Object> param);

	/**
	 * (정확도) 항목별 오차율/정확도 UPSERT (TB_PUMP_FLOW_COMB_ACCURACY). 스냅샷과 동일 PK.
	 * @param param rgstr_time, prdct_time, pump_grp, err_ / acc_ 항목값.
	 */
	int insertFlowCombAccuracy(HashMap<String, Object> param);

	/**
	 * (엑셀 다운로드) 기간 [from, to] 스냅샷 + 정확도 LEFT JOIN 결과.
	 * 항목별로 PRDCT_/ACTL_/ACC_/ERR_ 가 한 행에 모여 있다. 수위 컬럼은 ROUND(2) 적용.
	 * @param param from(String "yyyy-MM-dd HH:mm:ss"), to(String)
	 * @return RGSTR_TIME 내림차순(최근이 먼저), PUMP_GRP 오름차순 전 컬럼.
	 */
	java.util.List<HashMap<String, Object>> selectSnapshotsWithAccuracyByRange(HashMap<String, Object> param);

}
