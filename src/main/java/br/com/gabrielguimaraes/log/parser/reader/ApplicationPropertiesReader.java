package br.com.gabrielguimaraes.log.parser.reader;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class ApplicationPropertiesReader {
    private final String applicationPropertiesFilename;
    
    public ApplicationPropertiesReader() {
        this.applicationPropertiesFilename = "application.properties";
        readApplicationPropertiesFile();
    }
    
    public ApplicationPropertiesReader(final String applicationPropertiesFilename) {
        this.applicationPropertiesFilename = applicationPropertiesFilename;
        readApplicationPropertiesFile();
    }
    
    public void readApplicationPropertiesFile() {
        Stream<String> fileLines = readApplicationProperties();
        if (fileLines == null) {
            return;
        }
        fileLines.forEach(fileLine -> parseApplicationPropertiesLine(fileLine));
        fileLines.close();
    }

    private void parseApplicationPropertiesLine(String fileLine) {
        String[] keyValues = fileLine.split("=");
        if (keyValues.length != 2) {
            return;
        }
        
        System.setProperty(keyValues[0], keyValues[1]);
    }

    public Stream<String> readApplicationProperties() {
        if (applicationPropertiesFilename == null || applicationPropertiesFilename.isEmpty()) {
            return Stream.empty();
        }
        Stream<String> fileLines = Stream.empty();
        Path path = null;
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            URL resource = classLoader.getResource(applicationPropertiesFilename);
            if (resource.toString().contains(".jar")) {
                File jarFile = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
                path = Paths.get(jarFile.getParentFile().getPath() + "\\" + applicationPropertiesFilename);
            } else {
                path = Paths.get(resource.toURI());
            }

            fileLines = Files.lines(path);
        } catch (URISyntaxException e) {
            System.out.printf("Cannot read path to load application propertie file name. %s", e);
        } catch (IOException e) {
            System.out.printf("Cannot find file when trying to load application properties file. %s\n", e);
        }
        return fileLines;
    }
}
