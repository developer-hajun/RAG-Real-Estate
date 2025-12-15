package ssafy.realty.DTO.Request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RealtyRagQuestionRequestDto {
    private String question;
    private Integer topK = 5;
}
