import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArraySet;
 







import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
 
//��ע������ָ��һ��URI���ͻ��˿���ͨ�����URI�����ӵ�WebSocket������Servlet��ע��mapping��������web.xml�����á�
@ServerEndpoint("/websocket")
public class WebSocket {
    //��̬������������¼��ǰ������������Ӧ�ð�����Ƴ��̰߳�ȫ�ġ�
    private static int onlineCount = 0;
    DBConnector dbc = null; //���ݿ����Ӷ���
    
    //concurrent�����̰߳�ȫSet���������ÿ���ͻ��˶�Ӧ��MyWebSocket������Ҫʵ�ַ�����뵥һ�ͻ���ͨ�ŵĻ�������ʹ��Map����ţ�����Key����Ϊ�û���ʶ
    private static CopyOnWriteArraySet<WebSocket> webSocketSet = new CopyOnWriteArraySet<WebSocket>();
     
    //��ĳ���ͻ��˵����ӻỰ����Ҫͨ���������ͻ��˷�������
    private Session session;
    
    /**
     * ���ӽ����ɹ����õķ���
     * @param session  ��ѡ�Ĳ�����sessionΪ��ĳ���ͻ��˵����ӻỰ����Ҫͨ���������ͻ��˷�������
     * @throws Exception 
     */
    @OnOpen
    public void onOpen(Session session) throws Exception{
        this.session = session;
        webSocketSet.add(this);     //����set��
        addOnlineCount();           //��������1
        //System.out.println("�������Ӽ��룡��ǰ��������Ϊ" + getOnlineCount());
        
        //�������ݿ�����
        try {
			this.dbc = new DBConnector();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        Timer timer = new Timer();
        //�ӳ�0s��ͬʱÿ1�����һ��,�˴�����ʹ��new TimerTask()��ͬʱ��дrun����
        timer.schedule(new TimerTask(){
        	public void run(){
        		for(WebSocket item: webSocketSet){             
                    try {
                        item.sendMessage(dbc.getDevice());
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }
                }
        	}
        }, 0, 1000);
        
    }
     
    /**
     * ���ӹرյ��õķ���
     */
    @OnClose
    public void onClose(){
        webSocketSet.remove(this);  //��set��ɾ��
        subOnlineCount();           //��������1    
        //System.out.println("��һ���ӹرգ���ǰ��������Ϊ" + getOnlineCount());
    }
     
    /**
     * �յ��ͻ�����Ϣ����õķ���
     * @param message �ͻ��˷��͹�������Ϣ
     * @param session ��ѡ�Ĳ���
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("���Կͻ��˵���Ϣ:" + message);
    }
     
    /**
     * ��������ʱ����
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error){
        System.out.println("��������");
        error.printStackTrace();
    }
     
    /**
     * ������������漸��������һ����û����ע�⣬�Ǹ����Լ���Ҫ��ӵķ�����
     * @param message
     * @throws IOException
     */
    public void sendMessage(String message) throws IOException{
        this.session.getBasicRemote().sendText(message);
        //this.session.getAsyncRemote().sendText(message);
    }
 
    public static synchronized int getOnlineCount() {
        return onlineCount;
    }
 
    public static synchronized void addOnlineCount() {
        WebSocket.onlineCount++;
    }
     
    public static synchronized void subOnlineCount() {
        WebSocket.onlineCount--;
    }
}