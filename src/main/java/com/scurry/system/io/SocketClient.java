package com.scurry.system.io;

import java.io.*;
import java.net.Socket;

/**
 * @author: scurry
 * @create: 2020-05-17 16:18
 */
public class SocketClient {

    public static void main(String[] args) {

        try {
            Socket client = new Socket("192.168.11.133",9090);

            // 缓冲区大小
            client.setSendBufferSize(20);
            // 开启优化 缓冲区满了数据包就发送
            client.setTcpNoDelay(true);
            client.setOOBInline(false);
            OutputStream out = client.getOutputStream();

            InputStream in = System.in;
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            while(true){
                String line = reader.readLine();
                if(line != null ){
                    byte[] bb = line.getBytes();
                    for (byte b : bb) {
                        out.write(b);
                    }
                    // 这里要注意readline的数据读取结束时根据换行符号决定
                    // 如果服务端使用readline的方式 需要补一个换行符
                    out.write('\n');
                    // 强制刷新
                    out.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
