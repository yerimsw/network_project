package org.example;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import static org.example.Main.parking;
import static org.example.Main.spotArr;
import static org.example.RequestHandler.handleRequest;

public class Main {
    static String[] spotArr = {"P1", "P2", "P3", "P4",
            "J1", "J2", "J3", "J4",
            "H1", "H2", "H3", "H4",
            "F1", "F2", "F3", "F4"};

    static CarInfo[][] parking = new CarInfo[3][16];

    public static void main(String[] args) {
        // 클라이언트로부터 받은 JSON 문자열
        String json = "{\"flag\":11,\"carNum\":\"12가 1234\",\"inCarTime\":\"2023-09-26T07:11:54\",\"spot\":\"1\"}";

        // 요청 처리
        handleRequest(json);
    }

//        int port = 8080; // 원하는 포트 번호로 변경
//        try {
//            ServerSocket serverSocket = new ServerSocket(port);
//            System.out.println("서버가 " + port + " 포트에서 대기 중...");
//
//            while (true) {
//                Socket clientSocket = serverSocket.accept();
//                 클라이언트 요청을 처리하는 스레드를 시작하거나, 핸들러 함수를 호출하는 방식으로 진행
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
}

@AllArgsConstructor
class CarInfo {
    private String carNum;
    private LocalDateTime inCartime;
    private String spot;
}

@Getter
@Builder
@AllArgsConstructor
class InCarRequest {
    private int floor;
    private String carNum;
    private LocalDateTime inCarTime;
    private String spotString;
    private int spotInt;
}

class OutCarRequest {
    private int floor;
    private int spotInt;
    private LocalDateTime outCarTime;
}

class SearchCarRequest {
    private String carNum;
    private LocalDateTime nowTime;
}

class JsonMapper {
    // 1. json -> 입차
    public static InCarRequest jsonToInCarRequest(JsonNode json, int floor) {
        return InCarRequest.builder()
                .floor(floor)
                .carNum(json.get("carNum").asText())
                .inCarTime(LocalDateTime.parse/*문자열 -> 시간 객체타입으로 변환*/(json.get("inCarTime").asText()/* json 시간 문자열을 가져옴 */))
                .spotString(spotArr[json.get("spot").asInt()])
                .spotInt(json.get("spot").asInt())
                .build(); // 객체가 생성됨.
    }
    // 2. json -> 출차
    public static OutCarRequest jsonToOutCarRequest(JsonNode json, int floor) {
        return OutCarRequest.builder()
                .floor(floor)
                .spotInt(json.get("spot").asInt())
                .outCarTime(LocalDateTime.parse/*문자열 -> 시간 객체타입으로 변환*/(json.get("outCarTime").asText()/* json 시간 문자열을 가져옴 */))
                .build(); // 객체가 생성됨.
    }
    // 3. json -> 조회
    public static SearchCarRequest jsonToSearchCarRequest(JsonNode json) {
        return SearchCarRequest.builder()
                .carNum(json.get("carNum").asText())
                .nowTime(LocalDateTime.parse/*문자열 -> 시간 객체타입으로 변환*/(json.get("nowTime").asText()/* json 시간 문자열을 가져옴 */))
                .build();
    }
}

// 입차, 출차, 조회 처리하는 클래스
class Process {
    public static void inCarProcess(InCarRequest inCarRequest){
        try {
            if (parking[inCarRequest.getFloor - 1][inCarRequest.getSpotInt()] != null) {
                throw new RuntimeException(); // Todo 예외처리
            }
        } catch (RuntimeException e) {
            e.printStackTrace(); // Todo 예외처리
        }

        parking[inCarRequest.getFloor - 1][inCarRequest.getSpotInt()] =
                new CarInfo(inCarRequest.getCarNum(), inCarRequest.getInCarTime(), inCarRequest.getSpotString());
    }

    public static void outCarProcess(OutCarRequest outCarRequest){
        try {
            outCarRequest.getOutCarTime, parking[outCarRequest.getFloor - 1][outCarRequest.getSpotInt].getInCartime;
            parking[outCarRequest.getFloor - 1][outCarRequest.getSpotInt] = null;
            // TODO: 출차시간 처리하는 로직 넣기?

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void searchCarProcess(SearchCarRequest searchCarRequest){
        try{
            for (int i = 0; i < parking.length; i++) {
                for (int j = 0; j < parking[i].length; j++) {
                    CarInfo carInfo = parking[i][j];
                    if (carInfo != null && carInfo.getCarNum().equals(searchCarRequest.carNum)) {
                        System.out.println("찾은 차량 번호: " + carInfo.getCarNum());
                        // i+1은 층수 , spotArr[j]는 차량 위치
                        break;
                    }
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
//    public static void searchProcess(){
//        try(
//        // 주차 공간 배열을 순회 > carNum 찾음
//        for (int spotInt = 0; spotInt < parking[floor].length; spotInt++) {
//            CarInfo carInfo = parking[floor][spotInt];
//
//            if (carInfo != null && carInfo.getCarNum().equals(carNumToFind)) {
//                // 차량을 찾았으므로 해당 주차 공간의 차량 정보를 제거. 즉, 출차처리
//                parking[floor][spotInt] = null;
//
//                // TODO: 출차시간 처리하는 로직 넣기?
//
//                return; // 차량을 찾았으면 반복문을 빠져나옴
//            }
//        }
//        )
//        // 차량을 찾지 못한 경우
//        throw new RuntimeException("주차된 차량을 찾을 수 없습니다.");
//        catch (RuntimeException e) {
//            e.printStackTrace(); // 예외처리
//        }
//
//    }


}

class RequestHandler {
    public static void handleRequest(String jsonRequest) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            JsonNode json = objectMapper.readTree(jsonRequest);
            int flag = json.get("flag").asInt();
            int floor = flag / 10 - 1;
            int action = flag % 10;

            switch (action) {
                case 1:
                    // json -> 입차객체
                    InCarRequest inCarRequest = JsonMapper.jsonToInCarRequest(json, floor);
                    // 입차 처리 + 층수
                    Process.inCarProcess(inCarRequest);
                    break;
                case 2:
                    // 출차 처리
                    OutCarRequest outCarRequest = JsonMapper.jsonToOutCarRequest(json, floor);
                    // 출차 처리
                    Process.outCarProcess(outCarRequest);
                    break;
                case 3:
                    // 조회 처리
                    SearchCarRequest searchCarRequest = JsonMapper.jsonToSearchCarRequest(json);
                    Process.searchCarProcess(searchCarRequest);
                    break;
                default:
                    // 잘못된 action 값 처리
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}