<#ftl ns_prefixes={"xmi":"http://schema.omg.org/spec/XMI/2.1"}>
<#compress>
<#assign name = elemDoc["self::node()/@name"]>
<#assign connectors = doc["xmi:XMI/xmi:Extension/connectors/connector"]>
package com.capgemini.${variables.rootPackage}.${variables.component};

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


@Entity
@Table(name=${variables.className})


  <#-- Decide if extends tag is needed -->
  <#assign boolean = false>
  <#list connectors as connector>
        <#-- I am source -->
        <#if (connector["source/model/@type"] == "Class")>
            <#-- If I am the source connector, check target's multiplicity -->
            <#if ((connector["source/model/@name"]) == '${variables.className}')>
              <#if (boolean == false)>
                <#assign ext = connector["target/model/@name"]>  
              </#if>
              <#assign boolean = true>
            </#if>
        </#if>
  </#list>
  <#if boolean = true>
    public class ${variables.className}Entity extends ${ext} implements ${variables.className} {
  <#else>
    public class ${variables.className}Entity implements ${variables.className} {  
  </#if>



    private static final long serialVersionUID = 1L;

    <#-- Class attributes -->
    <#list elemDoc["self::node()/ownedAttribute"] as attribute>
        <#if (attribute["@name"])??>
    ${attribute["@visibility"]} ${attribute["type/@xmi:idref"]?replace("EAJava_","")} ${attribute["@name"]};
        </#if>
    </#list>

    <#-- Class connections/associations -->
    <#list connectors as connector>
        <#assign targetName = connector["target/model/@name"]> 
        <#assign sourceName = connector["source/model/@name"]> 
        <#-- I am source -->
        <#if (connector["source/model/@type"] == "Class")>
            <#-- If I am the source connector, check target's multiplicity -->
            <#if ((sourceName) == '${variables.className}')>
                <#if (connector["target/type/@multiplicity"] )?is_string>
                    <#if (connector["target/type/@multiplicity"] == "1")>
    // I want one
    private ${targetName} ${targetName?uncap_first};
    @Override
    public ${targetName?cap_first} get${targetName?cap_first}(){
        return this.${targetName?uncap_first};
    }
    @Override
    public void set${targetName?cap_first}(${targetName?cap_first} ${targetName?uncap_first}){
       ${targetName?uncap_first} = this.${targetName?uncap_first};
    }
                    <#elseif (connector["target/type/@multiplicity"] == "*")>   
    // I want many
    private List<${targetName}> ${targetName?uncap_first?remove_ending("s")}s;

    public List<${targetName?cap_first}> get${targetName?cap_first?remove_ending("s")}s(){
        return this.${targetName?uncap_first};
    }

    public void set${targetName?cap_first?remove_ending("s")}s(List<${targetName?cap_first}> ${targetName?uncap_first?remove_ending("s")}s){
       ${targetName?uncap_first?remove_ending("s")}s = this.${targetName?uncap_first?remove_ending("s")}s;
    }
                    </#if>
                </#if>
            </#if>
        </#if>
        <#-- I am target -->
        <#if (connector["target/model/@type"] == "Class")>
            <#-- If I am the target connector, check sources' multiplicity -->
            <#if ((targetName) == '${variables.className}')>
                <#if (connector["source/type/@multiplicity"] )?is_string>
                    <#if (connector["source/type/@multiplicity"] == "1")>
    // I want one
    private ${sourceName} ${sourceName?uncap_first};
    @Override
    public ${sourceName?cap_first} get${sourceName?cap_first}(){
        return this.${sourceName?uncap_first};
    }
    @Override
    public void set${sourceName?cap_first}(${sourceName?cap_first} ${sourceName?uncap_first}){
       ${sourceName?uncap_first} = this.${sourceName?uncap_first};
    }
                    <#elseif (connector["source/type/@multiplicity"] == "*")>   
    // I want many
    private List<${sourceName}> ${sourceName?uncap_first?remove_ending("s")}s;

    public List<${sourceName?cap_first}> get${sourceName?cap_first?remove_ending("s")}s(){
        return this.${sourceName?uncap_first?remove_ending("s")}s;
    }

    public void set${sourceName?cap_first?remove_ending("s")}s(List<${sourceName?cap_first}> ${sourceName?uncap_first?remove_ending("s")}s){
       ${sourceName?uncap_first?remove_ending("s")}s = this.${sourceName?uncap_first?remove_ending("s")}s;
    }
                    </#if>
                </#if>
            </#if>
        </#if>
    </#list>

    
    <#list elemDoc["self::node()/ownedAttribute"] as attribute>
        <#if (attribute["@name"])??>
    @Override
            <#if (attribute["type/@xmi:idref"]) == "EAJava_int">
    public Integer get${attribute["@name"]?cap_first}(){
            <#else>    
    public ${(attribute["type/@xmi:idref"]?replace("EAJava_",""))?cap_first} get${attribute["@name"]?capitalize}(){
            </#if>
        return this.${attribute["@name"]};
    }
            <#if (attribute["type/@xmi:idref"]) == "EAJava_int">
    public void set${attribute["@name"]?cap_first}(Integer ${attribute["@name"]}){
            <#else>    
    public void set${attribute["@name"]?cap_first}(${(attribute["type/@xmi:idref"]?replace("EAJava_",""))?capitalize}){
            </#if>
        this.${attribute["@name"]} = ${attribute["@name"]};
    }
        </#if>
    </#list>
}

</#compress>