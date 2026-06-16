package kr.co.mindone.ems.energy;
/**
 * packageName    : kr.co.mindone.ems.energy
 * fileName       : EnerSpendService
 * author         : geunwon
 * date           : 24. 9. 23.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 9. 23.        geunwon       최초 생성
 */
import kr.co.mindone.ems.common.CommonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Service
public class EnerSpendService {
	@Autowired
	private EnerSpendMapper enerSpendMapper;
	@Autowired
	private CommonMapper commonMapper;

	/**
	 * 선택된 시설의 설비 목록 반환
	 * @param map 조회에 필요한 파라미터
	 * @return 선택된 시설의 설비 목록 반환
	 */
	List<HashMap<String, Object>> selectFacUseSubList(HashMap<String, Object> map) {
		return enerSpendMapper.selectFacUseSubList(map);
	}

	/**
	 * 시설의 전력 값 반환
	 * @param map 조회에 필요한 파라미터
	 * @return 시설의 전력 값 반환
	 */
	List<HashMap<String, Object>> selectFacUseList(HashMap<String, Object> map) {
		return enerSpendMapper.selectFacUseList(map);
	}

	/**
	 * 시설의 전력 합 값 반환
	 * @param map 조회에 필요한 파라미터
	 * @return 시설의 전력 합 값
	 */
	List<HashMap<String, Object>> selectFacUseList_sum(HashMap<String, Object> map) {
		return enerSpendMapper.selectFacUseList_sum(map);
	}

	/**
	 * 설비의 전력 값 반환
	 * @param map 조회에 필요한 파라미터
	 * @return 설비의 전력 값
	 */
	List<HashMap<String, Object>> selectZoneUseList(HashMap<String, Object> map) {
		return enerSpendMapper.selectZoneUseList(map);
	}
	
	/**
	 * 설비의 전력 합 값 반환
	 * @param map 조회에 필요한 파라미터
	 * @return 설비의 전력 합
	 */
	@Transactional
	public List<HashMap<String, Object>> selectZoneUseList_sum(HashMap<String, Object> map) {
		List<HashMap<String, Object>> selectZoneUseList_sum = enerSpendMapper.selectZoneUseList_sum(map);
		List<HashMap<String, Object>> nowElec = selectNowElec();
		HashMap<String, Object> pwiMap = new HashMap<>();
		pwiMap.put("zone_code", "총전력");
		pwiMap.put("y", (nowElec.get(0)).get("nowPwi"));
		selectZoneUseList_sum.add(pwiMap);
		double allPwq = enerSpendMapper.selectAllPwq(map);
		HashMap<String, Object> pwqMap = new HashMap<>();
		pwqMap.put("zone_code", "총전력량");
		pwqMap.put("y", allPwq);
		selectZoneUseList_sum.add(pwqMap);

		return selectZoneUseList_sum;
	}

	/**
	 * 시설 전력 순시 
	 * @param map 조회에 필요한 파라미터
	 * @return 시설 전력 순시 
	 */
	public List<HashMap<String, Object>> sisul_sunsi(HashMap<String, Object> map) {
		return enerSpendMapper.sisul_sunsi(map);
	}

	/**
	 * 펌프 정보 리스트
	 * @param map 조회에 필요한 파라미터
	 * @return 펌프 가동 여부 및 정보
	 */
	List<HashMap<String, Object>> selectPumpPerformList(HashMap<String, Object> map) {
		return enerSpendMapper.selectPumpPerformList(map);
	}
	
	/**
	 * 산성 정수장 펌프 정보 리스트
	 * @param map 조회에 필요한 파라미터
	 * @return 산성 정수장 펌프 가동 여부 및 정보
	 */
	List<HashMap<String, Object>> selectPumpPerformList_ss_pwi(HashMap<String, Object> map) {
		return enerSpendMapper.selectPumpPerformList_ss_pwi(map);
	}

	/**
	 * 설비 정보 출력
	 * @param map 조회에 필요한 파라미터
	 * @return 설비 정보
	 */
	List<HashMap<String, Object>> selectFac(HashMap<String, Object> map) {
		return enerSpendMapper.selectFac(map);
	}

	/**
	 * 설비 순시 전력 차트 데이터
	 * @param map 조회에 필요한 파라미터
	 * @return 선비 순시 전력 차트 데이터
	 */
	List<HashMap<String, Object>> sunsiChart(HashMap<String, Object> map) {
		return enerSpendMapper.sunsiChart(map);
	}

