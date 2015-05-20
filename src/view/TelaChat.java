package view;

import java.awt.Container;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.json.JSONException;
import org.json.JSONObject;

public class TelaChat extends JFrame {
	
	private Socket socket;
	private String usuario;
	
	public TelaChat(Socket s, String titulo) {
		
		this.socket = s;
		this.usuario = titulo;
		
		setTitle( "Chat v1.0 - " + titulo );
		setBounds( 250, 200, 300, 600 );
		setLayout( null );
		
		Container container = getContentPane();
		
		JLabel lblUsuario = new JLabel("Usuário:");
		lblUsuario.setBounds( 25, 200, 200, 20 );
		
		container.add( lblUsuario );
		
		setResizable( false );
		setVisible( true );
		setDefaultCloseOperation( DISPOSE_ON_CLOSE );
		
	}
	

	public static void main(String[] args) {
		
	}
	
}