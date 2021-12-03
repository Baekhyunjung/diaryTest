package com.example.diary;

public class DiaryDTO {

    @Override
    public String toString() {
        return "DiaryDTO{" +
                "id=" + id +
                ", userid=" + userid + '\'' +
                ", c_date=" + c_date + '\'' +
                ", contents=" + contents + '\'' +
                '}';
    }

    private int id;
    private String userid;
    private String c_date;
    private String contents;


    public DiaryDTO(int id, String userid, String today, String content) {
        this.id = id;
        this.userid = userid;
        this.c_date = today;
        this.contents = content;
    }


    public int get_id() {return id;}
    public String get_userid() {return userid;}
    public String get_cdate() {return c_date;}
    public String get_contents() {return contents;}

    public void set_id(int id) {this.id = id;}
    public void set_userid(String user_id) {this.userid = user_id;}
    public void set_cdate(String tday) {this.c_date = tday;}
    public void set_contents(String ctt) {this.contents = ctt;}

}
