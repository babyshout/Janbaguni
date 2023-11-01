package kopo.poly.order.infra.naver.ocr;

import kopo.poly.order.utill.JsonUtill;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class NaverOrderPaperOcr {
    @Value("${naver.service.template.url}")
    private String url;

    /**
     * 네이버 OCR API를 호출하여 텍스트를 추출합니다.
     * @param {string} type 호출 메서드 타입
     * @param {string} filePath 파일 경로
     * @param {string} naver_secretKey 네이버 시크릿키 값
     * @param {string} ext 확장자
     * @returns {List} 추출된 텍스트 목록
     */
    public List<String> callApi(String type, String filePath, String naver_secretKey, String ext) {
        String apiURL = url; // 네이버 OCR API의 URL
        String secretKey = naver_secretKey; // 네이버 API에 접근하기 위한 시크릿 키
        String imageFile = filePath; // OCR 수행 대상 이미지 파일 경로
        List<String> parseData = null; // OCR 결과를 담을 리스트

        log.info("callApi 시작!");

        try {
            URL url = new URL(apiURL); // API URL을 URL 객체로 만듭니다.
            HttpURLConnection con = (HttpURLConnection)url.openConnection(); // URL 연결을 위한 HttpURLConnection 객체를 생성합니다.
            con.setUseCaches(false); // 캐시 사용을 비활성화합니다.
            con.setDoInput(true); // 입력 스트림 사용을 활성화합니다.
            con.setDoOutput(true); // 출력 스트림 사용을 활성화합니다.
            con.setReadTimeout(30000); // 읽기 타임아웃을 설정합니다.
            con.setRequestMethod(type); // HTTP 메서드를 설정합니다.
            String boundary = "----" + UUID.randomUUID().toString().replaceAll("-", ""); // 경계 문자열을 생성합니다.
            con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary); // HTTP 요청 헤더를 설정합니다.
            con.setRequestProperty("X-OCR-SECRET", secretKey); // 시크릿 키를 설정합니다.

            JSONObject json = new JSONObject(); // JSON 객체를 생성합니다.
            json.put("version", "V2"); // JSON 객체에 버전 정보를 추가합니다.
            json.put("requestId", UUID.randomUUID().toString()); // JSON 객체에 요청 ID를 추가합니다.
            json.put("timestamp", System.currentTimeMillis()); // JSON 객체에 타임스탬프를 추가합니다.
            JSONObject image = new JSONObject(); // JSON 객체를 생성합니다.
            image.put("format", ext); // 이미지 형식 정보를 추가합니다.
            image.put("name", "demo"); // 이미지 이름을 추가합니다.
            JSONArray images = new JSONArray(); // JSON 배열을 생성합니다.
            images.add(image); // JSON 배열에 이미지 정보를 추가합니다.
            json.put("images", images); // JSON 객체에 이미지 배열을 추가합니다.
            String postParams = json.toString(); // JSON 객체를 문자열로 변환합니다.

            con.connect(); // 서버에 연결합니다.
            DataOutputStream wr = new DataOutputStream(con.getOutputStream()); // 출력 스트림을 생성합니다.
            File file = new File(imageFile); // 이미지 파일 객체를 생성합니다.
            writeMultiPart(wr, postParams, file, boundary); // 멀티파트 요청을 작성합니다.
            wr.close(); // 출력 스트림을 닫습니다.

            int responseCode = con.getResponseCode(); // HTTP 응답 코드를 가져옵니다.
            BufferedReader br;
            if (responseCode == 200) { // HTTP 응답 코드가 200이면
                br = new BufferedReader(new InputStreamReader(con.getInputStream())); // 입력 스트림을 읽어옵니다.
            } else { // 그렇지 않으면
                br = new BufferedReader(new InputStreamReader(con.getErrorStream())); // 에러 스트림을 읽어옵니다.
            }
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();

            parseData = jsonParse(response); // API 응답을 가공하여 OCR 결과를 가져옵니다.

        } catch (Exception e) {
            System.out.println(e);
        }
        return parseData; // OCR 결과를 반환합니다.
    }

    /**
     * Multipart 요청을 생성합니다.
     * @param {OutputStream} out 데이터를 출력할 스트림
     * @param {string} jsonMessage 요청 파라미터
     * @param {File} file 요청 파일
     * @param {String} boundary 경계 문자열
     */
    private void writeMultiPart(OutputStream out, String jsonMessage, File file, String boundary) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("--").append(boundary).append("\r\n"); // 경계 문자열을 추가합니다.
        sb.append("Content-Disposition:form-data; name=\"message\"\r\n\r\n"); // 메시지 파라미터를 추가합니다.
        sb.append(jsonMessage); // JSON 파라미터를 추가합니다.
        sb.append("\r\n");

        out.write(sb.toString().getBytes("UTF-8")); // 출력 스트림에 문자열을 씁니다.
        out.flush();

        if (file != null && file.isFile()) { // 파일이 존재하면
            out.write(("--" + boundary + "\r\n").getBytes("UTF-8")); // 경계 문자열을 출력합니다.
            StringBuilder fileString = new StringBuilder();
            fileString
                    .append("Content-Disposition:form-data; name=\"file\"; filename="); // 파일 파라미터를 추가합니다.
            fileString.append("\"" + file.getName() + "\"\r\n"); // 파일 이름을 추가합니다.
            fileString.append("Content-Type: application/octet-stream\r\n\r\n"); // 파일

            out.write(fileString.toString().getBytes("UTF-8")); // 파일 정보를 출력 스트림에 씁니다.
            out.flush();

            try (FileInputStream fis = new FileInputStream(file)) { // 파일을 읽기 위한 FileInputStream을 생성합니다.
                byte[] buffer = new byte[8192]; // 버퍼를 생성합니다.
                int count;
                while ((count = fis.read(buffer)) != -1) { // 파일을 읽어서 버퍼에 저장하고
                    out.write(buffer, 0, count); // 버퍼의 내용을 출력 스트림에 씁니다.
                }
                out.write("\r\n".getBytes()); // 파일의 끝을 표시합니다.
            }

            out.write(("--" + boundary + "--\r\n").getBytes("UTF-8")); // 마지막 경계 문자열을 출력합니다.
        }
        out.flush();
    }

    /**
     * API 응답 데이터를 가공합니다.
     * @param {StringBuffer} response 응답값
     * @returns {List} 추출된 텍스트 목록
     */
    private List<String> jsonParse(StringBuffer response) throws ParseException {
        // JSON 파싱
        JSONParser jp = new JSONParser(); // JSON 파서 객체를 생성합니다.
        JSONObject jobj = (JSONObject) jp.parse(response.toString()); // JSON 응답을 파싱합니다.
        // "images" 배열 객체를 가져옵니다.
        JSONArray JSONArrayPerson = (JSONArray)jobj.get("images");
        JSONObject JSONObjImage = (JSONObject)JSONArrayPerson.get(0); // 배열에서 첫 번째 이미지 객체를 가져옵니다.
        JSONArray s = (JSONArray) JSONObjImage.get("fields"); // 이미지에서 필드 배열을 가져옵니다.
        // 필드 배열을 Map 리스트로 변환합니다.
        List<Map<String, Object>> m = JsonUtill.getListMapFromJsonArray(s);
        List<String> result = new ArrayList<>();
        List<String> productName = new ArrayList<>();
        for (Map<String, Object> as : m) {
            result.add((String) as.get("inferText")); // 추출된 텍스트를 결과 리스트에 추가합니다.
        }

        return result; // 추출된 텍스트 목록을 반환합니다.
    }
}
