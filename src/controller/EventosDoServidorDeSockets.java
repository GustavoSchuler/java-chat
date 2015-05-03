package controller;

import java.io.IOException;
import java.net.Socket;

public interface EventosDoServidorDeSockets {

	public void aoIniciarServidor();
	public void aoFinalizarServidor();
	public void aoReceberSocket( Socket s );
	public void reportDeErro( IOException e );
}