package ai.fasion.fabs.apollo.auth.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class ApplicationVO {

    private String name;

    private String company;

    private String req_note;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date timestamp;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getReq_note() {
        return req_note;
    }

    public void setReq_note(String req_note) {
        this.req_note = req_note;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "ApplicationVO{" +
                "name='" + name + '\'' +
                ", company='" + company + '\'' +
                ", req_note='" + req_note + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
