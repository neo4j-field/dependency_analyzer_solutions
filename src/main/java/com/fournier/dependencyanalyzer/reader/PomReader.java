package com.fournier.dependencyanalyzer.reader;

import com.fournier.dependencyanalyzer.model.Dependency;
import com.fournier.dependencyanalyzer.model.Pom;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class PomReader {

    public Pom readPom(String filePath, String parentDir) {
        List<Dependency> dependencies = new ArrayList<>();

        try {
            File pomFile = new File(filePath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(pomFile);

            doc.getDocumentElement().normalize();
            NodeList dependencyNodes = doc.getElementsByTagName("dependency");

            for (int i = 0; i < dependencyNodes.getLength(); i++) {
                Element dependencyElement = (Element) dependencyNodes.item(i);

                String groupId = getTagValue("groupId", dependencyElement);
                String artifactId = getTagValue("artifactId", dependencyElement);
                String version = getTagValue("version", dependencyElement);

                dependencies.add(new Dependency(groupId, artifactId, version));
            }
        } catch (Exception e) {
            System.err.println("Failed to read POM file: " + filePath);
            e.printStackTrace();
        }

        return new Pom(filePath, parentDir, dependencies);

    }

    private String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return "";
    }
}
