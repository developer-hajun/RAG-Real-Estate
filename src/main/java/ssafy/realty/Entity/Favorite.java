
package ssafy.realty.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Favorite {
    private int id;
    private User user;
    private Realty realty;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
