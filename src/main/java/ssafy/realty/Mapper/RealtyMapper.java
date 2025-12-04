package ssafy.realty.Mapper;

import org.apache.ibatis.annotations.Mapper;
import ssafy.realty.Entity.Realty;

import java.util.List;

@Mapper
public interface RealtyMapper {

    List<Realty> selectAllRealty();

    List<Realty> selectRealtyListByIds(List<Integer> ids);
}
