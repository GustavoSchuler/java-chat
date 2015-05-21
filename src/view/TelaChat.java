package view;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.json.JSONException;
import org.json.JSONObject;

public class TelaChat extends JFrame implements WindowListener {
	
	private Socket socket;
	private String usuario;
	private String fotoPadrao = "/view/images/chat-icon.png";
	private JTextArea areaChat, txtMensagem;
	private JButton btEnviar;
	private JLabel fotoContato, lblContato, fotoUsuario, lblUsuario;
	
	public TelaChat(Socket s, String titulo) {
		
		this.socket = s;
		this.usuario = titulo;
		
		setTitle( "Chat v1.0 - " + titulo );
		setBounds( 400, 100, 600, 500 );
		setLayout( null );
		
		Container container = getContentPane();
		
		fotoContato = new JLabel();
		fotoContato.setBounds( 15, 25, 120, 120 );
		fotoContato.setIcon( new ImageIcon( getClass().getResource( fotoPadrao ) ) );
		fotoContato.setHorizontalAlignment( fotoContato.CENTER );
		
		container.add( fotoContato );
		
		lblContato = new JLabel("NomeDoCara");
		lblContato.setBounds( 15, 145, 120, 20 );
		lblContato.setHorizontalAlignment( lblContato.CENTER );
		container.add( lblContato );	
		
		areaChat = new JTextArea();	
		areaChat.setEditable( false );
		JScrollPane tasp = new JScrollPane( areaChat );
		tasp.setBounds( 150, 25, 430, 300 );
		
		container.add( tasp );
		
		fotoUsuario = new JLabel();
		fotoUsuario.setBounds( 15, 310, 120, 120 );
		fotoUsuario.setIcon( new ImageIcon( getClass().getResource( fotoPadrao ) ) );
		fotoUsuario.setHorizontalAlignment( fotoUsuario.CENTER );
		
		container.add( fotoUsuario );
		
		lblUsuario = new JLabel( usuario );
		lblUsuario.setBounds( 15, 430, 120, 20 );
		lblUsuario.setHorizontalAlignment( lblUsuario.CENTER );
		container.add( lblUsuario );
		
		txtMensagem = new JTextArea();
		JScrollPane txsp = new JScrollPane( txtMensagem );
		txsp.setBounds( 150, 330, 430, 80 );
		
		txtMensagem.addKeyListener(new KeyListener(){
		    @Override
		    public void keyPressed(KeyEvent e){
		        if(e.getKeyCode() == KeyEvent.VK_ENTER){
		        	e.consume();
			    	//enviaMensagem();
		        }
		    }

		    @Override
		    public void keyTyped(KeyEvent e) {}

		    @Override
		    public void keyReleased(KeyEvent e) {}
		});
		
		getContentPane().add( txsp );
		
		container.add( txsp );
		
		btEnviar = new JButton( "Enviar" );
		btEnviar.setBounds( 480, 420, 100, 25);
		
		btEnviar.addActionListener( new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				//enviaMensagem();
				
			}
		});
		
		container.add( btEnviar );
		
		addWindowListener(this);
		
		setResizable( false );
		setVisible( true );
		setDefaultCloseOperation( DISPOSE_ON_CLOSE );
		
	}
	

	public static void main(String[] args) {
		
	}


	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}