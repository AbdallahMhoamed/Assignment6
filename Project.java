import java.io.*;

import java.util.*;

import javax.xml.parsers.*;

import org.w3c.dom.*;

import org.xml.sax.*;


public class AutosarFileReorder {


    public static void main(String[] args) throws Exception {

        try {

            if (args.length != 1) {

                System.out.println("Usage: AutosarFileReorder <inputfile>");

                return;

            }

            String inputFileName = args[0];

            if (!inputFileName.endsWith(".arxml")) {

                throw new NotValidAutosarFileException("Not a valid Autosar file.");

            }


            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

            DocumentBuilder db = dbf.newDocumentBuilder();

            Document doc = db.parse(new File(inputFileName));


            NodeList containerNodes = doc.getElementsByTagName("CONTAINER");


            List<Element> containerElements = new ArrayList<Element>();

            for (int i = 0; i < containerNodes.getLength(); i++) {

                Element containerElement = (Element) containerNodes.item(i);

                containerElements.add(containerElement);

            }

            Collections.sort(containerElements, new Comparator<Element>() {

                @Override

                public int compare(Element e1, Element e2) {

                    String shortName1 = e1.getElementsByTagName("SHORT-NAME").item(0).getTextContent();

                    String shortName2 = e2.getElementsByTagName("SHORT-NAME").item(0).getTextContent();

                    return shortName1.compareTo(shortName2);

                }

            });


            Document newDoc = db.newDocument();

            Element rootElement = newDoc.createElement("AUTOSAR");

            newDoc.appendChild(rootElement);

            for (Element containerElement : containerElements) {

                Element newContainerElement = (Element) newDoc.importNode(containerElement, true);

                rootElement.appendChild(newContainerElement);

            }


            String outputFileName = inputFileName.replace(".arxml", "_mod.arxml");

            TransformerFactory tf = TransformerFactory.newInstance();

            Transformer transformer = tf.newTransformer();

            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            transformer.setOutputProperty(OutputKeys.METHOD, "xml");

            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            DOMSource source = new DOMSource(newDoc);

            StreamResult result = new StreamResult(new File(outputFileName));

            transformer.transform(source, result);


        } catch (NotValidAutosarFileException ex1) {

            System.out.println("ERROR: " + ex1.getMessage());

        } catch (EmptyAutosarFileException ex2) {

            System.out.println("ERROR: " + ex2.getMessage());

        } catch (Exception ex3) {

            System.out.println("ERROR: " + ex3.getMessage());

        }

    }


    static class NotValidAutosarFileException extends Exception {

        public NotValidAutosarFileException(String message) {

            super(message);

        }

    }


    static class EmptyAutosarFileException extends Exception {

        public EmptyAutosarFileException(String message) {

            super(message);


        } 

}


 }
