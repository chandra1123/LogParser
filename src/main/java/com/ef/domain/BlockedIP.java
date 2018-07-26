package com.ef.domain;

public class BlockedIP {

    private String ip;
    private String reason;
    
    public BlockedIP(String ip, String reason) {
        this.ip = ip;
        this.reason = reason;
    }
    
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
