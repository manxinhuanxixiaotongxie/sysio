package com.scurry.system.io;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;

/**
 * 客户端代码
 *
 */
public class C10kClient {

    public static void main(String[] args) throws InterruptedException {
        /**
         * SocketChannel用于实现面向字节的、可选择的网络套接字通道 主要用于客户端
         * 与服务器之间的TCP连接和数据传输 是NIO中处理网络通信的核心组件之一
         * 对比传统Socket：非阻塞、可被选择器（Selector）管理等特性
         */
        LinkedList<SocketChannel> clients = new LinkedList<>();
        // 定义服务端地址

        InetSocketAddress serverAddr = new InetSocketAddress("192.168.11.133", 9090);
        for (int i = 10000;i < 65000;i++) {
            Thread.sleep(100);
            try {
                // jvm会通过系统调用（Linux中的socket()） 返回一个文件描述符
                // 内核会为套接字分配对应的缓冲区（发送缓冲区、接受缓冲区）用于暂存网络数据
                SocketChannel client1 = SocketChannel.open();

                SocketChannel client2 = SocketChannel.open();

                client1.bind(new  InetSocketAddress("localhost", i));

                System.out.printf("111");
                // 内核出发TCP三次握手 后续通过Selector监听连接就绪
                client1.connect(serverAddr);
                System.out.printf("222");
                boolean c1 = client1.isOpen();
                clients.add(client1);
                client2.bind(new  InetSocketAddress("192.168.11.1", i));
                client2.connect(serverAddr);
                boolean c2 = client2.isOpen();
                clients.add(client2);
            }catch (Exception e){
                e.printStackTrace();
            }
        }


    }
}
