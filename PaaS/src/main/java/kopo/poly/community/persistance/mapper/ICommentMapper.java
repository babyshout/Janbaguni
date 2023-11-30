package kopo.poly.community.persistance.mapper;

import kopo.poly.community.dto.CommentDTO;
import kopo.poly.community.dto.CommunityDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ICommentMapper {
    List<CommentDTO> getCommentList(CommunityDTO pDTO) throws Exception;

    CommentDTO getComment(CommentDTO pDTO) throws Exception;

//    CommentDTO getCommentInfo(CommentDTO pDTO, boolean type) throws Exception;


    void insertCommentInfo(CommentDTO pDTO) throws Exception;

    void updateCommentInfo(CommentDTO pDTO) throws Exception;

    void deleteCommentInfo(CommentDTO pDTO) throws Exception;
}
