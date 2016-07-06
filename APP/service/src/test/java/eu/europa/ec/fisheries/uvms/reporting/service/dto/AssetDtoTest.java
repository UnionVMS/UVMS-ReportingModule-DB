/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries � European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.uvms.reporting.service.dto;

import eu.europa.ec.fisheries.wsdl.asset.types.CarrierSource;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetId;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class AssetDtoTest {

    @Test
    public void testDelegation(){

        // given
        Asset asset = getAssetDto(1);

        // when
        AssetDTO dto = new AssetDTO(asset);

        // then
        assertEquals(dto.getCountryCode(), "SWE1");
        assertEquals(dto.getIrcs(), "IRCS-1");
        assertEquals(dto.getName(), "ASSET-1");
        assertEquals(dto.getCfr(), "CFR1");

    }

    private Asset getAssetDto(Integer id) {
        Asset dto = new Asset();

        dto.setCfr("CFR" + id);
        dto.setCountryCode("SWE" + id);
        dto.setExternalMarking("MARKING" + 1);
        dto.setGrossTonnage(BigDecimal.valueOf(1.2));
        dto.setHasIrcs("true");
        dto.setHasLicense(true);
        dto.setHomePort("PORT" + id);

        AssetId assetId = new AssetId();
        assetId.setGuid(id.toString());
        dto.setAssetId(assetId);
        dto.setIrcs("IRCS-" + id);
        dto.setLengthBetweenPerpendiculars(BigDecimal.valueOf(10));
        dto.setLengthOverAll(BigDecimal.valueOf(20));
        dto.setName("ASSET-" + id);
        dto.setOtherGrossTonnage(BigDecimal.valueOf(100));
        dto.setPowerAux(BigDecimal.valueOf(1000));
        dto.setPowerMain(BigDecimal.valueOf(50));
        dto.setSafetyGrossTonnage(BigDecimal.valueOf(55));
        dto.setActive(true);

        if (id % 3 == 0) {
            dto.setActive(true);
        }
        if (id % 2 == 0) {
            dto.setSource(CarrierSource.NATIONAL);
            dto.setActive(false);
        }
        if (id % 5 == 0) {
            dto.setSource(CarrierSource.XEU);
            dto.setActive(true);
            //dto.setAssetType("ASSET-TYPE: " + id);
        }
       // dto.setAssetType("ASSET-TYPE: " + id);
        return dto;
    }
}