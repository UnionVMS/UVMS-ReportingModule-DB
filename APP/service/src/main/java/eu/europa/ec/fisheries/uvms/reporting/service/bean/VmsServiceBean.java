package eu.europa.ec.fisheries.uvms.reporting.service.bean;

import eu.europa.ec.fisheries.schema.movement.search.v1.MovementMapResponseType;
import eu.europa.ec.fisheries.uvms.common.AuditActionEnum;
import eu.europa.ec.fisheries.uvms.exception.ProcessorException;
import eu.europa.ec.fisheries.uvms.reporting.message.mapper.ExtMovementMessageMapper;
import eu.europa.ec.fisheries.uvms.reporting.message.mapper.ExtAssetMessageMapper;
import eu.europa.ec.fisheries.uvms.reporting.model.exception.ReportingServiceException;
import eu.europa.ec.fisheries.uvms.reporting.service.dto.VmsDTO;
import eu.europa.ec.fisheries.uvms.reporting.service.entities.Report;
import eu.europa.ec.fisheries.uvms.reporting.service.mapper.FilterProcessor;
import eu.europa.ec.fisheries.uvms.service.interceptor.IAuditInterceptor;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.AreaIdentifierType;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.helpers.MessageFormatter;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

@Stateless
@Local(value = VmsService.class)
@Slf4j
public class VmsServiceBean implements VmsService {

    @EJB
    private ReportRepository repository;

    @EJB
    private AssetServiceBean assetModule;

    @EJB
    private MovementServiceBean movementModule;
    
    @EJB
    private SpatialService spatialModule;

    @Override
    @Transactional
    @IAuditInterceptor(auditActionType = AuditActionEnum.EXECUTE)
    public VmsDTO getVmsDataByReportId(final String username, final String scopeName, final Long id) throws ReportingServiceException {
        log.debug("[START] getVmsDataByReportId({}, {}, {})", username, scopeName, id);

        Report reportByReportId = repository.findReportByReportId(id, username, scopeName);

        if (reportByReportId == null) {

            String error = MessageFormatter.arrayFormat("No report found with id {}", new Object[]{id}).getMessage();

            log.error(error);

            throw new ReportingServiceException(error);

        }

        VmsDTO vmsDto;

        try {

            Map<String, Asset> assetMap;

            FilterProcessor processor = new FilterProcessor(reportByReportId.getFilters());

            addAreaCriteriaToProcessor(processor);

            Collection<MovementMapResponseType> movementMap;

            Map<String, MovementMapResponseType> responseTypeMap;

            log.debug("Running report {} assets or asset groups.", processor.hasAssetsOrAssetGroups()?"has":"doesn't have");

            if (processor.hasAssetsOrAssetGroups()) {

                assetMap = assetModule.getAssetMap(processor);

                processor.getMovementListCriteria().addAll(ExtMovementMessageMapper.movementListCriteria(assetMap.keySet()));

                movementMap = movementModule.getMovement(processor);

            } else {

                responseTypeMap = movementModule.getMovementMap(processor);

                Set<String> assetGuids = responseTypeMap.keySet();

                movementMap = responseTypeMap.values();

                processor.getAssetListCriteriaPairs().addAll(ExtAssetMessageMapper.assetCriteria(assetGuids));

                assetMap = assetModule.getAssetMap(processor);
            }

            vmsDto = new VmsDTO(assetMap, movementMap);

            reportByReportId.updateExecutionLog(username);


        } catch (ProcessorException e) {

            String error = "Error while processing reporting filters";

            log.error(error, e);

            throw new ReportingServiceException(error, e);

        }

        log.debug("[END] getVmsDataByReportId(...)");
        return vmsDto;

    }

    private void addAreaCriteriaToProcessor(FilterProcessor processor) throws ReportingServiceException {

        final Set<AreaIdentifierType> areaIdentifierList = processor.getAreaIdentifierList();

        if (isNotEmpty(areaIdentifierList)) {

    		String areaWkt = getFilterArea(areaIdentifierList);

        	processor.addAreaCriteria(areaWkt);

    	}

    }
    
	private String getFilterArea(Set<AreaIdentifierType> areaIdentifierList) throws ReportingServiceException {

		try {

            List<AreaIdentifierType> areaIdentifierTypeList = new ArrayList<>();

            areaIdentifierTypeList.addAll(areaIdentifierList);

			return spatialModule.getFilterArea(areaIdentifierTypeList);

		} catch (ReportingServiceException e) {

            String error = "Exception during retrieving filter area";

            log.error(error, e);

			throw new ReportingServiceException(error, e);

        }

	}

}