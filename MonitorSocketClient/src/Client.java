import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.Timer;
//http://blog.csdn.net/zhangyiacm/article/details/49488721
public class Client {
	
	public static void main(String[] args) throws Exception, IOException {
		
		 String host = "127.0.0.1";
		 int port = 9999; // �˿ں�
		 int count = 0; //��¼�����˶��ٴ�
		 Socket client = new Socket(host, port);// ����socket;

		while (true) {
			Writer wt = new OutputStreamWriter(client.getOutputStream()); // ���������
			int z1 = (new Random()).nextInt(2); // 1Ϊ������0Ϊ�ػ�,ʹ���������
			int z2 = 0; // 0��ʾ������������ʾ������
			if (z1 == 1)
				z2 = (new Random()).nextInt(4) + 1; // ����1-4֮�ڵ������
			else
				z2 = 0;
			String str = "#mj" + args[0] + "," + z1 + "," + z2 + ",000001,u";
			count++;
			System.out.println(count+" : "+str);
			
			wt.write(str); // #mj2011102701,1,0,000000,u
			wt.flush();

			try {
				Thread.sleep(1000 * 3);
			} catch (InterruptedException e) {
				e.printStackTrace();
				break; //����ʱ����������������ѭ��
			}
		}
		client.close();
	}
}
