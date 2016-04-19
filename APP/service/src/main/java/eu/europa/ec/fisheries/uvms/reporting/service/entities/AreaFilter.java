package eu.europa.ec.fisheries.uvms.reporting.service.entities;

import eu.europa.ec.fisheries.uvms.reporting.service.mapper.AreaFilterMapper;
import eu.europa.ec.fisheries.uvms.reporting.service.mapper.FilterVisitor;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.AreaIdentifierType;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("areas")
@EqualsAndHashCode(callSuper = true, of = {"areaType", "areaId"})
@ToString
public class AreaFilter extends Filter {

    private @Column(name = "area_type") String areaType;
    private @Column(name = "area_id") Long areaId;

    public AreaFilter() {
        super(FilterType.areas);
    }

    @Builder
    public AreaFilter(Long id, Long areaId, String areaType) {
        super(FilterType.areas);
        setId(id);
        this.areaId = areaId;
        this.areaType = areaType;
    }

    public String getAreaType() {
        return areaType;
    }

    public void setAreaType(String areaType) {
        this.areaType = areaType;
    }

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }

    @Override
    public <T> T accept(FilterVisitor<T> visitor) {
        return visitor.visitAreaFilter(this);
    }

    @Override
    public void merge(Filter filter) {
        AreaFilterMapper.INSTANCE.merge((AreaFilter) filter, this);
    }

    @Override
    public Object getUniqKey() {
        return hashCode();
    }
    
    @Override
    public AreaIdentifierType getAreaIdentifierType() {
        return AreaFilterMapper.INSTANCE.areaIdentifierTypeToAreaFilter(this);
    }

}
