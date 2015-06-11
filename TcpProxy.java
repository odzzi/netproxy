import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * please keep this comments.
 * @author billy.wuweibin@gmail.com
 * @date 2015-06-11
 * @desc to provide a channel for tcp connetions.
 */  
public class TcpProxy implements Runnable{
	private static int MAX_BUFFER = 65535;
	private String localip, remoteip;
	private int localport, remoteport;
	private boolean ruuning =false;
	private ServerSocket server;
	public TcpProxy(String localip, int localport, String remoteip, int remoteport){
		this.localip = localip;
		this.localport = localport;
		this.remoteip = remoteip;
		this.remoteport = remoteport;
	}
	class ProxyThread extends Thread{
		Socket a, z;
		public ProxyThread(Socket a, Socket z){
			this.a=a;
			this.z=z;
		}
		private void closeSocket(Socket s){
			if(null != s){
				if(!s.isClosed()){
					try {
						s.close();
					} catch (IOException e) {
						//e.printStackTrace();
					}
				}
			}
		}
		public void run(){
			byte b[] = new byte[MAX_BUFFER];
			int len;
			while(true){
				try {
					len = a.getInputStream().read(b);
					if(len<0){
						break;
					}
					z.getOutputStream().write(b,0,len);
				} catch (IOException e) {					
					break;
				}
			}
			closeSocket(a);
			closeSocket(z);
		}
	}
	public void run(){
		setRunning(true);
		
		try {
			server = new ServerSocket();
			server.bind(new InetSocketAddress(localip, localport));
			while(isRunning()){
				Socket uni = server.accept();
				Socket nni = new Socket(remoteip, remoteport);
				new ProxyThread(uni, nni).start();
				new ProxyThread(nni, uni).start();
			}
		} catch (IOException e) {			
			//e.printStackTrace();
			setRunning(false);
			return;
		}catch (Exception e) {			
			//e.printStackTrace();
			setRunning(false);
			return;
		}

		setRunning(false);
	}
	private synchronized void setRunning(boolean run) {
		ruuning = run;
	}
	private synchronized boolean isRunning() {
		return ruuning;
	}
	public void Stop() {
		setRunning(false);
		
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TcpProxy proxy = new TcpProxy(args[0], Integer.parseInt(args[1]), args[2], Integer.parseInt(args[3]));
		new Thread(proxy).start();
		//proxy.Stop();

	}
	

}
