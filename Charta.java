package ru.gnkoshelev.kontur.intern;

import javax.imageio.ImageIO;
import java.io.File;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Charta {
  public String path;
	public String id;
 	public int width;
	public int height;
  public BufferedImage img;
  public File output;
  public boolean isSaved = false;

  	public Charta(String id, int width, int height, String path) {
    	this.id = id;
    	this.width = width;
    	this.height = height;
      this.path = path;

      try {
      	img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
  	 	  //File output = new File(id + ".bmp");
        output = new File(path + id + ".bmp");
        ImageIO.write(img, "BMP", output);
        System.out.println("id = " + id + ", width: " + width + ", height: " + height);//delete
      } catch (Exception e) {
      	e.printStackTrace();
      }
  
      System.out.println("New object " + id + " created, width = " + width + ", height = " + height);
    }

  public int getImgWidth() {
    return width;
  }

  public int getImgHeight() {
    return height;
  }

  public boolean getSaved() {
    return isSaved;
  }

  public File getFile() {
    return output;
  }
}