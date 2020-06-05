package com.javainuse.controllers;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.javainuse.domain.AreaWiseZoneCategroization;
import com.javainuse.domain.ContainmentToAvgViolationDayWise;
import com.javainuse.domain.Locality;
import com.javainuse.domain.SocialDistancingCSV;

import util.AggregatorUtility;
import util.QuaranUtil;

@CrossOrigin
@Controller
public class SocialDistancingController {

	@GetMapping(path = "/download")
	public ResponseEntity<ByteArrayResource> getVideoFile(@RequestParam(value = "inputLat") Double inputLat,
			@RequestParam(value = "inputLong") Double inputLong) throws IOException {
		final Map<String, SocialDistancingCSV> map = QuaranUtil.getLatLongMappedSocialDistancingCsvs();
		final SocialDistancingCSV csv = map.get(inputLat + "/" + inputLong);
		String file = "Output_SocDis_Area_1.mp4";
		if (csv != null) {
			final String locality = csv.getLocality();
			if (locality.equalsIgnoreCase("Edana-Alpha1")) {
				file = "Output_SocDis_Area_2.mp4";
			}
		}

		final byte[] data = QuaranUtil.getFile(inputLat, inputLong, file);
		final ByteArrayResource resource = new ByteArrayResource(data);

		return ResponseEntity.ok().contentLength(data.length).header("Content-type", "application/octet-stream")
				.header("Content-disposition", "attachment; filename=\"" + file + "\"").body(resource);

	}

	/**
	 * Complete data set for the application, created by reading csv file from S3
	 *
	 * @return
	 */
	@GetMapping("/socialDistancingData")
	public @ResponseBody List<SocialDistancingCSV> getSocialDistancingData() {
		return QuaranUtil.getSocialDistancingData();
	}

	/**
	 * Get social distancing data according to the location provided as latitude and
	 * longitude
	 *
	 * @param inputLat
	 * @param inputLong
	 * @return
	 */
	@GetMapping("/socialDistancingDataBasedOnLatLong")
	public @ResponseBody SocialDistancingCSV getSocialDistancingDataBasedOnLatLong(
			@RequestParam(value = "inputLat") Double inputLat, @RequestParam(value = "inputLong") Double inputLong) {
		return QuaranUtil.getSocialDistancingDataBasedOnLatLong(inputLat, inputLong);
	}

	/**
	 * Get average violations for nearby localities with in a distance range as
	 * calculated from the input latitude and longitude. This is used to plot the
	 * graph Comparision of results with other areas
	 *
	 * @param inputLat
	 * @param inputLong
	 * @return
	 */
	@GetMapping("/distanceToAvgViolationsWithLocationForGraph")
	public @ResponseBody List<ContainmentToAvgViolationDayWise> getDistanceToAvgViolationsWithLocationMapping(
			@RequestParam(value = "inputLat") Double inputLat, @RequestParam(value = "inputLong") Double inputLong) {
		return AggregatorUtility.getDistanceToAvgViolationsWithLocationMapping(inputLat, inputLong);
	}

	/**
	 * Get average violations for nearby localities with in a distance range as
	 * calculated from the input latitude and longitude. This is used to plot the
	 * graph Comparision of results with other areas
	 *
	 * @param inputLat
	 * @param inputLong
	 * @return
	 */
	@GetMapping("/distanceToAvgViolationsWithLocationHighchartFormat")
	public @ResponseBody ContainmentToAvgViolationDayWise getDistanceToAvgViolationsWithLocationHighchartFormat(
			@RequestParam(value = "inputLat") Double inputLat, @RequestParam(value = "inputLong") Double inputLong) {
		return AggregatorUtility.getDistanceToAvgViolationsWithLocationHighchartFormat(inputLat, inputLong);
	}

	/**
	 * This shows average violations day wise for a locality that has come in input
	 * as latitude and longitude. This is to plot Pattern of violations across days
	 *
	 * @param inputLat
	 * @param inputLong
	 */
	@GetMapping("/patternOfViolationAccrossDaysForGraph")
	public @ResponseBody ContainmentToAvgViolationDayWise getPatternOfViolationAccrossDays(
			@RequestParam(value = "inputLat") Double inputLat, @RequestParam(value = "inputLong") Double inputLong) {
		return AggregatorUtility.getPatternOfViolationAccrossDays(inputLat, inputLong);
	}

	/**
	 * get percentage of localities lying in different zones(red, orange, yellow)
	 * for an area to be plotted on PieChart
	 *
	 * @param inputLat
	 * @param inputLong
	 * @return
	 */
	@GetMapping("/areaWiseZonePercentageForPieChart")
	public @ResponseBody Map<String, Float> getAreaWiseZoneCategoryPercentagePieChart(
			@RequestParam(value = "inputLat") Double inputLat, @RequestParam(value = "inputLong") Double inputLong) {
		return AggregatorUtility.getAreaWiseZoneCategoryPercentagePieChart(inputLat, inputLong);
	}

	/**
	 * Get All area names available
	 *
	 * @return
	 */
	@GetMapping("/areaNames")
	public @ResponseBody Set<String> getAllAreaNames() {
		return QuaranUtil.getAllAreaNames();
	}

	/**
	 * Get All localities along with latitude and longitude
	 *
	 * @return
	 */
	@GetMapping("/allLocalities")
	public @ResponseBody List<Locality> getAllLocalities() {
		return QuaranUtil.getAllLocalities();
	}

	/**
	 * Get All localities in an area along with latitude and longitude
	 *
	 * @param areaName
	 * @return
	 */
	@GetMapping("/localitiesMappedLatLong")
	public @ResponseBody Map<String, String> getLocalitiesMappedLatLong() {
		return QuaranUtil.getLocalitiesMappedLatLong();
	}

	@GetMapping("/areaWiseZoneCategorization")
	public @ResponseBody List<AreaWiseZoneCategroization> getAreaWiseZoneCategorization(
			@RequestParam(value = "inputLat") Double inputLat, @RequestParam(value = "inputLong") Double inputLong) {
		return AggregatorUtility.getAreaWiseZoneCategorization(inputLat, inputLong);
	}

	@GetMapping("/averageViolationsByLocation")
	public @ResponseBody Map<String, Double> getAverageViolationsByLocation() {
		return AggregatorUtility.getAverageViolationsByLocation();
	}

	@GetMapping("/containmentToAvgViolationsMapping")
	public @ResponseBody List<ContainmentToAvgViolationDayWise> getContainmentToAvgViolationsMapping(
			@RequestParam(value = "inputLat") Double inputLat, @RequestParam(value = "inputLong") Double inputLong) {
		return AggregatorUtility.getContainmentToAvgViolationsMapping(inputLat, inputLong);
	}

}