	/**
	 * 시설 별 전력 순시
	 * @param map 조회에 필요한 파라미터
	 * @return 시설 별 전력 순시
	 */
	List<HashMap<String, Object>> selectFacSunsi(HashMap<String, Object> map) {	
		return enerSpendMapper.selectFacSunsi(map);	
	}

	/**
	 * 정수장 총 전력 순시 데이터
	 * @return 정수장 총 전력 순시 데이터
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	List<HashMap<String, Object>> selectNowElec() {
		return enerSpendMapper.selectNowElec();
	}

	/**
	 * 현재 전력 피크 데이터 
	 * @return 현재 전력 피크 데이터
	 */
	List<HashMap<String, Object>> selectNowPeak() {
		return enerSpendMapper.selectNowPeak();
	}

	/**
	 * 전년, 전월, 전일 전력 데이터 출력
	 * @return 전년, 전월, 전일 전력 데이터 출력
	 */
	List<HashMap<String, Object>> selectYMD() {
		return enerSpendMapper.selectYMD();
	}

	/**
	 * 메인 화면 전력 절감량 데이터 출력
	 * @return 전력 절감량 데이터
	 */
	List<HashMap<String, Object>> baseElec() {
		return enerSpendMapper.baseElec();
	}

	/**
	 * 월별 전력 절감 데이터 출력
	 * @return 월별 전력 절감 데이터 출력
	 */
	List<HashMap<String, Object>> rstSavingTargetSum() {
		return enerSpendMapper.rstSavingTargetSum();
	}

	/**
	 * 요금제 정보에 따른 전력요금 출력
	 * @param map 조회에 필요한 데이터
	 * @return 요금제정보에 따른 전력요금
	 */
	List<HashMap<String, Object>> selectRateInfo(HashMap<String, Object> map) {
		//String mnth = LocalDate.now().format(DateTimeFormatter.ofPattern("MM"));
		return enerSpendMapper.selectRateInfo(map);
	}

	/**
	 * 시설별 전력 피크값 출력
	 * @param search 조회에 필요한 데이터
	 * @return 시설별 전력 피크 값
	 */
	List<HashMap<String, Object>> peakFac(String search) {
		return enerSpendMapper.peakFac(search);
	}

	/**
	 * 현재 전력 피크값 출력
	 * @return 현재 전력 피크
	 */
	public List<HashMap<String, Object>> nowPeak() {
		return enerSpendMapper.nowPeak();
	}

