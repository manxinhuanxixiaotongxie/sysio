package com.scurry.system.io;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.SocketOptions;
import java.net.StandardSocketOptions;

public class NettyIO {

    public static void main(String[] args) {

        NioEventLoopGroup boss = new NioEventLoopGroup(2);
        NioEventLoopGroup worker = new NioEventLoopGroup(2);
        ServerBootstrap boot = new ServerBootstrap();

        try {
            boot.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY,false)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new MyInbound());

                        }
                    })
                    .bind(9999)
                    .sync()             //阻塞当前线程到服务启动起来
                    .channel()
                    .closeFuture()
                    .sync();            //阻塞当前线程到服务停止


        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}

