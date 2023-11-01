package kopo.poly.order.service.impl;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import kopo.poly.order.dto.ImageDTO;
import kopo.poly.order.persistance.mapper.IImageMapper;
import kopo.poly.order.service.IS3UploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3UploadService implements IS3UploadService {
    private final AmazonS3Client amazonS3Client;
    private final IImageMapper imageMapper;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    @Override
    public String upload(MultipartFile mf, String fileName) throws Exception {
        log.info(this.getClass().getName() + ".upload Start!");
        File uploadFile = convert(mf);

        amazonS3Client.putObject(
                new PutObjectRequest(
                        bucket, fileName, uploadFile
                ).withCannedAcl(CannedAccessControlList.PublicRead)
        );

        removeNewFile(uploadFile);

        log.info("amazonS3 URL : " + amazonS3Client.getUrl(bucket, fileName).toString());

        log.info(this.getClass().getName() + ".upload End!");
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }


    @Override
    public int fileDelete(String fileName) throws Exception {
        int res = 0;
        try {
            amazonS3Client.deleteObject(bucket, fileName);
            res = 1;

        } catch (AmazonS3Exception e) {
            e.printStackTrace();
        } catch (SdkClientException e) {
            e.printStackTrace();
        }

        return res;
    }

    @Override
    public int insertImageUrl(ImageDTO pDTO, boolean save) throws Exception {
        log.info(this.getClass().getName() + ".insertImageUrl Start!!!");
        int res = 0;
        int success;
        if(save){
            success = imageMapper.insertImageUrlCheckY(pDTO);
        }else{
            success = imageMapper.insertImageUrlCheckN(pDTO);
        }

        if(success > 0){
            res = 1;
        }
        log.info(this.getClass().getName() + ".insertImageUrl End!!!");
        return res;
    }

    private File convert(MultipartFile file) throws IOException {
        log.info("user.dir system property : " + System.getProperty("user.dir"));
        log.info("file origin name : " +file.getOriginalFilename());


        File convertFile = new File(System.getProperty("user.dir") + "/" + file.getOriginalFilename());

        log.info("convertFile : " +  convertFile.getPath());


        try(FileOutputStream fos = new FileOutputStream(convertFile)){
            fos.write(file.getBytes());
        }
        return convertFile;
    }

    private void removeNewFile(File targetFile){
        if(targetFile.delete()){
            log.info("File delete success");
            return;
        }
        log.info("File delete fail");
    }
}
