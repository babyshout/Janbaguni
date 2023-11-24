package kopo.poly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;


@SpringBootApplication

public class PriceAnalyzerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PriceAnalyzerApplication.class, args);
    }


}
