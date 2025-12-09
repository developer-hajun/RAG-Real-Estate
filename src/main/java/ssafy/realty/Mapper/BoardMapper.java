package ssafy.realty.Mapper;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import ssafy.realty.Entity.Board; // Board 엔티티 경로를 가정합니다.

@Mapper
public interface BoardMapper {


    List<Board> selectAllBoardsWithoutPosts();


    Board selectBoardWithPostsById(@Param("id") int id);
}