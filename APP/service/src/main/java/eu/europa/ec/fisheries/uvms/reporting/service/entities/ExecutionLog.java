package eu.europa.ec.fisheries.uvms.reporting.service.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.ToString;

@Entity
@Table(name = "execution_log", uniqueConstraints = @UniqueConstraint(columnNames = {"report_id", "executed_by"}))
@ToString(exclude = "report")
public class ExecutionLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Valid
    @JoinColumn(name = "report_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Report report;

    @NotNull
    @Column(name = "executed_by")
    private String executedBy;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "executed_on")
    private Date executedOn;

    public ExecutionLog() {
    }

    @Builder
    public ExecutionLog(long id, Report report, String executedBy) {
        this.id = id;
        this.report = report;
        this.executedBy = executedBy;
        this.executedOn = new Date();
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Report getReport() {
        return this.report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public String getExecutedBy() {
        return this.executedBy;
    }

    public void setExecutedBy(String executedBy) {
        this.executedBy = executedBy;
    }

    public Date getExecutedOn() {
        return this.executedOn;
    }

    public void setExecutedOn(Date executedOn) {
        this.executedOn = executedOn;
    }

}
