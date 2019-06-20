package com.cobigen.picocli.constants;

/**
 * Messages constants printed to the user
 */
public class MessagesConstants {

    /**
     * Message constant: when a command has not been understood
     */
    public static final String COMMAND_NOT_UNDERSTOOD = "Command not understood,  please try again";

    /**
     * Message constant: when a command is not yet supported
     */
    public static final String COMMAND_NOT_YET_SUPPORTED = "Command not yet supported,  sorry for the unconvenience";

    /**
     * Message constant: welcome message when user executes CobiGen help
     */
    public static final String WELCOME_MESSAGE = "Welcome to CobiGen.\n"
        + "The Code-based incemental Generator for end to end code generation tasks, mostly used in Java projects.\n"
        + "Available Commands:\n" + "cg generate (g)\n" + "cg update\n" + "cg check\n" + "cg revert\n"
        + "with [-h] you can get more infos about the commands you want to use or the increment you want to generate";

    /**
     * Message constant: description of the generate command
     */
    public static final String GENERATE_DESCRIPTION =
        "Using an input file (Java entity or ETO, OpenAPI definition, XML...) can generate code to a location on your computer";

    /**
     * Message constant: description of the input files that can be used
     */
    public static final String INPUT_FILE_DESCRIPTION =
        "Input files (Java entity or ETO, OpenAPI definition, XML...) that will be parsed by CobiGen and generate code from them."
            + " You can use glob patterns on the path, for using multiple input files. "
            + "Also you can specify input files one by one separated by comma.";

    /**
     * Message constant: description of the output root path option
     */
    public static final String OUTPUT_ROOT_PATH_DESCRIPTION = "Location where the generated code will be stored.";

    /**
     * Message constant: description of the verbose option
     */
    public static final String VERBOSE_OPTION_DESCRIPTION =
        "If this options is enabled, we will print also debug messages";

    /**
     * Message constant: description of the increments option
     */
    public static final String INCREMENTS_OPTION_DESCRIPTION =
        "List of increments that will be generated. They need to be specified with numbers separated by comma.";

}
