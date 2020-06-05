package com.javainuse.domain;

public class AreaWiseZoneCategroization {

	private String area;
	private String localityName;
	private Double averageViolation;
	private String zone;

	public AreaWiseZoneCategroization(String area, String localityName, Double averageViolation, String zone) {
		super();
		this.area = area;
		this.localityName = localityName;
		this.averageViolation = averageViolation;
		this.zone = zone;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getLocalityName() {
		return localityName;
	}

	public void setLocalityName(String localityName) {
		this.localityName = localityName;
	}

	public Double getAverageViolation() {
		return averageViolation;
	}

	public void setAverageViolation(Double averageViolation) {
		this.averageViolation = averageViolation;
	}

	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}

	@Override
	public String toString() {
		return "AreaWiseZoneCategroization [area=" + area + ", localityName=" + localityName + ", averageViolation="
				+ averageViolation + ", zone=" + zone + "]";
	}

}
