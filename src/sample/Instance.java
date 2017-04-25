package sample;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Paint;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Gabriel on 2017/03/21.
 */
public class Instance implements Comparable {
   public static HashMap<String, Image> images;

   protected double x,y;
   protected double[] xArr, yArr;
   protected double xScale, yScale;
   protected Paint color1, color2;
   protected int depth;
   protected String itemName;
   protected String creationCode;

   public Instance() {

   }

   /**
    *
    * @param str {objectID, X, Y}
    */
   public Instance(String[] str) {

   }

   public void draw(Canvas c) {
      GraphicsContext gc = c.getGraphicsContext2D();

      // keep track of previous stroke and fill values
      Paint temp1 = gc.getFill();
      Paint temp2 = gc.getStroke();

      // draw fill
      gc.setFill(color2);
      gc.fillPolygon(xArr, yArr, yArr.length);

      // draw outline
      gc.setStroke(color1);
      gc.strokePolygon(xArr, yArr, yArr.length);

      // reset fill and stroke
      gc.setFill(temp1);
      gc.setStroke(temp2);
   }

   private void setColors(Paint border, Paint fill) {
      color1 = border;
      color2 = fill;
   }

   public void setXScale(double xScale) {
      this.xScale = xScale;
   }

   public void setYScale(double yScale) {
      this.yScale = yScale;
   }

   public void setCreationCode(String code) {
      this.creationCode = code;
   }

   public void setItemName(String itemName) {
      this.itemName = itemName;
   }

   public void setPosition(double x, double y) {
      this.x = x;
      this.y = y;
   }

   @Override
   public int compareTo(Object i) {
      return Integer.compare(this.depth, ((Instance)i).depth);
   }

   @Override
   public String toString() {
      String out = "";

      return out;
   }

   public void move(int xOffset, int yOffset) {
      x += xOffset;
      y += yOffset;
   }

   public static void init() {
      Element imageInfo;
      SAXBuilder builder = new SAXBuilder();

      images = new HashMap<>();

      try {
         imageInfo = builder.build(new File("SpriteInfo.xml")).getRootElement();

         imageInfo.getChildren().forEach(spr -> {
            Image img;
            String str;

            str = spr.getAttributeValue("id");
            img = new Image((new File(spr.getAttributeValue("img"))).toURI().toString());
            images.put(str,img);
         });
      } catch (JDOMException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
}
