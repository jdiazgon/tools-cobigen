package com.cobigen.picocli.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.apache.maven.plugin.MojoFailureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.CobiGen;
import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.api.extension.Merger;
import com.devonfw.cobigen.api.to.IncrementTo;
import com.devonfw.cobigen.eclipse.common.constants.external.ResourceConstants;
import com.devonfw.cobigen.eclipse.common.exceptions.GeneratorProjectNotExistentException;
import com.devonfw.cobigen.eclipse.common.tools.PlatformUIUtil;
import com.devonfw.cobigen.impl.CobiGenFactory;
import com.devonfw.cobigen.impl.extension.PluginRegistry;
import com.devonfw.cobigen.impl.util.TemplatesJarUtil;
import com.devonfw.cobigen.javaplugin.JavaTriggerInterpreter;
import com.devonfw.cobigen.maven.validation.InputPreProcessor;
import com.devonfw.cobigen.textmerger.TextAppender;
import com.devonfw.cobigen.tsplugin.merger.TypeScriptMerger;
import com.devonfw.cobigen.xmlplugin.XmlTriggerInterpreter;
import com.google.common.collect.Lists;

public class CreateJarFile {
    private static Logger logger = LoggerFactory.getLogger(CreateJarFile.class);

    private static Class<?> clazz;

    public CreateJarFile() {
        super();
    }

    File jarFile = new File("templates_jar/templates-devon4j-3.1.0.jar");

    File jarPath = new File("templates_jar");

    File jarFileDir = jarPath.getAbsoluteFile();

    List<Class<?>> utilClasses;

    Class<? extends Scanner> lineNum;

    /** Current registered input objects */
    private List<Object> inputs;

