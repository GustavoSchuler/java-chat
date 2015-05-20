package controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.json.JSONException;

import controller.EventosDoServidorDeSockets;

public class ServidorDeSockets extends Thread {
	
	private ServerSocket serverSoket;
	private boolean continua;
	private EventosDoServidorDeSockets eventos;
	
	public ServidorDeSockets( int nroPorta, EventosDoServidorDeSockets eventos ) throws IOException {
		serverSoket = new ServerSocket( nroPorta );
		this.eventos = eventos;
	}
	
	private Socket getSocket() throws IOException {
		
		Socket socket = serverSoket.accept();
		return socket;
	}
	
	@Override
	public void run() {
		
		System.out.println( "Iniciando serviço de sockets" );
		try {
			eventos.aoIniciarServidor();
		} catch (JSONException e1) {
			
		}
		
		continua = true;
		while( continua ) {

			try {
				System.out.println( "Servidor de sockets aguardando conexões..." );
				final Socket s = getSocket();
				
				new Thread() {
					public void run() {
						try {
							eventos.aoReceberSocket(s);
						} catch (JSONException e) {
							
						}
					};
				}.start();
				
			} catch (IOException e) {
				if( continua ) {
					try {
						eventos.reportDeErro(e);
					} catch (JSONException e1) {
						
					}
				}
			}
		}

		System.out.println( "Finalizando serviço de sockets" );
		try {
			eventos.aoFinalizarServidor();
		} catch (JSONException e) {

		}
	}
	
	public void finaliza() {
		
		continua = false;
		try {
			serverSoket.close();
		} catch (IOException e) {
		}
	}
	
}









