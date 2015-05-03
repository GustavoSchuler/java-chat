package view;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.Socket;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import view.TelaChat;

public class TelaPrincipal extends JFrame implements ActionListener, controller.EventosDoServidorDeSockets, WindowListener {
	
	private controller.ServidorDeSockets servidor;
	private JButton btnConectar;
	private JLabel lblInfo;
	private JTextField txtUsuario;
	
	public TelaPrincipal() {
		
		setTitle( "Chat v1.0" );
		setBounds( 200, 100, 300, 600 );
		setLayout( null );
		
		Container container = getContentPane();
		
		JLabel img = new JLabel();
		container.add( img );
		img.setBounds( 100, 80, 100, 100);
		
		ImageIcon imagem = new ImageIcon( getClass().getResource( "images/chat-icon.png" ) );
		img.setIcon( imagem );
		
		JLabel lblUsuario = new JLabel("Usuario:");
		lblUsuario.setBounds( 25, 200, 200, 20 );
		
		container.add( lblUsuario );
		
		txtUsuario = new JTextField();
		txtUsuario.setBounds( 25, 220, 250, 25 );
		
		container.add( txtUsuario );
		
		JLabel lblEndereco = new JLabel("Endereco:");
		lblEndereco.setBounds( 25, 260, 200, 20 );
		
		container.add( lblEndereco );
		
		JTextField txtEndereco = new JTextField();
		txtEndereco.setBounds( 25, 280, 150, 25 );
		txtEndereco.setText( "localhost" );
		
		container.add( txtEndereco );
		
		JLabel lblPorta = new JLabel("Porta:");
		lblPorta.setBounds( 180, 260, 200, 20 );
		
		container.add( lblPorta );
		
		JTextField txtPorta = new JTextField();
		txtPorta.setBounds( 180, 280, 90, 25 );
		txtPorta.setText( "1843" );
		
		container.add( txtPorta );
		
		btnConectar = new JButton("Conectar");
		btnConectar.setBounds( 25, 340, 250, 35 );
		btnConectar.addActionListener( this );
		
		container.add( btnConectar );
		
		lblInfo = new JLabel();
		lblInfo.setBounds( 25, 550, 250, 25);
		
		container.add( lblInfo );
		
		setResizable( false );
		setVisible( true );
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if( !txtUsuario.getText().equals( "" ) ){
			
			if( servidor == null ) {
				
				try {
					
					servidor = new controller.ServidorDeSockets( 1843, this );
					servidor.start();
					btnConectar.setText( "Desconectar" );
					
				} catch (IOException e1) {
					
					e1.printStackTrace();
					
				}
				
			} else {
				
				servidor.finaliza();
				btnConectar.setText( "Conectar" );
				servidor = null;
				
			}
		} else {
			
			JOptionPane.showMessageDialog( null, "Digite o Usuario!" );
			txtUsuario.requestFocusInWindow();
			
		}
		
	}

	@Override
	public void aoIniciarServidor() {
		
		lblInfo.setText( "Iniciando servidor..." );
		
	}

	@Override
	public void aoFinalizarServidor() {
		
		lblInfo.setText( "Desconectado" );
		
	}

	@Override
	public void aoReceberSocket(Socket s) {
		
		lblInfo.setText( "Conectando ao servidor..." );
		iniciaComunicacao( s );
		
	}

	private void iniciaComunicacao(Socket s) {
		
		new TelaChat( s, "Servidor" );
		lblInfo.setText( "Conectado" );
		
	}
	
	@Override
	public void reportDeErro(IOException e) {
		
		JOptionPane.showMessageDialog( this, "Erro: " + e.getMessage()  );
		
	}
	
	@Override
	public void windowActivated(WindowEvent arg0) {}

	@Override
	public void windowClosed(WindowEvent arg0) {
		
		if( servidor != null ) {
			servidor.finaliza();
		}
	}

	@Override
	public void windowClosing(WindowEvent arg0) {}

	@Override
	public void windowDeactivated(WindowEvent arg0) {}

	@Override
	public void windowDeiconified(WindowEvent arg0) {}

	@Override
	public void windowIconified(WindowEvent arg0) {}

	@Override
	public void windowOpened(WindowEvent arg0) {}
	
}