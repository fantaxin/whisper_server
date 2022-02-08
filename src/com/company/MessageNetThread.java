package com.company;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
/*
import static com.company.Main.messageServerSocket;


/**
 * 1.通信过程：
 * 1.1用户登录后，首先创建tcp socket与服务端连接。
 *    服务端收到请求后，线程池分配一个线程，在线程中创建socket与其连接。
 *    将socket加入socket_list。
 * 1.2私聊：
 * 接收:[TYPE_PRIVATE_CHAT]\r\n[sender_id]\r\n[(receive_id)@@(message_content)]
 * 通过receive在online中查找ip和port,然后在socket_list中查找相应的socket,转发
 * 转发:[TYPE_PRIVATE_CHAT]\r\n[sender_id]\r\n[message_content]
 * 1.3公共群聊：
 * 接收:[TYPE_PUBLIC_GROUP]\r\n[sender_id]\r\n[(group_id)@@(message_content)]
 * 依次在socket_list中转发
 * 转发:[TYPE_PUBLIC_GROUP]\r\n[sender_id]\r\n[(group_id)@@(message_content)]
 * 1.4获取在线列表:
 * 接受:[TYPE_GET_ONLINE]\r\n[sender_id]\r\n[get_online]
 * 发送:[TYPE_GET_ONLINE]\r\n[sender_id]\r\n[(id)|(name)@@(id2)|(name2)]
 * 1.5保持连接:
 * 接受:[TYPE_KEEP_CONNECTION]\r\n[sender_id]\r\n[name]
 * 发送:
 * 1.6退出:
 * 接收:[TYPE_EXIT]\r\n[sender_id]\r\n[goodbye]
 *
 * 2.在线列表：
 * 2.1发送心跳包判断是否在线
 */
//public class MessageNetThread extends Thread{

    //Socket messageSocket;
    //boolean exit = false;

    /*MessageNetThread(Socket socket)
    {
        messageSocket = socket;
    }*/

   /** private void TCPSend(Socket socket, Msg reply) throws IOException
    {
        String str_reply = reply.getType() + "\r\n" + reply.getSender_id() + "\r\n" + reply.getContent();
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        byte[] replyBuffer = str_reply.getBytes("GB2312");
        out.write(replyBuffer, 0, replyBuffer.length);
        System.out.println("Mes:reply:"+str_reply);
    }
    private String TCPReceive() throws IOException
    {
        DataInputStream in = new DataInputStream(messageSocket.getInputStream());
        byte[] buffer = new byte[10000];  //缓冲区的大小
        in.read(buffer);               //处理接收到的报文，转换成字符串
        String temp = new String(buffer,"GB2312").trim();
        System.out.println(temp);
        return temp;
    }
    @Override
    public void run() {
        while(true)
        {
            if(exit)
                break;
            try
            {
                //messageSocket = Main.messageServerSocket.accept();
                System.out.println("Mes:TCP连接成功");
                System.out.println("Mes:远程主机地址："+messageSocket.getRemoteSocketAddress());
                //Main.messageSocket_list.add(messageSocket);
                int i = 0;
                while(true) {
                    String temp = TCPReceive();
                    System.out.println("Mes111:"+temp);
                    if (!temp.isEmpty())//防止出现空包导致卡死？
                    {
                        Msg msg = new Msg(temp);
                        if(i==0)
                            Main.db.InsertOnline(msg.getSender_id(),messageSocket.getInetAddress().toString(),messageSocket.getPort(),msg.getContent());
                        execute(msg);
                        i=1;
                    }
                }

            }catch(SocketTimeoutException s)
            {
                System.out.println("Mes:Socket timed out!");
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
                System.out.println("Mes:test【"+df.format(new Date())+"】");// new Date()为获取当前系统时间
                //break;
            }catch(EOFException e)
            {
                //EOFException
                e.printStackTrace();
            }catch(SocketException e)
            {
                //Connection reset
                /*一端退出，但退出时并未关闭该连接，另一端如果在从连接中读数据则抛出该异常（Connection reset）。简单的说就是在连接断开后的读和写操作引起的。*/
                /*e.printStackTrace();
            }catch(IOException e)
            {
                e.printStackTrace();
                //break;
            }
        }
    }

    private void execute(Msg msg) throws IOException {
        String[] list = msg.parseContent();
        Msg reply;
        switch (msg.getType()) {
            case Msg.TYPE_PRIVATE_CHAT:
            {
                SearchResult sr = Main.db.SearchOnline(Integer.parseInt(list[0]));
                InetSocketAddress isa = new InetSocketAddress(sr.ip,sr.port);
                int index = 0;
                /*for(; index < Main.messageSocket_list.size(); index++) {
                    if(isa.equals(Main.messageSocket_list.get(index).getRemoteSocketAddress())) {
                        reply = new Msg(Msg.TYPE_PRIVATE_CHAT, msg.getSender_id(), list[1]);
                        TCPSend(Main.messageSocket_list.get(index), reply);
                        break;
                    }
                }*/
                /*break;
            }
            case Msg.TYPE_PUBLIC_GROUP:
            {
                int index = 0;
                /*for(; index < Main.messageSocket_list.size(); index++) {
                    TCPSend(Main.messageSocket_list.get(index), msg);
                }*/
                /*break;
            }
            case Msg.TYPE_GET_ONLINE:
            {
                List<SearchResult> sr_list = Main.db.SerchOnline();
                if(!sr_list.isEmpty())
                {
                    String data = "";
                    for (SearchResult temp : sr_list) {
                        data = data + temp.id + "|" + temp.name  + "@@";
                    }
                     //发送:[TYPE_GET_ONLINE]\r\n[sender_id]\r\n[(id)|(name)@@(id2)|(name2)]
                    reply = new Msg(Msg.TYPE_GET_ONLINE, 0, data);
                    TCPSend(messageSocket,reply);
                }
                break;
            }
            case Msg.TYPE_EXIT:
            {
                InetSocketAddress isa = (InetSocketAddress) messageSocket.getRemoteSocketAddress();
                int index = 0;
                /*for(; index < Main.messageSocket_list.size(); index++) {
                    if(isa.equals(Main.messageSocket_list.get(index).getRemoteSocketAddress())) {
                        Main.messageSocket_list.remove(index);
                        break;
                    }
                }*/
                /*messageSocket.close();
                exit = true;
                break;
            }
            default:
            {
                break;
            }
        }
    }
}*/