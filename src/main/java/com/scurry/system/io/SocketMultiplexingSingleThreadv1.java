package com.scurry.system.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * 多路复用器
 *
 */
public class SocketMultiplexingSingleThreadv1 {

    /**
     * 参考IO部分内核演进部分
     *
     */
    private ServerSocketChannel server = null;
    // selector 基于 select poll epoll kqueue进行封装
    // 多路复用器
    private Selector selector = null;
    int port = 9090;

    public void initServer() {
        try {
            /**
             * BIO java提供非阻塞式的Socket编程接口
             */
            server = ServerSocketChannel.open();
            server.configureBlocking(false);
            // 会创建监听的文件描述符
            server.bind(new InetSocketAddress(port));
            /** 如果是epoll会创建epoll的文件描述符
             * 相当于调用epoll_create
            如果是在poll模型下 会在jvm内部创建对象保存
            注意 内核的演进 select poll 支持多个文件描述符的监听 但是对于java代码来说 不需要关心多个文件描述符的维护

             jvm默认使用epoll模型 但是可以使用-D进行指定使用的多路复用器


            **/
            selector = Selector.open();  //  select  poll  *epoll
            /**
             * 注册的是事件类型
             * 对poll模型来说
             *
             */
            server.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        initServer();
        System.out.println("服务器启动了。。。。。");
        try {
            while (true) {
                Set<SelectionKey> keys = selector.keys();
                System.out.println(keys.size()+"   size");
                while (selector.select(500) > 0) {
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iter = selectionKeys.iterator();
                    while (iter.hasNext()) {
                        SelectionKey key = iter.next();
                        iter.remove();
                        if (key.isAcceptable()) {
                            acceptHandler(key);
                        } else if (key.isReadable()) {
                            readHandler(key);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void acceptHandler(SelectionKey key) {
        try {
            ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
            SocketChannel client = ssc.accept();
            client.configureBlocking(false);
            ByteBuffer buffer = ByteBuffer.allocate(8192);
            client.register(selector, SelectionKey.OP_READ, buffer);
            System.out.println("-------------------------------------------");
            System.out.println("新客户端：" + client.getRemoteAddress());
            System.out.println("-------------------------------------------");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readHandler(SelectionKey key) {
        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer buffer = (ByteBuffer) key.attachment();
        buffer.clear();
        int read = 0;
        try {
            while (true) {
                read = client.read(buffer);
                if (read > 0) {
                    buffer.flip();
                    while (buffer.hasRemaining()) {
                        client.write(buffer);
                    }
                    buffer.clear();
                } else if (read == 0) {
                    break;
                } else {
                    client.close();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SocketMultiplexingSingleThreadv1 service = new SocketMultiplexingSingleThreadv1();
        service.start();
    }
}
