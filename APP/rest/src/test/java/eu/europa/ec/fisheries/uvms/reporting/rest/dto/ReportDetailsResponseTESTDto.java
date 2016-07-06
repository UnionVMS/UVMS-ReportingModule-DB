/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries � European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.uvms.reporting.rest.dto;

import java.util.Objects;
import eu.europa.ec.fisheries.uvms.reporting.service.dto.ReportDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


@JsonInclude(Include.NON_NULL)
public class ReportDetailsResponseTESTDto {

    private ReportDTO data;
    private int code;
    private String msg;

    public ReportDetailsResponseTESTDto(ReportDTO data, int code) {
        this.data = data;
        this.code = code;
    }

    public ReportDetailsResponseTESTDto(int code) {
        this.code = code;
    }

    public ReportDetailsResponseTESTDto(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    
    public ReportDetailsResponseTESTDto(ReportDTO data, int code, String msg) {
    	this.data = data;
        this.code = code;
        this.msg = msg;
    }
    
    public ReportDetailsResponseTESTDto() {
    }
    
    
    public ReportDTO getData() {
        return data;
    }

    public int getCode() {
        return code;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + Objects.hashCode(this.data);
        hash = 23 * hash + Objects.hashCode(this.code);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ReportDetailsResponseTESTDto other = (ReportDetailsResponseTESTDto) obj;
        if (!Objects.equals(this.data, other.data)) {
            return false;
        }
        if (!Objects.equals(this.code, other.code)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ResponseDto{" + "data=" + data + ", code=" + code + '}';
    }

	/**
	 * @return the msg
	 */
	public String getMsg() {
		return msg;
	}

	/**
	 * @param msg the msg to set
	 */
	public void setMsg(String msg) {
		this.msg = msg;
	}

}