package com.xml;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.StringWriter;

public class Xml {

    private String filepath = "signatories_model_info.xml";
    private Document document;

    /*
    a. Get all elements/attributes which have ﬁeld_type API_BASED. List them by their
    ‘tag_name’ attribute
    b. Count all the elements which are ﬁeld_type TABLE_BASED.
    c. Get all the elements which are to be checked for duplicates and the associated ﬁelds
    d. Remove the XML elements RESTRICTED_ACCESS_NATIONALITIES_MATCH_TYPE,
    MAX_RESTRICTED_ACCESS_NATIONALITIES, RESTRICTED_ACCESS_NATIONALITIES
    e. Update all elements where use=’MANDATORY’, to be OPTIONAL
     */

    public Document readFile() throws Exception {

        // Specify the file path as a File object
        File xmlFile = new File(filepath);

        // Create a DocumentBuilder
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        // Parse the XML file
        this.document = builder.parse(xmlFile);

        return document;
    }

    // Utility method to print the entire XML document
    public void printDocument(Document doc) {
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");  // Pretty-print XML

            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));

            System.out.println(writer.toString());  // Print XML as a string
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    public void tagName() {
        // Access elements by tag name
        NodeList nodeList = document.getElementsByTagName("item");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;

                // Check if field_type is "API_BASED"
                if ("API_BASED".equals(element.getAttribute("field_type"))) {
                    System.out.println("Tag Name: " + element.getAttribute("tag_name"));
                }
            }
        }
    }

    public void tableBased() {
        // count of all elements which are field type able based
        int tables = 0;
        NodeList nodeList = document.getElementsByTagName("item");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            Element element = (Element) node;
            if ("TABLE_BASED".equals(element.getAttribute("field_type"))) {
                tables++; // Increase count for each match
            }
        }
        System.out.println("Count of TABLE_BASED elements: " + tables);

    }

    public void duplicates(){
        // Getting all the elements which are to be checked for duplicates and the associated ﬁelds\
        NodeList nodeList = document.getElementsByTagName("check_duplicate");
        for (int i=0; i<nodeList.getLength(); i++){
            Node node = nodeList.item(i);
            Element element = (Element) node;
            if ("YES".equals(element.getAttribute("value"))){
                // Get all associated fields
                NodeList associatedFields = element.getElementsByTagName("associated_field");
                for (int j = 0; j < associatedFields.getLength(); j++) {
                    Element associatedField = (Element) associatedFields.item(j);
                    System.out.println(" - Associated Field: " + associatedField.getAttribute("value"));
                }
            }
        }
    }

    public void remove(){
        // Removing the XML elements RESTRICTED_ACCESS_NATIONALITIES_MATCH_TYPE,
        //    MAX_RESTRICTED_ACCESS_NATIONALITIES, RESTRICTED_ACCESS_NATIONALITIES
        NodeList nodeList = document.getElementsByTagName("item");
        for(int i = nodeList.getLength() - 1; i >= 0; i--){// Reverse loop to avoid index shifting
            Node node = nodeList.item(i);
            Element element = (Element) node;
            if ("RESTRICTED_ACCESS_NATIONALITIES_MATCH_TYPE".equals(element.getAttribute("tag_name")) || "MAX_RESTRICTED_ACCESS_NATIONALITIES".equals(element.getAttribute("tag_name")) || "RESTRICTED_ACCESS_NATIONALITIES".equals(element.getAttribute("tag_name"))){
                // Remove the node from its parent
                Node parent = node.getParentNode();
                if (parent != null) {
                    parent.removeChild(node);
                }
            }
        }
        System.out.println("\nAfter Deletion:");
        printDocument(document);
    }

    public void update(){
        // Updating all elements where use=’MANDATORY’, to be OPTIONAL
        NodeList nodeList = document.getElementsByTagName("item");
        for (int i=0; i<nodeList.getLength(); i++){
            Node node = nodeList.item(i);
            Element element = (Element) node;
            if("MANDATORY".equals(element.getAttribute("use"))){
                element.setAttribute("use", "OPTIONAL");
            }
        }
        printDocument(document);
    }

    public static void main(String[] args) throws Exception {
        Xml parser = new Xml();
        parser.readFile();
        //parser.tagName();
        //parser.tableBased();
        //parser.duplicates();
        //parser.remove();
        //parser.update();
    }
}
