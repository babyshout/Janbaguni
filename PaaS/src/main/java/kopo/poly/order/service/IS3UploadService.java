package kopo.poly.order.service;

import kopo.poly.order.dto.ImageDTO;
import org.springframework.web.multipart.MultipartFile;

public interface IS3UploadService {

    // 파일 업로드
    String upload(MultipartFile mf, String fileName) throws Exception;

    int fileDelete(String fileName) throws Exception;

    int insertImageUrl(ImageDTO pDTO, boolean save) throws Exception;
}
