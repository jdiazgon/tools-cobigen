<#-- ---------------------------------------- -->
<#-- GENERAL JAVA SPECIFIC FUNCTIONS & MACROS -->
<#-- ---------------------------------------- -->

<#function getType simpleType>
  <#-- simpleType = sequence + hash -->
  <#-- google-Tip: This XML query result can't be used as string because for that it had to contain exactly 1 XML node, but it contains 0 nodes. -->
  <#return JavaUtil.getSimpleType(simpleType)>
  <#--  <#return "number">  -->
</#function>

<#-- -------------------- -->
<#-- SPECIFIC MACROS -->
<#-- -------------------- -->

<#-- Adds the input fields for the filter with types -->
<#macro getNG2Type_Grid_Search>
  <#list elemDoc["self::node()/ownedAttribute"] as field>
      <md-input-container style="width:100%;">
        <input mdInput name ="${field["@name"]}" type="${getType(field["@type"])}" ngModel [placeholder]= "'${variables.component}datagrid.columns.${field["@name"]}' | translate">
      </md-input-container>
   </#list>
</#macro>

<#-- Adds the input fields for the New Element dialog with types -->
<#macro getNG2Type_Add_Dialog>
  <#list elemDoc["self::node()/ownedAttribute"] as field>
    <md-input-container style="width:100%;">
      <input mdInput type="${getType(elemDoc["self::node()/ownedAttribute/xmi:idref"])}" required>
    </md-input-container>
   </#list>
</#macro>
 <#-- name = "${field["@type"]}" [placeholder]= "'${variables.component}datagrid.columns.${field["@name"]}' | translate" [(ngModel)] = "items.${field["@name"]}"  -->
