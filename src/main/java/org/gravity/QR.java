package org.gravity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class QR {

	private String message = null;
	private String path = null;
	private String filename = null;
	
	public QR(){
		
	}
	
	/**
	 * Constructor with 3 simple parameter to produce a QR
	 * 
	 * @param message the message to encode into QR code
	 * @param path the path where the QR code image to be placed
	 * @param filename the name of the QR code file to be generated
	 * <p>
	 * Example:
	 * <p>
	 * QR qr = new QR("foo bar","/path/to/image","foobar");
	 */
	public QR(String message, String path, String filename){
		this.message = message;
		this.path = path;
		this.filename = filename;
	}

	/**
	 * pojo function to get message
	 *
	 * @return	 	message behind QRcode
	 */
	public String getMessage(){
		return message;
	}

	/**
	 * pojo function to set message
	 *
	 * @param message String to set as message 
	 */
	public void setMessage(String message){
		this.message = message;
	}

	/**
	 * pojo function to get path in String
	 *
	 * @return		path of QR file
	 */
	public String getPath(){
		return path;
	}

	/**
	 * pojo function to set path
	 *
	 * @param path String to set as path
	 */
	public void setPath(String path){
		this.path = path;
	}

	/**
	 * pojo function to get filename in String
	 *
	 * @return		filename of QR
	 */
	public String getFilename(){
		return filename;
	}

	/**
	 * pojo function to set filename
	 *
	 * @param filename String to set as filename
	 */
	public void setFilename(String filename){
		this.filename = filename;
	}

	/**
	 * Encodes message in this.message to qr code bufferedImage
	 *
	 * @return		bufferedImage of QR, which is meant to be written to a file of to be manipulated into other image usages
	 */
	public BufferedImage encodeQR()
	{
		QRCodeWriter qrWriter = new QRCodeWriter();
		BitMatrix bitmatrix = new BitMatrix(128, 128);
		BufferedImage bufferedImage = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);
		
		try{
			bitmatrix = qrWriter.encode(message, BarcodeFormat.QR_CODE, 128, 128);
			bufferedImage.createGraphics();
			
			Graphics2D graphics2d = (Graphics2D) bufferedImage.getGraphics();
			graphics2d.setColor(Color.WHITE);
			graphics2d.fillRect(0, 0, 128, 128);
			graphics2d.setColor(Color.BLACK);
			for (int i = 0; i < 128; i++) {
	            for (int j = 0; j < 128; j++) {
	                if (bitmatrix.get(i, j)) {
	                    graphics2d.fillRect(i, j, 1, 1);
	                }
	            }
	        }
			
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		return bufferedImage;

	}
	
	public BufferedImage encodeRGBQR(){
		QRCodeWriter qrWriter = new QRCodeWriter();
		BitMatrix bitMatrixR = new BitMatrix(128,128);
		BitMatrix bitMatrixG = new BitMatrix(128,128);
		BitMatrix bitMatrixB = new BitMatrix(128,128);
		BufferedImage bufferedImage = new BufferedImage(128,128,BufferedImage.TYPE_INT_RGB);
		
		String[] messages = breakMessagesIntoThree(message);
		
		try{
			bitMatrixR = qrWriter.encode(messages[0], BarcodeFormat.QR_CODE, 128, 128);
			bitMatrixG = qrWriter.encode(messages[1], BarcodeFormat.QR_CODE, 128, 128);
			bitMatrixB = qrWriter.encode(messages[2], BarcodeFormat.QR_CODE, 128, 128);
			bufferedImage.createGraphics();
			
			Graphics2D graphics2d = (Graphics2D) bufferedImage.getGraphics();
			graphics2d.setColor(Color.WHITE);
			graphics2d.fillRect(0, 0, 128, 128);
			for(int i=0;i<128;i++){
				for(int j=0;j<128;j++){
					if(bitMatrixR.get(i,j)){
						if(bitMatrixG.get(i,j)){
							if(bitMatrixB.get(i, j)){
								graphics2d.setColor(Color.BLACK);
							}
							else{
								graphics2d.setColor(Color.YELLOW);
							}
						}
						else if(bitMatrixB.get(i, j)){
							graphics2d.setColor(Color.MAGENTA);
						}
						else{
							graphics2d.setColor(Color.RED);
						}
					}
					else if(bitMatrixG.get(i, j)){
						if(bitMatrixB.get(i, j)){
							graphics2d.setColor(Color.CYAN);
						}
						else{
							graphics2d.setColor(Color.GREEN);
						}
					}
					else if(bitMatrixB.get(i, j)){
						graphics2d.setColor(Color.BLUE);
					}
					else{
						graphics2d.setColor(Color.WHITE);
					}
					graphics2d.fillRect(i, j, 1, 1);
				}
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		
		return bufferedImage;
		
	}
	
	private String[] breakMessagesIntoThree(String message){
		String[] messages = {null,null,null};
		
		int length = (message.length())/3;
		
		messages[0] = message.substring(0,length);
		messages[1] = message.substring(length+1, length+length);
		messages[2] = message.substring(length+length+1);
		
		return messages;
	}

	/**
	 * creates this.filename as png image in the path given in this.path
	 *
	 * @param bufferedImage is the image passed down from encodeQR function
	 */
	public void generateQR(BufferedImage bufferedImage)
	{
		
		try{
			
			String filePath = path+filename+".png";
			System.out.println("output path: "+new File(filePath).getAbsolutePath());
			ImageIO.write(bufferedImage, "png", new File(filePath));
			
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public byte[] generateQR(BufferedImage bufferedImage,ImageOutputStream ioStream, File file)
	{

		ByteArrayOutputStream bos = new ByteArrayOutputStream(255);
		byte[] byteArray = new byte[255];
		try{
			ioStream = ImageIO.createImageOutputStream(bos);
			ImageIO.write(bufferedImage, "png", ioStream);
			ImageIO.write(bufferedImage, "png", file);
			ioStream.write(byteArray);
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		return byteArray;
	}
	
	public String readQR(File file){
		String qrDecoded = null;

		try{
			BinaryBitmap binaryBitmap = new BinaryBitmap(
				new HybridBinarizer(
			        new BufferedImageLuminanceSource(
			            ImageIO.read(file)
			            )
			        ));
			Result qrCodeResult = new MultiFormatReader().decode(binaryBitmap);
			qrDecoded = qrCodeResult.getText();
		}catch(NotFoundException nfEx){
			nfEx.printStackTrace();
		}catch(IOException ioEx){
			ioEx.printStackTrace();
		}
		return qrDecoded;
	}
	
//	public String readRGBQR(File file){
//		String rgbQrDecoded = null;
//		try{
////			BufferedImageLuminanceSource bils = new BufferedImageLuminanceSource(ImageIO.read(file));
////			
////			HybridBinarizer hybridBin = new HybridBinarizer(bils);
////			BinaryBitmap binaryBitmap = new BinaryBitmap(hybridBin);
////			Result qrCodeResult = new MultiFormatReader().decode(binaryBitmap);
//			
//			System.out.println(file.getAbsolutePath());
//			
//			File bitmapFile = new File("bitmap.bmp");
//			ImageIO.write(ImageIO.read(file), "bmp", bitmapFile);
//			
//			Bitmap bm = ImageIO.
//			
//			for(int i=0;i<bm.getWidth();i++){
//				for(int j=0;j<bm.getHeight();j++){
//					System.out.println(bm.getPixel(i, j));
//				}
//				System.out.println("\n");
//			}
//			
//			BitMatrix bmR = new BitMatrix(128, 128);
//			BitMatrix bmG = new BitMatrix(128, 128);
//			BitMatrix bmB = new BitMatrix(128, 128);
//			
//		}
//		catch(Exception exception){
//			exception.printStackTrace();
//		}
//		
//		return rgbQrDecoded;
//	}

	public static void main(String[] args){
		QR qrCode = new QR(args[0],args[1],args[2]);	
		qrCode.generateQR(qrCode.encodeQR());
	}


	
}
