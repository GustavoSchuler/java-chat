package view;

import java.awt.Container;
import java.awt.Graphics2D;
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.CodingErrorAction;

import javafx.stage.FileChooser;

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

import controller.EventosDoServidorDeSockets;

/*
 No seu form de chat, implemente a interface IFileDownloadHandler.java.

Utilize a thread FileSender.java para enviar um arquivo.

- hostAddress: o endereço do novo socket de envio de arquivo.
- port: a porta do novo socket de envio de arquivo.
- filePathToSend: o caminho do arquivo a ser enviado.
- fileDownloadHandler: o form que implementou ``IFileDownloadHandler.java``

Utilize a thread FileReceiver.java para receber um arquivo.

- socket: o novo socket para recebimento do arquivo.
- fileSize: o tamanho do arquivo que está sendo enviado.
- filePathToSave: o caminho onde deve salvar o arquivo.
- fileDonwloadHandler: o form que implementou ``IFileDownloadHandler.java``

 */

public class TelaChat extends JFrame implements WindowListener, controller.EventosDoServidorDeSockets, controller.IFileDownloadHandler {
	
	private Socket socket;
	private String usuario, contato;
	private String fotoPadrao = "/view/images/chat-icon.png";
	private JTextArea areaChat, txtMensagem;
	private JButton btEscolherArquivo, btEnviar;
	private JLabel fotoContato, lblContato, fotoUsuario, lblUsuario;
	private JFileChooser fc;
	private Recebedor recebedor;
	private controller.ServidorDeSockets servidorArquivo;
	private ServerSocket serverSoketArquivo;
	private boolean continua;
	
	public TelaChat(Socket s, String titulo, JLabel foto) {
		
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
	
	private void aceitaEnvioArquivo(){

		try {
			OutputStream os = socket.getOutputStream();
			DataOutputStream dos = new DataOutputStream( os );

			int nroPorta = 64000;
			
			JSONObject transacao = new JSONObject();
			transacao.put( "cod", 5 );
			transacao.put( "porta", nroPorta );
			
			dos.writeUTF( transacao.toString() );
			//controller.FileReceiver receiver = new controller.FileReceiver();
			
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
		
	
	private void iniciaServidorArquivo( int porta ){
			
			if( servidorArquivo == null ) {
				try {
					
					servidorArquivo = new controller.ServidorDeSockets( porta, this );
					servidorArquivo.start();
					
					FileInputStream fis = null;
					
					File arquivo = fc.getSelectedFile();
					String nomeArquivo = arquivo.getName();
					String tmpdir = System.getProperty("java.io.tmpdir");
					
					System.out.println("nome do arquivo: " + nomeArquivo);
					System.out.println("nome do arquivo: " + tmpdir);
					
					byte[] bFile = new byte[(int) arquivo.length()];
					 
					try {

						fis = new FileInputStream(arquivo);
						fis.read(bFile);
						fis.close();

						
						FileOutputStream fileOuputStream = 
						new FileOutputStream(tmpdir + nomeArquivo); 
						fileOuputStream.write(bFile);
						fileOuputStream.close();

						System.out.println("enviar cod 7 - confirma");
						}catch(Exception e){
							e.printStackTrace();
						}
					
					
	
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			} else {
				
				servidorArquivo.finaliza();
				servidorArquivo = null;
	
			}
			
		}
	
	public void finalizaServidorArquivo() {
			continua = false;
			try {
				serverSoketArquivo.close();
			} catch (IOException e) {
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
							areaChat.setText( areaChat.getText() + "\n" + contato + " quer enviar um arquivo." + "\nArquivo: " + objRecebido.getString("nomeArquivo") + " (" + objRecebido.getInt("tamanho") + "KB)");
							if (JOptionPane.showConfirmDialog(null, contato + " quer enviar um arquivo." + "\nArquivo: " + objRecebido.getString("nomeArquivo") + " (" + objRecebido.getInt("tamanho") + "KB)", "Solicitação de envio de arquivo",
								JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
								aceitaEnvioArquivo();
								areaChat.setText( areaChat.getText() + "\nVocê aceitou o envio de arquivo.");
								
							} else {
							    recusaEnvioArquivo();
								areaChat.setText( areaChat.getText() + "\nVocê recusou o envio de arquivo.");
							}
						}
						//Envio de arquivo aceito.
						else if (cod == 5){
							
							areaChat.setText( areaChat.getText() + "\n O envio do arquivo foi aceito por " + contato + "." );
							//recebe o número da porta e envia o arquivo
							
						}
						//Envio de arquivo recusado.
						else if (cod == 6){
							
							areaChat.setText( areaChat.getText() + "\n " + contato + " não aceitou sua solicitação de envio de arquivo." );
							
						}
						//Sucesso no envio de arquivo.
						else if (cod == 7){
							
							areaChat.setText( areaChat.getText() + "\n Arquivo enviado." );
							
						}
						//Erro no envio de arquivo.
						else if (cod == 8){
							
							areaChat.setText( areaChat.getText() + "\n Ocorreu um erro no envio do arquivo." );
							//Ver o que fazer para enviar novamente.
							
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onErrorSendFile(Exception e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onErrorReceiveFile(Exception e) {
		// TODO Auto-generated method stub
		
	}
	
	/*
	public static void main(String[] args) {
		new TelaChat(new Socket(), "teste", new JLabel(""));
	}
	*/
}