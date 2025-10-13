package src.main.java.com.scurry.system.io;



/**
 * @author: scurry
 * @create: 2020-04-26 15:59
 */
public class NettyClient {

    public static void main(String[] args) {
        try {
            NioEventLoopGroup worker = new NioEventLoopGroup();
            Bootstrap boot = new Bootstrap();
            boot.group(worker)
                    .channel(NioSocketChannel.class)
                    .remoteAddress("localhost", 9090)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel sc) throws Exception {
                            System.out.println("初始化client");
                            ChannelPipeline p = sc.pipeline();
                            p.addLast(new MyInbound());
                        }
                    });


            ChannelFuture conn = boot.connect().sync();


            Channel client = conn.channel();
            System.out.println(client);

            ByteBuf byteBuf = Unpooled.copiedBuffer("hello world".getBytes());
            client.writeAndFlush(byteBuf).sync();




        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
