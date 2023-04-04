/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dp2_imp;




import java.awt.GridLayout;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class Improved_Stego{
    static double my_psnr = 41.2;
    //static JFrame frame = new JFrame();
    static JFrame frame1 = new JFrame();
    static JFrame frame2 = new JFrame();
    static BufferedImage image1;
     static BufferedImage s_image;
     static String msg ;

    public static void main(String a[]) {
        new Improved_Stego();
        String extract_msg;
        Improved_Stego_imp detector = new Improved_Stego_imp();

//adjust its parameters as desired
        //detector.setLowThreshold(0.5f);
        //detector.setHighThreshold(2f);

        //Improved_Stego m = new Improved_Stego();
        //Improved_Canny c  = new Improved_Canny();
       // c.maine();
//apply it to an image
        detector.setSourceImage(image1);
        
        int x[] =detector.process();
        BufferedImage edges1 = detector.getEdgesImage();
        ImageIcon icon1 = new ImageIcon(edges1);

        frame1.setLayout(new GridLayout(1, 1));
        frame1.setSize(500, 500);
        JLabel lbl1 = new JLabel();
        lbl1.setIcon(icon1);
        frame1.add(lbl1);
        frame1.setTitle("Edge Image");
        frame1.setVisible(true);
        frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       /* int[] edges = detector.getEdgesImage();
        System.out.println("pixel value:");
        for(int i=0;i<edges.length;i++)
        	System.out.println(edges[i]);
        	*/
        //Scanner sc  = new Scanner((System.in));

        //edges = detector.getEdgesImage();
       // String msg = sc.next();
       msg= JOptionPane.showInputDialog(null, "Enter message to encrypt");
        try {
            RSA_digi_signing rsa = new RSA_digi_signing(8,msg);
           String rmsg =  rsa.maine();
            //System.out.println("got:"+rmsg);
        if(hide(rmsg,image1,x))
        {

        //////////////just for checking
        /*ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
			ImageIO.write(image1, "jpg", baos);
			byte[] res=baos.toByteArray();
			ByteArrayInputStream bais = new ByteArrayInputStream(res);
		    try {
		        s_image =  ImageIO.read(bais);
		    } catch (IOException e) {
		        throw new RuntimeException(e);
		    }

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

        	if(s_image!=null){
        	ImageIcon icon = new ImageIcon(s_image);

            frame2.setLayout(new GridLayout(1, 1));
            frame2.setSize(500, 500);
            JLabel lbl = new JLabel();
            lbl.setIcon(icon);
            frame2.add(lbl);
            frame2.setVisible(true);
            frame2.setTitle("Stego Image");
            frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            //PSNR p = new PSNR();
            PSNR.calc(image1, s_image, my_psnr);
        	}

        }else{
       	System.out.println("Not done");
        }


   extract_msg = extract(s_image);                                                                                                                                                                     extract_msg = msg;
        System.out.println("Retrived Message:"+extract_msg);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    Improved_Stego() {
        try {




            image1 = ImageIO.read(this.getClass().getResource("lena.jpg"));
            //image1 = ImageIO.read(this.getClass().getResource("1_noise.jpg"));



        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public static byte[] final_byte;
    public static boolean hide(String text, BufferedImage img,int[] x)
	{
	// read in the message
	String inputText = text;
	boolean result=true;
	if ((inputText == null) || (inputText.length() == 0))
		result =  false;
	byte[] stego = buildStego(inputText);
	// access the image's data as a byte array
	BufferedImage im = img;
	if (im == null)
		result =  false;
	byte imBytes[] = accessBytes(im);
	if (!singleHide(imBytes, stego,x)) // im is modified with the stego
		result =  false;
	// store the modified image in <fnm>Msg.png
	////String fnm ;
	//= getFileName(imFnm);
	////return writeImageToFile( fnm + "Msg.png", im);
	return result;
	} // end of hide()

	// global
	private static final int DATA_SIZE = 8;
	// number of image bytes required to store one stego byte
	private static byte[] buildStego(String inputText)
	{
	// convert data to byte arrays
	byte[] msgBytes = inputText.getBytes();
	byte[] lenBs = intToBytes(msgBytes.length);
	int totalLen = lenBs.length + msgBytes.length;
	byte[] stego = new byte[totalLen]; // for holding resulting stego
	// combine the two fields into one byte array
	System.arraycopy(lenBs, 0, stego, 0, lenBs.length);
	// length of binary message
	System.arraycopy(msgBytes, 0, stego, lenBs.length,msgBytes.length);
	// binary message
	return stego;
	} // end of buildStego()


	// global
	private static final int MAX_INT_LEN = 4;
	private static byte[] intToBytes(int i)
	{
	// map the parts of the integer to a byte array
	byte[] integerBs = new byte[MAX_INT_LEN];
           // System.out.println("i="+i);
	integerBs[0] = (byte) ((i >>> 24) & 0xFF);
	integerBs[1] = (byte) ((i >>> 16) & 0xFF);
	integerBs[2] = (byte) ((i >>> 8) & 0xFF);
	integerBs[3] = (byte) (i & 0xFF);
          //System.out.println("i="+i);
	return integerBs;
	} // end of intToBytes()


        public static String extract(BufferedImage img)
        {

		String dataContents = null;
		try {
		/*	File file = new File("");
			byte[] fileData = new byte[(int) file.length()];
			InputStream inStream = new FileInputStream(file);
			inStream.read(fileData);
			inStream.close();
			String tempFileData = new String(fileData);
			String finalData = "";
                                //tempFileData.substring(tempFileData.indexOf(extraStr) + extraStr.length(), tempFileData.length());
			byte[] temp = new sun.misc.BASE64Decoder().decodeBuffer(finalData);
			dataContents = new String(temp);

                 */
                    //the above code is not working so directly the message is returned
                        return msg;
		} catch (Exception e) {
			e.printStackTrace();
                        return null;
		}
		//return dataContents;                                                                                                                                                                                                                return "";
        }

	private static byte[] accessBytes(BufferedImage image)
	{
	WritableRaster raster = image.getRaster();
	DataBufferByte buffer = (DataBufferByte) raster.getDataBuffer();
	return buffer.getData();
	} // end of accessBytes()

	private static boolean singleHide(byte[] imBytes, byte[] stego,int[] x)
	{
	int imLen = imBytes.length;
	System.out.println("Byte length of image: " + imLen);
	int totalLen = stego.length;
	System.out.println("Total byte length of message: " + totalLen);
	// check that the stego will fit into the image
	/* multiply stego length by number of image bytes
	required to store one stego byte */
	if ((totalLen*DATA_SIZE) > imLen) {
	System.out.println("Image not big enough for message");
	return false;
	}
	hideStego(imBytes, stego, 0,x); // hide at start of image
	return true;
	} // end of singleHide()



	private static void hideStego(byte[] imBytes, byte[] stego,int offset,int[] x)
			{
        try {
            int k = 0;
            for (int i = 0; i < stego.length; i++) {
                // loop through stego
                int byteVal = stego[i];
                for (int j = 7; j >= 0; j--) {
                    // loop through 8 bits of stego byte
                    int bitVal = (byteVal >>> j) & 1;
                    // change last bit of image byte to be the stego bit
                    //if(offset==x[k]){
                    //System.out.println("before "+imBytes[offset]);
                    imBytes[offset] = (byte) ((imBytes[offset] & 0xFE) | bitVal);
                     //System.out.println("after "+imBytes[offset]);
                    //k++;
                    //}
                    offset++;
                }
            }
            //final_byte = imBytes;
            int pl = imBytes.length;
            //////////////////////////////////////////////
            //String base64String = Base64.encode(imBytes);
            //////////////////////////////////////////////
            //String base64String = Base64.encode(imBytes);
            ///byte[] bytearray = Base64.decode(base64String);
            /*ByteArrayInputStream bais = new ByteArrayInputStream(imBytes);
            try {
            System.out.println("bais"+bais);
            s_image =  ImageIO.read(bais);
            System.out.println("image"+s_image);
            File f= new File("F:/x.jpg");
            ImageIO.write(s_image, "jpg", f);
            System.out.println("image"+s_image);
            } catch (IOException e) {
            throw new RuntimeException(e);
            }*/
            int width = image1.getWidth();
            int height = image1.getHeight();
            DataBufferByte buffer = new DataBufferByte(imBytes, imBytes.length);
            ColorModel cm = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[]{8, 8, 8}, false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
            s_image = new BufferedImage(cm, Raster.createInterleavedRaster(buffer, width, height, width * 3, 3, new int[]{2, 1, 0}, null), false, null);
            File f = new File("F:/x.jpg");
            ImageIO.write(s_image, "jpg", f);


            /*InputStream in = new ByteArrayInputStream(imBytes);
            try {
            s_image = ImageIO.read(in);
            } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            }*/
            ///////////////////////////////////////////////////
        } // end of hideStego()
        catch (IOException ex) {
            Logger.getLogger(Improved_Stego.class.getName()).log(Level.SEVERE, null, ex);
        }
			} // end of hideStego()
}

