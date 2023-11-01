package kopo.poly.community.service.impl;

import kopo.poly.community.dto.CommunityDTO;
import kopo.poly.community.persistance.mapper.ICommunityMapper;
import kopo.poly.community.service.ICommunityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommunityService implements ICommunityService {

    // @RequiredArgsConstructor으로 생성자를 자동으로 생성함
    // noticeMapper 변수에 이미 메모리에 올라간 INoticeMapper 객체를 넣어줌
    // 예전에는 autowired 어노테이션을 통해 생성자를 설정했지만, 이제는 생성자를 통해 객체를 주입시킴
    private final ICommunityMapper communityMapper;
    @Override
    public List<CommunityDTO> getCommunityList() throws Exception {

        log.info(this.getClass().getName() + ".getCommunityList Start!");

        return communityMapper.getCommunityList(); // -> Mapper 호출(SQL문 호출함)
    }

    @Transactional // -> 데이터의 변화(등록,수정,삭제)가 발생하는 함수는 반드시 트랜잭션 처리가 필요함
                    // 트랜잭션 설정으로 ACID를 적용시켜야함 Transactional 어노테이션으로 Commit과 rollback을 지원
    @Override
    public CommunityDTO getCommunityInfo(CommunityDTO pDTO, boolean type) throws Exception {

        log.info(this.getClass().getName() + ".getCommunityInfo Start!");

        //상세보기 할 때 마다, 조회수 증가하기(수정보기는 하지 않고)
        if (type) { // -> 조회수 증가에 따라 트랜잭션 필요
            log.info("Update ReadCNT");
            communityMapper.updateCommunityReadCnt(pDTO);
        }
        return communityMapper.getCommunityInfo(pDTO); // -> Mapper 호출(SQL문 호출함)
    }

    @Transactional
    @Override
    public void insertCommunityInfo(CommunityDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".insertCommunityInfo Start!");

        communityMapper.insertCommunityInfo(pDTO);

    }

    @Transactional
    @Override
    public void updateCommunityInfo(CommunityDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".updateCommunityInfo Start!");

        communityMapper.updateCommunityInfo(pDTO);
    }

    @Transactional
    @Override
    public void deleteCommunityInfo(CommunityDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".deleteCommunityInfo Start!");

        communityMapper.deleteCommunityInfo(pDTO);

    }
}
