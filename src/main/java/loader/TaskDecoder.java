package loader;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class TaskDecoder {

    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document document = builder.parse(new File("build/resources/main/tasks.xml"));

    public TaskDecoder() throws Exception {
    }

    public String getClass(int i) {
        return document.getElementsByTagName("class").item(i).getTextContent().trim();
    }

    public String getTest(int i) {
        return document.getElementsByTagName("test").item(i).getTextContent().trim();
    }

    public String getTestName(int i) {
        return document.getElementsByTagName("test").item(i).getAttributes().getNamedItem("name").getTextContent();
    }

    public String getClassName(int i) {
        return document.getElementsByTagName("class").item(i).getAttributes().getNamedItem("name").getTextContent();
    }

    public String getDescription(int i) {
        return document.getElementsByTagName("description").item(i).getTextContent().trim();
    }

    public String getExcercise(int i) {
        return document.getElementsByTagName("exercise").item(i).getAttributes().getNamedItem("name").getTextContent();
    }

    public NodeList getTasks() {
        return document.getElementsByTagName("exercise");
    }

    public Boolean isBabysteps(int i) {
        return Boolean.parseBoolean(document.getElementsByTagName("babysteps").item(i).getAttributes().getNamedItem("value").getTextContent());
    }

    public int getBabystepsTime(int i) {
        return Integer.parseInt(document.getElementsByTagName("babysteps").item(i).getAttributes().getNamedItem("time").getTextContent());
    }

}