    /**
     * Resolves all classes, which have been defined in the template configuration folder from a jar.
     *
     * @return the list of classes
     * @throws GeneratorProjectNotExistentException
     *             if no generator configuration project called {@link ResourceConstants#CONFIG_PROJECT_NAME}
     *             exists
     * @throws IOException
     *             {@link IOException} occurred
     */
    List<Class<?>> resolveTemplateUtilClassesFromJar(File jarPath)
        throws GeneratorProjectNotExistentException, IOException {
        final List<Class<?>> result = new LinkedList<>();
        Path templateRoot;
        ClassLoader inputClassLoader =
            URLClassLoader.newInstance(new URL[] { jarPath.toURI().toURL() }, getClass().getClassLoader());
        URL contextConfigurationLocation = inputClassLoader.getResource("context.xml");
        if (contextConfigurationLocation == null
            || contextConfigurationLocation.getPath().endsWith("target/classes/context.xml")) {
            contextConfigurationLocation = inputClassLoader.getResource("src/main/templates/context.xml");
            if (contextConfigurationLocation == null) {
                throw new CobiGenRuntimeException("No context.xml could be found in the classpath!");
            } else {

                final Map<String, String> env = new HashMap<>();

                String[] pathTemplate = contextConfigurationLocation.toString().split("!");
                final FileSystem fs = FileSystems.newFileSystem(URI.create(pathTemplate[0]), env);
                final Path path = fs.getPath(pathTemplate[1]);

                templateRoot = Paths.get(URI.create("file://" + path.toString())).getParent().getParent().getParent();
                // templateRoot =
                // Paths.get(URI.create(contextConfigurationLocation.toString())).getParent().getParent()
                // .getParent();
            }
        } else {
            templateRoot = Paths.get(URI.create(contextConfigurationLocation.toString()));
        }
        logger.debug("Found context.xml @ " + contextConfigurationLocation.toString());
        final List<String> foundClasses = new LinkedList<>();
        if (contextConfigurationLocation.toString().startsWith("jar")) {
            logger.info("Processing configuration archive " + contextConfigurationLocation.toString());
            try {
                // Get the URI of the jar from the URL of the contained context.xml
                URI jarUri = URI.create(contextConfigurationLocation.toString().split("!")[0]);
                FileSystem jarfs = FileSystems.getFileSystem(jarUri);

                // walk the jar file
                logger.debug("Searching for classes in " + jarUri.toString());
                Files.walkFileTree(jarfs.getPath("/"), new SimpleFileVisitor<Path>() {

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        if (file.toString().endsWith(".class")) {
                            logger.debug("    * Found class file " + file.toString());
                            // remove the leading '/' and the trailing '.class'
                            String fileName = file.toString().substring(1, file.toString().length() - 6);
                            // replace the path separator '/' with package separator '.' and add it to the
                            // list of found files
                            foundClasses.add(fileName.replace("/", "."));
                        }
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                        // Log errors but do not throw an exception
                        logger.warn(exc.getMessage());
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {
                logger.error("An exception occurred while processing Jar files to create CobiGen_Templates folder", e);
                PlatformUIUtil.openErrorDialog(
                    "An exception occurred while processing Jar file to create CobiGen_Templates folder", e);
            }
            for (String className : foundClasses) {
                try {
                    result.add(inputClassLoader.loadClass(className));
                } catch (ClassNotFoundException e) {
                    logger.warn("Could not load " + className + " from classpath", e);
                }
            }
        }

        return result;

    }

    /**
     * @param User
     *            input entity file
     */
    public void createJarAndGenerateIncr(File inputFile) {
        jarFile = TemplatesJarUtil.getJarFile(false, jarPath);
        logger.info("get jar file");
        URLClassLoader classLoader = null;
        File root = inputFile.getParentFile();
        // Call method to get utils from jar
        try {

            utilClasses = resolveTemplateUtilClassesFromJar(jarFile);
        } catch (GeneratorProjectNotExistentException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        } catch (IOException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }

        // JavaParser. parse(inputFile.getAbsolutePath());

        if (jarFile != null) {
            try {

                CobiGen cg = CobiGenFactory.create(jarFile.toURI());
                Object input = null;
                try {
                    // Compile source file.
                    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler() ;
                   
                    compiler.run(null, null, null, inputFile.toString());
                   
                    // Load and instantiate compiled class.
                    ClassLoader clsLoader = new URLClassLoader(new URL[] { root.toURI().toURL() });

                    input = InputPreProcessor.process(cg, inputFile, this.getClass().getClassLoader());
                    System.out.println("input for getMatchingIncrements => "+input);
                    List<IncrementTo> matchingIncrements = cg.getMatchingIncrements(input);
                    for (IncrementTo inc : matchingIncrements) {

                        System.out.println("Increments Available = " + inc.getDescription());
                    }

                    cg.generate(input, matchingIncrements, Paths.get("C:\\\\Users\\\\syadav9\\\\Desktop\\\\temp"), false,
                        utilClasses);
                    System.out.println("Successfully generated Template");
                } catch (MojoFailureException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                List<IncrementTo> matchingIncrements = cg.getMatchingIncrements(input);
                if (matchingIncrements.size() > 0) {

                    System.out.println("Increments Available = " + matchingIncrements.get(0).getDescription());
                }
            } catch (InvalidConfigurationException e) {
                // if the context configuration is not valid
                e.printStackTrace();
            } catch (IOException e) {
                // If I/O operation failed then it will throw exception
                e.printStackTrace();
            }

            logger.info("After create method");
        }

    }

    /**
     * Registers the given triggerInterpreter,tsmerge, to be registered
     */
    public void registerPlugin() {
        JavaTriggerInterpreter javaTriger = new JavaTriggerInterpreter("java");
        PluginRegistry.registerTriggerInterpreter(javaTriger);
        //
        TypeScriptMerger tsmerger = new TypeScriptMerger("tsmerge", false);
        PluginRegistry.registerMerger(tsmerger);
        XmlTriggerInterpreter xmlTriggerInterpreter = new XmlTriggerInterpreter("xml");
        PluginRegistry.registerTriggerInterpreter(xmlTriggerInterpreter);

        List<Merger> merger = Lists.newLinkedList();
        merger.add(new TextAppender("textmergerActivator", false));
    }

    /**
     * Validating user input file is correct or not
     *
     * @param inputFile
     *            user input file
     */
    public boolean validateFile(File inputFile) {
        if (!inputFile.exists() || !inputFile.canRead()) {
            logger.info("The file " + inputFile.getAbsolutePath() + " is not a valid input for CobiGen.");
            try {
                throw new Exception("Could not read input file " + inputFile.getAbsolutePath());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return false;
        }
        return true;
    }

}