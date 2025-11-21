package ssafy.realty.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private int id;

    private String email;
    private String password;
    private String name;
    private int age;
    private List<Post> myPosts;
    private List<Post> myComments;
    private List<SearchHistory> searchHistories;

    private LocalDateTime birthDate;

    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

}
