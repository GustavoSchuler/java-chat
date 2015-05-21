package view;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javafx.stage.FileChooser;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.json.JSONException;
import org.json.JSONObject;

import controller.EventosDoServidorDeSockets;

public class TelaChat extends JFrame implements WindowListener, controller.EventosDoServidorDeSockets {
	
	private Socket socket;
	private String usuario, contato;
	private String fotoPadrao = "/view/images/chat-icon.png";
	private JTextArea areaChat, txtMensagem;
	private JButton btEscolherArquivo, btEnviar;
	private JLabel fotoContato, lblContato, fotoUsuario, lblUsuario;
	private JFileChooser fc;
	private Recebedor recebedor;
	
	public TelaChat(Socket s, String titulo, String contato) {
		
		this.socket = s;
		this.usuario = titulo;
		this.contato = contato;
		
		setTitle( "Chat v1.0 - " + titulo );
		setBounds( 400, 100, 600, 500 );
		setLayout( null );
		
		Container container = getContentPane();
		
		fotoContato = new JLabel();
		fotoContato.setBounds( 15, 25, 120, 120 );
		fotoContato.setIcon( new ImageIcon( getClass().getResource( fotoPadrao ) ) );
		fotoContato.setHorizontalAlignment( fotoContato.CENTER );
		
		container.add( fotoContato );
		
		lblContato = new JLabel( contato );
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
			    	try {
						enviaMensagem();
					} catch (JSONException e1) {
					}
		        }
		    }

		    @Override
		    public void keyTyped(KeyEvent e) {}

		    @Override
		    public void keyReleased(KeyEvent e) {}
		});
		
		getContentPane().add( txsp );
		
		container.add( txsp );
		
		btEscolherArquivo = new JButton( "Arquivo" );
		btEscolherArquivo.setBounds( 370, 420, 100, 25 );
		
		btEscolherArquivo.addActionListener(
	            new ActionListener(){
	                public void actionPerformed(ActionEvent e){
	                    fc = new JFileChooser();
	                    int res = fc.showOpenDialog(null);
	                    
	                    if(res == JFileChooser.APPROVE_OPTION){
	                        File arquivo = fc.getSelectedFile();  
	                        
	                        try {
	                			OutputStream os = socket.getOutputStream();
	                			DataOutputStream dos = new DataOutputStream( os );

	                			JSONObject solicitacao = new JSONObject();
	                			solicitacao.put("cod", 4);
	                			solicitacao.put("nomeArquivo",arquivo.getName());
	                			
	                			int tamanhoArquivo = (int)(long)(arquivo.length()/1024);
	                			
	                			solicitacao.put("tamanho", tamanhoArquivo);
	                			
	                			areaChat.setText( areaChat.getText() + "\nSolicitação de transferência de arquivo enviada.\nArquivo: " + arquivo.getName() + " (" + tamanhoArquivo + "KB)");
	                			txtMensagem.setText( "" );
	                			txtMensagem.requestFocusInWindow();
	                			
	                			dos.writeUTF( solicitacao.toString() );
	                			
	                        } catch (Exception ee) {
	                			JOptionPane.showMessageDialog( null, "Não foi possível atender sua requisição: " + ee.getMessage() );
	                		}
	                        
	                    } 
	                }
	            }   
	        );
		
		container.add( btEscolherArquivo );
		
		btEnviar = new JButton( "Enviar" );
		btEnviar.setBounds( 480, 420, 100, 25 );
		
		btEnviar.addActionListener( new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					enviaMensagem();
				} catch (JSONException e) {
					
				}
			}
		});
		
		container.add( btEnviar );
		btEnviar.setEnabled( false );
		
		addWindowListener(this);
		
		setResizable( false );
		setVisible( true );
		setDefaultCloseOperation( DISPOSE_ON_CLOSE );
		
		recebedor = new Recebedor();
		recebedor.start();
		
	}
	
	private void enviaMensagem() throws JSONException {
		
		String txt = txtMensagem.getText();
		
		if( txt.length() > 0 ) {
			
			areaChat.setText( areaChat.getText() + "\n " + usuario + ": " + txt );
			txtMensagem.setText( "" );
			txtMensagem.requestFocusInWindow();
			
			JSONObject msg = new JSONObject();
			
			msg.put( "cod", 2 );
			msg.put( "mensagem", txt );
			
			txt = msg.toString();
			
			enviaPeloSocket( txt );
		}
	}
	
	void enviaSolicitacao() throws JSONException {
		
		JSONObject solicitacao = new JSONObject();
		
		solicitacao.put("cod", 1);
		solicitacao.put("nome", usuario);
		solicitacao.put("img", "imgAqui");
		
		enviaPeloSocket( solicitacao.toString() );
		
		areaChat.setText( areaChat.getText() + "/n Solicitação enviada" );
		
	}
	
	private void confirmaConexao() throws JSONException {
		
		JSONObject confirmacao = new JSONObject();
		
		confirmacao.put("cod", 0);
		confirmacao.put("nome", usuario);
		confirmacao.put("img", "imgAqui");
		
		enviaPeloSocket( confirmacao.toString() );
		
	}
	
	private void negaConexao() throws JSONException {
		
		JSONObject negacao = new JSONObject();
		
		negacao.put("cod", -1);
		
		enviaPeloSocket( negacao.toString() );
		
	}
	
	private void enviaPeloSocket( String txt ) {
		
		try {
			OutputStream os = socket.getOutputStream();
			DataOutputStream dos = new DataOutputStream( os );
			
			dos.writeUTF( txt );
			
		} catch (IOException e) {
			JOptionPane.showMessageDialog( this, "Não foi possível enviar sua mensagem: " + e.getMessage() );
		}
	}
	
	private class Recebedor extends Thread {

		@Override
		public void run() {
			
			try {
				InputStream is = socket.getInputStream();
				DataInputStream dis = new DataInputStream( is );

				while( isVisible() ) {
					
					String msg = dis.readUTF();
					if( msg != null ) {
						
						JSONObject objRecebido = new JSONObject( msg );

						int cod = objRecebido.getInt( "cod" );
						
						//Conexão rejeitada
						if (cod == -1){
							
							JOptionPane.showMessageDialog( null , "O usuário negou a conexão." );
							
						}
						//Conexão aceita
						else if (cod == 0) {
							
							//new TelaChat( socket, usuario, objRecebido.getString( "nome" ) );
							btEnviar.setEnabled( true );
						}
						//Solicitação de conexão.
						else if (cod == 1){
							
							int n = JOptionPane.showConfirmDialog(
					            null,
					            "O usuário " + objRecebido.getString( "nome" ) + " deseja iniciar uma conversa.",
					            "Solicitação de conexão",
					            JOptionPane.YES_NO_OPTION);

					        if(n == 0){
					        	if( usuario.equals( "" ) ){
					        		JOptionPane.showMessageDialog(null, "Digite o nome de usuário!");
					        		//txtUsuario.requestFocusInWindow();
					        	}else{
					        		contato = objRecebido.getString( "nome" );
					        		btEnviar.setEnabled( true );
					        		repaint();
									confirmaConexao();
					        	}
					        }
					        else {
					            negaConexao();
					        }
					        
						}
						//Mensagem.
						else if (cod == 2){
							
							areaChat.setText( areaChat.getText() + "\n " + contato + ": " + objRecebido.getString( "mensagem" ) );
							
						}
						//Logout.
						else if (cod == 3){
							
							areaChat.setText( areaChat.getText() + "\n " + contato + " desconectou-se." );
							txtMensagem.setEnabled( false );
							btEnviar.setEnabled( false );
							btEscolherArquivo.setEnabled(false);
							
						}
						//Requisição de envio de arquivo.
						else if (cod == 4){
							
						}
						//Envio de arquivo aceito.
						else if (cod == 5){
							
						}
						//Envio de arquivo recusado.
						else if (cod == 6){
							
						}
						//Sucesso no envio de arquivo.
						else if (cod == 7){
							
						}
						//Erro no envio de arquivo.
						else if (cod == 8){
							
						}
					}
				}
			} catch (IOException | JSONException e) {
				e.printStackTrace();
			}
		}
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

	@Override
	public void aoIniciarServidor() throws JSONException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void aoFinalizarServidor() throws JSONException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void aoReceberSocket(Socket s) throws JSONException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reportDeErro(IOException e) throws JSONException {
		// TODO Auto-generated method stub
		
	}
	
}