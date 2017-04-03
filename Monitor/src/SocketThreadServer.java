import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

//���ö��߳̽��մ���ͻ���
public class SocketThreadServer extends Thread{
	private Socket client;
	private DBConnector dbc;
	public SocketThreadServer(Socket c) throws Exception{
		this.dbc = new DBConnector(); // �½����ݿ�����
		this.client = c;
	}
	
	public void run(){
		String backup = "0",content = "",deviceId = "",deviceState = "",state,groupId = "000001";
		try{
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));  
            // Mutil User but can't parallel  
            while (true) {
            	char[] s = new char[27];
                int rt = in.read(s);
                if(rt == -1) break;
                content = new String(s);
                System.out.println("Server��"+content);  
				state = content.substring(16, 17);
                if( backup.equals(state))//������״̬���ϴ��б仯��д�����ݿ�
                	continue; //��������������һ��ѭ��
            
                backup = state;//����ǰ��״̬��������
				// ����Ϣ�������ݿ�
				deviceId = content.substring(3, 13);
				deviceState = content.substring(14, 15);
				dbc.insertDevice(deviceId, deviceState, state, groupId);
            }
		}catch(Exception e){
			System.out.println("������~");
		} finally {
			deviceState = "0"; // ����Ϊ����״̬
			state = "0";
			try {
				dbc.insertDevice(deviceId, deviceState, state, groupId);
				System.out.println("�ͻ��������رգ�д�����ݿ�Ϊ����״̬���"+content);
	            client.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        } 
	}
    @SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
        ServerSocket server = new ServerSocket(9999);  
        while (true) {
        	System.out.println("Socket Thread Server �����µĿͻ���");  
        	SocketThreadServer mc = new SocketThreadServer(server.accept());  
            mc.start();
        }  
    }  
}
