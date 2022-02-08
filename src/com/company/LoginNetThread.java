package com.company;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 1.注册：
 * 接收:[TYPE_REGISTER]\r\n[sender_id(default:0)]\r\n[(name)@@(password)]
 * 发送:[SERVER]\r\n[sender_id(default:0)]\r\n[REGISTER_DEFAULT/REGISTER_SUCCESS]
 * 2.登录：
 * 接收:[TYPE_LOGIN]\r\n[sender_id(default:0)]\r\n[(name)@@(password)@@(port)]
 * 失败:[SERVER]\r\n[sender_id(default:0)]\r\n[PASSWORD_ERROR/UNREGISTER]
 * 成功:[SERVER]\r\n[sender_id(default:0)]\r\n[(LOGIN_SUCCESS)@@(id)]
 */
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
 * 转发:[TYPE_PUBLIC_GROUP]\r\n[sender_id]\r\n[(message_content)]
 * 1.4获取在线列表:
 * 接受:[TYPE_GET_ONLINE]\r\n[sender_id]\r\n[get_online]
 * 发送:[TYPE_GET_ONLINE]\r\n[sender_id]\r\n[(id):(name)@@(id2):(name2)]
 * 1.5保持连接:
 * 接受:[TYPE_KEEP_CONNECTION]\r\n[sender_id]\r\n[name]
 * 发送:
 * 1.6退出:
 * 接收:[TYPE_EXIT]\r\n[sender_id]\r\n[goodbye]
 *
 * 2.在线列表：
 * 2.1发送心跳包判断是否在线
 */
public class LoginNetThread extends Thread{
    Socket loginSocket;
    ServerSocket loginServerSocket = Main.loginServerSocket;

    private void TCPSend(Socket socket, Msg reply) throws IOException {
        String str_reply = reply.getType() + "\r\n" + reply.getSender_id() + "\r\n" + reply.getContent();
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        //byte[] replyBuffer = str_reply.getBytes("GB2312");
        //out.write(replyBuffer, 0, replyBuffer.length);
        out.writeUTF(str_reply);
        System.out.println("reply:"+reply.getType()+","+reply.getSender_id()+","+reply.getContent());
    }
    private String TCPReceive() throws IOException {
        DataInputStream in = new DataInputStream(loginSocket.getInputStream());
        String temp = in.readUTF();
        //byte[] buffer = new byte[10000];  //缓冲区的大小
        //in.read(buffer);               //处理接收到的报文，转换成字符串
        //String temp = new String(buffer,"GB2312").trim();
        System.out.println(temp);
        return temp;
    }

