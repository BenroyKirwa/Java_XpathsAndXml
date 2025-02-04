package com.xpaths;

import org.w3c.dom.*;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import javax.xml.parsers.*;
import java.io.*;

public class Xpaths {
    private Document document;
    private XPath xPath;

    public Xpaths() {
        this.xPath = XPathFactory.newDefaultInstance().newXPath();
    }

    public void loadDocument(String xmlFilePath) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        this.document = builder.parse(new File(xmlFilePath));
    }

    public void processXPathOperations(String xmlFilePath) {
        try {
            loadDocument(xmlFilePath);

            // a. Get API_BASED elements
            System.out.println("a. Elements with field_type='API_BASED':");
            NodeList apiBased = (NodeList) xPath.evaluate(
                    "//item[@field_type='API_BASED']/@tag_name",
                    document,
                    XPathConstants.NODESET
            );
            for (int i = 0; i < apiBased.getLength(); i++) {
                System.out.println("Tag name: " + apiBased.item(i).getNodeValue());
            }

            // b. Count TABLE_BASED elements
            NodeList tableBased = (NodeList) xPath.evaluate(
                    "//item[@field_type='TABLE_BASED']",
                    document,
                    XPathConstants.NODESET
            );
            System.out.println("\nb. Number of TABLE_BASED elements: " + tableBased.getLength());

            // c. Get elements with duplicate checks
            System.out.println("\nc. Elements with duplicate checks and their associated fields:");
            NodeList duplicateChecks = (NodeList) xPath.evaluate(
                    "//check_duplicate[@value='YES']/..",
                    document,
                    XPathConstants.NODESET
            );
            for (int i = 0; i < duplicateChecks.getLength(); i++) {
                Element element = (Element) duplicateChecks.item(i);
                String tagName = element.getAttribute("tag_name");
                Node associatedField = (Node) xPath.evaluate(
                        ".//associated_field/@value",
                        element,
                        XPathConstants.NODE
                );
                System.out.println("Tag: " + tagName + ", Associated field: " + associatedField.getNodeValue());
            }

            // d. Remove specified elements
            String[] elementsToRemove = {
                    "RESTRICTED_ACCESS_NATIONALITIES_MATCH_TYPE",
                    "MAX_RESTRICTED_ACCESS_NATIONALITIES",
                    "RESTRICTED_ACCESS_NATIONALITIES"
            };

            for (String elementName : elementsToRemove) {
                Node elementToRemove = (Node) xPath.evaluate(
                        "//item[@tag_name='" + elementName + "']",
                        document,
                        XPathConstants.NODE
                );
                if (elementToRemove != null) {
                    elementToRemove.getParentNode().removeChild(elementToRemove);
                }
            }

            // e. Update MANDATORY to OPTIONAL
            NodeList mandatoryElements = (NodeList) xPath.evaluate(
                    "//item[@use='MANDATORY']",
                    document,
                    XPathConstants.NODESET
            );
            for (int i = 0; i < mandatoryElements.getLength(); i++) {
                Element element = (Element) mandatoryElements.item(i);
                element.setAttribute("use", "OPTIONAL");
            }

            // Save the modified document
            saveDocument(xmlFilePath + ".modified.xml");
            System.out.println("\nModified XML has been saved to " + xmlFilePath + ".modified.xml");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveDocument(String outputPath) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(new File(outputPath));
        transformer.transform(source, result);
    }

    public static void main(String[] args) {
        Xpaths xpaths = new Xpaths();
        String xmlFilePath = "signatories_model_info.xml";
        xpaths.processXPathOperations(xmlFilePath);
    }
}