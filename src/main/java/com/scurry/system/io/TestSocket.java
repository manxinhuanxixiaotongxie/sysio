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
        System.in.read();
        Socket socket = serverSocket.accept();
        System.out.printf("Accepted connection from %s\n", socket.getRemoteSocketAddress());
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
