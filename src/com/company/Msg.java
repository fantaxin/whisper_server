package com.company;

public class Msg {
    public static final int SERVER = 0;
    public static final int CLIENT = 1;//
    public static final int TYPE_LOGIN = 2;
    public static final int TYPE_REGISTER = 3;

    public static final int TYPE_PRIVATE_CHAT = 4;//私聊
    public static final int TYPE_PUBLIC_GROUP = 5;//公共群聊
    public static final int TYPE_GET_ONLINE = 6;//获取在线列表
    public static final int TYPE_KEEP_CONNECTION = 7;
    public static final int TYPE_EXIT = 9;//退出

    public static final int TYPE_ADD_FRIEND = 3;
    public static final int TYPE_ADD_GROUP = 4;
    public static final int TYPE_CREATE_GROUP = 5;
    public static final int TYPE_MATCH = 6;

    public static final String SERVER_READY = "111111";//服务器就绪

    public static final String REGISTER_SUCCESS = "1000";//注册成功
    public static final String REGISTER_DEFAULT = "1001";//注册失败(账号已存在)
    public static final String LOGIN_SUCCESS = "1100";//登录成功
    public static final String PASSWORD_ERROR = "1011";//密码错误
    public static final String UNREGISTER = "1101";//未注册账号
    public static final String OTHER_ERROR = "1111";//其它错误

    private int type;
    private int sender_id;
    private String content;
    Msg(int type, int sender_id, String content)
    {
        this.type = type;
        this.sender_id = sender_id;
        this.content = content;
    }

    Msg (String message)
    {
        String[] list=message.split("\r\n");
        type=Integer.parseInt(list[0]);
        sender_id=Integer.parseInt(list[1]);
        content=list[2];
    }

    public int getType() {
        return type;
    }
    public int getSender_id() {
        return sender_id;
    }
    public String getContent() {
        return content;
    }
    public String[] parseContent()
    {
        String[] list ;
        switch(type)
        {
            case TYPE_PRIVATE_CHAT:
            case TYPE_REGISTER:
            case TYPE_LOGIN: {
                list = content.split("@@");
                break;
            }
            default: {
                list = new String[1];
                list[0] = content;
                break;
            }
        }
        return list;
    }
}
