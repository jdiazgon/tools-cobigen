package utils;

import java.util.Collection;
import java.util.Map;

import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.org.apache.xerces.internal.dom.DeferredElementNSImpl;

import constants.pojo.Field;

/**
 * A class for shared oasp4j specific functions in the templates
 *
 */
public class OaspUtil {

    /**
     * Check whether the given 'canonicalType' is an OASP Entity, which is declared in the given 'component'
     *
     * @param canonicalType
     *            the type name
     * @param component
     *            the component name
     * @return true iff the canonicalType is an OASP Entity
     */
    public boolean isEntityInComponent(String canonicalType, String component) {

        return canonicalType.matches(String.format(".+%1$s\\.dataaccess\\.api\\.[A-Za-z0-9]+Entity(<.*)?", component));
    }

    /**
     * Determines the ID getter for a given 'field' dependent on whether the getter should access the ID via
     * an object reference or a direct ID getter
     *
     * @param field
     *            the field
     * @param byObjectReference
     *            boolean
     * @param component
     *            the OASP4j component name
     * @return 'get' + {@link #resolveIdVariableNameOrSetterGetterSuffix(Map, boolean, boolean, String)} +
     *         '()' with capitalize=true
     */
    public String resolveIdGetter(Map<String, Object> field, boolean byObjectReference, String component) {

        return "get" + resolveIdVariableNameOrSetterGetterSuffix(field, byObjectReference, true, component) + "()";
    }

    /**
     * Determines the ID getter for a given 'field' dependent on whether the getter should access the ID via
     * an object reference or a direct ID getter
     *
     * @param pojoClass
     *            the class object of the pojo
     * @param fieldMap
     *            the field as Map&lt;String, Object>
     * @param byObjectReference
     *            boolean
     * @param component
     *            the OASP4j component name
     * @return 'get' + {@link #resolveIdVariableNameOrSetterGetterSuffix(Map, boolean, boolean, String)} +
     *         '()' with capitalize=true
     * @throws NoSuchFieldException
     *             indicating a severe problem in the used model
     * @throws SecurityException
     *             if the field cannot be accessed for any reason
     */
    public String resolveIdGetter(Class<?> pojoClass, Map<String, Object> fieldMap, boolean byObjectReference,
        String component) throws NoSuchFieldException, SecurityException {

        return "get"
            + resolveIdVariableNameOrSetterGetterSuffix(pojoClass, fieldMap, byObjectReference, true, component) + "()";
    }

    /**
     * same as {@link #resolveIdGetter(Map, boolean, String)} but with byObjectReference=false and
     * component=""
     *
     * @param field
     *            the field
     * @return 'get' + {@link #resolveIdVariableNameOrSetterGetterSuffix(Map,boolean,boolean,String)} + '()'
     *         with capitalize=true
     */
    public String resolveIdGetter(Map<String, Object> field) {

        return this.resolveIdGetter(field, false, "");
    }

    /**
     * same as {@link #resolveIdGetter(Class,Map,boolean,String)} but with byObjectReference=false and
     * component=""
     *
     * @param pojoClass
     *            the class object of the pojo
     * @param fieldMap
     *            the field as Map&lt;String, Object>
     * @return 'get' + {@link #resolveIdVariableNameOrSetterGetterSuffix(Map,boolean,boolean,String)} + '()'
     *         with capitalize=true
     * @throws NoSuchFieldException
     *             indicating a severe problem in the used model
     * @throws SecurityException
     *             if the field cannot be accessed for any reason
     */
    public String resolveIdGetter(Class<?> pojoClass, Map<String, Object> fieldMap)
        throws NoSuchFieldException, SecurityException {

        return resolveIdGetter(pojoClass, fieldMap, false, "");
    }

    /**
     * Determines the ID setter for a given 'field' dependent on whether the setter should access the ID via
     * an object reference or a direct ID setter. In contrast to resolveIdGetter, this function does not
     * generate the function parenthesis to enable parameter declaration.
     *
     * @param field
     *            the field
     * @param byObjectReference
     *            boolean
     * @param component
     *            the OASP4j component name
     * @return 'set' + {@link #resolveIdVariableNameOrSetterGetterSuffix(Map, boolean, boolean, String)} with
     *         capitalize=true
     */
    public String resolveIdSetter(Map<String, Object> field, boolean byObjectReference, String component) {

        return "set" + resolveIdVariableNameOrSetterGetterSuffix(field, byObjectReference, true, component);
    }

