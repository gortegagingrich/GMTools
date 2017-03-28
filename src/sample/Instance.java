package sample;

import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Paint;

/**
 * Created by Gabriel on 2017/03/21.
 */
public class Instance implements Comparable {
   protected double x,y;
   protected double[] xArr, yArr;
   protected double xScale, yScale;
   protected Paint color1, color2;
   protected int depth;
   protected String itemName;
   protected String creationCode;

   public Instance(String[] str) {

   }

   public void draw(Canvas c) {

   }

   private void setColors(Paint c1, Paint c2) {

   }

   public void setXScale(double xScale) {

   }

   public void setYScale(double yScale) {

   }

   public void setCreationCode(String code) {

   }

   @Override
   public int compareTo(Object i) {
      return 0;
   }

   @Override
   public String toString() {
      String out = "";

      return out;
   }
}
