package eu.europa.ec.fisheries.uvms.reporting.service.dao;

import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.reporting.service.entities.Filter;
import eu.europa.ec.fisheries.uvms.service.AbstractDAO;

import javax.persistence.EntityManager;
import java.util.List;

import static eu.europa.ec.fisheries.uvms.reporting.service.entities.Filter.DELETE_BY_ID;
import static eu.europa.ec.fisheries.uvms.reporting.service.entities.Filter.LIST_BY_REPORT_ID;
import static eu.europa.ec.fisheries.uvms.service.QueryParameter.with;

public class FilterDAO extends AbstractDAO<Filter> {

    private EntityManager em;

    public FilterDAO(EntityManager em){
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public List<Filter> listByReportId(final Long reportId) throws ServiceException {
        return findEntityByNamedQuery(Filter.class, LIST_BY_REPORT_ID, with("reportId", reportId).parameters());
    }

    public void deleteBy(final Long id) throws ServiceException {
        deleteEntityByNamedQuery(Filter.class, DELETE_BY_ID, with("id", id).parameters());
    }
}
