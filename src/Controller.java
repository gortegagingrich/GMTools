import javafx.event.ActionEvent;
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
import org.jdom2.output.XMLOutputter;

import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class Controller implements Initializable {
   private GraphicsContext gc;
   private ArrayList<Instance> list;
   private Room room = null;

   @FXML
   private Canvas   img;
   @FXML
   private CodeArea code;
   @FXML
   private MenuItem newClear;
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
      Room.initTemplate();
      initCanvas();

      // make sure it only pans with middle mouse button
      img.addEventHandler(MouseEvent.ANY, event -> {
         if (event.getButton() != MouseButton.MIDDLE) {
            event.consume();
         }
      });

      setRoom("test.jmap",Room.Source.JMAP);

      code.setParagraphGraphicFactory(LineNumberFactory.get(code));
      code.setWrapText(false);

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

      updateText();
      refresh();
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
   }

   @FXML
   private void updateText() {
      String text;
      XMLOutputter xmOut = new XMLOutputter();
      text = xmOut.outputString(room.toXML());

      code.clear();
      code.insertText(0,0,text);
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

      result.ifPresent(s -> {
         try {
            int pos = Integer.parseInt(s);

            // make sure position is within bounds
            pos = (pos < 1) ? 1 : pos;
            pos = (pos - 1 > code.getParagraphs().size()) ? code.getParagraphs().size() : pos;

            // might need to be changed
            code.moveTo(pos - 1, 0, NavigationActions.SelectionPolicy.CLEAR);
         } catch (Exception e) {
            // just do nothing if something goes wrong
         }
      });

   }

   private void initCanvas() {
      gc = img.getGraphicsContext2D();
      gc.setFill(Color.BLACK);
   }

   private void refresh() {
      resetCanvas();
      drawInstances();
      drawGrid();
   }

   @SuppressWarnings("SameParameterValue")
   private void setRoom(String str, Room.Source source) {
      if (room == null) {
         room = new Room();
      } else {
         room.clear();
      }

      switch (source) {
         case JMAP:
            room.addFromJMAP(str);
            break;
         case XML:
            room.addFromXML(str);
            break;
      }
   }

   @FXML
   private void clearInstances() {
      room.clear();
      resetCanvas();
      drawGrid();
      drawInstances();
   }

   @FXML
   private void printXML() {
      XMLOutputter xmOut = new XMLOutputter();
      System.out.println(xmOut.outputString(room.toXML()));
   }

   @FXML
   private void exit() {
      Stage stage = (Stage) img.getScene().getWindow();
      stage.close();
   }

   public void readCode(ActionEvent actionEvent) {
      room.setFromXMLString(code.getText());
      refresh();
   }
}
