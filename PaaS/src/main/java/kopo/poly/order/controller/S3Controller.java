package kopo.poly.order.controller;

import kopo.poly.order.dto.ImageDTO;
import kopo.poly.order.service.IS3UploadService;
import kopo.poly.order.utill.CmmUtil;
import kopo.poly.order.utill.DateUtil;
import kopo.poly.order.utill.StringPreprocessingUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Controller
@Slf4j
@RequiredArgsConstructor
public class S3Controller {
    private final IS3UploadService s3UploadService;


    @PostMapping("/uploadImage")
    public String uploadImage(@RequestParam(value = "s3Image") MultipartFile mf, HttpServletRequest request){
        StringPreprocessingUtil stringUtill = new StringPreprocessingUtil();
        String orgFileName = mf.getOriginalFilename();
        String ext = stringUtill.getExt(orgFileName);
        String saveFileName = DateUtil.getDateTime("HHmmss") + "." + ext;
        String imageUrl;
        String date = CmmUtil.nvl((String)request.getAttribute("date"));

        String userId = Optional.ofNullable(
                (String) request.getAttribute("userId")
        ).orElseGet(String::new);

        if(userId.isEmpty()){
            userId = "user01";
        }

        ImageDTO orderDTO = new ImageDTO();
        try{
            imageUrl = s3UploadService.upload(mf, saveFileName);

            orderDTO.setUserId(userId);
            orderDTO.setUrl(imageUrl);
            orderDTO.setOcrDate(date);
        }catch (Exception e){
            e.printStackTrace();
        }

        return "saveImage";
    }
}
