package uk.ac.sanger.sprint.config;

import uk.ac.sanger.sprint.model.Printer;

import java.nio.file.Path;
import java.util.*;

/**
 * A tool to load the config for the application.
 */
public interface ConfigLoader {
    /**
     * Reads the config and returns a map of printer hostname to printer.
     * Multiple config files can be read and aggregated into one map.
     * @param paths the paths to read to get the config
     * @return a map to all printers from their hostnames
     */
    Map<String, Printer> getPrinters(Collection<Path> paths);
}
