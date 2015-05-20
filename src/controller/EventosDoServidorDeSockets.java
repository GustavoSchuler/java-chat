package controller;

import java.io.IOException;
import java.net.Socket;

import org.json.JSONException;

public interface EventosDoServidorDeSockets {

	public void aoIniciarServidor() throws JSONException;
	public void aoFinalizarServidor() throws JSONException;
	public void aoReceberSocket( Socket s ) throws JSONException;
	public void reportDeErro( IOException e ) throws JSONException;
}