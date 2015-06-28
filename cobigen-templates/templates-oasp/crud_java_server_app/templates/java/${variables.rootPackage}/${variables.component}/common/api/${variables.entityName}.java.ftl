<#include '/functions.ftl'>
package ${variables.rootPackage}.${variables.component}.common.api;

import ${variables.rootPackage}.general.common.api.ApplicationEntity;

public interface ${variables.entityName} extends ApplicationEntity {

<#list pojo.fields as field>
<#if field.type?contains("Entity")> <#-- add ID getter & setter for Entity references only for ID references -->
	/**
   * Returns the {@link ${variables.entityName}}s ID.
   *
   * @return the {@link ${variables.entityName}}s ID
   */
	${getSimpleEntityTypeAsLongReference(field)} ${resolveIdGetter(field)};
	
	<#assign idVar = resolveIdVariableName(field)>
	/**
	* Sets a new {@link ${variables.entityName}} with the given ID.
	* 
	* @param ${idVar} of the {@link ${variables.entityName}} to be set
	*/
	void ${resolveIdSetter(field)}(${getSimpleEntityTypeAsLongReference(field)} ${idVar});
<#else>
	/**
   * Returns the field ${field.name}.
   *
   * @return the field ${field.name}.
   */
   	${field.type} get${field.name?cap_first}();

	/**
	* Sets the new value for the field ${field.name}.
	* 
	* @param ${field.name} new value for the field ${field.name}.
	*/
   	void set${field.name?cap_first}(${field.type} ${field.name});
</#if>
</#list>

}