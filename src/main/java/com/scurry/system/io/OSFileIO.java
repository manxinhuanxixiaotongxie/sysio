package com.scurry.system.io;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 磁盘IO测试
 *
 */
public class OSFileIO {

    static byte[] data = "123456789\n".getBytes();
    static String path = "/root/testfileio/out.txt";


    public static void main(String[] args) throws Exception {


        switch (args[0]) {
            case "0":
                testBasicFileIO();
                break;
            case "1":
                testBufferedFileIO();
                break;
            case "2":
                testRandomAccessFileWrite();
            case "3":
//                whatByteBuffer();
            default:

        }
    }


    //最基本的file写
    public static void testBasicFileIO() throws Exception {
        File file = new File(path);
        FileOutputStream out = new FileOutputStream(file);
        while (true) {
            Thread.sleep(10);
            // 系统调用 syscall
            out.write(data);
        }

    }

    //测试buffer文件IO
    //  jvm  8kB   syscall  write(8KBbyte[])

    public static void testBufferedFileIO() throws Exception {
        File file = new File(path);
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
        while (true) {
            Thread.sleep(10);
            // 堆内
            out.write(data);
        }
    }


    //测试文件NIO
    public static void testRandomAccessFileWrite() throws Exception {
        // 文件随机读写
        RandomAccessFile raf = new RandomAccessFile(path, "rw");

        raf.write("hello mashibing\n".getBytes());
        raf.write("hello seanzhou\n".getBytes());
        System.out.println("write------------");
        System.in.read();
        // 指针来到下标为4的位置(随机读写能力)
        raf.seek(4);
        // 从下标为4的位置开始写 会覆盖掉原有的内容
        raf.write("ooxx".getBytes());

        System.out.println("seek---------");
        System.in.read();

        // 文件读写channel
        FileChannel rafchannel = raf.getChannel();
        // lsof -op 多一个的mem fd  要么使用buffer要么使用fd进行文件的读写
        //mmap  堆外  和文件映射的   byte  not  object

        /**
         * 标准nio 并没有实现mmap（）方法
         *
         * 原因：
         *   1.数据载体是 可随机访问 持久化的缓冲区（磁盘空间的页缓存） 这类缓冲区生命周期稳定 地址空间可被安全映射
         *   2.数据操作双向可同步  用户态修改映射区域侯 内核可通过页表同步到底层存储（如磁盘）反之亦然
         *
         *   网络IO不适合直接实现mmap()
         *      网络IO是socket缓冲区（内核维护的接受 发送缓冲区） 其特性与磁盘文件的页缓存有本质区别  导致mmap难以直接使用
         *
         *      1.网络缓冲区的临时性与不可控性
         *          生命周期短暂
         *          大小动态变化
         *          数据不可随机访问
         *      2.安全性与并发风险
         *         内核与用户态同步问题
         *         权限控制困难
         *      3.跨平台性与内核接口差异
         *      4.替代方案：
         *      java NIO提供了更适合的高效方案
         *        1.直接缓冲区  DirectByteBuffer    ByteBuffer.allocate(8192);堆外  linux堆内    ByteBuffer.allocateDirect(1024);linux堆外
         *
         *        2.多路复用
         *
         *
         *
         *
         *
         *
         *   文件IO
         *      1.文件数据持久化
         *      2.支持随机访问
         *      3.同步机制成熟  受到内核控制
         *      4.跨平台支持好
         *
         *
         */
        MappedByteBuffer map = rafchannel.map(FileChannel.MapMode.READ_WRITE, 0, 4096);

        // 不是系统调用  但是数据会到达 内核的pagecache
        map.put("@@@".getBytes());
        // 曾经我们是需要out.write()  这样的系统调用，才能让程序的data 进入内核的pagecache
        // 曾经必须有用户态内核态切换
        // mmap的内存映射，依然是内核的pagecache体系所约束的！！！
        // 换言之，丢数据
        // 你可以去github上找一些 其他C程序员写的jni扩展库，使用linux内核的Direct IO
        // 直接IO是忽略linux的pagecache
        // 是把pagecache  交给了程序自己开辟一个字节数组当作pagecache，动用代码逻辑来维护一致性/dirty。。。一系列复杂问题

        System.out.println("map--put--------");
        System.in.read();

        // 要么内核协助刷写 要么是手工刷写
//        map.force(); //  flush

        raf.seek(0);

        // 堆上分配
        ByteBuffer buffer = ByteBuffer.allocate(8192);
        // 堆外
//        ByteBuffer buffer = ByteBuffer.allocateDirect(1024);

        int read = rafchannel.read(buffer);   //buffer.put()
        System.out.println(buffer);
        // pos来到0位置 limit来到文件size的最后一位的
        buffer.flip();
        System.out.println(buffer);
        for (int i = 0; i < buffer.limit(); i++) {
            Thread.sleep(200);
            System.out.print(((char) buffer.get(i)));
        }
    }

    public void whatByteBuffer() {

//        ByteBuffer buffer = ByteBuffer.allocate(1024);
        ByteBuffer buffer = ByteBuffer.allocateDirect(1024);


        System.out.println("postition: " + buffer.position());
        System.out.println("limit: " + buffer.limit());
        System.out.println("capacity: " + buffer.capacity());
        System.out.println("mark: " + buffer);

        buffer.put("123".getBytes());

        System.out.println("-------------put:123......");
        System.out.println("mark: " + buffer);

        buffer.flip();   //读写交替

        System.out.println("-------------flip......");
        System.out.println("mark: " + buffer);

        buffer.get();

        System.out.println("-------------get......");
        System.out.println("mark: " + buffer);

        buffer.compact();

        System.out.println("-------------compact......");
        System.out.println("mark: " + buffer);

        buffer.clear();

        System.out.println("-------------clear......");
        System.out.println("mark: " + buffer);

    }


}
