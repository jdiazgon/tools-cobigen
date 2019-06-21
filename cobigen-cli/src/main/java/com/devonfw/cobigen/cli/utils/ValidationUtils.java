package com.devonfw.cobigen.cli.utils;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.cli.CobiGenCLI;

/**
 * Utilities class for validating user's input
 */
public final class ValidationUtils {

    /**
     * Logger useful for printing information
     */
    private static Logger logger = LoggerFactory.getLogger(CobiGenCLI.class);

    /**
     * Extension of a POM file
     */
    private static final String POM_EXTENSION = "xml";

    /**
     * Validating user input file is correct or not. We check if file exists and it can be read
     *
     * @param inputFile
     *            user input file
     * @return true when file is valid
     */
    public boolean validateFile(File inputFile) {
        if (inputFile == null) {
            return false;
        }

        if (!inputFile.exists()) {
            logger.error("The input file " + inputFile.getAbsolutePath() + " has not been found on your system.");
            return false;
        }

        if (!inputFile.canRead()) {
            logger.error("The input file " + inputFile.getAbsolutePath()
                + " cannot be read. Please check file permissions on the file");
            return false;
        }
        return true;
    }

    /**
     * Tries to find a pom.xml file in the passed folder
     * @param source
     *            folder where we check if a pom.xml file is found
     * @return the pom.xml file if it was found, null otherwise
     */
    public static File findPom(File source) {

        String filename = source.getName();
        if (source.isFile()) {
            String basename;
            File pomFile;
            int lastDot = filename.lastIndexOf('.');
            if (lastDot > 0) {
                basename = filename.substring(0, lastDot);
                pomFile = new File(source.getParent(), basename + '.' + POM_EXTENSION);
                if (pomFile.exists() || pomFile.toString().contains("pom.xml")) {
                    logger.info("This is a valid maven project project ");
                    return pomFile;

                }
            }
            int lastSlash = filename.indexOf('-');
            if (lastSlash > 0) {
                basename = filename.substring(0, lastSlash);
                pomFile = new File(source.getParent(), basename + POM_EXTENSION);
                if (pomFile.exists()) {
                    return pomFile;
                }
            }
            return findPomFromFolder(source.getAbsoluteFile().getParentFile());
        } else if (source.isDirectory()) {
            return findPomFromFolder(source);
        }
        return null;
    }

    /**
     * Recursively tries to find a pom.xml file in the parent folders
     * @param folder
     *            folder where we want to recursively find the pom.xml
     * @param recursion
     *            current recursion level
     * @return the pom.xml file if it was found, null otherwise
     */
    private static File findPomFromFolder(File folder) {

        if (folder == null) {
            return null;
        }
        String POM_XML = "pom.xml";
        File pomFile = new File(folder, POM_XML);
        if (pomFile.exists()) {
            return pomFile;
        }
        return findPomFromFolder(folder.getParentFile());
    }

}