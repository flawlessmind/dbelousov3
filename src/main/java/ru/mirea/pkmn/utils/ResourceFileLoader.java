package ru.mirea.pkmn.utils;

import com.google.common.io.Resources;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.FileInputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;

public class ResourceFileLoader {

    Logger logger;

    @Autowired
    public ResourceFileLoader(Logger logger) {

        this.logger = logger;
    }

    public FileInputStream getResourceFile(String name) {

        try {
            URL resource =  Resources.getResource(name);

            Path path = Paths.get(resource.toURI());

            return new FileInputStream(path.toString());

        } catch (Exception e) {
            logger.error("Unable to load resource: " + e.getMessage());
        }

        return null;
    }

    public String getResourcePath(String name) {

        try {
            URL resource =  Resources.getResource(name);

            Path path = Paths.get(resource.toURI());

            return path.toString();

        } catch (Exception e) {
            logger.error("Unable to get resource path: " + e.getMessage());
        }

        return null;
    }
}
