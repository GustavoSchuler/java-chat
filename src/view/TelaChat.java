package view;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.imageio.ImageIO;
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


public class TelaChat extends JFrame implements WindowListener, controller.EventosDoServidorDeSockets, controller.IFileDownloadHandler {
	
	private Socket socket;
	private String usuario, contato;
	private String fotoPadrao = "/view/images/chat-icon.png";
	private JTextArea areaChat, txtMensagem;
	private JButton btEscolherArquivo, btEnviar;
	private JLabel fotoContato, lblContato, fotoUsuario, lblUsuario;
	private JFileChooser fc;
	private Recebedor recebedor;
	private File arquivo;
	
	public TelaChat(Socket s, String titulo, JLabel foto) {
		
		this.socket = s;
		this.usuario = titulo;
		
		setTitle( "Chat v1.0 - " + titulo );
		setBounds( 400, 100, 600, 500 );
		setLayout( null );
		
		Container container = getContentPane();
		
		fotoContato = new JLabel();
		fotoContato.setBounds( 15, 25, 100, 100 );
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
		fotoUsuario.setBounds( 15, 310, 100, 100 );
		fotoUsuario.setIcon( foto.getIcon() );
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
	                			
	                			long tamanhoArquivo = (long) arquivo.length();
	                			
	                			solicitacao.put("tamanho", tamanhoArquivo);
	                			
	                			areaChat.setText( areaChat.getText() + "\nSolicitação de transferência de arquivo enviada.\nArquivo: " + arquivo.getName() + " (" + arquivo.length() + "B)");
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
	
	void enviaSolicitacao() throws JSONException, IOException {
		
		JSONObject solicitacao = new JSONObject();
		
		solicitacao.put("cod", 1);
		solicitacao.put("nome", usuario);
		solicitacao.put("img", controller.ImagemEncoderHelper.encodeImage( fotoUsuario ));
		
		enviaPeloSocket( solicitacao.toString() );
		
		areaChat.setText( areaChat.getText() + "\n Solicitação enviada" );
		
	}
	
	private void confirmaConexao() throws JSONException, IOException {
		
		JSONObject confirmacao = new JSONObject();
		
		confirmacao.put("cod", 0);
		confirmacao.put("nome", usuario);
		confirmacao.put("img", controller.ImagemEncoderHelper.encodeImage( fotoUsuario ));
		
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
	
	private void aceitaEnvioArquivo(long tamanho){

		try {
			OutputStream os = socket.getOutputStream();
			DataOutputStream dos = new DataOutputStream( os );

			int nroPorta = 1752;
			
			JSONObject transacao = new JSONObject();
			transacao.put( "cod", 5 );
			transacao.put( "porta", nroPorta );
			
			dos.writeUTF( transacao.toString() );
			
			Socket socketArquivo = new Socket( socket.getInetAddress().toString().substring(1), nroPorta );
			new controller.FileReceiver( socketArquivo, (int)tamanho, "C:/temp", TelaPrincipal.tlachat);
			
		} catch (Exception e) {
			JOptionPane.showMessageDialog( this, "Não foi possível atender sua requisição: " + e.getMessage() );
		}
		
	}
	
	private void recusaEnvioArquivo(){
		
		try {
			OutputStream os = socket.getOutputStream();
			DataOutputStream dos = new DataOutputStream( os );

			JSONObject transacao = new JSONObject();
			transacao.put( "cod", 6 );
			
			dos.writeUTF( transacao.toString() );

			
		} catch (Exception e) {
			JOptionPane.showMessageDialog( this, "Não foi possível atender sua requisição: " + e.getMessage() );
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
							
							contato = objRecebido.getString( "nome" );
							lblContato.setText( contato );
							
							BufferedImage imag = ImageIO.read(new ByteArrayInputStream(controller.ImagemEncoderHelper.decodeImage( objRecebido.getString( "img" ) )));
							//ImageIO.write(imag, "jpg", new File("C:/temp/", "snap.jpg"));

							areaChat.setText( areaChat.getText() + "\n " + contato + " aceitou a solicitação de conexão." );
							btEnviar.setEnabled( true );
							
			        		fotoContato.setIcon(  new ImageIcon( imag ) );
							repaint();
							
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
					        	}else{
					        		setVisible( true );
					        		contato = objRecebido.getString( "nome" );
					        		lblContato.setText( contato );
					        		
					        		BufferedImage imag = ImageIO.read(new ByteArrayInputStream(controller.ImagemEncoderHelper.decodeImage( objRecebido.getString( "img" ) )));
									//ImageIO.write(imag, "jpg", new File("C:/temp/", "snap.jpg"));
									
					        		areaChat.setText( areaChat.getText() + "\n Conectado com " + contato + "." );
					        		btEnviar.setEnabled( true );
					        		
					        		fotoContato.setIcon(  new ImageIcon( imag )  );
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
							areaChat.setText( areaChat.getText() + "\n" + contato + " quer enviar um arquivo." + "\nArquivo: " + objRecebido.getString("nomeArquivo") + " (" + objRecebido.getLong("tamanho") + "B)");
							if (JOptionPane.showConfirmDialog(null, contato + " quer enviar um arquivo." + "\nArquivo: " + objRecebido.getString("nomeArquivo") + " (" + objRecebido.getLong("tamanho") + "B)", "Solicitação de envio de arquivo",
								JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
								
								aceitaEnvioArquivo( objRecebido.getLong("tamanho") );
								areaChat.setText( areaChat.getText() + "\nVocê aceitou o envio de arquivo.");
								
							} else {
							    recusaEnvioArquivo();
								areaChat.setText( areaChat.getText() + "\nVocê recusou o envio de arquivo.");
							}
						}
						//Envio de arquivo aceito.
						else if (cod == 5){
							
							areaChat.setText( areaChat.getText() + "\n O envio do arquivo foi aceito por " + contato + "." );
							//Antes de instanciar o FilSender tem que ir separando o arquivo em pedaços de 4096 bytes, melhor criar um método.
							//Mandar por aqui os primeiros 4096 bytes, depois disso tem que receber um cód 7 para ir mandando os próximos.
							new controller.FileSender( socket.getInetAddress().toString().substring(1), objRecebido.getInt("porta"), arquivo.getAbsolutePath(), view.TelaPrincipal.tlachat);
							
						}
						//Envio de arquivo recusado.
						else if (cod == 6){
							
							areaChat.setText( areaChat.getText() + "\n " + contato + " não aceitou sua solicitação de envio de arquivo." );
							
						}
						//Sucesso no envio de arquivo.
						else if (cod == 7){
							
							//areaChat.setText( areaChat.getText() + "\n Arquivo enviado." );
							//Daria pra botar até um ProgressBar nessa birosca.
							
						}
						//Erro no envio de arquivo.
						else if (cod == 8){
							
							//Tentar de novo, caso de erro, cancelar.
							areaChat.setText( areaChat.getText() + "\n Ocorreu um erro no envio do arquivo e o mesmo foi cancelado." );
							
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
		
		JSONObject logout = new JSONObject();
		
		try {
			
			logout.put( "cod", 3);
			enviaPeloSocket( logout.toString() );
			
		} catch (JSONException e1) {
			
		}
		
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

	@Override
	public void onFinishSendFile(String fileName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFinishReceiveFile(String fileName) {
		
		JSONObject posicao = new JSONObject();
		
		try {
			
			posicao.put( "cod", 7);
			enviaPeloSocket( posicao.toString() );
			
		} catch (JSONException e1) {
			
		}
		
	}

	@Override
	public void onErrorSendFile(Exception e) {
		
		
	}

	@Override
	public void onErrorReceiveFile(Exception e) {
		
		JSONObject posicao = new JSONObject();
		
		try {
			
			posicao.put( "cod", 8);
			enviaPeloSocket( posicao.toString() );
			
		} catch (JSONException e1) {
			
		}
		
	}
	
}