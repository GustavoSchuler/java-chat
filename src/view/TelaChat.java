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
	private Recebedor recebedor;
	
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
		
		recebedor = new Recebedor();
		recebedor.start();
		
	}
	
	private class Recebedor extends Thread {

		@Override
		public void run() {
			
			try {
				InputStream is = socket.getInputStream();
				
				while( isVisible() ) {
					
					int tam = is.read();
					
					if( tam > 0 ) {
						byte[] buffer = new byte[ tam ];
						
						is.read( buffer );
						
						String msg = new String( buffer );
						
						JSONObject objRecebido = new JSONObject( msg );

						int cod = objRecebido.getInt( "cod" );
						
						//Solicitação de conexão.
						if (cod == 1){
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
									confirmaConexao();
									//areaChat.setText( areaChat.getText() + "\n Conectado com: " + msg.substring(3) );
					        	}
					        }
					        else {
					            negaConexao();
					        }
						}
						//Conexão aceita
						else if (cod == 0) {
							
						}
						//Conexão rejeitada
						else if (cod == -1){
							JOptionPane.showMessageDialog( null , "O usuário negou a conexão." );
						}
						
					}
				}
			} catch (IOException | JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	void enviaSolicitacao() throws JSONException {
		
		JSONObject solicitacao = new JSONObject();
		
		solicitacao.put("cod", 1);
		solicitacao.put("nome", usuario);
		solicitacao.put("img", "imgAqui");
		
		enviaPeloSocket( solicitacao.toString() );
		
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
			
			byte[] enviar = txt.getBytes();
			
			os.write( enviar.length );
			os.write( enviar );
			os.flush();
			
		} catch (IOException e) {
			JOptionPane.showMessageDialog( this, "Não foi possível enviar sua mensagem: " + e.getMessage() );
		}
	}

	public static void main(String[] args) {
		
	}
	
}