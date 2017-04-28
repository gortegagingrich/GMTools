import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Gabriel on 2017/03/21.
 */
@SuppressWarnings("ALL")
public class Room {
	private static Element ROOM_TEMPLATE;

	private String creationCode;
	private int width, height;
	private ArrayList<Instance> instances;

	public enum Source {
		JMAP, XML
	}

	public Room() {
		creationCode = "";
		width = 800;
		height = 608;
		instances = new ArrayList<>();
	}

	public Element toXML() {
		Element room = ROOM_TEMPLATE.clone();
		Element child;
		child = room.getChild("instances");

		instances.forEach(instance -> {
			instance.addAsElement(child);
		});

		child.addContent("\n    ");
		return room;
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
		room.instances.forEach(instance -> {
			instance.move(xOffset, yOffset);
			addInstance(instance);
		});
	}

	public void setFromXMLString(String xml) {
		instances.clear();
		SAXBuilder builder = new SAXBuilder();
		Element root;

		try {
			root = builder.build(new StringReader(xml)).getRootElement();
			parseRoomElement(root, 0, 0);
		} catch (JDOMException | IOException e) {
			e.printStackTrace();
		}
	}

	public void addFromXML(String fName) {
		addFromXML(fName, 0, 0);
	}

	public void addFromXML(String fName, double xOffset, double yOffset) {
		Instance toAdd;
		Element room;

		SAXBuilder builder = new SAXBuilder();

		try {
			room = builder.build(new File(fName)).getRootElement();
			parseRoomElement(room, xOffset, yOffset);
		} catch (IOException | JDOMException e) {
			// should not mean anything particularly bad
		}
	}

	private void parseRoomElement(Element room, double xOffset, double yOffset) {
		Instance toAdd;

		// add instances from GMX
		for (Element child : room.getChild("instances").getChildren()) {
			toAdd = new Instance();
			toAdd.setCreationCode(child.getAttributeValue("code"));
			toAdd.setXScale(Double.parseDouble(child.getAttributeValue("scaleX")));
			toAdd.setYScale(Double.parseDouble(child.getAttributeValue("scaleY")));
			toAdd.setItemName(child.getAttributeValue("objName"));
			toAdd.setPosition(xOffset + Double.parseDouble(child.getAttributeValue("x")),
					  yOffset + Double.parseDouble(child.getAttributeValue("y")));

			instances.add(toAdd);
		}
	}

	public void addFromJMAP(String fName) {
		addFromJMAP(fName, 0, 0);
	}

	@SuppressWarnings("ConstantConditions")
	public void addFromJMAP(String fName, double xOffset, double yOffset) {
		File file = new File(fName);
		String line, objName;
		String[] lineContents;
		Scanner scan = null;
		Instance instance;

		try {
			scan = new Scanner(file);

			while (scan.hasNextLine()) {
				line = scan.nextLine();

				if (line.equals("objects: (x, y, type)")) {
					line = scan.nextLine();
					lineContents = line.split(" ");

					for (int i = 0; lineContents != null && i < lineContents.length; i += 3) {
						objName = Instance.num2Name(lineContents[i + 2]);

						if (objName != null) {
							instance = new Instance(objName, lineContents[i], lineContents[i + 1]);
							instance.move(xOffset, yOffset);
							instances.add(instance);
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
	}

	public ArrayList<Instance> getInstances() {
		return instances;
	}

	public static void initTemplate() {
		SAXBuilder saxBuilder = new SAXBuilder();

		try {
			ROOM_TEMPLATE = saxBuilder.build(new File("RoomTemplate.xml")).getRootElement();
		} catch (JDOMException | IOException e) {
			e.printStackTrace();
		}
	}
}
