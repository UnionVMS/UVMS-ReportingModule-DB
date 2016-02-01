package eu.europa.ec.fisheries.uvms.reporting.service.mapper;

import eu.europa.ec.fisheries.uvms.reporting.service.entities.AreaFilter;
import eu.europa.ec.fisheries.uvms.reporting.service.entities.CommonFilter;
import eu.europa.ec.fisheries.uvms.reporting.service.entities.AssetFilter;
import eu.europa.ec.fisheries.uvms.reporting.service.entities.AssetGroupFilter;
import eu.europa.ec.fisheries.uvms.reporting.service.entities.VmsPositionFilter;
import eu.europa.ec.fisheries.uvms.reporting.service.entities.VmsSegmentFilter;
import eu.europa.ec.fisheries.uvms.reporting.service.entities.VmsTrackFilter;

public interface FilterVisitor<T> {

    T visitVmsTrackFilter(VmsTrackFilter trackFilter);
    T visitVmsSegmentFilter(VmsSegmentFilter segmentFilter);
    T visitVmsPositionFilter(VmsPositionFilter positionFilter);
    T visitAssetFilter(AssetFilter assetFilter);
    T visitAssetGroupFilter(AssetGroupFilter assetGroupFilter);
    T visitAreaFilter(AreaFilter areaFilter);
    T visitCommonFilter(CommonFilter commonFilter);

}
