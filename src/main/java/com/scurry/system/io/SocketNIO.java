package src.main.java.com.scurry.system.io;

import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;

/**
 * NIO 非阻塞式IO
 *
 */
public class SocketNIO {

    public static void main(String[] args) throws Exception {

        LinkedList<SocketChannel> clients = new LinkedList<>();

        ServerSocketChannel ss = ServerSocketChannel.open();
        // 绑定端口
        ss.bind(new InetSocketAddress(9090));
        // 设置非阻塞
        ss.configureBlocking(false); //重点  OS  NONBLOCKING!!!

        ss.setOption(StandardSocketOptions.TCP_NODELAY, false);
//        StandardSocketOptions.TCP_NODELAY
//        StandardSocketOptions.SO_KEEPALIVE
//        StandardSocketOptions.SO_LINGER
//        StandardSocketOptions.SO_RCVBUF
//        StandardSocketOptions.SO_SNDBUF
//        StandardSocketOptions.SO_REUSEADDR


        /**
         * 非阻塞时代
         * 注意：这里并没有使用多路复用器
         * 多路复用器是在select poll epoll  kqueue出现之后才有的
         *
         * 不阻塞
         */

        while (true) {
            Thread.sleep(1000);
            // 设置非阻塞  非阻塞
            SocketChannel client = ss.accept(); //不会阻塞？  -1NULL

            if (client == null) {
                System.out.println("null.....");
            } else {
                // 新连接进来
                client.configureBlocking(false);
                int port = client.socket().getPort();
                System.out.println("client...port: " + port);
                // 连接创建
                /**
                 * 但是对于服务端来说
                 * 尽管已经创建了连接 但是那些连接是就绪的或者说那些连接有数据包发送过来是不清楚的
                 * 因此必须要进行遍历 查看具体是哪个连接有数据到达
                 *
                 * 要理解这个地方 必须要参考IO模块的内容
                 * 程序都是是工作在用户态的
                 * 因此这个地方发生的轮训都是发生在用户态
                 */
                clients.add(client);
            }

            ByteBuffer buffer = ByteBuffer.allocateDirect(4096);  //可以在堆里   堆外

            /**
             * 数据到达之后其实计算机等相关的很多个硬件软件都参与了复杂的过程
             * 包括资源的开辟  数据的复制 内存的分配以及一系列中断等等
             *
             * 数据包网络传输  参考网络部分的资料
             * 计算机的数据处理以及中断以及数据拷贝等方面的内容参考计算机组成以及IO部分
             *
             * 数据包达到之后 说明可写
             */
            for (SocketChannel c : clients) {   //串行化！！！！  多线程！！
                // 进行频繁的用户态内核态切换
                int num = c.read(buffer);  // >0  -1  0   //不会阻塞
                if (num > 0) {
                    // 读取之间将pos指针以及limit指针放置在正确的位置开始读取
                    // 注意这里读取的是文件描述符所描述的文件内容
                    // 针对连接的内容
                    // 使用lsof -op pid 能看到具体的描述信息
                    // 包括数据包接受的、数据包发送的等信息
                    buffer.flip();
                    byte[] aaa = new byte[buffer.limit()];
                    buffer.get(aaa);

                    String b = new String(aaa);
                    System.out.println(c.socket().getPort() + " : " + b);
                    buffer.clear();
                }


            }
        }
    }

}
