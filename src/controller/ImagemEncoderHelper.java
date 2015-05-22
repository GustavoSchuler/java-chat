package feevale.chat;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.Icon;
import javax.swing.JLabel;

import org.apache.commons.codec.binary.Base64;


public class ImagemEncoderHelper {

	/**
     * Converte um array de bytes de uma imagem em uma string.
     *
     * @param imageByteArray - byte array da imagem
     * @return o base64 da imagem.
     */
    public static String encodeImage(byte[] imageByteArray) {
        return Base64.encodeBase64URLSafeString(imageByteArray);
    }
    
    /**
     * Converte um array de bytes de uma imagem em uma string.
     *
     * @param label - label onde está a imagem
     * @return o base64 da imagem.
     */
    public static String encodeImage(JLabel label) throws IOException {
    	
    	Icon icon = label.getIcon();
    	BufferedImage img = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
    	Graphics2D g2d = img.createGraphics();
    	icon.paintIcon(null, g2d, 0, 0);
    	g2d.dispose();

    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	
	    ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
	    try {
	        ImageIO.write(img, "jpeg", ios);
	        // Set a flag to indicate that the write was successful
	    } finally {
	        ios.close();
	    }
	    
	    byte[] bytes = baos.toByteArray();
	    
	    return encodeImage(bytes);
    }
 
    /**
     * Converte o base64 de uma imagem em bytes.
     *
     * @param imageDataString - o base64 da imagem.
     * @return os bytes da imagem.
     */
    public static byte[] decodeImage(String imageDataString) {
        return Base64.decodeBase64(imageDataString);
    }
    
    
}
