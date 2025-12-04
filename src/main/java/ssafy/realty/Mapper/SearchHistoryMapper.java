package ssafy.realty.Mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import ssafy.realty.Entity.SearchHistory;

import java.util.List;

@Mapper
public interface SearchHistoryMapper {

    List<SearchHistory> searchHistory(int userId);

    List<SearchHistory> searchHistoryTop3(int userId);

    int addSearchHistory(@Param("searchHistory") SearchHistory searchHistory, @Param("userId") int userId);
}
