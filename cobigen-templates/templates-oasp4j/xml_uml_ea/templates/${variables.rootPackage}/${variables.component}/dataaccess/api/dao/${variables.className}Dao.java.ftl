<#ftl ns_prefixes={"xmi":"http://schema.omg.org/spec/XMI/2.1"}>
<#compress>
<#assign name = elemDoc["self::node()/@name"]>
<#assign connectors = doc["xmi:XMI/xmi:Extension/connectors/connector"]>
package com.capgemini.${variables.rootPackage}.${variables.component}.dataaccess.api.dao;


import com.capgemini.${variables.rootPackage}.general.dataaccess.api.dao.ApplicationDao;
import com.capgemini.${variables.rootPackage}.${variables.component}.dataaccess.api.${variables.className}Entity;
import com.capgemini.${variables.rootPackage}.${variables.component}.logic.api.to.${variables.className}SearchCriteriaTo;
import io.oasp.module.jpa.common.api.to.PaginatedListTo;


/**
 * Data access interface for ${variables.className} entities
 */
public interface ${variables.className}Dao extends ApplicationDao<${variables.className}Entity> {
  
  /**
   * Finds the {@link ${variables.className}Entity } matching the given {@link ${variables.className}SearchCriteriaTo}.
   *
   * @param criteria is the {@link ${variables.className}SearchCriteriaTo}.
   * @return the {@link PaginatedListTo} with the matching {@link ${variables.className}Entity} objects.
   */
  PaginatedListTo<${variables.className}Entity> find${OaspUtil.removePlural(variables.className)}s(${variables.className}SearchCriteriaTo criteria);
}

</#compress>