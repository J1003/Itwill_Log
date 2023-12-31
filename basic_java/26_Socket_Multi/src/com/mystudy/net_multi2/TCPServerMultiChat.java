package com.mystudy.net_multi2;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

// === 멀티채팅 서버측 기능 ===
// 다중 접속을 처리할 수 있는 서버(서버에서 읽기만 - 독립적인 쓰레드)
// <읽기전용Thread> 
//	 ㄴ 접속자가 보낸 메세지를 언제든지 읽을 수 있어야 함
//	 ㄴ 접속자 명단을 가지고 있어야 함
//	 ㄴ 받은(읽은) 메세지를 접속자 모두에게 전송하는 기능도 있어야 함

public class TCPServerMultiChat {
	private HashMap<String, DataOutputStream> clients;

	public static void main(String[] args) {
		System.out.println("==== main() 시작 =======");
		new TCPServerMultiChat().serverStart();
		System.out.println("==== main() 끝 =======");

	}
	
	public TCPServerMultiChat() {
		clients = new HashMap<String, DataOutputStream>();
	}

	private void serverStart() {
		// 서버소켓을 만들고, 클라이언트 접속처리를 반복한다
		ServerSocket server = null;
		
		try {
			System.out.println(">> 서버소켓 객체 생성");
			server = new ServerSocket(10000);
			
			while (true) {
				System.out.println(">> 접속 대기중~~~ " 
						+ InetAddress.getLocalHost().getHostAddress()
						+ ":" + server.getLocalPort());
				
				Socket socket = server.accept();
				System.out.println("사용자가 접속되었습니다 - " 
						+ socket.getRemoteSocketAddress());
				
				// 접속된 클라이언트(사용자)에 대한 처리 - 쓰레드 생성(읽기전용)
				System.out.println(":: 접속유저 읽기 전용 쓰레드 만들었지롱!");
				ServerReceiver thread = new ServerReceiver(socket);
				thread.start(); //쓰레드로 동작시키기
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// ServerReceiver : 클라이언트에서 보내온 메시지 수신 전용
	private class ServerReceiver extends Thread {
		Socket socket;
		DataInputStream in;
		DataOutputStream out;
		String name;

		public ServerReceiver(Socket socket) {
			this.socket = socket;
			try {	//받은 메세지를 보내기 위한 목적으로 output객체도 같이 작성
				in = new DataInputStream(socket.getInputStream());
				out = new DataOutputStream(socket.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			// 생성될때 : 필드에 접속자명단(필드 clients)에 등록(추가)
			// 종료될때 : 필드에 접속자명단(필드 clients)에 제외(삭제)
			// 메시지 받고, 받은 메시지 전체에게 전달
			System.out.println(">>> 읽기 전용 쓰레드 시작 -----");
			try {
				// 사용자 등록 작업
				name = socket.getInetAddress().getHostAddress();
				clients.put(name, out); //접속자 정보 등록
				
				while (true) {
					// 메세지 읽고 화면 출력
					String msg = in.readUTF();
					if (msg == null || "exit".equalsIgnoreCase(msg)) {
						break;
					}
					System.out.println(name + "> " + msg);
					
					//접속자 전원에게 메시지 일괄 전송하기
					sendToAll(name + "] " + msg);
				}
			} catch (IOException e) {
				//e.printStackTrace();
				System.out.println("[예외발생] " + e.getMessage());
			} finally {
				System.out.println(socket.getInetAddress().getHostAddress() + "> 클라이언트 종료");
				// 종료할 때 접속자 명단에서 제외(clients에서 삭제)
				clients.remove(name);
				// 삭제되었으면 메세지 입력 및 출력
				String outMsg = "<" + name + ">님이 나갔습니다.";
				sendToAll(outMsg);
				System.out.println(outMsg);
			}
		}
		
		private void sendToAll(String msg) {
			//접속자 전원에게 메시지 일괄 전송하기
			// clients.values()하면 ouput객체를 가져올 수 있다.
			for (DataOutputStream out : clients.values()) {
				try {
					out.writeUTF(msg);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		// (개인적 실습) 전체메세지 : 내가 보낸 메세지는 받지 않도록 처리
	
	}
	
} //end class



