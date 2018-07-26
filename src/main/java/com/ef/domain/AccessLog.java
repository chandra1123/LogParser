package com.ef.domain;

import com.opencsv.bean.CsvBindByPosition;

public class AccessLog {
    @CsvBindByPosition(position = 0)
    private String date;

    @CsvBindByPosition(position = 1)
    private String ip;

    @CsvBindByPosition(position = 2)
    private String request;

    @CsvBindByPosition(position = 3)
    private String status;

    @CsvBindByPosition(position = 4)
    private String userAgent;

    // Getters and setters go here.
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("date=")
            .append(date);
        sb.append(", ip=")
            .append(ip);
        sb.append(", request=")
            .append(request);
        sb.append(", status=")
            .append(status);
        sb.append(", user agent=")
            .append(userAgent);
        return sb.toString();
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
}
