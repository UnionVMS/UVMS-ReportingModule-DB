package eu.europa.ec.fisheries.uvms.reporting.service.entities;

import lombok.*;

import javax.persistence.*;
import java.io.*;

@Embeddable
@EqualsAndHashCode
public class TimeRange implements Serializable {

    @Column(name = "MIN_TIME")
    private Float minTime;

    @Column(name = "MAX_TIME")
    private Float maxTime;

    public TimeRange() {
    }

    public TimeRange(Float minTime, Float maxTime) {
        this.minTime = minTime;
        this.maxTime = maxTime;
    }

    public Float getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(Float maxTime) {
        this.maxTime = maxTime;
    }

    public Float getMinTime() {
        return minTime;
    }

    public void setMinTime(Float minTime) {
        this.minTime = minTime;
    }

}