    /**
     * same as {@link #resolveIdSetter(Map, boolean, String)} but with byObjectReference=false and
     * component=""
     *
     * @param field
     *            the field
     * @return 'set' + {@link #resolveIdVariableNameOrSetterGetterSuffix(Map, boolean, boolean, String)} with
     *         capitalize=true
     */
    public String resolveIdSetter(Map<String, Object> field) {

        return this.resolveIdSetter(field, false, "");
    }

    /**
     * Determines the ID setter for a given 'field' dependent on whether the setter should access the ID via
     * an object reference or a direct ID setter. In contrast to resolveIdGetter, this function does not
     * generate the function parenthesis to enable parameter declaration.
     *
     * @param pojoClass
     *            the class object of the pojo
     * @param fieldMap
     *            the field as Map&lt;String, Object>
     * @param byObjectReference
     *            boolean
     * @param component
     *            the OASP4j component name
     * @return 'set'+ {@link #resolveIdVariableNameOrSetterGetterSuffix(Map,boolean,boolean,String)} with
     *         capitalize=true
     * @throws NoSuchFieldException
     *             indicating a severe problem in the used model
     * @throws SecurityException
     *             if the field cannot be accessed for any reason
     */
    public String resolveIdSetter(Class<?> pojoClass, Map<String, Object> fieldMap, boolean byObjectReference,
        String component) throws NoSuchFieldException, SecurityException {

        return "set"
            + resolveIdVariableNameOrSetterGetterSuffix(pojoClass, fieldMap, byObjectReference, true, component);
    }

    /**
     * same as {@link #resolveIdSetter(Class,Map,boolean,String)} but with byObjectReference=false and
     * component=""
     *
     * @param pojoClass
     *            the class object of the pojo
     * @param fieldMap
     *            the field as Map&lt;String, Object>
     * @return 'set' + {@link #resolveIdVariableNameOrSetterGetterSuffix(Map,boolean,boolean,String)} with
     *         capitalize=true
     * @throws NoSuchFieldException
     *             indicating a severe problem in the used model
     * @throws SecurityException
     *             if the field cannot be accessed for any reason
     */
    public String resolveIdSetter(Class<?> pojoClass, Map<String, Object> fieldMap)
        throws NoSuchFieldException, SecurityException {

        return resolveIdSetter(pojoClass, fieldMap, false, "");
    }

    /**
     * Determines the variable name for the id value of the 'field'
     *
     * @param field
     *            the field
     * @return {@link #resolveIdVariableNameOrSetterGetterSuffix(Map, boolean, boolean, String)}) with
     *         byObjectReference=false, capitalize=false and component=""
     */
    public String resolveIdVariableName(Map<String, Object> field) {

        // the component is passed down as an empty string since byObjectReference is false and therefore the
        // component is
        // never touched
        return resolveIdVariableNameOrSetterGetterSuffix(field, false, false, "");
    }

    /**
     * Determines the variable name for the id value of the specified field in the pojo
     *
     * @param pojoClass
     *            the class object of the pojo
     * @param fieldMap
     *            the field as Map&lt;String, Object>
     * @return {@link #resolveIdVariableNameOrSetterGetterSuffix(Class, Map, boolean, boolean, String)}) with
     *         byObjectReference=false, capitalize=false and component=""
     * @throws NoSuchFieldException
     *             indicating a severe problem in the used model
     * @throws SecurityException
     *             if the field cannot be accessed for any reason
     */
    public String resolveIdVariableName(Class<?> pojoClass, Map<String, Object> fieldMap)
        throws NoSuchFieldException, SecurityException {

        // the component is passed down as an empty string since byObjectReference is false and therefore the
        // component is
        // never touched
        return resolveIdVariableNameOrSetterGetterSuffix(pojoClass, fieldMap, false, false, "");
    }

