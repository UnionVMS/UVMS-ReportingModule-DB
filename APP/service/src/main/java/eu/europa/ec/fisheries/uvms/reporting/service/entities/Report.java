package eu.europa.ec.fisheries.uvms.reporting.service.entities;

import eu.europa.ec.fisheries.uvms.common.DateUtils;
import eu.europa.ec.fisheries.uvms.domain.BaseEntity;
import eu.europa.ec.fisheries.uvms.reporting.model.VisibilityEnum;
import eu.europa.ec.fisheries.uvms.reporting.model.exception.ReportingServiceException;
import eu.europa.ec.fisheries.uvms.reporting.service.entities.converter.CharBooleanConverter;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Where;

import static javax.persistence.CascadeType.ALL;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;

@Entity
@Table(name = "report")
@NamedQueries({
        @NamedQuery(name = Report.LIST_BY_USERNAME_AND_SCOPE, query =
                "SELECT DISTINCT r FROM Report r LEFT JOIN FETCH r.executionLogs l " +
                        "WHERE (1=:isAdmin) OR ((r.details.scopeName = :scopeName AND (r.details.createdBy = :username OR r.visibility = 'SCOPE')) OR r.visibility = 'PUBLIC') " +
                        "AND r.isDeleted <> :existent " +
                        "ORDER BY r.id"),
        @NamedQuery(name = Report.FIND_BY_ID, query =
                "SELECT r FROM Report r LEFT JOIN FETCH r.executionLogs l " +
                        "WHERE r.id = :reportID AND r.isDeleted <> 'Y' AND ((1=:isAdmin) OR (r.details.createdBy = :username " +
                        "OR (r.details.scopeName = :scopeName AND r.visibility = 'SCOPE') OR r.visibility = 'PUBLIC'))")
})
@Where(clause = "is_deleted <> 'Y'")
@EqualsAndHashCode(exclude = {"executionLogs", "filters", "audit"})
@ToString
@Data
@FilterDef(name = Report.EXECUTED_BY_USER, parameters = @ParamDef(name = "username", type = "string"))
//@SequenceGenerator(name = "default_gen", sequenceName = "report_seq", allocationSize = 1)
public class Report extends BaseEntity {

    public static final String IS_DELETED = "is_deleted";
    public static final String VISIBILITY = "visibility";
    public static final String EXECUTED_BY_USER = "executedByUser";
    public static final String LIST_BY_USERNAME_AND_SCOPE = "Report.listByUsernameAndScope";
    public static final String FIND_BY_ID = "Report.findReportByReportId";

    @OneToMany(mappedBy = "report", cascade = ALL)
    @org.hibernate.annotations.Filter(name = EXECUTED_BY_USER, condition = "executed_by = :username")
    private Set<ExecutionLog> executionLogs = new HashSet<>();

    @OneToMany(mappedBy = "report", cascade = ALL)
    private Set<Filter> filters = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = VISIBILITY)
    @NotNull
    private VisibilityEnum visibility;

    @Convert(converter = CharBooleanConverter.class)
    @Column(name = IS_DELETED, nullable = true, length = 1)
    private Boolean isDeleted;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "deleted_on")
    private Date deletedOn;

    @Column(name = "deleted_by")
    private String deletedBy;

    @Embedded
    private ReportDetails details = new ReportDetails();

    @Embedded
    private Audit audit;

    @Builder
    public Report(ReportDetails details, String createdBy, Set<Filter> filters,
                  Set<ExecutionLog> executionLogs, Audit audit) {
        this.details = details;
        this.visibility = VisibilityEnum.PRIVATE;
        this.filters = filters;
        this.executionLogs = executionLogs;
        this.isDeleted = false;
        this.audit = audit;
    }

    public Report() {

    }

    public void updateExecutionLog(final String username) throws ReportingServiceException {

        ExecutionLog executionLog;

        if (isEmpty(executionLogs)) {

            executionLog = ExecutionLog.builder().report(this).executedBy(username).build();

            executionLogs.add(executionLog);

        } else {

            executionLog = executionLogs.iterator().next();

            executionLog.setExecutedOn(DateUtils.nowUTC().toDate());

        }

    }

    public void merge(Report incoming) {
        mergeDetails(incoming.details);
        this.isDeleted = incoming.isDeleted;
        this.deletedOn = incoming.deletedOn;
        this.deletedBy = incoming.deletedBy;
        this.visibility = incoming.visibility;
    }

    public void mergeDetails(ReportDetails reportDetails) {
        this.details.merge(reportDetails);
    }

    @PrePersist
    private void onCreate() {
        audit = new Audit(DateUtils.nowUTC().toDate());
    }

    public Audit getAudit() {
        return audit;
    }

    public void setAudit(Audit audit) {
        this.audit = audit;
    }

    public Set<Filter> getFilters() {
        return filters;
    }

    public void setFilters(Set<Filter> filters) {
        this.filters = filters;
    }

    public ReportDetails getDetails() {
        return details;
    }

    public void setDetails(ReportDetails details) {
        this.details = details;
    }

    public VisibilityEnum getVisibility() {
        return visibility;
    }

    public void setVisibility(VisibilityEnum visibility) {
        this.visibility = visibility;
    }
}