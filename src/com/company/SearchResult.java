package com.company;

public class SearchResult {
    public boolean success;
    public int id;
    public String name;
    public String password;
    public String ip;
    public int port;

    public SearchResult(boolean success, int id, String name, String password, String ip, int port) {
        this.success = success;
        this.id = id;
        this.name = name;
        this.password = password;
        this.ip = ip;
        this.port = port;
    }

    public SearchResult(boolean success, int id, String name, String password) {
        this.success = success;
        this.id = id;
        this.name = name;
        this.password = password;
    }

    public SearchResult(boolean success, int id, String ip, int port) {
        this.success = success;
        this.id = id;
        this.ip = ip;
        this.port = port;
    }

    public SearchResult(boolean success, int id, String name, String ip, int port) {
        this.success = success;
        this.id = id;
        this.name = name;
        this.ip = ip;
        this.port = port;
    }
}
