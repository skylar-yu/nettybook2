package com.phei.netty.basic;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.AbstractNioChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author lilinfeng
 * @date 2014年2月14日
 * @version 1.0
 */
public class TimeServer {

	public void bind(int port) throws Exception {
		// 配置服务端的NIO线程组
		EventLoopGroup bossGroup = new NioEventLoopGroup();	//用于服务端接受客户端连接
		EventLoopGroup workerGroup = new NioEventLoopGroup();//用于SocketChannel的网络读写
		try {
			ServerBootstrap b = new ServerBootstrap();	//用于启动NIO服务端的辅助启动类
			b.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class) //对应JDK NIO类库中的ServerSocketChannel类
					.option(ChannelOption.SO_BACKLOG, 1024)
					.childHandler(new ChildChannelHandler());//绑定 I/O事件的处理类
			// 绑定端口，同步等待成功
			ChannelFuture f = b.bind(port).sync(); //绑定监听端口，并调用其同步阻塞方法sync等待绑定操作完成
			Channel c = null;
			AbstractNioChannel a  = null;
			// 等待服务端监听端口关闭
			f.channel().closeFuture().sync(); //进行阻塞，等待服务端链路关闭
		} finally {
			// 优雅退出，释放线程池资源
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {
		@Override
		protected void initChannel(SocketChannel arg0) throws Exception {
			arg0.pipeline().addLast(new TimeServerHandler());
		}

	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		int port = 8080;
		if (args != null && args.length > 0) {
			try {
				port = Integer.valueOf(args[0]);
			} catch (NumberFormatException e) {
				// 采用默认值
			}
		}
		new TimeServer().bind(port);
	}
}
