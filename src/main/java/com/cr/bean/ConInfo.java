package com.cr.bean;

/**
 * create in 2017年07月11日
 * @category TODO
 * @author chenyi
 */
public class ConInfo {

    String host;
    String username;
    String password;

    public ConInfo(String host, String username, String password) {
        super();
        this.host = host;
        this.username = username;
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
