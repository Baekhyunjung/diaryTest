package com.example.diary;

public class ShareDiaryDTO {

    @Override
    public String toString() {
        return "ShareDiaryDTO{" +
                "id=" + id +
                ", ccontents=" + content + '\'' +
                ", host=" + host + '\'' +
                ", create_at=" + date + '\'' +
                '}';
    }

    private int id;
    private String content;
    private String host;
    private String date;


    public ShareDiaryDTO(int id, String content, String host, String date) {
        this.id = id;
        this.content = content;
        this.host = host;
        this.date = date;
    }


    public int get_id() {return id;}
    public String get_host() {return host;}
    public String get_content() {return content;}
    public String get_date() {return date;}

    public void set_id(int id) {this.id = id;}
    public void set_host(String user_id) {this.host = host;}
    public void set_date(String day) {this.date = date;}
    public void set_content(String ctt) {this.content = ctt;}
}
