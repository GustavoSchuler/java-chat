package view;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class TelaPrincipal extends JFrame{
	
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
		
		JTextField txtUsuario = new JTextField();
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
		
		JButton btnConectar = new JButton("Conectar");
		btnConectar.setBounds( 25, 340, 250, 35 );
		
		container.add( btnConectar );
		
		setResizable( false );
		setVisible( true );
		
	}
	
	
	
}