package eu.europa.ec.fisheries.uvms.reporting.service.mapper;

import com.google.common.collect.Sets;
import eu.europa.ec.fisheries.uvms.reporting.model.VisibilityEnum;
import eu.europa.ec.fisheries.uvms.reporting.security.AuthorizationCheckUtil;
import eu.europa.ec.fisheries.uvms.reporting.service.dto.ExecutionLogDTO;
import eu.europa.ec.fisheries.uvms.reporting.service.dto.FilterDTO;
import eu.europa.ec.fisheries.uvms.reporting.service.dto.ReportDTO;
import eu.europa.ec.fisheries.uvms.reporting.service.entities.ExecutionLog;
import eu.europa.ec.fisheries.uvms.reporting.service.entities.Filter;
import eu.europa.ec.fisheries.uvms.reporting.service.entities.Report;
import eu.europa.ec.fisheries.uvms.reporting.service.entities.ReportDetails;
import lombok.Builder;

import java.util.*;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

public class ReportMapper {

    private final ObjectFactory factory = new ObjectFactory();
    private final AuditMapper auditMapper = new AuditMapperImpl();
    private final ExecutionLogMapper executionLogMapper = new ExecutionLogMapperImpl();
    private Set<String> features = null;
    private boolean filters = false;
    private String currentUser = null;

    @Builder(builderMethodName = "ReportMapperBuilder")
    public ReportMapper(boolean filters, Set<String> features, String currentUser) {
        this.currentUser = currentUser;
        this.features = features;
        this.filters = filters;
    }
    public ReportMapper() {

    }

    public ReportDTO reportToReportDTO(final Report report) {
        if (report == null) {
            return null;
        }
        ReportDTO reportDTO = factory.createReportDTO();
        reportDTO.setId(report.getId());
        reportDTO.setName(report.getDetails().getName());
        reportDTO.setDescription(report.getDetails().getDescription());
        reportDTO.setWithMap(report.getDetails().getWithMap());
        reportDTO.setScopeName(report.getDetails().getScopeName());
        reportDTO.setAudit(auditMapper.auditToAuditDTO(report.getAudit()));
        reportDTO.setCreatedBy(report.getDetails().getCreatedBy());
        if (report.getIsDeleted() != null) {
            reportDTO.setDeleted(report.getIsDeleted());
        }
        reportDTO.setExecutionLog(getExecutionLogDTO(report.getExecutionLogs()));
        reportDTO.setDeletedOn(report.getDeletedOn());
        reportDTO.setDeletedBy(report.getDeletedBy());
        reportDTO.setVisibility(report.getVisibility());

        if (filters) {
            reportDTO.setFilters(filterSetToFilterDTOSet(report.getFilters()));
        }

        if (features != null) {
            List<VisibilityEnum> visibilityEnumList = AuthorizationCheckUtil.listAllowedVisibilityOptions(report.getDetails().getCreatedBy(), currentUser, features);

            if (!visibilityEnumList.isEmpty()) {
                reportDTO.setShareable(visibilityEnumList);
            }

            reportDTO.setDeletable(AuthorizationCheckUtil.isDeleteAllowed(reportDTO, currentUser, features));
            reportDTO.setEditable(AuthorizationCheckUtil.isEditAllowed(reportDTO, currentUser, features));
        }
        return reportDTO;
    }

    public Report reportDTOToReport(final ReportDTO dto) {
        if (dto == null) {
            return null;
        }
        Report report = factory.createReport();
        report.setDetails(new ReportDetails(
                dto.getDescription(), dto.getName(), dto.getWithMap(), dto.getScopeName(), dto.getCreatedBy())
        );
        report.setFilters(filterDTOSetToFilterSet(dto.getFilters(), report));
        report.setExecutionLogs(executionLogDTOToExecutionLogSet(dto.getExecutionLog(), report));
        report.setIsDeleted(dto.isDeleted());
        report.setDeletedOn(dto.getDeletedOn());
        report.setDeletedBy(dto.getDeletedBy());
        report.setVisibility(dto.getVisibility());
        return report;
    }

    private ExecutionLogDTO getExecutionLogDTO(Set<ExecutionLog> executionLogs) {
        if (isNotEmpty(executionLogs)) {
            return executionLogMapper.executionLogFilterToExecutionLogFilterDTO(executionLogs.iterator().next());
        }
        return null;
    }

    private Set<ExecutionLog> executionLogDTOToExecutionLogSet(ExecutionLogDTO executionLogDto, Report report) {
        if (executionLogDto != null) {
            ExecutionLog executionLog = new ExecutionLog(report, executionLogDto.getExecutedBy());
            return Sets.newHashSet(executionLog);
        }
        return Collections.emptySet();
    }

    public List<FilterDTO> filterSetToFilterDTOSet(final Set<Filter> filterSet) {
        if (filterSet == null) {
            return null;
        }
        List<FilterDTO> filterDTOSet = new ArrayList<>();
        for (Filter filter : filterSet) {
            FilterDTO filterDTO = filter.accept(new Filter.FilterToDTOVisitor());
            filterDTO.setType(filter.getType());
            filterDTOSet.add(filterDTO);
        }
        return filterDTOSet;
    }

    private Set<Filter> filterDTOSetToFilterSet(final List<FilterDTO> filterDTOSet, final Report report) {
        if (filterDTOSet == null) {
            return null;
        }
        Set<Filter> filterSet = new HashSet<Filter>();
        for (FilterDTO filterDTO : filterDTOSet) {
            Filter filter = filterDTO.convertToFilter();
            filter.setReport(report);
            filterSet.add(filter);
        }
        return filterSet;
    }

}

