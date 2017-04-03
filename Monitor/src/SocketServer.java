import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class SocketServer {
	private static final int port = 9999; // �����˿�
	private Selector selector = null; // ����һ��selector�������е�channel
	private Charset charset = Charset.forName("UTF-8"); // �����ַ���ʽ
	private DBConnector dbc;

	public void init() throws Exception {
		dbc = new DBConnector(); // �½����ݿ�����
		selector = Selector.open();

		ServerSocketChannel svCnl = ServerSocketChannel.open();
		InetSocketAddress isa = new InetSocketAddress("127.0.0.1", port);
		svCnl.bind(isa);
		svCnl.configureBlocking(false);
		svCnl.register(selector, SelectionKey.OP_ACCEPT);// ��svCnl����selector����
		System.out.println("�������ӿ�ʼѭ������");

		Map<SelectionKey, StringBuilder> map = new HashMap<SelectionKey, StringBuilder>(); // ����һ��SK��String��map�������ڿͻ����ϴε���Ϣ

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

					try { // ����try catch����read�쳣��ĳ���ͻ����˳���

						if (sc.read(bb) > 0) {
							bb.flip(); // ��ֹ����
							content.append(charset.decode(bb));

							// ����java.nio.ByteBuffer;
							// http://blog.csdn.net/zhoujiaxq/article/details/22822289
							System.out.println("���������յ���" + content);

							// ����Ϣ�������ݿ�
							String deviceId = content.substring(3, 13);
							String deviceState = content.substring(14, 15);
							String state = content.substring(16, 17);
							String groupId = content.substring(18, 24);
//							dbc.insertDevice(deviceId, deviceState, state, groupId);

							map.put(sk, content);// ����Ӧ��content��sk���ȥmap��,�Զ�����
							sk.interestOps(SelectionKey.OP_READ);// ��sk��Ӧ��channelΪ׼���´ζ�ȡ
							
						}

					} catch (Exception e) {
						System.out.println("�ͻ��������ر�ʱ�رոÿͻ���");
						// ��ʾ�ͻ��˶Ͽ����� ���ο����²���
						// http://blog.csdn.net/cao478208248/article/details/41648513
						// http://www.oschina.net/question/558872_168070
						content = map.get(sk);// ��map���ҵ���Ҫ�رյ�sk�Լ����Ӧ��content;
						
						String deviceId = content.substring(3, 13);
						String deviceState = "0"; // ����Ϊ����״̬
						String state = "0";
						String groupId = content.substring(18, 24);
						dbc.insertDevice(deviceId, deviceState, state, groupId);

						System.out.println("�ͻ��������رգ�д�����ݿ�Ϊ����״̬���"+content);
						sc.close(); //�ر�SocketChannel
						sk.cancel(); //ȡ����SelectionKey.
					}
				}
			}
		}
	}
/*//IO��·���ã�����������ͻ��˵�ʱ���е�ʱ�����for (SelectionKey sk : selector.selectedKeys())�������⣬
	public static void main(String[] args) throws Exception, IOException {
		new SocketServer().init();
	}*/
}
