/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.uvms.reporting.service.bean;

import eu.europa.ec.fisheries.uvms.common.AuditActionEnum;
import eu.europa.ec.fisheries.uvms.common.DateUtils;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.reporting.model.ReportFeatureEnum;
import eu.europa.ec.fisheries.uvms.reporting.model.VisibilityEnum;
import eu.europa.ec.fisheries.uvms.reporting.model.exception.ReportingServiceException;
import eu.europa.ec.fisheries.uvms.reporting.model.schemas.ReportGetStartAndEndDateRS;
import eu.europa.ec.fisheries.uvms.reporting.security.AuthorizationCheckUtil;
import eu.europa.ec.fisheries.uvms.reporting.service.dto.FilterDTO;
import eu.europa.ec.fisheries.uvms.reporting.service.dto.MapConfigurationDTO;
import eu.europa.ec.fisheries.uvms.reporting.service.dto.ReportDTO;
import eu.europa.ec.fisheries.uvms.reporting.service.entities.CommonFilter;
import eu.europa.ec.fisheries.uvms.reporting.service.entities.Filter;
import eu.europa.ec.fisheries.uvms.reporting.service.entities.Report;
import eu.europa.ec.fisheries.uvms.reporting.service.mapper.ReportDateMapper;
import eu.europa.ec.fisheries.uvms.reporting.service.mapper.ReportMapper;
import eu.europa.ec.fisheries.uvms.service.interceptor.IAuditInterceptor;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.transaction.Transactional;
import java.util.*;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Session Bean implementation class ReportBean
 */
@Stateless
@LocalBean
public class ReportServiceBean {

    private @EJB ReportRepository repository;
    private @EJB SpatialService spatialModule;
    private ReportMapper reportMapper;

    @PostConstruct
    public void init() {
        reportMapper = new ReportMapper();
    }
    @IAuditInterceptor(auditActionType = AuditActionEnum.CREATE)
    @Transactional
    public ReportDTO create(ReportDTO report) throws ReportingServiceException {
        ReportDTO reportDTO = saveReport(report);

        saveMapConfiguration(reportDTO.getId(), report.getWithMap(), report.getMapConfiguration());

        return reportDTO;
    }

    public ReportDTO saveReport(ReportDTO report) {
        try {
            ReportMapper mapper = ReportMapper.ReportMapperBuilder().filters(true).build();
            Report reportEntity = mapper.reportDTOToReport(report);
            reportEntity = repository.createEntity(reportEntity); // TODO @Greg mapping in repository
            ReportDTO reportDTO = mapper.reportToReportDTO(reportEntity);
            return reportDTO;
        } catch (Exception e) {
            throw new RuntimeException("Error during the creation of the report", e);
        }
    }

    @Transactional
    public ReportDTO findById(final Set<String> features, Long id, String username, String scopeName, Boolean isAdmin, List<String> permittedServiceLayers) {

        ReportDTO reportDTO;

        try {
            ReportMapper mapper = ReportMapper.ReportMapperBuilder().features(features).currentUser(username).filters(true).build();
            Report reportByReportId = repository.findReportByReportId(id, username, scopeName, isAdmin);
            reportDTO =  mapper.reportToReportDTO(reportByReportId);

            if (reportDTO != null && reportDTO.getWithMap()) {
                MapConfigurationDTO mapConfiguratioDTO = spatialModule.getMapConfiguration(id, permittedServiceLayers);
                reportDTO.setMapConfiguration(mapConfiguratioDTO);
            }
        }

        catch (Exception e){
            throw new RuntimeException("Error during reading map configuration in spatial module");//FIXME @Greg investigate runtime is there not a cleaner way?
        }

        return reportDTO;
    }

    @IAuditInterceptor(auditActionType = AuditActionEnum.MODIFY)
    @Transactional
    public ReportDTO update(final ReportDTO report, Boolean oldWithMapValue, MapConfigurationDTO oldMapConfigurationDTO) throws ReportingServiceException, ServiceException {

        if (report == null) {
            throw new IllegalArgumentException("REPORT CAN NOT BE NULL");
        }

        repository.update(report);

        updateMapConfiguration(report.getId(), report.getWithMap(), report.getMapConfiguration(), oldWithMapValue, oldMapConfigurationDTO);

        Report reportByReportId = repository.findReportByReportId(report.getId());
        ReportDTO reportDTO = reportMapper.reportToReportDTO(reportByReportId);
        List<FilterDTO> filterDTOs = reportMapper.filterSetToFilterDTOSet(reportByReportId.getFilters());
        reportDTO.setFilters(filterDTOs);
        return reportDTO;
    }



