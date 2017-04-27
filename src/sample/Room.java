package sample;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Gabriel on 2017/03/21.
 */
public class Room {
   private String creationCode;
   private int width, height;
   private ArrayList<Instance> instances;

   String toXML() {
      return "";
   }

   public Room() {
      creationCode = "";
      width = 800;
      height = 608;
      instances = new ArrayList<>();
   }

   public boolean addInstance(Instance obj) {
      return instances.add(obj);
   }

   public boolean removeInstance(Instance obj) {
      return instances.remove(obj);
   }

   public boolean addTile() {
      System.err.println("This is not supported");
      return false;
   }

   public boolean removeTile() {
      System.err.println("This is not supported");
      return false;
   }

   public void setCreationCode(String str) {
      creationCode = str;
   }

   public void setSize(int width, int height) {
      this.width = width;
      this.height = height;
   }

   public void merge(Room room, int xOffset, int yOffset) {
      for (Instance instance : room.instances) {
         instance.move(xOffset, yOffset);
         addInstance(instance);

      }

   }

   public void fromGMX(String fName) {
      Instance toAdd;
      Element room;

      SAXBuilder builder = new SAXBuilder();

      try {
         room = builder.build(new File(fName)).getRootElement();

         // add instances from GMX
         for (Element child : room.getChild("instances").getChildren()) {
            toAdd = new Instance();
            toAdd.setCreationCode(child.getAttributeValue("code"));
            toAdd.setXScale(Double.parseDouble(child.getAttributeValue("scaleX")));
            toAdd.setYScale(Double.parseDouble(child.getAttributeValue("scaleY")));
            toAdd.setItemName(child.getAttributeValue("objName"));
            toAdd.setPosition(Double.parseDouble(child.getAttributeValue("x")),
                    Double.parseDouble(child.getAttributeValue("y")));

            instances.add(toAdd);
         }


      } catch (IOException | JDOMException e) {
         // should not mean anything particularly bad
      }
   }

   public void addFromJMAP(String fName) {
      File file = new File(fName);
      String line, objName;
      String[] lineContents;
      Scanner scan = null;
      try {
         scan = new Scanner(file);

         while (scan.hasNextLine()) {
            line = scan.nextLine();

            if (line.equals("objects: (x, y, type)")) {
               line = scan.nextLine();
               lineContents = line.split(" ");

               for (int i = 0; lineContents != null && i < lineContents.length; i += 3) {
                  objName = Instance.num2Name(lineContents[i+2]);
                  if (objName != null) {
                     instances.add(new Instance(objName, lineContents[i], lineContents[i+1]));
                  }
               }

               break;
            }
         }
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      } finally {
         if (scan != null) {
            scan.close();
         }
      }
   }

   public void clear() {
      instances.clear();
      width = 800;
      height = 608;
   }

   public ArrayList<Instance> getInstances() {
      return instances;
   }
}
