package com.scurry.system.io;

import javax.swing.plaf.synth.SynthTreeUI;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.Buffer;
import java.util.function.Function;

/**
 * 测试socket
 * BIO
 *
 *更优雅的实现方式
 * 参考myhttpframework中的socket实现
 *
 */
public class TestSocket {

    ServerSocket serverSocket;


    public void listen(int port) throws  IOException {
        serverSocket = new ServerSocket(port);
        System.out.printf("Listening on port %d\n", port);
        while (true) {
            this.accept();
        }
    }

    public void accept() throws IOException {
        // blocking  文件描述符已经创建 基于socket的
        // 因为这里是阻塞的  不会分配进程 进行文件描述符监听
        System.in.read();
        // blocking
        Socket socket = serverSocket.accept();
        System.out.printf("Accepted connection from %s\n", socket.getRemoteSocketAddress());

        /**
         * 这里也是BIO的弊端
         * 因为是阻塞的  如果要具备更多连接的数据处理能力 必须要开辟资源
         * 线程 进程等
         */
        new Thread(() -> {
            // 处理数据逻辑
            try {

                System.out.printf("Connected to %s\n", socket.getRemoteSocketAddress());
                InputStream inputStream = socket.getInputStream();
                // 获取socket输入流
                BufferedReader  br = new BufferedReader(new InputStreamReader(inputStream));

                String readLine;
                while ((readLine = br.readLine()) != null) {
                    System.out.printf("received line from server: %s\n", readLine);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public static void main(String[] args) throws IOException {
        TestSocket testSocket = new TestSocket();
        // 监听端口
        testSocket.listen(8090);
    }


}
