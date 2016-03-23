package eu.europa.ec.fisheries.uvms.reporting.service.entities;

import com.google.common.collect.Lists;
import eu.europa.ec.fisheries.schema.movement.search.v1.ListCriteria;
import eu.europa.ec.fisheries.schema.movement.search.v1.RangeCriteria;
import eu.europa.ec.fisheries.uvms.common.DateUtils;
import eu.europa.ec.fisheries.uvms.reporting.service.mapper.CommonFilterMapper;
import eu.europa.ec.fisheries.uvms.reporting.service.mapper.FilterVisitor;
import eu.europa.ec.fisheries.uvms.reporting.service.validation.CommonFilterIsValid;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import org.joda.time.DateTime;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import java.util.Date;
import java.util.List;

@Entity
@DiscriminatorValue("DATETIME")
@EqualsAndHashCode(callSuper = true)
@CommonFilterIsValid
public class CommonFilter extends Filter {

    @Embedded
    private DateRange dateRange;

    @Embedded
    private PositionSelector positionSelector;

    public CommonFilter(){
        super(FilterType.common);
    }

    @Builder
    public CommonFilter(Long id, DateRange dateRange, PositionSelector positionSelector) {
        super(FilterType.common);
        this.dateRange = dateRange;
        this.positionSelector = positionSelector;
        setId(id);
        validate();
    }

    @Override
    public <T> T accept(FilterVisitor<T> visitor) {
        return visitor.visitCommonFilter(this);
    }

    @Override
    public void merge(Filter filter) {
       CommonFilterMapper.INSTANCE.merge((CommonFilter) filter, this);
    }

    @Override
    public List<RangeCriteria> movementRangeCriteria(DateTime now) {
        CommonFilterMapper mapper = CommonFilterMapper.INSTANCE;
        List<RangeCriteria> rangeCriteria = Lists.newArrayList();

        if (Position.hours.equals(positionSelector.getPosition())) {
            Float hours = positionSelector.getValue();
            Date to = DateUtils.nowUTCMinusSeconds(now, hours).toDate();
            rangeCriteria.add(mapper.dateRangeToRangeCriteria(to, now.toDate()));
        }
        else {
            RangeCriteria date = mapper.dateRangeToRangeCriteria(this);
            setDefaultValues(date, now);
            rangeCriteria.add(date);
        }

        return rangeCriteria;
    }

    private void setDefaultValues(final RangeCriteria date, DateTime now) {
        if (date.getTo() == null) {
            date.setTo(DateUtils.dateToString(now.toDate()));
        }
        if (date.getFrom() == null) {
            date.setFrom(DateUtils.dateToString(DateUtils.START_OF_TIME.toDate()));
        }
    }

    @Override
    public List<ListCriteria> movementListCriteria() {
        ListCriteria criteria = new ListCriteria();
        List<ListCriteria> listCriteria = Lists.newArrayList();
        if (Position.positions.equals(positionSelector.getPosition())) {
            criteria = CommonFilterMapper.INSTANCE.positionToListCriteria(this);
        }
        if (criteria.getKey() != null) {
            listCriteria.add(criteria);
        }
        return listCriteria;
    }

    // UT
    protected DateTime nowUTC() {
        return DateUtils.nowUTC();
    }

    @Override
    public Object getUniqKey() {
        return getId();
    }

    public DateRange getDateRange() {
        return dateRange;
    }

    public void setDateRange(DateRange dateRange) {
        this.dateRange = dateRange;
    }

    public PositionSelector getPositionSelector() {
        return positionSelector;
    }

    public void setPositionSelector(PositionSelector positionSelector) {
        this.positionSelector = positionSelector;
    }

}
