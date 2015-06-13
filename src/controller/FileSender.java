package controller;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.Socket;


public class FileSender extends Thread {

	private String _hostAddress;
	private int port;
	private String path;
	private IFileDownloadHandler fileTransferHandler;
	
	/**
	 * Construtor da classe
	 * 
	 * @param hostAddress
	 * @param newPort
	 * @param pathToSend
	 * @param handler
	 */
	public FileSender(String hostAddress, int newPort, String pathToSend, IFileDownloadHandler handler) {
		_hostAddress = hostAddress;
		port = newPort;
		path = pathToSend;
		fileTransferHandler = handler;
	}
	
	
	@Override
	public void run() {

	    BufferedInputStream bis = null;
	    OutputStream os = null;
	    Socket socket = null;
	    
		try {
			
		    socket = new Socket(_hostAddress, port);
			
		    File myFile = new File(path);
		    byte[] mybytearray = new byte[(int) myFile.length()];
	         
	        FileInputStream fis = new FileInputStream(myFile);
	        bis = new BufferedInputStream(fis);
	        bis.read(mybytearray, 0, mybytearray.length);
	         
	        os = socket.getOutputStream();
	         
	        os.write(mybytearray, 0, mybytearray.length);
	         
	        os.flush();
	         
	        socket.close();

	        fileTransferHandler.onFinishSendFile(path);
		} catch (Exception e) {
			e.printStackTrace();
			fileTransferHandler.onErrorSendFile(e);
		}
		
		finally {
			try {
				if (bis != null) bis.close();
		        if (os != null) os.close();
		        if (socket!=null) socket.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
}
