package eu.europa.ec.fisheries.uvms.reporting.service.bean;

import eu.europa.ec.fisheries.uvms.message.MessageException;
import eu.europa.ec.fisheries.uvms.reporting.message.mapper.ExtAssetMessageMapper;
import eu.europa.ec.fisheries.uvms.reporting.message.service.ReportingModuleReceiverBean;
import eu.europa.ec.fisheries.uvms.reporting.message.service.AssetModuleSenderBean;
import eu.europa.ec.fisheries.uvms.reporting.model.exception.ReportingServiceException;
import eu.europa.ec.fisheries.uvms.reporting.service.mapper.FilterProcessor;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelMapperException;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.TextMessage;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@LocalBean
@Stateless
public class AssetServiceBean {

    @EJB
    private AssetModuleSenderBean assetSender;

    @EJB
    private ReportingModuleReceiverBean receiver;

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Map<String, Asset> getAssetMap(final FilterProcessor processor) throws ReportingServiceException {
        Set<Asset> assetList = new HashSet<>();

        try {
            if (processor.hasAssets()) {
                String request = ExtAssetMessageMapper.createAssetListModuleRequest(processor.toAssetListQuery());
                String moduleMessage = assetSender.sendModuleMessage(request, receiver.getDestination());
                TextMessage response = receiver.getMessage(moduleMessage, TextMessage.class);
                List<Asset> assets = getAssets(moduleMessage, response);
                assetList.addAll(assets);
            }

            if (processor.hasAssetGroups()) {
                String request = ExtAssetMessageMapper.createAssetListModuleRequest(processor.getAssetGroupList());
                String moduleMessage = assetSender.sendModuleMessage(request, receiver.getDestination());
                TextMessage response = receiver.getMessage(moduleMessage, TextMessage.class);
                List<Asset> groupList = getAssets(moduleMessage, response);
                assetList.addAll(groupList);
            }


        } catch (MessageException | AssetModelMapperException e) {
            throw new ReportingServiceException("FAILED TO GET DATA FROM ASSET", e);
        }

        return ExtAssetMessageMapper.getAssetMap(assetList);
    }

    // UT
    protected List<Asset> getAssets(String moduleMessage, TextMessage response) throws AssetModelMapperException {
        return ExtAssetMessageMapper.mapToAssetListFromResponse(response, moduleMessage);
    }
}
