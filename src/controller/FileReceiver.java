package controller;


import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


public class FileReceiver extends Thread {

	private Socket socket;
	private int fileSize;
	private String path;
	private IFileDownloadHandler fileTransferHandler;
	
	public FileReceiver(Socket so, int size, String pathToSave, IFileDownloadHandler handler) {
		socket = so;
		fileSize = size;
		path = pathToSave;
		fileTransferHandler = handler;
	}
	
	@Override
	public void run() {
		int bytesRead;
	    FileOutputStream fos = null;
	    BufferedOutputStream bos = null;
	    OutputStream output = null;
	    
	    try {
	    	InputStream in = socket.getInputStream();
	        output = new FileOutputStream(path);
	           
	        byte[] buffer = new byte[1024];
	        while ((bytesRead = in.read(buffer)) != -1) {
	            output.write(buffer, 0, bytesRead);
	        }
	        
	        fileTransferHandler.onFinishReceiveFile(path);
	    }
	    catch(Exception e) {
	    	e.printStackTrace();
	    	fileTransferHandler.onErrorReceiveFile(e);
	    }
	    finally {
		    try {
		    	if (output != null) output.close();
		    	if (fos != null) fos.close();
			    if (bos != null) bos.close();
			    if (socket != null) socket.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		    
	    }
	}
}