	/**
	 * 전력 피크 분석 종합 데이터 조회
	 * @return 
	 */
	public List<HashMap<String, Object>> getPowerPeakAnalysisData() {

		/* 
			<왼쪽>
			1.총 전력 : PK00KE_E_SVHV4NSVCB_IKW 값
			2.송수펌프 순시 전력 : 송수펌프 태그들의 실시간 순시전력 합
			3.목표 피크 전력 : 사용자가 입력(저장)한 목표 피크 전력
			4.요금 적용 전력 피크 : 총 전력인데 태그VALUE * PWQ_UNIT_VALUE 한것.
			5.전력 피크 예상 시간 : 현재 상태로 얼마나 더 가면 언제쯤 피크에 도달하는지? 총 순시 전력 예측값 >= 목표 피크 전력 값의 시점

			<오른쪽1 - 차트>
			1.용수수요예측 펌프사용 전력량 예측
			=> 총 순시 전력(예측값), 총 순시 전력(실측값)
			=> 송수펌프 전력(예측값), 송수펌프 전력(실측값)
			=> 중계펌프 전력(예측값), 중계펌프 전력(실측값)
			=> 회수펌프 전력(예측값), 회수펌프 전력(실측값)

			<오른쪽2 - 차트>
			1.전력 피크 예상 시간
			=> 목표 피크 전력 : <왼쪽1번> 사용하면될듯 
			=> 요금 적용 전력 피크 : <왼쪽4번> 사용하면될듯
			=> 예상전력 : 그 시간대 예상전력
		*/

		// 전력 피크 분석 좌측 요약 카드에 필요한 총 전력, 송수펌프 전력, 월 목표 피크, 요금 적용 피크를 한 번에 조합합니다.
		final String totalPowerTag = "PK00KE_E_SVHV4NSVCB_IKW";
		List<HashMap<String, Object>> resultList = new ArrayList<>();
		HashMap<String, Object> resultMap = new HashMap<>();
		List<String> predictionChartX = new ArrayList<>();
		List<Double> predictionChartY = new ArrayList<>();
		List<String> totalPowerChartX = new ArrayList<>();
		List<Double> totalPowerChartY = new ArrayList<>();
		String peakRemainTime = "-";
		Integer peakRemainMinutes = null;

		// 1. 공통 최신 태그 조회를 재사용해 총 전력 태그의 최근 값을 읽습니다.
		HashMap<String, Object> totalTagParam = new HashMap<>();
		totalTagParam.put("tagIds", Arrays.asList(totalPowerTag));
		List<HashMap<String, Object>> totalTagData = commonMapper.selectLatestTagData(totalTagParam);

		double allPwi = 0.0;
		String ts = null;
		if (totalTagData != null && !totalTagData.isEmpty()) {
			HashMap<String, Object> totalTagRow = totalTagData.get(0);
			Object totalValueObj = totalTagRow.get("VALUE");
			Object tsObj = totalTagRow.get("TS");

			if (tsObj != null) {
				ts = tsObj.toString();
			}

			if (totalValueObj instanceof Number) {
				allPwi = ((Number) totalValueObj).doubleValue();
			} else if (totalValueObj != null) {
				String totalValueText = totalValueObj.toString().trim();
				if (!totalValueText.isEmpty() && !"-".equals(totalValueText)) {
					allPwi = Double.parseDouble(totalValueText);
				}
			}
		}

		// 1-1. 우측 하단 차트는 총 전력 태그의 최근 24시간 1분 데이터를 사용합니다.
		HashMap<String, Object> totalPowerChartParam = new HashMap<>();
		totalPowerChartParam.put("tagName", totalPowerTag);
		List<HashMap<String, Object>> totalPowerHistory = enerSpendMapper.selectTagData24H(totalPowerChartParam);
		for (HashMap<String, Object> totalPowerHistoryRow : totalPowerHistory) {
			if (totalPowerHistoryRow == null) {
				continue;
			}

			Object historyTsObj = totalPowerHistoryRow.get("TS");
			Object historyValueObj = totalPowerHistoryRow.get("VALUE");

			if (historyTsObj != null) {
				totalPowerChartX.add(historyTsObj.toString());
			}

			double historyValue = 0.0;
			if (historyValueObj instanceof Number) {
				historyValue = ((Number) historyValueObj).doubleValue();
			} else if (historyValueObj != null) {
				String historyValueText = historyValueObj.toString().trim();
				if (!historyValueText.isEmpty() && !"-".equals(historyValueText)) {
					historyValue = Double.parseDouble(historyValueText);
				}
			}
			totalPowerChartY.add(Math.round(historyValue * 100d) / 100d);
		}

		// 2. 송수동 모니터링 대상 태그들을 가져와 최신값을 모두 더해 송수펌프 순시 전력을 계산합니다.
		HashMap<String, Object> peakTagParam = new HashMap<>();
		peakTagParam.put("clsfc", "송수동");
		peakTagParam.put("is_all", "0");
		peakTagParam.put("mntr_yn", "1");
		List<HashMap<String, Object>> peakTagList = enerSpendMapper.selectPeakTagList(peakTagParam);

		List<String> pumpTagIds = new ArrayList<>();
		for (HashMap<String, Object> peakTag : peakTagList) {
			if (peakTag == null || peakTag.get("PWI_TAG") == null) {
				continue;
			}

			String pwiTag = peakTag.get("PWI_TAG").toString().trim();
			if (!pwiTag.isEmpty()) {
				pumpTagIds.add(pwiTag);
			}
		}

		double pumpPwi = 0.0;
		if (!pumpTagIds.isEmpty()) {
			HashMap<String, Object> pumpTagParam = new HashMap<>();
			pumpTagParam.put("tagIds", pumpTagIds);
			List<HashMap<String, Object>> pumpTagData = commonMapper.selectLatestTagData(pumpTagParam);

			for (HashMap<String, Object> pumpTagRow : pumpTagData) {
				if (pumpTagRow == null) {
					continue;
				}

				Object pumpValueObj = pumpTagRow.get("VALUE");
				if (pumpValueObj instanceof Number) {
					pumpPwi += ((Number) pumpValueObj).doubleValue();
					continue;
				}

				if (pumpValueObj == null) {
					continue;
				}

				String pumpValueText = pumpValueObj.toString().trim();
				if (pumpValueText.isEmpty() || "-".equals(pumpValueText)) {
					continue;
				}
				pumpPwi += Double.parseDouble(pumpValueText);
			}
		}

		// 3. 이번 달 목표 피크 전력은 월별 피크 테이블에서 읽고, 데이터가 없으면 기본값 10000을 사용합니다.
		String ym = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
		HashMap<String, Object> monthlyPeakParam = new HashMap<>();
		monthlyPeakParam.put("ym", ym);
		HashMap<String, Object> monthlyPeak = enerSpendMapper.selectMonthlyPeak(monthlyPeakParam);

		double goalPeak = 10000.0;
		if (monthlyPeak != null && monthlyPeak.get("goalPeak") != null) {
			Object goalPeakObj = monthlyPeak.get("goalPeak");
			if (goalPeakObj instanceof Number) {
				goalPeak = ((Number) goalPeakObj).doubleValue();
			} else {
				String goalPeakText = goalPeakObj.toString().trim();
				if (!goalPeakText.isEmpty()) {
					goalPeak = Double.parseDouble(goalPeakText);
				}
			}
		}

		// 4. 총 전력 태그의 12개월치 중 MAX값에 PWI_UNIT_VALUE를 곱해 요금 적용 전력 피크를 계산합니다.
		HashMap<String, Object> unitValueParam = new HashMap<>();
		unitValueParam.put("pwi_tag", totalPowerTag);
		HashMap<String, Object> costPwr = enerSpendMapper.selectPeakTagUnitValue(unitValueParam);

		double costValue = 0;
		if (costPwr != null && costPwr.get("pwiUnitValue") != null) {
			Object unitValueObj = costPwr.get("pwiUnitValue");
			if (unitValueObj instanceof Number) {
				costValue = ((Number) unitValueObj).doubleValue();
			} else {
				String unitValueText = unitValueObj.toString().trim();
				if (!unitValueText.isEmpty()) {
					costValue = Double.parseDouble(unitValueText);
				}
			}
		}

		// 5. 송수수요예측 펌프사용 전력량 예측값 (향후 현재시간 이후의 값(예측값)을 붙혀서 보여줘야할꺼임)
		// 그리고 그 중 요금 적용 전력 피크 수치보다 높아지는 시점을 좌측 전력 피크 예상 시간에 표출
		List<HashMap<String, Object>> futurePeakPredictData = enerSpendMapper.selectFuturePeakPredictData();
		for (HashMap<String, Object> futurePeakRow : futurePeakPredictData) {
			if (futurePeakRow == null) {
				continue;
			}

			Object anlyTimeObj = futurePeakRow.get("anlyTime");
			Object prdctPwrObj = futurePeakRow.get("prdctPwr");
			Object peakYnObj = futurePeakRow.get("peakYn");
			Object remainMinutesObj = futurePeakRow.get("remainMinutes");

			if (anlyTimeObj != null) {
				predictionChartX.add(anlyTimeObj.toString());
			}

			double prdctPwr = 0.0;
			if (prdctPwrObj instanceof Number) {
				prdctPwr = ((Number) prdctPwrObj).doubleValue();
			} else if (prdctPwrObj != null) {
				String prdctPwrText = prdctPwrObj.toString().trim();
				if (!prdctPwrText.isEmpty()) {
					prdctPwr = Double.parseDouble(prdctPwrText);
				}
			}
			predictionChartY.add(Math.round(prdctPwr * 100d) / 100d);

			if (!"Y".equals(String.valueOf(peakYnObj)) || peakRemainMinutes != null) {
				continue;
			}

			int remainMinutes = 0;
			if (remainMinutesObj instanceof Number) {
				remainMinutes = ((Number) remainMinutesObj).intValue();
			} else if (remainMinutesObj != null) {
				String remainMinutesText = remainMinutesObj.toString().trim();
				if (!remainMinutesText.isEmpty()) {
					remainMinutes = Integer.parseInt(remainMinutesText);
				}
			}

			if (remainMinutes >= 0) {
				peakRemainMinutes = remainMinutes;
				int remainHour = remainMinutes / 60;
				int remainMinute = remainMinutes % 60;
				peakRemainTime = String.format("%02d:%02d", remainHour, remainMinute);
			}
		}

		resultMap.put("allPwi", Math.round(allPwi * 100d) / 100d);
		resultMap.put("pumpPwi", Math.round(pumpPwi * 100d) / 100d);
		resultMap.put("goalPeak", Math.round(goalPeak * 100d) / 100d);
		resultMap.put("costPwr", Math.round(costValue * 100d) / 100d);
		resultMap.put("peakRemainTime", peakRemainTime);
		resultMap.put("peakRemainMinutes", peakRemainMinutes);
		resultMap.put("predictionChartX", predictionChartX);
		resultMap.put("predictionChartY", predictionChartY);
		resultMap.put("totalPowerChartX", totalPowerChartX);
		resultMap.put("totalPowerChartY", totalPowerChartY);
		resultMap.put("ym", ym);
		resultMap.put("baseTag", totalPowerTag);
		resultMap.put("ts", ts);

		resultList.add(resultMap);
		return resultList;
	}

}
