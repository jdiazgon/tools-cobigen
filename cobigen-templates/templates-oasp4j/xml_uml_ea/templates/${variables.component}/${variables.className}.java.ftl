<#ftl ns_prefixes={"xmi":"http://schema.omg.org/spec/XMI/2.1", "uml":"http://schema.omg.org/spec/UML/2.1"}>
<#compress>
<#assign name = elemDoc["self::node()/@name"]>

${packagedElement._at_visibility} ${name}
</#compress>