    @IAuditInterceptor(auditActionType = AuditActionEnum.DELETE)
    @Transactional
    public void delete(Long reportId, String username, String scopeName, Boolean isAdmin) throws ReportingServiceException {
        repository.remove(reportId, username, scopeName, isAdmin);
    }

    /**
     * <p>This API search for Reports created by the User or shared to the user scope or
     * a public Reports those are allowed to the logged in User. In case of the user is Admin then
     * All the Reports are accessible.
     * </p>
     * @param features Usm features
     * @param username User Name
     * @param scopeName Scope Name
     * @param existent Include deleted report if true
     * @param defaultReportId default report Id stored in USM
     * @param numberOfReport Limit number of report. If null then no limit is set
     * @return List of Reports User is permited to see
     * @throws ReportingServiceException
     */
    @Transactional
    public Collection<ReportDTO> listByUsernameAndScope(final Set<String> features,
                                                        final String username,
                                                        final String scopeName,
                                                        final Boolean existent,
                                                        final Long defaultReportId,
                                                        final Integer numberOfReport) throws ReportingServiceException {

        ReportMapper mapper = ReportMapper.ReportMapperBuilder().features(features).currentUser(username).build();

        boolean isAdmin = AuthorizationCheckUtil.isAllowed(ReportFeatureEnum.MANAGE_ALL_REPORTS, features);

        List<Report> reports;
        if (numberOfReport != null) {
            reports = repository.listTopExecutedReportByUsernameAndScope(username, scopeName, existent, isAdmin, numberOfReport);
        } else {
            reports = repository.listByUsernameAndScope(username, scopeName, existent, isAdmin);
        }

        List<ReportDTO> toReportDTOs = new ArrayList<>();

        for (Report report : reports) {

            ReportDTO reportDTO = mapper.reportToReportDTO(report);
            if(reportDTO.getId().equals(defaultReportId)){
                reportDTO.setDefault(true);
            }
            toReportDTOs.add(reportDTO);
        }
        return toReportDTOs;
    }


    @IAuditInterceptor(auditActionType = AuditActionEnum.SHARE)
    @Transactional
    public void share(Long reportId, VisibilityEnum newVisibility, String username, String scopeName, Boolean isAdmin) throws ReportingServiceException {
        repository.changeVisibility(reportId, newVisibility, username, scopeName, isAdmin);
    }


    @Transactional
    public ReportGetStartAndEndDateRS getReportDates(String now, Long id, String userName, String scopeName) throws ReportingServiceException {
        Date currentDate = DateUtils.UI_FORMATTER.parseDateTime(now).toDate();
        Report report = repository.findReportByReportId(id, userName, scopeName, false);
        if (report != null) {
            Set<Filter> filters = report.getFilters();
            for (Filter filter : filters) {
                if (filter instanceof CommonFilter) {
                    ReportDateMapper reportDateMapper = ReportDateMapper.ReportDateMapperBuilder().now(currentDate).build();
                    return reportDateMapper.getReportDates((CommonFilter)filter);
                }
            }
        }
        return new ReportGetStartAndEndDateRS();
    }

    private void saveMapConfiguration(long reportId, Boolean withMap, MapConfigurationDTO mapConfiguration) throws ReportingServiceException {
        if (withMap) {
            try {
                if (mapConfiguration == null) {
                    throw new ReportingServiceException("When withMap is set to true you must specify mapConfiguration attributes.");
                }
                saveOrUpdateMapConfiguration(reportId, mapConfiguration);
            } catch (Exception e) {
                throw new RuntimeException("Error during the creation of the map configuration");
            }
        }
    }

    private void saveOrUpdateMapConfiguration(long reportId, MapConfigurationDTO mapConfiguration) throws ReportingServiceException {
        boolean isSuccess = spatialModule.saveOrUpdateMapConfiguration(reportId, mapConfiguration);
        if (!isSuccess) {
            throw new ReportingServiceException("Error during saving or updating map configuration in spatial module");
        }
    }

    private void updateMapConfiguration(long reportId, Boolean newWithMapValue, MapConfigurationDTO newMapConfiguration, boolean oldWithMapValue, MapConfigurationDTO oldMapConfigurationDTO) throws ReportingServiceException {

        try {
            if (newWithMapValue) {
                saveOrUpdateMapConfiguration(reportId, newMapConfiguration);
            } else if (oldWithMapValue) {
                spatialModule.deleteMapConfiguration(newArrayList(oldMapConfigurationDTO.getSpatialConnectId()));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error during the update of the map configuration", e);
        }
    }

    private void validateReport(ReportDTO report) {

    }

}