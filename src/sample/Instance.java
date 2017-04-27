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

   Instance(String itemName, String x, String y) {
      this(itemName, Integer.parseInt(x), Integer.parseInt(y));
   }

   public Instance() {
      this("",0,0);
   }

   public void draw(GraphicsContext gc) {

      if (images.containsKey(itemName)) { // if there is an image defined for this kind of instance
         Object[] img = images.get(itemName);
         gc.drawImage((Image)img[1], x + (Integer)img[2] * xScale, y + (Integer)img[3] * yScale, (Integer)img[4] * xScale, (Integer)img[5] * yScale);
      }
      // don't want to deal with polygons yet
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

   public static String num2Name(String num) {
      String out = null;

      // assumes values have not changed since version 1.2.1
      // I might just have these values mapped in an external file
      switch (num) {
         case "1":
            out = "objBlock";
            break;
         case "2":
            out = "objMiniBlock";
            break;
         case "3":
            out = "objSpikeUp";
            break;
         case "4":
            out = "objSpikeRight";
            break;
         case "5":
            out = "objSpikeLeft";
            break;
         case "6":
            out = "objSpikeDown";
            break;
         case "7":
            out = "objMiniUp";
            break;
         case "8":
            out = "objMiniRight";
            break;
         case "9":
            out = "objMiniLeft";
            break;
         case "10":
            out = "objMiniDown";
            break;
         case "11":
            out = "objCherry";
            break;
         case "12":
            out = "objSave";
            break;
         case "13":
            out = "objMovingPlatform";
            break;
         case "14":
            out = "objWater";
            break;
         case "15":
            out = "objWater2";
            break;
         case "16":
            out = "objWalljumpL";
            break;
         case "17":
            out = "objWalljumpR";
            break;
         case "18":
            out = "objKillerBlock";
            break;
         case "19":
            out = "objBulletBlocker";
            break;
         case "20":
            out = "objPlayerStart";
            break;
         case "21":
            out = "objWarp";
            break;

         default:
            System.err.printf("Object with id %s is not supported\n", num);
      }

      return out;
   }
}
