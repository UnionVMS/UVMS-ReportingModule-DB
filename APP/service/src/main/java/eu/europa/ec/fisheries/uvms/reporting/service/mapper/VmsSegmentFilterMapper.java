package eu.europa.ec.fisheries.uvms.reporting.service.mapper;

import eu.europa.ec.fisheries.schema.movement.search.v1.RangeCriteria;
import eu.europa.ec.fisheries.schema.movement.v1.SegmentCategoryType;
import eu.europa.ec.fisheries.uvms.reporting.model.vms.Vmssegment;
import eu.europa.ec.fisheries.uvms.reporting.service.dto.VmsSegmentFilterDTO;
import eu.europa.ec.fisheries.uvms.reporting.service.entities.DurationRange;
import eu.europa.ec.fisheries.uvms.reporting.service.entities.VmsSegmentFilter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(uses = ObjectFactory.class, imports = {DurationRange.class, SegmentCategoryType.class})
public interface VmsSegmentFilterMapper {

    VmsSegmentFilterMapper INSTANCE = Mappers.getMapper(VmsSegmentFilterMapper.class);

    @Mappings({
            @Mapping(target = "minDuration", source = "vmsSegmentFilter.durationRange.minDuration"),
            @Mapping(target = "maxDuration", source = "vmsSegmentFilter.durationRange.maxDuration")
    })
    VmsSegmentFilterDTO vmsSegmentFilterToVmsSegmentFilterDTO(VmsSegmentFilter vmsSegmentFilter);

    @Mapping(target = "durationRange", expression = "java(new DurationRange(dto.getMinDuration(), dto.getMaxDuration()))")
    VmsSegmentFilter vmsSegmentFilterDTOToVmsSegmentFilter(VmsSegmentFilterDTO dto);


    @Mappings({
            @Mapping(source = "segMaxSpeed", target = "maximumSpeed"),
            @Mapping(source = "segMinSpeed", target = "minimumSpeed"),
            @Mapping(target = "category", expression = "java(Enum.valueOf( SegmentCategoryType.class, dto.getSegCategory()))"),
            @Mapping(target = "durationRange", expression = "java(new DurationRange(Float.valueOf(dto.getSegMinDuration()), Float.valueOf(dto.getSegMaxDuration())))") // TODO try change to float
    })
    VmsSegmentFilter vmsSegmentToVmsSegmentFilter(Vmssegment dto);

    @Mappings({
            @Mapping(constant = "SEGMENT_SPEED", target = "key"),
            @Mapping(source = "minimumSpeed", target = "from", defaultValue = "0"),
            @Mapping(source = "maximumSpeed", target = "to", defaultValue = "1000000")
    })
    RangeCriteria speedRangeToRangeCriteria(VmsSegmentFilter segmentFilter);

    @Mappings({
            @Mapping(constant = "SEGMENT_DURATION", target = "key"),
            @Mapping(source = "durationRange.minDuration", target = "from", defaultValue = "0"),//TODO remove the bloody default values
            @Mapping(source = "durationRange.maxDuration", target = "to", defaultValue = "1000000")//TODO remove the bloody default values
    })
    RangeCriteria durationRangeToRangeCriteria(VmsSegmentFilter segmentFilter);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "report", ignore = true),
            @Mapping(target = "reportId", ignore = true)
    })
    void merge(VmsSegmentFilter incoming, @MappingTarget VmsSegmentFilter current);
}
