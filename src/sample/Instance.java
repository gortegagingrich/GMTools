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
   public static HashMap<String, Object[]> images = null;

   private double x,y;
   private double[] xArr, yArr;
   private double xScale, yScale;
   private Paint color1, color2;
   private int    depth;
   private String itemName;
   private String creationCode;

   public Instance(String itemName, int x, int y) {
      this.itemName = itemName;
      this.x = x;
      this.y = y;

      depth = 0;
      xArr = null;
      yArr = null;
      xScale = 1;
      yScale = 1;
      creationCode = "";
   }

   /**
    *
    * @param str {objectID, X, Y}
    */
   public Instance(String[] str) {
      this(str[0], Integer.parseInt(str[1]), Integer.parseInt(str[2]));
   }

   public Instance() {
      this("",0,0);
   }

   public void draw(GraphicsContext gc) {

      if (images.containsKey(itemName)) { // if there is an image defined for this kind of instance
         Object[] img = images.get(itemName);
         gc.drawImage((Image)img[1], x + (Integer)img[2] * xScale, y + (Integer)img[3] * yScale, (Integer)img[4] * xScale, (Integer)img[5] * yScale);
      } else { // if there is no image defined for this kind of instance
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

      if (images != null) {
         images.clear();
      }

      images = new HashMap<>();

      try {
         imageInfo = builder.build(new File("SpriteInfo.xml")).getRootElement();

         imageInfo.getChildren().forEach(spr -> {
            Image img = null;
            String str;
            String path;
            int xOffset, yOffset, width, height;

            // set id
            str = spr.getAttributeValue("id");

            // set the image
            path = spr.getAttributeValue("img");

            // determine whether or not another instance is mapped to the same image file
            for (Object[] i: images.values()) {

               // if it finds a match, set img to reference to preexisting image
               if (i[0].equals(path)) {
                  img = (Image) i[1];
                  break;
               }
            }

            // if img has not been initialized by now, it's safe to create a new Image
            if (img == null) {
               img = new Image((new File(spr.getAttributeValue("img")))
                                       .toURI().toString());
            }

            // set offsets
            xOffset = Integer.parseInt(spr.getAttributeValue("xOffset"));
            yOffset = Integer.parseInt(spr.getAttributeValue("yOffset"));

            width = Integer.parseInt(spr.getAttributeValue("width"));
            height = Integer.parseInt(spr.getAttributeValue("height"));

            images.put(str,new Object[] {path, img, xOffset, yOffset, width, height});
         });
      } catch (JDOMException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
}
