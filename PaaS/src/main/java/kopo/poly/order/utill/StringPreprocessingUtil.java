package kopo.poly.order.utill;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringPreprocessingUtil {

    /**
     * 
     * @param str  \n 단위로 split할 문자열 리스트
     * @param index split할 문자열의 인덱스
     * @return
     */
    public List<String> splitString(List<String> str, int index){
        List<String> splitList = new ArrayList<>();
        splitList = Arrays.asList(str.get(index).split("\n"));

        if(splitList.size() != 0){
            return splitList;
        }else{
            return str;
        }
    }

    /***
     * 
     * @param str 파싱할 문자열 리스트
     * @param index split이 필요한 문장 인덱스
     * @return
     */
    public List<String> parseStringList(List<String> str, int index) {
        List<String> origin = splitString(str, index);

        List<String> parseString = new ArrayList<>();
        String regex = "[!@#$%^&*()_+\\-=\\[\\]{};':\",.<>?l]"; // 특수 문자 패턴 + 알파벳 l
        Pattern pattern = Pattern.compile(regex); // 패턴 객체 생성

        List<String> result = new ArrayList<>();
        for (String s : origin) {
            if (s.indexOf("(") > -1) {
                StringBuffer sb = new StringBuffer(s);
                sb.delete(s.indexOf("("), s.indexOf(")") + 1);
                parseString.add(sb.toString());
            }
        }
        for(String s : parseString){
            Matcher matcher = pattern.matcher(s);
            if(matcher.find()){
                StringBuffer sb = new StringBuffer(s);
                // 특수문자가 시작되는 부분을 찾았을 때
                int startIndex = matcher.start();
                sb.delete(startIndex, sb.length());
                result.add(sb.toString());
            }
        }

        if(result.size() == 0){
            return origin;
        }
        return result;
    }

    public List<String> replaceUnit(List<String> list){
        List<String> result = new ArrayList<>();
        List<String> upperCaseList = new ArrayList<>();
        for(String str : list){
            upperCaseList.add(str.toUpperCase());
        }

        for(String str : upperCaseList){
            if(str.equals("KG")|| str.equals("PACK") || str.equals("PK") || str.equals("EA") || str.equals("BOX") || str.equals("단")){
                result.add(str);
            }

            if (str.equals("KK") || str.equals("KKK")) {
                int kCount = str.equals("KK") ? 2 : 3; // K의 개수
                for (int i = 0; i < kCount; i++) {
                    result.add("KG"); // K의 개수에 따라 KG을 추가
                }
            }

            if(str.equals("K")){
                str = "KG";

                result.add(str);
            }
        }

        return result;
    }

    public String replaceDate(String input) {
        // 괄호와 그 안의 내용을 빈 문자열로 대체
        return input.replaceAll("\\([^)]+\\)", "");
    }

    public String getExt(String fileName){
        String ext = "";

        if(fileName != null){
            ext = fileName.substring(
                    fileName.lastIndexOf(".") + 1,
                    fileName.length()
            ).toLowerCase();
        }
        return ext;
    }


}