    /**
     * Determines the ID setter/getter suffix for a given 'field' dependent on whether the setter/getter
     * should access the ID via an object reference or a direct ID setter/getter
     *
     * @param field
     *            the field
     * @param byObjectReference
     *            boolean
     * @param capitalize
     *            if the field name should be capitalized
     * @param component
     *            the oasp component. Only needed if $byObjectReference is true
     * @return idVariable name or getter/setter suffix
     */
    public String resolveIdVariableNameOrSetterGetterSuffix(Map<String, Object> field, boolean byObjectReference,
        boolean capitalize, String component) {

        String fieldName = (String) field.get(Field.NAME.toString());
        if (capitalize) {
            fieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        }
        String suffix = "";

        String fieldType = (String) field.get(Field.TYPE.toString());
        String fieldCType = (String) field.get(Field.CANONICAL_TYPE.toString());
        if (fieldType.contains("Entity")) {
            if (fieldCType.startsWith("java.util.List") || fieldCType.startsWith("java.util.Set")) {
                suffix = "Ids";
                if (fieldName.endsWith("s")) {
                    // Assume trailing 's' as indicator for a plural
                    fieldName = fieldName.substring(0, fieldName.length() - 1);
                }
            } else {
                suffix = "Id";
            }
            if (byObjectReference && isEntityInComponent(fieldCType, component)) {
                // direct references for Entities in same component, so get id of the object reference
                suffix = "().getId";
            }
        }

        return fieldName + suffix;

    }

    /**
     * Determines the ID setter/getter suffix for a given 'field' dependent on whether the setter/getter
     * should access the ID via an object reference or a direct ID setter/getter
     *
     * @param pojoClass
     *            the {@link Class} object of the pojo
     * @param fieldMap
     *            the field as Map&lt;String, Object>
     * @param byObjectReference
     *            boolean
     * @param capitalize
     *            if the field name should be capitalized
     * @param component
     *            the oasp component. Only needed if byObjectReference is true
     * @return idVariable name or getter/setter suffix
     * @throws NoSuchFieldException
     *             indicating a severe problem in the used model
     * @throws SecurityException
     *             if the field cannot be accessed for any reason
     */
    public String resolveIdVariableNameOrSetterGetterSuffix(Class<?> pojoClass, Map<String, Object> fieldMap,
        boolean byObjectReference, boolean capitalize, String component)
        throws NoSuchFieldException, SecurityException {

        String resultName = (String) fieldMap.get(Field.NAME.toString());
        if (capitalize) {
            resultName = resultName.substring(0, 1).toUpperCase() + resultName.substring(1);
        }
        String suffix = "";
        String fieldType = (String) fieldMap.get(Field.TYPE.toString());
        String fieldName = (String) fieldMap.get(Field.NAME.toString());
        if (fieldType.contains("Entity")) {
            if (Collection.class.isAssignableFrom(pojoClass.getDeclaredField(fieldName).getType())) {
                suffix = "Ids";
                if (resultName.endsWith("s")) {
                    // Assume trailing 's' as indicator for a plural
                    resultName = resultName.substring(0, resultName.length() - 1);
                }
            } else {
                suffix = "Id";
            }
            if (byObjectReference
                && isEntityInComponent(pojoClass.getDeclaredField(fieldName).getType().getName(), component)) {
                // direct references for Entities in same component, so get id of the object reference
                suffix = "().getId";
            }
        }

        return resultName + suffix;

    }

    /**
     * Converts all occurrences of OASP Entities types in the given 'field' simple type (possibly generic) to
     * Longs
     *
     * @param field
     *            the field
     * @return the field type as String. If field type contains 'Entity' the result is the field type under
     *         the regex /[^<>]+Entity/Long/
     */
    public String getSimpleEntityTypeAsLongReference(Map<String, Object> field) {

        String fieldType = (String) field.get(Field.TYPE.toString());
        if (fieldType.contains("Entity")) {
            fieldType = fieldType.replaceAll("[^<>]+Entity", "Long");
        }
        return fieldType;
    }

    /**
     * Jaime testing method
     */

