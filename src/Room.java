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
@SuppressWarnings("DefaultFileTemplate")
public class Room {
	private static Element ROOM_TEMPLATE;

	private String creationCode;
	private int width, height;
	private final ArrayList<Instance> instances;

	public enum Source {
		JMAP, XML
	}

	public Room() {
		creationCode = "";
		width = 800;
		height = 608;
		instances = new ArrayList<>();
	}

	/**
	 * Creates a copy of template room.
	 * Then adds contents of room to that copy and returns it.
	 *
	 * @return Element representing the contents of the room
	 */
	public Element toXML() {
		Element room;
		Element child;

		room = ROOM_TEMPLATE.clone();
		child = room.getChild("instances");

		//noinspection CodeBlock2Expr
		instances.forEach(instance -> {
			instance.addAsElement(child);
		});

		child.addContent("\n    ");
		return room;
	}

	/**
	 * Adds the given instance to the room
	 *
	 * @param instance Instance object to be added
	 */
	public void addInstance(Instance instance) {
		instances.add(instance);
	}

	/**
	 * I might eventually allow for tiling.
	 * Currently does nothing.
	 */
	@SuppressWarnings("SameReturnValue")
	public void addTile() {
		System.err.println("This is not supported");
	}

	/**
	 * I might eventually allow for tiling.
	 * Currently does nothing.
	 */
	@SuppressWarnings("SameReturnValue")
	public void removeTile() {
		System.err.println("This is not supported");
	}

	/**
	 * Sets room's creation code.
	 *
	 * @param str Contents of creation code
	 */
	public void setCreationCode(String str) {
		creationCode = str;
	}

	/**
	 * Changes the room's size.
	 *
	 * @param width Width of the room in pixels
	 * @param height Height of the room in pixels
	 */
	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	/**
	 * Adds contents of given room at given offsets.
	 *
	 * @param room Room to be added
	 * @param xOffset Horizontal offset for all Instances.
	 * @param yOffset Vertical offset for all Instances.
	 */
	public void merge(Room room, int xOffset, int yOffset) {
		room.instances.forEach(instance -> {
			instance.move(xOffset, yOffset);
			addInstance(instance);
		});
	}

	/**
	 * Clears instances.  Then parses given String as XML and adds contents.
	 * Used to update the room from the code area in the GUI.
	 *
	 * @param xml XML representation of room
	 */
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

	/**
	 * Parses given file as XML representation of room and adds contents.
	 *
	 * @param fName File containing XML representation
	 */
	public void addFromXML(String fName) {
		addFromXML(fName, 0, 0);
	}

	/**
	 * Parses given file as XML representation of room and adds contents at given offsets.
	 *
	 * @param fName File containing XML representation
	 * @param xOffset Horizontal offset for new Instances
	 * @param yOffset Vertical offset for new Instances
	 */
	@SuppressWarnings("SameParameterValue")
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

	/**
	 * Adds each child in given Element's "instances" child as an Instance with given offset.
	 *
	 * @param room XML element containing Instances to be added
	 * @param xOffset Horizontal offset for new Instances
	 * @param yOffset Vertical offset for new Instances
	 */
	private void parseRoomElement(Element room, double xOffset, double yOffset) {
		Instance toAdd;

		// add instances from GMX
		for (Element child : room.getChild("instances").getChildren()) {
			toAdd = new Instance();
			toAdd.setCreationCode(child.getAttributeValue("code"));
			toAdd.setXScale(Double.parseDouble(child.getAttributeValue("scaleX")));
			toAdd.setYScale(Double.parseDouble(child.getAttributeValue("scaleY")));
			toAdd.setObjName(child.getAttributeValue("objName"));
			toAdd.setPosition(xOffset + Double.parseDouble(child.getAttributeValue("x")),
					  yOffset + Double.parseDouble(child.getAttributeValue("y")));

			instances.add(toAdd);
		}
	}

	/**
	 * Adds contents of given JMAP file.
	 *
	 * @param fName JMAP file to add
	 */
	public void addFromJMAP(String fName) {
		addFromJMAP(fName, 0, 0);
	}

	/**
	 * Adds contents of given JMAP file with given offsets
	 *
	 * @param fName JMAP file to add
	 * @param xOffset Horizontal offset for new Instances
	 * @param yOffset Vertical offset for new Instances
	 */
	@SuppressWarnings({"ConstantConditions", "SameParameterValue"})
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

	/**
	 * Removes all instances.
	 */
	public void clear() {
		instances.clear();
	}

	/**
	 * Returns an ArrayList containing all instances
	 *
	 * @return copy of instances
	 */
	public ArrayList<Instance> getInstances() {
		return (ArrayList<Instance>)instances.clone();
	}

	/**
	 * Initializes ROOM_TEMPLATE using file RoomTemplate.xml
	 */
	public static void initTemplate() {
		SAXBuilder saxBuilder = new SAXBuilder();

		try {
			ROOM_TEMPLATE = saxBuilder.build(new File("RoomTemplate.xml")).getRootElement();
		} catch (JDOMException | IOException e) {
			e.printStackTrace();
		}
	}
}
