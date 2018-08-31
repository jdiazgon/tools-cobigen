package com.devonfw.cobigen.eclipse.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.devonfw.cobigen.eclipse.common.constants.external.ResourceConstants;
import com.devonfw.cobigen.eclipse.test.common.SystemTest;
import com.devonfw.cobigen.eclipse.test.common.swtbot.AllJobsAreFinished;
import com.devonfw.cobigen.eclipse.test.common.utils.EclipseCobiGenUtils;
import com.devonfw.cobigen.eclipse.test.common.utils.EclipseUtils;

/**
 * General Eclipse Plug-in Tests.
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class ExternalIncrementsGenerationTest extends SystemTest {

    /** Root path of the Test Resources */
    private static final String resourcesRootPath = "src/main/resources/ExternalIncrementsGeneration/";

    /** Line separator, e.g. for windows '\r\n' */
    public static final String LINE_SEPARATOR = java.lang.System.getProperty("line.separator");

    /**
     * Setup workbench appropriately for tests
     * @throws Exception
     *             test fails
     */
    @BeforeClass
    public static void setupClass() throws Exception {

        try {
            // import the configuration project for this test
            EclipseUtils.importExistingGeneralProject(bot, new File(resourcesRootPath + "templates").getAbsolutePath());
            EclipseUtils.updateMavenProject(bot, ResourceConstants.CONFIG_PROJECT_NAME);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Test for external projects (not in workspace) taken as input for generation
     * @throws Exception
     *             test fails
     */
    @Test
    public void testBasicExternalIncrementGeneration() throws Exception {

        // copy sample project to external location and import it into the workspace
        String testProjName = "ExtTestProj";
        IJavaProject project = tmpMavenProjectRule.createProject(testProjName);
        FileUtils.copyFile(new File(resourcesRootPath + "input/uml.xml"),
            project.getUnderlyingResource().getLocation().append("uml.xml").toFile());
        project.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
        EclipseUtils.updateMavenProject(bot, testProjName);

        // expand the new file in the package explorer
        SWTBotView view = bot.viewById(JavaUI.ID_PACKAGES);
        SWTBotTreeItem javaClassItem = view.bot().tree().expandNode(testProjName, "uml.xml");
        javaClassItem.select();

        // execute CobiGen
        EclipseCobiGenUtils.processCobiGen(bot, javaClassItem, "3");
        // increase timeout as the openAPI parser is slow on initialization
        EclipseCobiGenUtils.confirmSuccessfullGeneration(bot, 20000);

        // check assertions
        bot.waitUntil(new AllJobsAreFinished(), 10000);
        IProject proj = ResourcesPlugin.getWorkspace().getRoot().getProject(testProjName);
        IFile generationResult = proj.getFile("src/main/java/Bill/templateDecl.java");

        try (InputStream in = generationResult.getContents()) {
            assertThat(IOUtils.toString(in)).isEqualToIgnoringWhitespace("package Bill.general.dataaccess.api;"
                + LINE_SEPARATOR + LINE_SEPARATOR + "public class Bill {" + LINE_SEPARATOR + LINE_SEPARATOR
                + "private static final long serialVersionUID = 1L;" + LINE_SEPARATOR + LINE_SEPARATOR + "}");
        }

    }
}
