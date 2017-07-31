package mydog.haley.com.walktime.domain;

/**
 * 산책 시간 기록 관련 데이터베이스 관리
 */

public class WalkTimeVO {

    // 1. 테이블 컬럼 이름 인스턴스 변수
    private int code;
    private String start;
    private String end;
    private long walk_time;
    private String reg_date;

    // 2-1) 기본 생성자
    public WalkTimeVO() {
    }

    // 2-2) 모두 입력 받는 생성자
    public WalkTimeVO(int code, String start, String end, long walk_time, String reg_date) {
        this.code = code;
        this.start = start;
        this.end = end;
        this.walk_time = walk_time;
        this.reg_date = reg_date;
    }

    // 2-3) 코드 & reg_date 제외하고 입력 받는 생성자
    public WalkTimeVO(String start, String end, long walk_time) {
        this.start = start;
        this.end = end;
        this.walk_time = walk_time;
    }


    // 3. get set
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public long getWalk_time() {
        return walk_time;
    }

    public void setWalk_time(long walk_time) {
        this.walk_time = walk_time;
    }

    public String getReg_date() {
        return reg_date;
    }

    public void setReg_date(String reg_date) {
        this.reg_date = reg_date;
    }

    // 4. toString()
    @Override
    public String toString() {
        return "WalkTimeVO{" +
                "code=" + code +
                ", start='" + start + '\'' +
                ", end='" + end + '\'' +
                ", walk_time=" + walk_time +
                ", reg_date='" + reg_date + '\'' +
                '}';
    }
}
