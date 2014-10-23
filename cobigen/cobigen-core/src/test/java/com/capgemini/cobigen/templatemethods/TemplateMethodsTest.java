package com.capgemini.cobigen.templatemethods;

import static com.capgemini.cobigen.common.matchers.CustomHamcrestMatchers.hasItemsInList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.internal.matchers.Any.ANY;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.capgemini.cobigen.CobiGen;
import com.capgemini.cobigen.common.matchers.MatcherToMatcher;
import com.capgemini.cobigen.common.matchers.VariableAssignmentToMatcher;
import com.capgemini.cobigen.config.ContextConfiguration.ContextSetting;
import com.capgemini.cobigen.extension.IInputReader;
import com.capgemini.cobigen.extension.IMatcher;
import com.capgemini.cobigen.extension.ITriggerInterpreter;
import com.capgemini.cobigen.extension.to.TemplateTo;
import com.capgemini.cobigen.pluginmanager.PluginRegistry;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

/**
 * TestCase testing the FreeMarker template methods provided by the InputReader.
 * @author fkreis (23.10.2014)
 */
public class TemplateMethodsTest {

    /**
     * Root path to all resources used in this test case
     */
    private static String testFileRootPath = "src/test/resources/TemplateMethodsTest/";

    /**
     * JUnit Rule to temporarily create files and folders, which will be automatically removed after test
     * execution
     */
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    /**
     * Tests whether the call of a template method is possible.
     * @author fkreis (23.10.2014)
     * @throws Exception
     *             test fails
     */
    @Test
    public void callTemplateMethodTest() throws Exception {

        // Mocking
        Object containerInput = createTestDataAndConfigureMock(true, false);
        File generationRootFolder = tmpFolder.newFolder("generationRootFolder");
        // Useful to see generates if necessary, comment the generationRootFolder above then
        // File generationRootFolder = new File(testFileRootPath + "generates");

        // pre-processing
        File templatesFolder = new File(testFileRootPath + "templates");
        CobiGen target = new CobiGen(templatesFolder);
        target.setContextSetting(ContextSetting.GenerationTargetRootPath,
            generationRootFolder.getAbsolutePath());
        List<TemplateTo> templates = target.getMatchingTemplates(containerInput);

        // Execution
        // should not throw any Exceptions
        target.generate(containerInput, templates.get(0), false);
    }

    /**
     * Creates simple to debug test data, which includes on container object and one child of the container
     * object. A {@link ITriggerInterpreter TriggerInterpreter} will be mocked with all necessary supplier
     * classes to mock a simple java trigger interpreter. Furthermore, the mocked trigger interpreter will be
     * directly registered in the {@link PluginRegistry}.
     * @param containerChildMatchesTrigger
     *            defines whether the child of the container input should match any non-container matcher
     * @param multipleContainerChildren
     *            defines whether the container should contain multiple children
     * @return the container as input for generation interpreter for
     * @author mbrunnli (16.10.2014)
     */
    @SuppressWarnings("unchecked")
    private Object createTestDataAndConfigureMock(boolean containerChildMatchesTrigger,
        boolean multipleContainerChildren) {
        // we only need any objects for inputs to have a unique object reference to affect the mocked method
        // calls as intended
        Object container = new Object() {
            @Override
            public String toString() {
                return "container";
            }
        };
        Object firstChildResource = new Object() {
            @Override
            public String toString() {
                return "child";
            }
        };

        // Pre-processing: Mocking
        ITriggerInterpreter triggerInterpreter = mock(ITriggerInterpreter.class);
        IMatcher matcher = mock(IMatcher.class);
        IInputReader inputReader = mock(IInputReader.class);

        when(triggerInterpreter.getType()).thenReturn("java");
        when(triggerInterpreter.getMatcher()).thenReturn(matcher);
        when(triggerInterpreter.getInputReader()).thenReturn(inputReader);

        when(inputReader.isValidInput(any())).thenReturn(true);
        when(matcher.matches(argThat(new MatcherToMatcher(equalTo("fqn"), ANY, sameInstance(container)))))
            .thenReturn(false);
        when(matcher.matches(argThat(new MatcherToMatcher(equalTo("package"), ANY, sameInstance(container)))))
            .thenReturn(true);

        // Simulate container children resolution of any plug-in
        when(inputReader.combinesMultipleInputObjects(argThat(sameInstance(container)))).thenReturn(true);
        if (multipleContainerChildren) {
            Object secondChildResource = new Object() {
                @Override
                public String toString() {
                    return "child2";
                }
            };
            when(inputReader.getInputObjects(any(), any(Charset.class))).thenReturn(
                Lists.newArrayList(firstChildResource, secondChildResource));
        } else {
            when(inputReader.getInputObjects(any(), any(Charset.class))).thenReturn(
                Lists.newArrayList(firstChildResource));
        }

        // simulate return of method map
        Map<String, Object> methodMap = new HashMap<>();
        methodMap.put("isSubtypeOf", new IsSubtypeOfMethod(this.getClass().getClassLoader()));
        when(inputReader.getTemplateMethods(any())).thenReturn(methodMap);

        when(
            matcher.matches(argThat(new MatcherToMatcher(equalTo("fqn"), ANY,
                sameInstance(firstChildResource))))).thenReturn(containerChildMatchesTrigger);

        // Simulate variable resolving of any plug-in
        when(
            matcher.resolveVariables(
                argThat(new MatcherToMatcher(equalTo("fqn"), ANY, sameInstance(firstChildResource))),
                argThat(hasItemsInList(
                    //
                    new VariableAssignmentToMatcher(equalTo("regex"), equalTo("rootPackage"), equalTo("1")),
                    new VariableAssignmentToMatcher(equalTo("regex"), equalTo("entityName"), equalTo("3"))))))
            .thenReturn(
                ImmutableMap.<String, String> builder().put("rootPackage", "com.capgemini")
                    .put("entityName", "Test").build());

        PluginRegistry.registerTriggerInterpreter(triggerInterpreter);

        return container;
    }
}
