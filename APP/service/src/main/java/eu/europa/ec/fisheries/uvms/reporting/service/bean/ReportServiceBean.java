package eu.europa.ec.fisheries.uvms.reporting.service.bean;

import eu.europa.ec.fisheries.uvms.common.AuditActionEnum;
import eu.europa.ec.fisheries.uvms.reporting.model.exception.ReportingServiceException;
import eu.europa.ec.fisheries.uvms.reporting.service.dto.MapConfigurationDTO;
import eu.europa.ec.fisheries.uvms.reporting.service.dto.ReportDTO;
import eu.europa.ec.fisheries.uvms.reporting.service.entities.Report;
import eu.europa.ec.fisheries.uvms.reporting.service.mapper.ReportMapper;
import eu.europa.ec.fisheries.uvms.service.interceptor.IAuditInterceptor;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Session Bean implementation class ReportBean
 * <p/>
 * TODO: add Authorization
 * TODO: add proper exception handling
 * TODO: add business validation
 * TODO: add data type validation
 */
@Stateless
@LocalBean
public class ReportServiceBean {

    @EJB
    private ReportRepository repository;

    @EJB
    private SpatialService spatialModule;

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
            throw new RuntimeException("Error during the creation of the report");
        }
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

    @Transactional
    public ReportDTO findById(long id, String username, String scopeName) {
        ReportDTO reportDTO = readReport(id, username, scopeName);

        populateMapConfiguration(id, reportDTO);

        return reportDTO;
    }

    private ReportDTO readReport(long id, String username, String scopeName) {
        try {
            ReportMapper mapper = ReportMapper.ReportMapperBuilder().filters(true).build();
            Report reportByReportId = repository.findReportByReportId(id, username, scopeName);
            return mapper.reportToReportDTO(reportByReportId);
        } catch (Exception e) {
            throw new RuntimeException("Error during reading the report");
        }
    }

    private void populateMapConfiguration(long id, ReportDTO reportDTO) {
        try {
            if (reportDTO != null) {
                MapConfigurationDTO mapConfiguratioDTO = spatialModule.getMapConfiguration(id);
                reportDTO.setMapConfiguration(mapConfiguratioDTO);
            }
        } catch (ReportingServiceException e) {
            throw new RuntimeException("Error during reading map configuration in spatial module");
        }
    }

    @IAuditInterceptor(auditActionType = AuditActionEnum.MODIFY)
    @Transactional
    public boolean update(final ReportDTO report, Boolean oldWithMapValue, MapConfigurationDTO oldMapConfigurationDTO) throws ReportingServiceException {
        validateReport(report);

        boolean update = repository.update(report);

        updateMapConfiguration(report.getId(), report.getWithMap(), report.getMapConfiguration(), oldWithMapValue, oldMapConfigurationDTO);

        return update;
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
        if (report == null) {
            throw new IllegalArgumentException("REPORT CAN NOT BE NULL");
        }
    }

    @IAuditInterceptor(auditActionType = AuditActionEnum.DELETE)
    @Transactional
    public void delete(Long reportId, String username, String scopeName) throws ReportingServiceException {
        repository.remove(reportId, username, scopeName);
    }

    @Transactional
    public Collection<ReportDTO> listByUsernameAndScope(final Set<String> features, final String username, final String scopeName, final Boolean existent) throws ReportingServiceException {

        ReportMapper mapper = ReportMapper.ReportMapperBuilder().features(features).currentUser(username).build();

        List<Report> reports = repository.listByUsernameAndScope(username, scopeName, existent);

        List<ReportDTO> toReportDTOs = new ArrayList<>();

        for (Report report : reports) {

            toReportDTOs.add(mapper.reportToReportDTO(report));

        }

        return toReportDTOs;
    }

    @IAuditInterceptor(auditActionType = AuditActionEnum.SHARE)
    public void share(ReportDTO reportToUpdate, Boolean withMap, MapConfigurationDTO mapConfiguration) throws ReportingServiceException {
        update(reportToUpdate, withMap, mapConfiguration);
    }

}