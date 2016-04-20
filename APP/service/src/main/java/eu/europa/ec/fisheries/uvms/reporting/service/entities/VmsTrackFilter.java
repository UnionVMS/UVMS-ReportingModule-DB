package eu.europa.ec.fisheries.uvms.reporting.service.entities;

import eu.europa.ec.fisheries.schema.movement.search.v1.RangeCriteria;
import eu.europa.ec.fisheries.schema.movement.search.v1.RangeKeyType;
import eu.europa.ec.fisheries.uvms.reporting.service.mapper.TrackFilterMapper;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.joda.time.DateTime;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.valueOf;

@Entity
@DiscriminatorValue("VMSTRACK")
@EqualsAndHashCode(callSuper = true)
@ToString
public class VmsTrackFilter extends Filter {

    private static final float MIN_DEFAULT = 0F;
    private static final float DEFAULT_MAX_AVG_SPEED = Float.MAX_VALUE;
    private static final float DEFAULT_MAX_DISTANCE = Float.MAX_VALUE;
    private static final float DEFAULT_MAX_TIME_AT_SEA = Float.MAX_VALUE;
    private static final float DEFAULT_MAX_FULL_DURATION = Float.MAX_VALUE;

    private @Embedded TimeRange timeRange;
    private @Embedded DurationRange durationRange;
    private @Column(name = "MIN_AVG_SPEED") Float minAvgSpeed;
    private @Column(name = "MAX_AVG_SPEED") Float maxAvgSpeed;

    public VmsTrackFilter() {
        super(FilterType.vmstrack);
    }

    @Builder
    public VmsTrackFilter(Long id, Long reportId, TimeRange timeRange, DurationRange durationRange,
                          DistanceRange distanceRange, Float minAvgSpeed, Float maxAvgSpeed) {
        super(FilterType.vmstrack, id, reportId);
        this.timeRange = timeRange;
        this.durationRange = durationRange;
        this.minAvgSpeed = minAvgSpeed;
        this.maxAvgSpeed = maxAvgSpeed;
    }

    @Override
    public <T> T accept(FilterVisitor<T> visitor) {
        return visitor.visitVmsTrackFilter(this);
    }

    @Override
    public void merge(Filter filter) {
        TrackFilterMapper.INSTANCE.merge((VmsTrackFilter) filter, this);
    }

    @Override
    public List<RangeCriteria> movementRangeCriteria(DateTime now) {
        List<RangeCriteria> rangeCriteria = new ArrayList<>();
        addDurationAtSeaCriteria(rangeCriteria);
        addTotalDurationCriteria(rangeCriteria);
        addSpeedCriteria(rangeCriteria);
        return rangeCriteria;
    }

    private void addSpeedCriteria(List<RangeCriteria> rangeCriteria) {
        if (minAvgSpeed != null || maxAvgSpeed != null) {
            RangeCriteria lengthCriteria = new RangeCriteria();
            lengthCriteria.setKey(RangeKeyType.TRACK_SPEED);
            lengthCriteria.setFrom(valueOf(minAvgSpeed != null ? minAvgSpeed : MIN_DEFAULT));
            lengthCriteria.setTo(valueOf(maxAvgSpeed != null ? maxAvgSpeed : DEFAULT_MAX_AVG_SPEED));
            rangeCriteria.add(lengthCriteria);
        }
    }

    private void addTotalDurationCriteria(List<RangeCriteria> rangeCriteria) {
        if (durationRange!=null) {
            Float maxDuration = durationRange.getMaxDuration();
            Float minDuration = durationRange.getMinDuration();
            RangeCriteria durationCriteria = new RangeCriteria();
            durationCriteria.setKey(RangeKeyType.TRACK_DURATION);
            durationCriteria.setFrom(valueOf(minDuration != null ? minDuration : MIN_DEFAULT));
            durationCriteria.setTo(valueOf(maxDuration != null ? maxDuration : DEFAULT_MAX_FULL_DURATION));
            rangeCriteria.add(durationCriteria);
        }
    }

    private void addDurationAtSeaCriteria(List<RangeCriteria> rangeCriteria) {
        if (timeRange!=null) {
            Float minTime = timeRange.getMinTime();
            Float maxTime = timeRange.getMaxTime();
            RangeCriteria timeCriteria = new RangeCriteria();
            timeCriteria.setKey(RangeKeyType.TRACK_DURATION_AT_SEA);
            timeCriteria.setFrom(valueOf(minTime != null ? minTime : MIN_DEFAULT));
            timeCriteria.setTo(valueOf(maxTime != null ? maxTime : DEFAULT_MAX_TIME_AT_SEA));
            rangeCriteria.add(timeCriteria);
        }
    }

    public TimeRange getTimeRange() {
        return timeRange;
    }

    public void setTimeRange(TimeRange timeRange) {
        this.timeRange = timeRange;
    }

    public DurationRange getDurationRange() {
        return durationRange;
    }

    public void setDurationRange(DurationRange durationRange) {
        this.durationRange = durationRange;
    }

    @Override
    public Object getUniqKey() {
        return getType();
    }

    public Float getMinAvgSpeed() {
        return minAvgSpeed;
    }

    public void setMinAvgSpeed(Float minAvgSpeed) {
        this.minAvgSpeed = minAvgSpeed;
    }

    public Float getMaxAvgSpeed() {
        return maxAvgSpeed;
    }

    public void setMaxAvgSpeed(Float maxAvgSpeed) {
        this.maxAvgSpeed = maxAvgSpeed;
    }

}
