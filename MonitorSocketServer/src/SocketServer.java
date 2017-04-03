import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class SocketServer {
	private static final int port = 9999; // �����˿�
	private Selector selector = null; // ����һ��selector�������е�channel
	public Charset charset = Charset.forName("UTF-8");

	public void init() throws Exception {
		selector = Selector.open();

		ServerSocketChannel svCnl = ServerSocketChannel.open();
		InetSocketAddress isa = new InetSocketAddress("127.0.0.1", port);

		svCnl.bind(isa);
		svCnl.configureBlocking(false);
		svCnl.register(selector, SelectionKey.OP_ACCEPT);// ��svCnl����selector����
		System.out.println("�������ӿ�ʼѭ������");
		while (selector.select() > 0) {
			for (SelectionKey sk : selector.selectedKeys()) {
				selector.selectedKeys().remove(sk);

				if (sk.isAcceptable()) {
					SocketChannel sc = svCnl.accept();
					sc.configureBlocking(false);
					sc.register(selector, SelectionKey.OP_READ);
				}
				if (sk.isReadable()) {
					SocketChannel sc = (SocketChannel) sk.channel();

					ByteBuffer bb = ByteBuffer.allocate(1024);
					StringBuilder content = new StringBuilder();

					try {
						while (sc.read(bb) > 0) {
							bb.flip(); // ��ֹ����
							content.append(charset.decode(bb));
						}

						// ����java.nio.ByteBuffer;
						// http://blog.csdn.net/zhoujiaxq/article/details/22822289
						System.out.println("���������յ���" + content);

						sk.interestOps(SelectionKey.OP_READ);// ��sk��Ӧ��channelΪ׼���´ζ�ȡ

					} catch (Exception e) {
						System.out.println("�ͻ��������ر�ʱ�رոÿͻ���");
						sc.close();
						sk.cancel();
					}
				}
			}
		}
	}

	public static void main(String[] args) throws Exception, IOException {
		new SocketServer().init();
	}
}
