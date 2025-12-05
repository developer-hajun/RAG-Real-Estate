package ssafy.realty.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import ssafy.realty.DTO.Response.RealtyResponseDto;
import ssafy.realty.Entity.Realty;
import ssafy.realty.DTO.Response.RealtyRecommendationResponse;
import ssafy.realty.Mapper.RealtyMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RealtyRagService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final RealtyMapper realtyMapper;

    public RealtyRecommendationResponse getRealtyRecommendation(String userQuery) {

        // 1. 벡터 검색 설정 (상위 5개)
        SearchRequest searchRequest = SearchRequest.builder()
                .query(userQuery)
                .topK(5)
                .build();

        // 2. 유사도 검색 수행
        List<Document> similarDocuments = vectorStore.similaritySearch(searchRequest);

        // 검색 결과가 없을 경우 빈 리스트 반환
        if (similarDocuments.isEmpty()) {
            return RealtyRecommendationResponse.builder()
                    .aiMessage("죄송합니다. 요청하신 조건에 맞는 매물 정보를 찾을 수 없습니다.")
                    .realties(new ArrayList<>())
                    .build();
        }

        // 3. Document 메타데이터에서 ID 추출 (String -> Integer 변환)
        List<Integer> realtyIds = similarDocuments.stream()
                .map(doc -> {
                    String idStr = (String) doc.getMetadata().get("realty_id");
                    return Integer.parseInt(idStr); // int 변환
                })
                .collect(Collectors.toList());

        log.info("검색된 매물 ID 목록: {}", realtyIds);

        // 4. 추출한 ID 리스트로 DB 일괄 조회 (여기가 질문하신 부분의 핵심!)
        List<Realty> recommendedRealties = realtyMapper.selectRealtyListByIds(realtyIds);

        // 5. AI 프롬프트 생성용 텍스트 병합
        String context = similarDocuments.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n"));

        // 6. AI 응답 생성 (ChatClient)
        String systemText = """
            당신은 '전문 부동산 컨설턴트'입니다.
            제공된 [매물 정보]를 바탕으로 사용자에게 매물을 추천해주세요.
            매물의 이름, 가격, 위치를 명확히 언급하고 방문을 유도하세요.
            """;

        String aiResponse = chatClient.prompt()
                .system(systemText)
                .user(u -> u.text("""
                    [매물 정보]
                    {context}

                    [사용자 질문]
                    {query}
                    """)
                        .param("context", context)
                        .param("query", userQuery)
                )
                .call()
                .content();

        // 7. 최종 DTO 반환 (메시지 + 데이터)
        List<RealtyResponseDto> realtyResponseDtos = changeDto(recommendedRealties);
        return RealtyRecommendationResponse.builder()
                .aiMessage(aiResponse)            // 텍스트 설명
                .realties(realtyResponseDtos)    // 실제 데이터 객체 리스트
                .build();
    }

    protected List<RealtyResponseDto> changeDto(List<Realty> realties){
        return realties.stream()
                .map(RealtyResponseDto::new)
                .collect(Collectors.toList());
    }
}