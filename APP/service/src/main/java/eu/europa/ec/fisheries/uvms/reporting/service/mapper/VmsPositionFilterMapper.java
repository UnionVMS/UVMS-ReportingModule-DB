package eu.europa.ec.fisheries.uvms.reporting.service.mapper;

import eu.europa.ec.fisheries.schema.movement.search.v1.ListCriteria;
import eu.europa.ec.fisheries.schema.movement.search.v1.RangeCriteria;
import eu.europa.ec.fisheries.schema.movement.search.v1.SearchKey;
import eu.europa.ec.fisheries.schema.movement.v1.MovementActivityTypeType;
import eu.europa.ec.fisheries.schema.movement.v1.MovementTypeType;
import eu.europa.ec.fisheries.uvms.reporting.model.vms.VmsPosition;
import eu.europa.ec.fisheries.uvms.reporting.service.dto.VmsPositionFilterDTO;
import eu.europa.ec.fisheries.uvms.reporting.service.entities.VmsPositionFilter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(uses = ObjectFactory.class, imports = {MovementActivityTypeType.class, MovementTypeType.class})
public interface VmsPositionFilterMapper {

    VmsPositionFilterMapper INSTANCE = Mappers.getMapper(VmsPositionFilterMapper.class);

    VmsPositionFilterDTO vmsPositionFilterToVmsPositionFilterDTO(VmsPositionFilter vmsPositionFilter);

    VmsPositionFilter vmsPositionFilterDTOToVmsPositionFilter(VmsPositionFilterDTO vmsPositionFilterDTO);

    @Mappings({
            @Mapping(target = "minimumSpeed", source = "movMinSpeed"),
            @Mapping(target = "maximumSpeed", source = "movMaxSpeed"),
            @Mapping(target = "movementActivity", expression = "java(dto.getMovActivity() != null ? Enum.valueOf( MovementActivityTypeType.class, dto.getMovActivity()) : null)"),
            @Mapping(target = "movementType", expression = "java(dto.getMovType() != null ? Enum.valueOf( MovementTypeType.class, dto.getMovType()) : null)")
    })
    VmsPositionFilter vmsPositionToVmsPositionFilter(VmsPosition dto);

    @Mappings({
            @Mapping(constant = "ACTIVITY_TYPE", target = "key"),
            @Mapping(source = "movementActivity", target = "value")
    })
    ListCriteria movementActivityToListCriteria(VmsPositionFilter vmsPositionFilter);

    @Mappings({
            @Mapping(constant = "MOVEMENT_TYPE", target = "key"),
            @Mapping(source = "movementType", target = "value")
    })
    ListCriteria movementTypeToListCriteria(VmsPositionFilter vmsPositionFilter);

    @Mappings({
            @Mapping(constant = "MOVEMENT_SPEED", target = "key"),
            @Mapping(source = "minimumSpeed", target = "from", defaultValue = "0"),
            @Mapping(source = "maximumSpeed", target = "to", defaultValue = "9223372036854775807")
    })
    RangeCriteria speedRangeToRangeCriteria(VmsPositionFilter vmsPositionFilter);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "report", ignore = true),
            @Mapping(target = "reportId", ignore = true)
    })
    void merge(VmsPositionFilter incoming, @MappingTarget VmsPositionFilter current);
}