    public String getMultiplicityContent(Object source, Object target, String className) {
        DeferredElementNSImpl sourceNode = (DeferredElementNSImpl) source;
        DeferredElementNSImpl targetNode = (DeferredElementNSImpl) target;
        String textContent = "";

        // returnValue = returnValue + "\nAttribute: " + deferredElement.getAttribute("xmi:idref");
        // returnValue = returnValue + "\nLocalName: " + deferredElement.getLocalName();
        // returnValue = returnValue + "\nNodeName: " + deferredElement.getNodeName();

        NodeList childs = sourceNode.getChildNodes();
        // Check if source is className
        for (int i = 0; i < childs.getLength(); i++) {
            Node childElement = childs.item(i);
            // Get child model
            if (childElement.getNodeName().equals("model")) {
                // Get model attributes
                NamedNodeMap attrs = childElement.getAttributes();
                for (int j = 0; j < attrs.getLength(); j++) {
                    Attr attribute = (Attr) attrs.item(j);
                    // This is for every type of connector
                    // Get name attribute and check if it is className
                    if (attribute.getName().equals("name")) {
                        if (attribute.getValue().equals(className)) {
                            textContent = getContent(targetNode);
                            return textContent;
                        }
                    }
                }
            }
        }

        childs = targetNode.getChildNodes();
        // Check if source is className
        for (int i = 0; i < childs.getLength(); i++) {
            Node childElement = childs.item(i);
            // Get child model
            if (childElement.getNodeName().equals("model")) {
                // Get model attributes
                NamedNodeMap attrs = childElement.getAttributes();
                for (int j = 0; j < attrs.getLength(); j++) {
                    Attr attribute = (Attr) attrs.item(j);
                    // This is for every type of connector
                    // Get name attribute and check if it is className
                    if (attribute.getName().equals("name")) {
                        if (attribute.getValue().equals(className)) {
                            textContent = getContent(sourceNode);
                            return textContent;
                        }
                    }
                }
            }
        }

        return textContent;
    }

    /**
     * @param connectedNode
     * @return
     */
    private String getContent(DeferredElementNSImpl connectedNode) {
        String connectedClassName = "Error Name";
        String multiplicity = "1";
        String content = "";

        NodeList childs = connectedNode.getChildNodes();
        // Get target class name
        for (int i = 0; i < childs.getLength(); i++) {
            Node childElement = childs.item(i);
            // Get child model
            if (childElement.getNodeName().equals("model")) {
                // Get model attributes
                NamedNodeMap attrs = childElement.getAttributes();
                for (int j = 0; j < attrs.getLength(); j++) {
                    Attr attribute = (Attr) attrs.item(j);
                    // This is for every type of connector
                    // Get name attribute
                    if (attribute.getName().equals("name")) {
                        connectedClassName = attribute.getValue();
                        break;
                    }
                }
            }
            // Get child type
            if (childElement.getNodeName().equals("type")) {
                // Get model attributes
                NamedNodeMap attrs = childElement.getAttributes();
                for (int j = 0; j < attrs.getLength(); j++) {
                    Attr attribute = (Attr) attrs.item(j);
                    // This is for every type of connector
                    // Get multiplicity attribute
                    if (attribute.getName().equals("multiplicity")) {
                        multiplicity = attribute.getValue();
                        break;
                    }
                }
            }
        }

        if (multiplicity.equals("1")) {
            content = "\t\t// I want one" + "\n\tprivate " + connectedClassName + " " + connectedClassName.toLowerCase()
                + ";" + "\n\t@Override" + "\n\tpublic " + connectedClassName + " get" + connectedClassName + "(){"
                + "\n\t\treturn this." + connectedClassName.toLowerCase() + ";" + "\n\t}" + "\n\t@Override"
                + "\n\tpublic void set" + connectedClassName + "(" + connectedClassName + " "
                + connectedClassName.toLowerCase() + "){" + "\n\t\tthis." + connectedClassName.toLowerCase() + " = "
                + connectedClassName.toLowerCase() + ";" + "\n\t}";
        }

        if (multiplicity.equals("*")) {
            removePlural(connectedClassName);

            content = "\t\t// I want many" + "\n\tprivate List<" + connectedClassName + "> "
                + removePlural(connectedClassName.toLowerCase()) + "s;" + "\n\tpublic List<" + connectedClassName
                + "> get" + removePlural(connectedClassName) + "s(){" + "\n\t\treturn this."
                + removePlural(connectedClassName.toLowerCase()) + "s;" + "\n\t}" + "\n\tpublic void set"
                + removePlural(connectedClassName) + "s(List<" + connectedClassName + "> "
                + removePlural(connectedClassName.toLowerCase()) + "s){" + "\n\t\tthis."
                + removePlural(connectedClassName.toLowerCase()) + "s = "
                + removePlural(connectedClassName.toLowerCase()) + "s;" + "\n\t}";
        }
        return content;
    }

    /**
     * @param targetClassName
     * @return
     */
    private String removePlural(String targetClassName) {
        // Remove last 's' for Many multiplicity
        if (targetClassName.charAt(targetClassName.length() - 1) == 's') {
            targetClassName = targetClassName.substring(0, targetClassName.length() - 1);
        }
        return targetClassName;
    }
}