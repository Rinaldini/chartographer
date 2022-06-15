package ru.gnkoshelev.kontur.intern;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import javax.imageio.ImageIO;
import java.io.File;
import java.awt.Color;
import java.awt.image.BufferedImage;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.TreeSet;

public class Chartographer {

	public static String path = "c:\\Mydocs\\Java\\temp\\";
	public byte [] response;
	public String query, uri, method;
	public int codeResponse, getImgCounter = 0;
	public FileReader reader;
	public ArrayList<Charta> listOfChartas = new ArrayList<>();
	public ByteArrayOutputStream result = new ByteArrayOutputStream();
	public static String id;
	public int width, height, x, y;
  
  	public static void main(String[] args) throws Exception {
  		Chartographer chartographer = new Chartographer();
    	chartographer.doChartographer();
    	//path = args[0];
  	}

	public void doChartographer() throws Exception {
  		HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
  		server.createContext("/chartas", new ChartasHandler());
  		server.setExecutor(null);
  		server.start();
  		System.out.println("Server started");
	}

  	/**
  	 * Возвращает координату X верхнего левого угла фрагмента
  	 * @param s String query (запрос - всё что после знака ?)
  	 * @return x integer (численное значение координаты X)
  	 */
  	public int getX(String s) {
  		Integer _x = 0;

    	for (String item : s.split("&")) {
    		if (item.split("=")[0].equals("x")) {
    			_x = Integer.parseInt(item.split("=")[1]);
			}
		}
    	return _x;
  	}

  	/**
  	 * Возвращает координату Y верхнего левого угла фрагмента
  	 * @param s String query (запрос - всё что после знака ?)
  	 * @return y integer (численное значение координаты Y)
  	 */
	public int getY(String s) {
    	Integer _y = 0;

    	for (String item : s.split("&")) {
			if (item.split("=")[0].equals("y")) {
				_y = Integer.parseInt(item.split("=")[1]);
			}
		}
    	return _y;
	}

	/**
  	 * Возвращает ширину фрагмента или холста
  	 * @param s String query (запрос - всё что после знака ?)
  	 * @return _width integer (численное значение ширины)
  	 */
  	public int getWidth(String s) {
  		Integer _width = 0;

    	for (String item : s.split("&")) {
    		if (item.split("=")[0].equals("width")) {
    			_width = Integer.parseInt(item.split("=")[1]);
			}
		}
    	return _width;
	}

	/**
  	 * Возвращает высоту фрагмента или холста
  	 * @param s String query (запрос - всё что после знака ?)
  	 * @return _height integer (численное значение высоты)
  	 */
	public int getHeight(String s) {
		Integer _height = 0;

    	for (String item : s.split("&")) {
    		if (item.split("=")[0].equals("height")) {
    			_height = Integer.parseInt(item.split("=")[1]);
    	  	}
    	}
    	return _height;
  	}

  	/**
  	 * Возвращает ID фрагмента или холста
  	 * @param s String URI (полный путь: адрес с запросом)
  	 * @return _id String (строковое значение ID)
  	 */
  	public String getId(String s) {
  		String _id;
  	  	_id = s.split("/")[2];
  	  	return _id;
  	}

  	public class ChartasHandler implements HttpHandler {

