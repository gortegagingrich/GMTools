import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Gabriel on 2017/03/21.
 */
@SuppressWarnings("ALL")
public class Instance implements Comparable {
   public static HashMap<String, Object[]> images = null;
   private static int instanceNumber = (int)(System.currentTimeMillis() / 100000);

   private double x,y;
   private double xScale, yScale;
   private int    depth;
   private String objName;
   private String creationCode;

	/**
	 * Creates instance at given position
	 * Initializes depth to 0, xScale and yScale to 1, and creationCode to ""
	 *
	 * @param objName Instance type's name
	 * @param x X position of Instance in room.
	 * @param y Y position of instance in room.
	 */
   public Instance(String objName, int x, int y) {
      this.objName = objName;
      this.x = x;
      this.y = y;

      depth = 0;
      xScale = 1;
      yScale = 1;
      creationCode = "";
   }

	/**
	 * Creates instance at given position
	 * Initializes depth to 0, xScale and yScale to 1, and creationCode to ""
	 *
	 * @param objName Instance type's name
	 * @param x X position of Instance in room.
	 * @param y Y position of instance in room.
	 */
   Instance(String objName, String x, String y) {
      this(objName, Integer.parseInt(x), Integer.parseInt(y));
   }

	/**
	 * Creates "blank" Instance at (0,0)
	 */
	public Instance() {
      this("",0,0);
   }

	/**
	 * Draw's Instance type's corresponding sprites at Instance's position
	 *
	 * @param gc GraphicsContext of canvas Instance will be drawn to.
	 */
	public void draw(GraphicsContext gc) {

      if (images.containsKey(objName)) { // if there is an image defined for this kind of instance
         Object[] img = images.get(objName);
         gc.drawImage((Image)img[1], x + (Integer)img[2] * xScale, y + (Integer)img[3] * yScale, (Integer)img[4] * xScale, (Integer)img[5] * yScale);
      }
      // don't want to deal with polygons yet
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

   public void setObjName(String objName) {
      this.objName = objName;
   }

   public void setPosition(double x, double y) {
      this.x = x;
      this.y = y;
   }

	/**
	 * Casts given Object as Instance and compares based on depth.
	 *
	 * @param instance Instance to compare to
	 * @return Result of comparing depths as Integers
	 */
	@Override
   public int compareTo(Object instance) {
      return Integer.compare(this.depth, ((Instance)instance).depth);
   }

	/**
	 * Currently not implemented.
	 *
	 * @return Empty String
	 */
	@Override
   public String toString() {
      return "";
   }

	/**
	 * Moves Instance given amount.
	 * (x,y) -> (x + xOffset, y + yOffset)
	 *
	 * @param xOffset Horizontal offset
	 * @param yOffset Vertical offset
	 */
	public void move(double xOffset, double yOffset) {
      x += xOffset;
      y += yOffset;
   }

	/**
	 * Adds this Instance to given Element as a child element.
	 * Inserted as child of "instances" Element.
	 *
	 * @param room Room element this should be added to.
	 */
	public void addAsElement(Element room) {
      Element element = new Element("instance");

      element.setName("instance")
              .setAttribute("objName", objName)
              .setAttribute("x",String.format("%f",x))
              .setAttribute("y",String.format("%f",y))
              .setAttribute("name",String.format("inst_%d", instanceNumber++))
              .setAttribute("locked","0")
              .setAttribute("code",creationCode)
              .setAttribute("scaleX",String.format("%f", xScale))
              .setAttribute("scaleY",String.format("%f", yScale))
              .setAttribute("colour","4294967295")
              .setAttribute("rotation","0");

      room.addContent("\n        ");
      room.addContent(element);
   }

	/**
	 * Initializes Instance.images
	 * Parses SpriteInfo.xml to map specified Instance names to specified Images
	 * Only neesd to be called once.
	 */
	public static void initImages() {
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
      } catch (JDOMException | IOException e) {
         e.printStackTrace();
      }
   }

	/**
	 * Converts object ID read from JMAP file to an Instance name.
	 * Based on format of JTool 1.2.1.
	 * Does not support unnecessary types of objects
	 *
	 * @param num "[0-9]+"
	 * @return If num is valid, returns corresponding Instance name.
	 *         Otherwise, returns null.
	 */
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
