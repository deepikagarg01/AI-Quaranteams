package com.javainuse.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ContainmentToAvgViolationDayWise {

	private String locality;
	private Map<String, Double> contToAvgViolationsDayWise; // timestamp-violations
	private Double distanceFromInputLocation;
	private List<Map<String, Double>> contToAvgViolationsDayWises = new ArrayList<>();
	private List<Double> distances = new ArrayList<>();

	private Map<Double, Double> distanceViolations;
	private List<Map<Double, Double>> distanceViolationList = new ArrayList<>();

	public ContainmentToAvgViolationDayWise(String locality, Map<String, Double> contToAvgViolationsDayWise,
			Double distanceFromInputLocation) {
		super();
		this.locality = locality;
		this.contToAvgViolationsDayWise = contToAvgViolationsDayWise;
		this.distanceFromInputLocation = distanceFromInputLocation;
	}

	public String getLocality() {
		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	public Map<String, Double> getContToAvgViolationsDayWise() {
		return contToAvgViolationsDayWise;
	}

	public void setContToAvgViolationsDayWise(Map<String, Double> contToAvgViolationsDayWise) {
		this.contToAvgViolationsDayWise = contToAvgViolationsDayWise;
	}

	public Double getDistanceFromInputLocation() {
		return distanceFromInputLocation;
	}

	public void setDistanceFromInputLocation(Double distanceFromInputLocation) {
		this.distanceFromInputLocation = distanceFromInputLocation;
	}

	@Override
	public String toString() {
		return "ContainmentToAvgViolationDayWise [locality=" + locality + ", contToAvgViolationsDayWise="
				+ contToAvgViolationsDayWise + ", distanceFromInputLocation=" + distanceFromInputLocation + "]";
	}

	public List<Map<String, Double>> getContToAvgViolationsDayWises() {
		return contToAvgViolationsDayWises;
	}

	public void setContToAvgViolationsDayWises(List<Map<String, Double>> contToAvgViolationsDayWises) {
		this.contToAvgViolationsDayWises = contToAvgViolationsDayWises;
	}

	public void addToContToAvgViolationsDayWises(Map<String, Double> contToAvgViolationsDayWise) {
		contToAvgViolationsDayWises.add(contToAvgViolationsDayWise);
	}

	public List<Double> getDistances() {
		return distances;
	}

	public void addDistance(Double distance) {
		distances.add(distance);
	}

	public void setDistances(List<Double> distances) {
		this.distances = distances;
	}

	public ContainmentToAvgViolationDayWise() {
		super();
	}

	public Map<Double, Double> getDistanceViolations() {
		return distanceViolations;
	}

	public void setDistanceViolations(Map<Double, Double> distanceViolations) {
		this.distanceViolations = distanceViolations;
	}

	public void addToDistanceViolationList(Map<Double, Double> distanceViolationList) {
		this.distanceViolationList.add(distanceViolationList);
	}

	public List<Map<Double, Double>> getDistanceViolationList() {
		return distanceViolationList;
	}

	public void setDistanceViolationList(List<Map<Double, Double>> distanceViolationList) {
		this.distanceViolationList = distanceViolationList;
	}

}