    	@Override
    	public void handle(HttpExchange exchange) throws IOException {

			width = 0;
			height = 0;
			x = 0;
			y = 0;

			method = exchange.getRequestMethod();
			uri = exchange.getRequestURI().toString();
			query = exchange.getRequestURI().getQuery();

    		if (method.equals("DELETE")) {

    			id = getId(uri);
				System.out.println(method + ": " + id); // delete
				codeResponse = 404;
				for (Charta item : listOfChartas) {
					if (id.equals(item.id)) {
						item.getFile().delete();
						item = null;
						listOfChartas.remove(item);
						codeResponse = 200;
						break;
					}
				}
			}
      		else if (method.equals("GET")) {

      			id = getId(uri);
        		x = getX(query);
        		y = getY(query);
        		width = getWidth(query);
        		height = getHeight(query);
        		codeResponse = 404;

        		if (width < 5000 && height < 5000) {
        			int boundX = x + width;
          			int boundY = y + height;

          			try {
          				BufferedImage resultImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            			// Находим объект с запрошенным id
            			BufferedImage sourceImg = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
            			for (Charta item : listOfChartas) {
            			  	if (id.equals(item.id)) {
            			  		sourceImg = ImageIO.read(new File(path + id + ".bmp"));
            			  		codeResponse = 200;
            			  		break;
            			  	}
            			}

            			// Переносим переданный фрагмент на "холст"
            			for (int xPixel = x; xPixel < (boundX < sourceImg.getWidth() ? boundX : sourceImg.getWidth()); xPixel++) {
            				for (int yPixel = x; yPixel < (boundY < sourceImg.getHeight() ? boundY : sourceImg.getHeight()); yPixel++) {
            					Color color = new Color(sourceImg.getRGB(xPixel, yPixel));
              					resultImg.setRGB(xPixel - x, yPixel - y, color.getRGB());
              				}
            			}

            			ImageIO.write(resultImg, "BMP", new File(path + id + "get" + getImgCounter + ".bmp"));
            			getImgCounter++;
            			ByteArrayOutputStream bos = new ByteArrayOutputStream();
            			ImageIO.write(resultImg, "BMP", bos);
            			response = bos.toByteArray();
          
          			} catch (Exception e) {
          				e.printStackTrace();
          			}
        		} else {
        		codeResponse = 404;
        		}
			}
			else if (method.equals("POST")) {

				width = getWidth(query);
        		height = getHeight(query);

        		if (uri.split("/")[2].startsWith("?")) {
        			if (width > 0 && width <= 20000 && height > 0 && height <= 50000) {


        				// запрос без id
            			id = Integer.toString(listOfChartas.size());

            			// проверка совпадения id (переделать)
            			if (listOfChartas.size() > 0) {
            				for (Charta item : listOfChartas) {
								if (id.equals(item.id)) {
									id = Integer.toString(listOfChartas.size() + 10);
            			    		break;
            			  		}
            				}
            			}
            		

            			Charta charta = new Charta(id, width, height, path);
            			listOfChartas.add(charta);

            			codeResponse = 201;
            			response = id.getBytes();
          			} else {
            			codeResponse = 400;
          			}
        		
        		} else {

        			// запрос с id
          			codeResponse = 404;
          			id = getId(uri);
          			x = getX(query);
          			y = getY(query);
          
          			BufferedImage resultImg = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
          			// Находим объект с запрошенным id
          			for (Charta item : listOfChartas) {
          				if (id.equals(item.id)) {
          					resultImg = ImageIO.read(new File(path + id + ".bmp"));
          			    	codeResponse = 200;
          			    	break;
          			  	}
          			}
          
        			if (codeResponse == 200) {
        				if (x > 0 && x <= resultImg.getWidth() && y > 0 && y <= resultImg.getHeight()) {
        					try {
        						byte[] buffer = new byte[1024];
        			      		int length;
        			      		while ((length = exchange.getRequestBody().read(buffer)) != -1) {
        			      			result.write(buffer, 0, length);
        			      		}
        			    	} catch (Exception e) {
        			    		e.printStackTrace();
        			    	}

              				ByteArrayInputStream bis = new ByteArrayInputStream(result.toByteArray());
              				BufferedImage sourceImg = ImageIO.read(bis);
          
              				// Переносим переданный фрагмент на "холст"
              				for (int xPixel = 0; xPixel < sourceImg.getWidth(); xPixel++) {
              					for (int yPixel = 0; yPixel < sourceImg.getHeight(); yPixel++) {

              						Color color = new Color(sourceImg.getRGB(xPixel, yPixel));

                  					if ((xPixel + x) < resultImg.getWidth() && (yPixel + y) < resultImg.getHeight()) {
                  						resultImg.setRGB(x + xPixel, y + yPixel, color.getRGB());
                  					}
                  				}
							}
          
              				ImageIO.write(resultImg, "BMP", new File(path + id + ".bmp"));
    
              				result.reset();
              				result.close();
            			}

            		} else {
            			codeResponse = 400;
          			}
        		}
      		}

			exchange.sendResponseHeaders(codeResponse, 0);
			OutputStream os = exchange.getResponseBody();
			os.write(response);
			os.flush();
			os.close();
			exchange.close();
			reader.close();
			response = "".getBytes();
    	}
	}
}