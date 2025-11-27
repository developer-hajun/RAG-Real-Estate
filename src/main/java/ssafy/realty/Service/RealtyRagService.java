package ssafy.realty.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RealtyRagService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public RealtyRagService(ChatClient.Builder chatClinentBuilder, VectorStore vectorStore) {
        this.chatClient = chatClinentBuilder.build();
        this.vectorStore = vectorStore;
    }

    public String getRealtyRecommendation(String userQuery) {

        SearchRequest searchRequest = SearchRequest.builder()
                .query(userQuery)
                .topK(5)
                .build();

        List<Document> similarDocuments = vectorStore.similaritySearch(searchRequest);
        
        if (similarDocuments.isEmpty()) {
            return "죄송합니다. 요청하신 조건에 맞는 매물 정보를 찾을 수 없습니다.";
        }

        String context = similarDocuments.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n"));
        
        log.info("검색된 매물 개수: {}", similarDocuments.size());

        return chatClient.prompt()
                .system(sp -> sp.text("""
                    당신은 친절하고 전문적인 부동산 매물 추천 AI입니다.
                    아래 제공된 [매물 정보]를 바탕으로 사용자의 질문에 답변하세요.
                    
                    - 제공된 정보에 없는 내용은 지어내지 말고 모른다고 답하세요.
                    - 보증금과 월세 정보를 명확하게 언급하세요.
                    - 매물의 이름과 주소를 함께 알려주세요.
                    """))
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

        //1. 답변 예시 추가 , 2. 답변 듣는 대상 특정
    }
}