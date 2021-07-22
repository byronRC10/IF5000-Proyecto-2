package Utility;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Base64;

import javax.imageio.ImageIO;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;


public class Conversiones {

    public static String xmlToString(Element element) {
        XMLOutputter outputter = new XMLOutputter(Format.getCompactFormat().getCompactFormat());
        String xmlStringElement = outputter.outputString(element);
        xmlStringElement = xmlStringElement.replace("\n", "");
        return xmlStringElement;
    }

    public static Element stringToXML(String stringElement) throws JDOMException, IOException {
        SAXBuilder saxBuilder = new SAXBuilder();
        StringReader stringReader = new StringReader(stringElement);
        Document doc = saxBuilder.build(stringReader);
        return doc.getRootElement();
    } // stringToXML

    public static String anadirAccion(Element element, String accion) {
        Element eAccion = new Element("accion");
        eAccion.addContent(accion);

        element.addContent(eAccion);

        return xmlToString(element);
    }// anadirAccion

} // fin clase
