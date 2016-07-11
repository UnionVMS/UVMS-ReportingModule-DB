/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.uvms.reporting.service.bean;

import eu.europa.ec.fisheries.schema.movement.search.v1.MovementMapResponseType;
import eu.europa.ec.fisheries.uvms.common.AuditActionEnum;
import eu.europa.ec.fisheries.uvms.common.DateUtils;
import eu.europa.ec.fisheries.uvms.exception.ProcessorException;
import eu.europa.ec.fisheries.uvms.interceptors.TracingInterceptor;
import eu.europa.ec.fisheries.uvms.reporting.message.mapper.ExtAssetMessageMapper;
import eu.europa.ec.fisheries.uvms.reporting.message.mapper.ExtMovementMessageMapper;
import eu.europa.ec.fisheries.uvms.reporting.model.exception.ReportingServiceException;
import eu.europa.ec.fisheries.uvms.reporting.service.dto.VmsDTO;
import eu.europa.ec.fisheries.uvms.reporting.service.entities.Report;
import eu.europa.ec.fisheries.uvms.reporting.service.mapper.FilterProcessor;
import eu.europa.ec.fisheries.uvms.reporting.service.mapper.ReportMapperV2;
import eu.europa.ec.fisheries.uvms.rest.security.bean.USMService;
import eu.europa.ec.fisheries.uvms.service.interceptor.IAuditInterceptor;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.AreaIdentifierType;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.transaction.Transactional;
import java.util.*;

import static eu.europa.ec.fisheries.uvms.reporting.model.Constants.*;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

@Stateless
@Local(value = VmsService.class)
@Slf4j
@Interceptors(TracingInterceptor.class)
public class VmsServiceBean implements VmsService {

    private @EJB ReportRepository repository;
    private @EJB AuditService auditService;
    private @EJB AssetServiceBean assetModule;
    private @EJB MovementServiceBean movementModule;
    private @EJB SpatialService spatialModule;
    private @EJB USMService usmService;

    @Override
    @Transactional
    @IAuditInterceptor(auditActionType = AuditActionEnum.EXECUTE)
    public VmsDTO getVmsDataByReportId(final String username, final String scopeName, final Long id, final List<AreaIdentifierType> areaRestrictions, final DateTime now, Boolean isAdmin) throws ReportingServiceException {

        log.debug("[START] getVmsDataByReportId({}, {}, {})", username, scopeName, id);
        Report reportByReportId = repository.findReportByReportId(id, username, scopeName, isAdmin);

        if (reportByReportId == null) {
            final String error = "No report found with id " + id;
            log.error(error);
            throw new ReportingServiceException(error);
        }

        VmsDTO vmsDto = getVmsData(reportByReportId, areaRestrictions, now);
        reportByReportId.updateExecutionLog(username);
        log.debug("[END] getVmsDataByReportId(...)");
        return vmsDto;
    }

    @Override
    public VmsDTO getVmsDataBy(final eu.europa.ec.fisheries.uvms.reporting.model.vms.Report report, final List<AreaIdentifierType> areaRestrictions) throws ReportingServiceException {

        Map additionalProperties = (Map) report.getAdditionalProperties().get(ADDITIONAL_PROPERTIES);
        DateTime dateTime = DateUtils.UI_FORMATTER.parseDateTime((String) additionalProperties.get(TIMESTAMP));
        Report toReport = ReportMapperV2.INSTANCE.reportDtoToReport(report);
        VmsDTO vmsData = getVmsData(toReport, areaRestrictions, dateTime);
        auditService.sendAuditReport(AuditActionEnum.EXECUTE, report.getName());
        return vmsData;
    }

    private VmsDTO getVmsData(Report report, List<AreaIdentifierType> scopeAreaIdentifierList, DateTime dateTime) throws ReportingServiceException {

        try {

            Collection<MovementMapResponseType> movementMap;
            Map<String, MovementMapResponseType> responseTypeMap;
            Map<String, Asset> assetMap;
            FilterProcessor processor = new FilterProcessor(report.getFilters(), dateTime);
            final Set<AreaIdentifierType> areaIdentifierList = processor.getAreaIdentifierList();

            log.debug("Running report {} assets or asset groups.", processor.hasAssetsOrAssetGroups() ? "has" : "doesn't have");

            //We are blocking call to spatial to not make unnecessary JMS traffic and calculations
            if (isNotEmpty(areaIdentifierList) || isNotEmpty(scopeAreaIdentifierList)) {
                HashSet<AreaIdentifierType> areaIdentifierTypes = null;
                if (isNotEmpty(scopeAreaIdentifierList)){
                    areaIdentifierTypes = new HashSet<>(scopeAreaIdentifierList);
                }
                String areaWkt = spatialModule.getFilterArea(areaIdentifierTypes, areaIdentifierList);
                processor.addAreaCriteria(areaWkt);
            }

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

            return new VmsDTO(assetMap, movementMap);

        } catch (ProcessorException e) {
            String error = "Error while processing reporting filters";
            log.error(error, e);
            throw new ReportingServiceException(error, e);
        } catch (ReportingServiceException e) {
            String error = "Exception during retrieving filter area";
            log.error(error, e);
            throw new ReportingServiceException(error, e);
        }
    }
}