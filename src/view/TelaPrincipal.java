package view;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.json.JSONException;

import view.TelaChat;

public class TelaPrincipal extends JFrame implements ActionListener, controller.EventosDoServidorDeSockets, WindowListener {
	
	private controller.ServidorDeSockets servidor;
	private JLabel img;
	private JButton btnConectar;
	private JButton btnIniciar;
	private JLabel lblInfo;
	private JTextField txtUsuario;
	private JTextField txtEndereco;
	private JTextField txtPorta;
	private Socket socket;
	public static TelaChat tlachat;
	private String contato;
	
	public TelaPrincipal() {
		
		setTitle( "Chat v1.0" );
		setBounds( 200, 100, 300, 600 );
		setLayout( null );
		
		Container container = getContentPane();
		
		img = new JLabel();
		container.add( img );
		img.setBounds( 100, 50, 100, 100 );
		
		ImageIcon imagem = new ImageIcon( getClass().getResource( "images/chat-icon.png" ) );
		img.setIcon( imagem );
		
		JButton btImagem = new JButton( "Imagem..." );
		btImagem.setBounds( 100, 160, 100, 20 );
		
		btImagem.addActionListener(
            new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    JFileChooser fc = new JFileChooser();
                    int res = fc.showOpenDialog(null);
                    
                    if(res == JFileChooser.APPROVE_OPTION){
                        File arquivo = fc.getSelectedFile();  
                        
                        String extensao = arquivo.getName().substring(arquivo.getName().lastIndexOf(".")+1).toLowerCase();
                        	
                        if(!extensao.equals("jpg") && !extensao.equals("jpeg")){
                        	JOptionPane.showMessageDialog(null, "Formato de Arquivo inv�lido, selecione um arquivo JPG.");
                        }else{
	                        img.setIcon(new ImageIcon(arquivo.getAbsolutePath()));
                        }
                    }
                }

            }   
        );
		
		container.add( btImagem );
		
		JLabel lblUsuario = new JLabel("Usu�rio:");
		lblUsuario.setBounds( 25, 200, 200, 20 );
		
		container.add( lblUsuario );
		
		txtUsuario = new JTextField();
		txtUsuario.setBounds( 25, 220, 250, 25 );
		
		container.add( txtUsuario );
		
		JLabel lblEndereco = new JLabel("Endere�o:");
		lblEndereco.setBounds( 25, 260, 200, 20 );
		
		container.add( lblEndereco );
		
		txtEndereco = new JTextField();
		txtEndereco.setBounds( 25, 280, 150, 25 );
		txtEndereco.setText( "192.168.25.5" );
		
		container.add( txtEndereco );
		
		JLabel lblPorta = new JLabel("Porta:");
		lblPorta.setBounds( 180, 260, 200, 20 );
		
		container.add( lblPorta );
		
		txtPorta = new JTextField();
		txtPorta.setBounds( 180, 280, 90, 25 );
		txtPorta.setText( "8080" );
		
		container.add( txtPorta );
		
		btnConectar = new JButton("Login");
		btnConectar.setBounds( 25, 340, 250, 35 );
		btnConectar.addActionListener( this );
		
		container.add( btnConectar );
		
		btnIniciar = new JButton("Iniciar Chat");
		btnIniciar.setBounds( 25, 380, 250, 35 );
		btnIniciar.setEnabled( false );
		btnIniciar.addActionListener( new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				conectar();
			}
		} );
		
		container.add( btnIniciar );
		
		lblInfo = new JLabel();
		lblInfo.setBounds( 25, 550, 250, 25);
		
		container.add( lblInfo );
		
		addWindowListener( new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent e) {
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
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				
				if (servidor != null){
					servidor.finaliza();
				}
				
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		setResizable( false );
		setVisible( true );
		setDefaultCloseOperation( DISPOSE_ON_CLOSE );
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if( !txtUsuario.getText().equals( "" ) ){
			
			if( servidor == null ) {
				
				try {
					
					servidor = new controller.ServidorDeSockets( Integer.parseInt(txtPorta.getText()), this );
					servidor.start();
					btnConectar.setText( "Desconectar" );
					btnIniciar.setEnabled( true );
					
				} catch (IOException e1) {
					
					e1.printStackTrace();
					
				}
				
			} else {
				
				servidor.finaliza();
				btnConectar.setText( "Conectar" );
				btnIniciar.setEnabled( false );
				servidor = null;
				
			}
			
		} else {
			
			JOptionPane.showMessageDialog( null, "Digite o Usu�rio!" );
			txtUsuario.requestFocusInWindow();
			
		}
		
	}

	@Override
	public void aoIniciarServidor() throws JSONException {
		
		lblInfo.setText( "Servidor iniciado...  Aguardando conex�es" );
		txtUsuario.setEnabled( false );
		txtEndereco.setEnabled( false );
		txtPorta.setEnabled( false );		
		
	}
	

	@Override
	public void aoFinalizarServidor() throws JSONException {
		
		lblInfo.setText( "Desconectado" );
		txtUsuario.setEnabled( true );
		txtEndereco.setEnabled( true );
		txtPorta.setEnabled( true );
		
	}

	@Override
	public void aoReceberSocket(Socket s) throws JSONException {
		
		lblInfo.setText( "Conectando ao servidor..." );
		iniciaComunicacao( s );
		
	}

	private void iniciaComunicacao(Socket s) throws JSONException {
		this.socket = s;
		if (tlachat == null){
			tlachat = new TelaChat( socket, txtUsuario.getText(), img );
		}
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
	
	
	protected void conectar() {

		String end = txtEndereco.getText().trim();
		String prt = txtPorta.getText().trim();
		
		if( end.length() == 0 ) {
			JOptionPane.showMessageDialog( this, "Defina o endere�o para conex�o" );
			txtEndereco.requestFocusInWindow();
			return;
		}
		
		try {
			int nrPrt = Integer.parseInt( prt );

			try {
				socket = new Socket( end, nrPrt );
				if (tlachat == null){
					tlachat = new TelaChat( socket, txtUsuario.getText(), img );
					tlachat.enviaSolicitacao();
				}
			} catch( Exception e ) {
				JOptionPane.showMessageDialog( this, "Erro: " + e.getMessage()  );
			}
		} catch( Exception e ) {
			JOptionPane.showMessageDialog( this, "Defina o n�mero da porta para conex�o" );
			txtPorta.requestFocusInWindow();
			return;
		}
	}
	

}