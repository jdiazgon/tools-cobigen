<#ftl ns_prefixes={"xmi":"http://schema.omg.org/spec/XMI/2.1"}>
<#compress>
import javax.dataaccess.MappedSuperclass;

import ${variables.rootPackage}.general.common.api.ApplicationEntity;

import io.oasp.module.jpa.dataaccess.api.AbstractPersistenceEntity;

/**
 * Abstract Entity for all Entities with an id and a version field.
 */
@MappedSuperclass
public abstract class ApplicationPersistenceEntity extends AbstractPersistenceEntity implements ApplicationEntity {

  private static final long serialVersionUID = 1L;

  /**
   * The constructor.
   */
  public ApplicationPersistenceEntity() {

    super();
  }

}

</#compress>