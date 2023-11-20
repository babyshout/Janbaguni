package kopo.poly.community.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CommentDTO {

    private String rNO; //기본키 댓글번호
    private String writer; // 작성자
    private String contents; // 댓글내용
    private String wdate; // 작성날짜



}
