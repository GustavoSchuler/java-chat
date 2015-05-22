package controller;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.swing.JOptionPane;

public class FileReceiver extends Thread {

	private Socket _socket;
	private int _fileSize;
	private String _filePathToSave;
	private IFileDownloadHandler _fileDonwloadHandler;
	
	public FileReceiver(Socket socket, int fileSize, String filePathToSave, IFileDownloadHandler fileDonwloadHandler) {
		_socket = socket;
		_fileSize = fileSize;
		_filePathToSave = filePathToSave;
		_fileDonwloadHandler = fileDonwloadHandler;
	}
	
	@Override
	public void run() {
		int bytesRead;
	    FileOutputStream fos = null;
	    BufferedOutputStream bos = null;
	    OutputStream output = null;
	    
	    try {
	      
	        // receive file
	    	InputStream in = _socket.getInputStream();
	         
	        // Writing the file to disk
	        // Instantiating a new output stream object
	        output = new FileOutputStream(_filePathToSave);
	           
	        byte[] buffer = new byte[1024];
	        while ((bytesRead = in.read(buffer)) != -1) {
	            output.write(buffer, 0, bytesRead);
	        }
	        
	      _fileDonwloadHandler.onFinishReceiveFile(_filePathToSave);
	    }
	    catch(Exception e) {
	    	e.printStackTrace();
	    	_fileDonwloadHandler.onErrorReceiveFile(e);
	    }
	    finally {
		    try {
		    	if (output != null) output.close();
		    	if (fos != null) fos.close();
			    if (bos != null) bos.close();
			    if (_socket != null) _socket.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		    
	    }
	}
}
