package eu.europa.ec.fisheries.uvms.reporting.service.entity;

import eu.europa.ec.fisheries.schema.movement.search.v1.ListCriteria;
import eu.europa.ec.fisheries.schema.movement.search.v1.RangeCriteria;
import eu.europa.ec.fisheries.schema.movement.search.v1.RangeKeyType;
import eu.europa.ec.fisheries.schema.movement.search.v1.SearchKey;
import eu.europa.ec.fisheries.uvms.common.DateUtils;
import eu.europa.ec.fisheries.uvms.reporting.service.entities.CommonFilter;
import eu.europa.ec.fisheries.uvms.reporting.service.entities.DateRange;
import eu.europa.ec.fisheries.uvms.reporting.service.entities.Position;
import eu.europa.ec.fisheries.uvms.reporting.service.entities.PositionSelector;
import eu.europa.ec.fisheries.uvms.reporting.service.entities.Selector;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import static junitparams.JUnitParamsRunner.$;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(JUnitParamsRunner.class)
public class CommonFilterTest {

    @Test
    @Parameters(method = "rangeCriteria")
    public void shouldReturnRangeCriteria(CommonFilter filter, RangeCriteria rangeCriteria){

        Iterator<RangeCriteria> iterator = filter.movementRangeCriteria().iterator();

        if (iterator.hasNext()) {
            assertEquals(rangeCriteria, iterator.next());
        }
    }

    @Test
    @Parameters(method = "listCriteria")
    public void shouldReturnListCriteria(CommonFilter filter, ListCriteria listCriteria){

        assertEquals(listCriteria, filter.movementListCriteria().iterator().next());

    }

    protected Object[] listCriteria(){

        CommonFilter filter = CommonFilter.builder()
                .positionSelector(PositionSelector.builder()
                        .selector(Selector.last)
                        .position(Position.positions).value(100.1F).build())
                .build();

        ListCriteria criteria = new ListCriteria();
        criteria.setKey(SearchKey.NR_OF_LATEST_REPORTS);
        criteria.setValue(String.valueOf(100));

        return $(
                $(filter, criteria)
        );
    }

    protected Object[] rangeCriteria(){

        final String beginningOfTime = "292269055-12-02 17:47:04 +0100";
        final String now = "2013-02-28 12:24:56 +0100";

        String fromMinus24Hours = "2013-02-27 12:24:56 +0100";

        CommonFilter filter1 = new CommonFilter(){
            protected DateTime nowUTC() {
                return new DateTime(DateUtils.stringToDate(now));
            }
        };

        filter1.setPositionSelector(PositionSelector.builder()
                        .selector(Selector.last).value(24F)
                        .position(Position.hours).build()
        );
        RangeCriteria expectedCriteria = new RangeCriteria();
        expectedCriteria.setKey(RangeKeyType.DATE);
        expectedCriteria.setFrom(fromMinus24Hours);
        expectedCriteria.setTo(now);
        setDefaultValues(expectedCriteria);

        String to = "2014-02-28 12:24:56 +0100";
        CommonFilter filter2 = CommonFilter.builder()
                .positionSelector(PositionSelector.builder()
                        .selector(Selector.all).build())
                .dateRange(new DateRange(DateUtils.stringToDate(now), DateUtils.stringToDate(to)))
                .build();

        RangeCriteria expectedCriteria2 = new RangeCriteria();
        expectedCriteria2.setKey(RangeKeyType.DATE);
        expectedCriteria2.setFrom(now);
        expectedCriteria2.setTo(to);
        setDefaultValues(expectedCriteria2);

        final Float hours = 100F;

        CommonFilter filter3 = new CommonFilter(){
            @Override
            protected DateTime nowUTC() {
                return new DateTime(DateUtils.stringToDate(now));
            }
        };

        filter3.setPositionSelector(PositionSelector.builder()
                .selector(Selector.last).position(Position.positions).value(hours).build());

        RangeCriteria expectedCriteria3 = new RangeCriteria();
        expectedCriteria3.setKey(RangeKeyType.DATE);
        expectedCriteria3.setFrom(beginningOfTime);
        expectedCriteria3.setTo(now);

        return $(
                $(filter1, expectedCriteria),
                $(filter2, expectedCriteria2),
                $(filter3, expectedCriteria3)
        );
    }

    @Test
    public void shouldBeEqualWhenMerging() {

        CommonFilter filter;

        final Calendar calendar = new GregorianCalendar(2013, 1, 28, 13, 24, 56);
        Date startDate = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, -10);
        Date endDate = calendar.getTime();

        CommonFilter incoming = CommonFilter.builder()
                .positionSelector(PositionSelector.builder().selector(Selector.all).build())
                .dateRange(new DateRange(startDate, endDate))
                .build();

        filter = CommonFilter.builder()
                .positionSelector(PositionSelector.builder()
                        .selector(Selector.last).value(1F).position(Position.hours).build())
                .build();

        assertNotEquals(filter, incoming);

        filter.merge(incoming);

        assertEquals(filter, incoming);

    }

    private void setDefaultValues(final RangeCriteria date) {
        if (date.getTo() == null) {
            date.setFrom(DateUtils.dateToString(DateUtils.nowUTC().toDate())); // FIXME use offset
        }
        if (date.getFrom() == null) {
            date.setFrom(DateUtils.dateToString(new Date(Long.MIN_VALUE)));
        }
    }


}