    private void execute(Msg msg) throws IOException
    {
        Msg reply;
        String[] list = msg.parseContent();
        System.out.println("receive:"+msg.getType()+","+msg.getSender_id()+","+msg.getContent());
        switch (msg.getType()) {
            case Msg.CLIENT:
            {
                if(msg.getContent().equals(new String("isOK?"))){
                    reply = new Msg(Msg.SERVER, 0, Msg.SERVER_READY);
                    TCPSend(loginSocket,reply);
                }
                break;
            }
            case Msg.TYPE_REGISTER:
            {
                SearchResult sr = Main.db.SearchRegister(list[0]);
                System.out.println(sr.success);
                if (sr.success) {
                    reply = new Msg(Msg.SERVER, 0, Msg.REGISTER_DEFAULT);
                } else {
                    Main.db.InsertRegister(list[0], list[1]);
                    reply = new Msg(Msg.SERVER, 0, Msg.REGISTER_SUCCESS);
                }
                TCPSend(loginSocket,reply);
                break;
            }
            case Msg.TYPE_LOGIN:
            {
                SearchResult sr = Main.db.SearchRegister(list[0]);
                System.out.println(sr.success);
                if (sr.success) {
                    if(list[1].equals(sr.password)) {
                        reply = new Msg(Msg.SERVER, 0, Msg.LOGIN_SUCCESS+"@@"+sr.id);
                        if(Main.db.InsertOnline(sr.id,loginSocket.getInetAddress().getHostAddress(),Integer.parseInt(list[2]),list[0]))
                            ;
                        else {
                            Main.db.UpdateOnline(sr.id, loginSocket.getInetAddress().getHostAddress());
                            System.out.println("更改");
                        }
                    }
                    else{
                        reply = new Msg(Msg.SERVER, 0, Msg.PASSWORD_ERROR);
                    }
                } else {
                    reply = new Msg(Msg.SERVER, 0, Msg.UNREGISTER);
                }
                TCPSend(loginSocket,reply);
                break;
            }
            case Msg.TYPE_PRIVATE_CHAT:
            {
                SearchResult sr2 = Main.db.SearchOnline(Integer.parseInt(list[0]));
                InetSocketAddress isa = new InetSocketAddress(sr2.ip,sr2.port);
                Socket socket = new Socket();
                socket.connect(isa);
                reply = new Msg(Msg.TYPE_PRIVATE_CHAT, msg.getSender_id(), list[1]);
                TCPSend(socket, reply);
                /*int index = 0;
                for(; index < Main.messageSocket_list.size(); index++) {
                    if(isa.equals(Main.messageSocket_list.get(index).getRemoteSocketAddress())) {
                        reply = new Msg(Msg.TYPE_PRIVATE_CHAT, msg.getSender_id(), list[1]);
                        TCPSend(Main.messageSocket_list.get(index), reply);
                        break;
                    }
                }*/
                break;
            }
            case Msg.TYPE_PUBLIC_GROUP:
            {
                 /* 接收:[TYPE_PUBLIC_GROUP]\r\n[sender_id]\r\n[(group_id)@@(message_content)]
                  * 转发:[TYPE_PUBLIC_GROUP]\r\n[sender_id]\r\n[(message_content)]*/
                List<SearchResult> lsr = Main.db.SerchOnline();
                for(int i=0;i<lsr.size();i++)
                {
                    Socket socket = new Socket();
                    try {
                        SearchResult temp = lsr.get(i);
                        if (Integer.valueOf(msg.getSender_id()).equals(temp.id))
                            continue;
                        socket.connect(new InetSocketAddress(temp.ip, temp.port),500);
                        TCPSend(socket, msg);
                        socket.close();
                    }catch (ConnectException e) {
                        e.printStackTrace();
                        continue;
                    }
                    catch (SocketTimeoutException e){
                        System.out.println("public_group timeout");
                        continue;
                    }
                }
                break;
            }
            case Msg.TYPE_GET_ONLINE:
            {
                List<SearchResult> sr_list = Main.db.SerchOnline();
                if(!sr_list.isEmpty())
                {
                    String data = "";
                    for (SearchResult temp : sr_list) {
                        data = data + temp.id + ":" + temp.name  + "@@";
                    }
                    //发送:[TYPE_GET_ONLINE]\r\n[sender_id]\r\n[(id)|(name)@@(id2)|(name2)]
                    reply = new Msg(Msg.TYPE_GET_ONLINE, 0, data);
                    TCPSend(loginSocket,reply);
                }
                break;
            }
            case Msg.TYPE_EXIT:
            {
                Main.db.DeleteOnline(msg.getSender_id());
                break;
            }
            default:
            {
                reply = new Msg(Msg.SERVER, 0, Msg.OTHER_ERROR);
                TCPSend(loginSocket,reply);
                /*String str_reply = reply.getType() + "\r\n" + reply.getSender_id() + "\r\n" + reply.getContent();
                DataOutputStream out = new DataOutputStream(loginSocket.getOutputStream());
                byte[] replyBuffer = str_reply.getBytes("GB2312");
                out.write(replyBuffer, 0, replyBuffer.length);*/
                break;
            }
        }
    }

    public void run()
    {
        while(true)
        {
            try
            {
                System.out.println("等待远程连接，端口号为：" + loginServerSocket.getLocalPort() + "...");
                loginSocket = loginServerSocket.accept();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
                System.out.println("【"+df.format(new Date())+"】");// new Date()为获取当前系统时间
                System.out.println("远程主机地址："+loginSocket.getRemoteSocketAddress());

                DataInputStream in = new DataInputStream(loginSocket.getInputStream());
                //byte[] buffer = new byte[10000];  //缓冲区的大小
                //in.read(buffer);               //处理接收到的报文，转换成字符串
                //String temp = new String(buffer,"GB2312").trim();
                String temp = in.readUTF();

                if(!temp.isEmpty())//防止出现空包导致卡死？
                {
                    Msg msg = new Msg(temp);
                    execute(msg);
                }
                loginSocket.close();

                System.out.println("【end】");// new Date()为获取当前系统时间
            }catch(SocketTimeoutException s)
            {
                System.out.println("SocketServer timed out!");
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
                System.out.println("test【"+df.format(new Date())+"】");// new Date()为获取当前系统时间
                //break;
            }catch(EOFException e)
            {
                //EOFException
                e.printStackTrace();
            }catch(SocketException e)
            {
                //Connection reset
                /*一端退出，但退出时并未关闭该连接，另一端如果在从连接中读数据则抛出该异常（Connection reset）。简单的说就是在连接断开后的读和写操作引起的。*/
                e.printStackTrace();
            }catch(IOException e)
            {
                e.printStackTrace();
                //break;
            }
        }
    }
}
