package com.javainuse.domain;

public class SocialDistancingCSV {

	private String id;
	private String timestamp;
	private String peopleInFrame;
	private Double noOfViolations;
	private String locality;
	private Double latitude;
	private Double longitude;
	private String localityType;
	private String zoneCategory;
	private String containmentZoneDistance;
	private String hourFromTimestamp;
	private String minsFromTimestamp;
	private String dateFromTimestamp;
	private String areaName;
	private String activeCovidCases;
	private Double distanceFromInput;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getPeopleInFrame() {
		return peopleInFrame;
	}

	public void setPeopleInFrame(String peopleInFrame) {
		this.peopleInFrame = peopleInFrame;
	}

	public Double getNoOfViolations() {
		return noOfViolations;
	}

	public void setNoOfViolations(Double noOfViolations) {
		this.noOfViolations = noOfViolations;
	}

	public String getLocality() {
		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public String getLocalityType() {
		return localityType;
	}

	public void setLocalityType(String localityType) {
		this.localityType = localityType;
	}

	public String getZoneCategory() {
		return zoneCategory;
	}

	public void setZoneCategory(String zoneCategory) {
		this.zoneCategory = zoneCategory;
	}

	public String getContainmentZoneDistance() {
		return containmentZoneDistance;
	}

	public void setContainmentZoneDistance(String containmentZoneDistance) {
		this.containmentZoneDistance = containmentZoneDistance;
	}

	public Double getDistanceFromInput() {
		return distanceFromInput;
	}

	public void setDistanceFromInput(Double distanceFromInput) {
		this.distanceFromInput = distanceFromInput;
	}

	public String getHourFromTimestamp() {
		return hourFromTimestamp;
	}

	public void setHourFromTimestamp(String hourFromTimestamp) {
		this.hourFromTimestamp = hourFromTimestamp;
	}

	public String getMinsFromTimestamp() {
		return minsFromTimestamp;
	}

	public void setMinsFromTimestamp(String minsFromTimestamp) {
		this.minsFromTimestamp = minsFromTimestamp;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public String getDateFromTimestamp() {
		return dateFromTimestamp;
	}

	public void setDateFromTimestamp(String dateFromTimestamp) {
		this.dateFromTimestamp = dateFromTimestamp;
	}

	public String getActiveCovidCases() {
		return activeCovidCases;
	}

	public void setActiveCovidCases(String activeCovidCases) {
		this.activeCovidCases = activeCovidCases;
	}

	@Override
	public String toString() {
		return "SocialDistancingCSV [id=" + id + ", timestamp=" + timestamp + ", peopleInFrame=" + peopleInFrame
				+ ", noOfViolations=" + noOfViolations + ", locality=" + locality + ", latitude=" + latitude
				+ ", longitude=" + longitude + ", localityType=" + localityType + ", zoneCategory=" + zoneCategory
				+ ", containmentZoneDistance=" + containmentZoneDistance + ", hourFromTimestamp=" + hourFromTimestamp
				+ ", minsFromTimestamp=" + minsFromTimestamp + ", dateFromTimestamp=" + dateFromTimestamp
				+ ", areaName=" + areaName + ", activeCovidCases=" + activeCovidCases + ", distanceFromInput="
				+ distanceFromInput + "]";
	}

}
