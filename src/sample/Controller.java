package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.NavigationActions;

import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class Controller implements Initializable {
   private GraphicsContext gc;
   private ArrayList<Instance> list;
   private Room room;

   @FXML
   private Canvas   img;
   @FXML
   private CodeArea code;
   @FXML
   private MenuBar  menubar;
   @FXML
   private MenuItem newClear;
   @FXML
   private MenuItem exit;
   @FXML
   private MenuItem gotoItem;
   @FXML
   private Tab      plusTab;
   @FXML
   private TabPane  tabPane;

   @Override
   public void initialize(URL location, ResourceBundle resources) {
      Instance.init();
      initInstances();

      gc = img.getGraphicsContext2D();
      gc.setFill(Color.BLACK);

      // make sure it only pans with middle mouse button
      img.addEventHandler(MouseEvent.ANY, event -> {
         if (event.getButton() != MouseButton.MIDDLE) {
            event.consume();
         }
      });

      room = new Room();
      room.addFromJMAP("test.jmap");

      resetCanvas();
      drawInstances();
      drawGrid();

      code.setParagraphGraphicFactory(LineNumberFactory.get(code));

      newClear.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));

      gotoItem.setAccelerator(new KeyCodeCombination(KeyCode.G, KeyCombination.CONTROL_DOWN));

      tabPane.getSelectionModel()
              .selectedItemProperty()
              .addListener((observable, oldTab, newTab) -> {
                 if (newTab.getText() != null && newTab.getText().equals("+")) {
                    Tab tab = new Tab("test");
                    tabPane.getTabs().remove(plusTab);
                    tabPane.getTabs().add(tab);
                    tabPane.getTabs().add(plusTab);
                    tabPane.getSelectionModel().select(tab);
                 }
              });
   }

   private void resetCanvas() {
      gc.clearRect(0, 0, img.getWidth(), img.getHeight());
   }

   private void drawGrid() {
      // draw grid
      gc.setLineWidth(1);

      gc.setGlobalAlpha(0.25);

      for (int i = 0; i <= img.getWidth(); i += 32) {
         gc.strokeLine(i, 0, i, img.getHeight());
      }

      for (int i = 0; i <= img.getHeight(); i += 32) {
         gc.strokeLine(0, i, img.getWidth(), i);
      }

      gc.setGlobalAlpha(1);
   }

   @FXML
   private void reloadImages() {
      Instance.init();
      resetCanvas();
      drawGrid();
      drawInstances();
   }

   private void drawInstances() {

      room.getInstances().forEach(instance -> {
         instance.draw(gc);
      });
   }

   private void initInstances() {
      int x, y;

      list = new ArrayList<>();

      x = 256;
      y = 256;

      for (String objName : Instance.images.keySet()) {
         list.add(new Instance(objName, x, y));
         x += 32;

         if (x > 440) {
            x = 256;
            y += 32;
         }
      }
   }

   @FXML
   private void resizeDrawingCanvas() {
      System.out.println("called");
   }

   @FXML
   public void codeAreaGoTo() {
      TextInputDialog tid;

      tid = new TextInputDialog(Integer.toString(code.getCurrentParagraph() + 1));
      tid.setTitle("Goto Line");
      tid.setHeaderText("");

      Optional<String> result = tid.showAndWait();

      if (result.isPresent()) {
         try {
            int pos = Integer.parseInt(result.get());

            // make sure position is within bounds
            pos = (pos < 1) ? 1 : pos;
            pos = (pos - 1 > code.getParagraphs().size()) ? code.getParagraphs().size() : pos;

            // might need to be changed
            code.moveTo(pos - 1, 0, NavigationActions.SelectionPolicy.CLEAR);
         } catch (Exception e) {
            // just do nothing if something goes wrong
         }
      }

   }

   @FXML
   private void exit() {
      Stage stage = (Stage) img.getScene().getWindow();
      stage.close();
   }
}
