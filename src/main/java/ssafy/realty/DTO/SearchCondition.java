package ssafy.realty.DTO;

public record SearchCondition(
    String region,       // 지역 (예: 부산, 서울, NONE)
    String tradeType,    // 거래유형 (예: 전세, 월세, 매매, ALL)
    Integer maxPrice,    // 최대 보증금/전세가 (단위: 만원, 조건 없으면 null)
    Integer maxMonthly   // 최대 월세 (단위: 만원, 조건 없으면 null)
) {}