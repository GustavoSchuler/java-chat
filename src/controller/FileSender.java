package controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.swing.JOptionPane;

public class FileSender extends Thread {

	private String _hostAddress;
	private int _port;
	private String _filePathToSend;
	private IFileDownloadHandler _fileDownloadHandler;
	
	public FileSender(String hostAddress, int port, String filePathToSend, IFileDownloadHandler fileDownloadHandler) {
		_hostAddress = hostAddress;
		_port = port;
		_filePathToSend = filePathToSend;
		_fileDownloadHandler = fileDownloadHandler;
	}
	
	
	@Override
	public void run() {

	    BufferedInputStream bis = null;
	    OutputStream os = null;
	    Socket socket = null;
	    
		try {
			
		    socket = new Socket(_hostAddress, _port);
			
		    File myFile = new File(_filePathToSend);
		    byte[] mybytearray = new byte[(int) myFile.length()];
	         
	        FileInputStream fis = new FileInputStream(myFile);
	        bis = new BufferedInputStream(fis);
	        bis.read(mybytearray, 0, mybytearray.length);
	         
	        os = socket.getOutputStream();
	         
	        os.write(mybytearray, 0, mybytearray.length);
	         
	        os.flush();
	         
	        socket.close();

	        _fileDownloadHandler.onFinishSendFile(_filePathToSend);
		} catch (Exception e) {
			e.printStackTrace();
			_fileDownloadHandler.onErrorSendFile(e);
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
