<#ftl ns_prefixes={"xmi":"http://schema.omg.org/spec/XMI/2.1"}>
<#compress>
<#assign name = elemDoc["/packagedElement/@name"]>
<#assign connectors = doc["xmi:XMI/xmi:Extension/connectors/connector"]>

//package?

//imports?
import io.oasp.gastronomy.restaurant.general.dataaccess.api.dao.ApplicationRevisionedDao;
import io.oasp.gastronomy.restaurant.offermanagement.dataaccess.api.DrinkEntity;
import io.oasp.module.jpa.dataaccess.api.MasterDataDao;

public interface ${variables.className}Dao extends ApplicationRevisionedDao<${variables.className}Entity>, MasterDataDao<${variables.className}Entity> {



}

</#compress>