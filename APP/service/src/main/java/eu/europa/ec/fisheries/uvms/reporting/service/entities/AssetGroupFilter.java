package eu.europa.ec.fisheries.uvms.reporting.service.entities;

import eu.europa.ec.fisheries.uvms.reporting.service.mapper.AssetGroupFilterMapper;
import eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

@Entity
@DiscriminatorValue("VGROUP")
@EqualsAndHashCode(callSuper = false, of = {"guid"})
@ToString
public class AssetGroupFilter extends Filter {


    private @NotNull String guid;
    private @NotNull String name;
    private @NotNull String userName;

    public AssetGroupFilter() {
        super(FilterType.vgroup);
    }

    @Builder
    public AssetGroupFilter(Long id, String groupId, String userName, String name){
        super(FilterType.vgroup);
        this.guid = groupId;
        this.userName = userName;
        this.name = name;
        setId(id);
    }

    @Override
    public <T> T accept(FilterVisitor<T> visitor) {
        return visitor.visitAssetGroupFilter(this);
    }

    @Override
    public void merge(Filter filter) {
        AssetGroupFilterMapper.INSTANCE.merge((AssetGroupFilter) filter, this);
    }

    @Override
    public List<AssetGroup> assetGroupCriteria(){
        return Arrays.asList(AssetGroupFilterMapper.INSTANCE.assetGroupFilterToAssetGroup(this));
    }

    @Override
    public Object getUniqKey() {
        return getGuid();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
