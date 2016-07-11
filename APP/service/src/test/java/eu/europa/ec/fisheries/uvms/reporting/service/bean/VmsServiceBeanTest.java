/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.uvms.reporting.service.bean;

import com.google.common.collect.ImmutableMap;
import eu.europa.ec.fisheries.uvms.reporting.model.exception.ReportingServiceException;
import eu.europa.ec.fisheries.uvms.reporting.service.entities.CommonFilter;
import eu.europa.ec.fisheries.uvms.reporting.service.entities.Filter;
import eu.europa.ec.fisheries.uvms.reporting.service.entities.PositionSelector;
import eu.europa.ec.fisheries.uvms.reporting.service.entities.Report;
import eu.europa.ec.fisheries.uvms.reporting.service.entities.Selector;
import eu.europa.ec.fisheries.uvms.reporting.service.entities.AssetFilter;
import eu.europa.ec.fisheries.uvms.reporting.service.entities.AssetGroupFilter;
import lombok.SneakyThrows;
import org.junit.Ignore;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;
import org.unitils.mock.Mock;
import org.unitils.mock.MockUnitils;
import org.unitils.mock.PartialMock;

import javax.ejb.EJB;
import javax.jms.TextMessage;
import java.util.HashSet;
import java.util.Set;

public class VmsServiceBeanTest extends UnitilsJUnit4 {

    @TestedObject
    private VmsServiceBean service;

    @InjectIntoByType
    private Mock<SpatialService> spatialModule;

    @InjectIntoByType
    private Mock<ReportRepository> repository;

    @InjectIntoByType
    private Mock<AssetServiceBean> asset;

    @InjectIntoByType
    private Mock<MovementServiceBean> movement;

    private Mock<Report> report;

    private PartialMock<TextMessage> assetResponse;

    @Test
    @SneakyThrows
    public void testGetVmsDataByReportId() {

        Set<Filter> filterSet = new HashSet<>();
        filterSet.add(AssetFilter.builder().guid("1234").build());

        report.returns(filterSet).getFilters();

        asset.returns(ImmutableMap.<String, String>builder().build()).getAssetMap(null);
        repository.returns(report.getMock()).findReportByReportId(null, "userName", null, false);

        service.getVmsDataByReportId("userName", "scope",  null, null, null, false);

        asset.assertInvokedInSequence().getAssetMap(null);
        movement.assertInvokedInSequence().getMovement(null);
        report.assertInvokedInSequence().updateExecutionLog("userName");
        MockUnitils.assertNoMoreInvocations();
    }

    @Test
    @SneakyThrows
    public void testGetVmsDataByReportIdWithAssetGroupFilters() {

        Set<Filter> filterSet = new HashSet<>();
        filterSet.add(AssetGroupFilter.builder().groupId("123").build());

        report.returns(filterSet).getFilters();
        asset.returns(ImmutableMap.<String, String>builder().build()).getAssetMap(null);
        repository.returns(report.getMock()).findReportByReportId(null, "test", null, false);
        service.getVmsDataByReportId("test", null, null, null, null, false);

        asset.assertInvokedInSequence().getAssetMap(null);
        movement.assertInvokedInSequence().getMovement(null);
        report.assertInvokedInSequence().updateExecutionLog("test");

        MockUnitils.assertNoMoreInvocations();

    }

    @Test
    @SneakyThrows
    @Ignore("Repair later")
    public void testGetVmsDataByReportIdWithoutAsset() {

        Set<Filter> filterSet = new HashSet<>();
        filterSet.add(CommonFilter.builder()
                .positionSelector(PositionSelector.builder().selector(Selector.all).build())
                .build());

        report.returns(filterSet).getFilters();
        asset.returns(ImmutableMap.<String, String>builder().build()).getAssetMap(null);
        repository.returns(report.getMock()).findReportByReportId(null, "test", null, false);
        service.getVmsDataByReportId("test", null, null, null, null, false);

        movement.assertInvokedInSequence().getMovementMap(null);
        asset.assertInvokedInSequence().getAssetMap(null);
        report.assertInvokedInSequence().updateExecutionLog("test");

        MockUnitils.assertNoMoreInvocations();

    }

    @Test(expected = ReportingServiceException.class)
    @SneakyThrows
    public void testReportNull() {
        service.getVmsDataByReportId("test", null, null, null, null, false);
    }
}