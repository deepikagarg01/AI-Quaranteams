package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.javainuse.domain.AreaWiseZoneCategroization;
import com.javainuse.domain.ContainmentToAvgViolationDayWise;
import com.javainuse.domain.SocialDistancingCSV;

public class AggregatorUtility {

	/**
	 * Get average violations for nearby localities with in a distance range as
	 * calculated from the input latitude and longitude. This is used to plot the
	 * graph Comparison of results with other areas
	 *
	 * @param inputLat
	 * @param inputLong
	 * @return
	 */
	public static List<ContainmentToAvgViolationDayWise> getDistanceToAvgViolationsWithLocationMapping(Double inputLat,
			Double inputLong) {
		final List<ContainmentToAvgViolationDayWise> containmentToAvgViolationDayWises = new ArrayList<>();

		/**
		 * Grouping all the same locality data
		 */
		final Map<String, List<SocialDistancingCSV>> map = QuaranUtil.getLocalityNameMappedSocialDistancingCsvs();

		for (final Entry<String, List<SocialDistancingCSV>> entry : map.entrySet()) {
			final List<SocialDistancingCSV> groupedByLocalityCSV = entry.getValue();

			final TreeMap<String, Double> averages = groupedByLocalityCSV.stream()
					.collect(Collectors.groupingBy(e -> e.getLocality(), TreeMap::new,
							Collectors.averagingDouble(SocialDistancingCSV::getNoOfViolations)));

			/**
			 * getting distinct elements to find the distance from given latitude and long
			 * for all the available locations
			 */
			final Double distance = DistanceCalculator.distance(groupedByLocalityCSV.get(0).getLatitude(),
					groupedByLocalityCSV.get(0).getLongitude(), inputLat, inputLong, 'K');

			final ContainmentToAvgViolationDayWise avgViolationDayWise = new ContainmentToAvgViolationDayWise(
					entry.getKey(), averages, distance);

			containmentToAvgViolationDayWises.add(avgViolationDayWise);

		}

		return containmentToAvgViolationDayWises;

	}

	public static ContainmentToAvgViolationDayWise getDistanceToAvgViolationsWithLocationHighchartFormat(
			Double inputLat, Double inputLong) {
		final List<ContainmentToAvgViolationDayWise> containmentToAvgViolationDayWises = new ArrayList<>();

		/**
		 * Grouping all the same locality data
		 */
		final Map<String, List<SocialDistancingCSV>> map = QuaranUtil.getLocalityNameMappedSocialDistancingCsvs();

		for (final Entry<String, List<SocialDistancingCSV>> entry : map.entrySet()) {
			final List<SocialDistancingCSV> groupedByLocalityCSV = entry.getValue();

			final TreeMap<String, Double> averages = groupedByLocalityCSV.stream()
					.collect(Collectors.groupingBy(e -> e.getLocality(), TreeMap::new,
							Collectors.averagingDouble(SocialDistancingCSV::getNoOfViolations)));

			/**
			 * getting distinct elements to find the distance from given latitude and long
			 * for all the available locations
			 */
			final Double distance = DistanceCalculator.distance(groupedByLocalityCSV.get(0).getLatitude(),
					groupedByLocalityCSV.get(0).getLongitude(), inputLat, inputLong, 'K');

			final ContainmentToAvgViolationDayWise avgViolationDayWise = new ContainmentToAvgViolationDayWise(
					entry.getKey(), averages, distance);
			final Double violations = averages.get(entry.getKey());
			final Map<Double, Double> distanceViolations = new HashMap<>();
			distanceViolations.put(distance, violations);
			avgViolationDayWise.setDistanceViolations(distanceViolations);

			containmentToAvgViolationDayWises.add(avgViolationDayWise);

		}
		final ContainmentToAvgViolationDayWise avgViolationDayWise = new ContainmentToAvgViolationDayWise();
		for (final ContainmentToAvgViolationDayWise wise : containmentToAvgViolationDayWises) {
			avgViolationDayWise.addToContToAvgViolationsDayWises(wise.getContToAvgViolationsDayWise());
			avgViolationDayWise.addDistance(wise.getDistanceFromInputLocation());
			avgViolationDayWise.addToDistanceViolationList(wise.getDistanceViolations());

		}

		return avgViolationDayWise;

	}

	/**
	 * This shows average violations day wise for a locality that has come in input
	 * as latitude and longitude. This is to plot Pattern of violations across days
	 *
	 * @param inputLat
	 * @param inputLong
	 */
	public static ContainmentToAvgViolationDayWise getPatternOfViolationAccrossDays(Double inputLat, Double inputLong) {
		ContainmentToAvgViolationDayWise avgViolationDayWise = null;
		final Map<String, SocialDistancingCSV> latLongMappedLOcalities = QuaranUtil
				.getLatLongMappedSocialDistancingCsvs();
		final SocialDistancingCSV socialDistancingCSV = latLongMappedLOcalities.get(inputLat + "/" + inputLong);
		if (socialDistancingCSV != null) {
			final String locality = socialDistancingCSV.getLocality();
			final Map<String, List<SocialDistancingCSV>> localityMappedLOcalities = QuaranUtil
					.getLocalityNameMappedSocialDistancingCsvs();

			final List<SocialDistancingCSV> groupedByLocalityCSV = localityMappedLOcalities.get(locality);

			final TreeMap<String, Double> averages = groupedByLocalityCSV.stream()
					.collect(Collectors.groupingBy(e -> e.getDateFromTimestamp(), TreeMap::new,
							Collectors.averagingDouble(SocialDistancingCSV::getNoOfViolations)));

			avgViolationDayWise = new ContainmentToAvgViolationDayWise(locality, averages, null);
		}
		return avgViolationDayWise;
	}

	/**
	 * get percentage of localities lying in differnet zones(red, orange, yellow)
	 * for an area to be plotted on PieChart
	 *
	 * @param inputLat
	 * @param inputLong
	 * @return
	 */
	public static Map<String, Float> getAreaWiseZoneCategoryPercentagePieChart(Double inputLat, Double inputLong) {
		Map<String, Float> zonePercentage = null;
		final Map<String, SocialDistancingCSV> latLongMappedLOcalities = QuaranUtil
				.getLatLongMappedSocialDistancingCsvs();
		final SocialDistancingCSV socialDistancingCSV = latLongMappedLOcalities.get(inputLat + "/" + inputLong);
		if (socialDistancingCSV != null) {
			final String areaName = socialDistancingCSV.getAreaName();
			final Map<String, List<SocialDistancingCSV>> areaMappedLOcalities = QuaranUtil
					.getAreaNameMappedSocialDistancingCsvs();
			final List<SocialDistancingCSV> groupedByAreaCSV = areaMappedLOcalities.get(areaName);
			final Map<String, Long> zoneCount = groupedByAreaCSV.stream()
					.collect(Collectors.groupingBy(p -> p.getZoneCategory(), Collectors.counting()));
			zonePercentage = new HashMap<>();
			for (final Entry<String, Long> entry : zoneCount.entrySet()) {
				final float percentage = entry.getValue() * 100 / groupedByAreaCSV.size();
				zonePercentage.put(entry.getKey(), percentage);
			}
		}
		return zonePercentage;

	}

	public static Map<String, Double> getAverageViolationsByLocation() {
		final List<SocialDistancingCSV> distancingCSVs = QuaranUtil.getSocialDistancingData();
		// Get distinct objects by key
		final Map<String, Double> sum = distancingCSVs.stream().collect(Collectors.groupingBy(
				SocialDistancingCSV::getLocality, Collectors.averagingDouble(SocialDistancingCSV::getNoOfViolations)));
		return sum;
	}

	public static List<ContainmentToAvgViolationDayWise> getContainmentToAvgViolationsMapping(Double inputLat,
			Double inputLong) {
		final List<ContainmentToAvgViolationDayWise> containmentToAvgViolationDayWises = new ArrayList<>();

		/**
		 * Grouping all the same locality data
		 */
		final Map<String, List<SocialDistancingCSV>> map = QuaranUtil.getLocalityNameMappedSocialDistancingCsvs();

		for (final Entry<String, List<SocialDistancingCSV>> entry : map.entrySet()) {
			final List<SocialDistancingCSV> groupedByLocalityCSV = entry.getValue();

			final TreeMap<String, Double> averages = groupedByLocalityCSV.stream()
					.collect(Collectors.groupingBy(e -> e.getHourFromTimestamp(), TreeMap::new,
							Collectors.averagingDouble(SocialDistancingCSV::getNoOfViolations)));

			/**
			 * getting distinct elements to find the distance from given latitude and long
			 * for all the available locations
			 */
			final Double distance = DistanceCalculator.distance(groupedByLocalityCSV.get(0).getLatitude(),
					groupedByLocalityCSV.get(0).getLongitude(), inputLat, inputLong, 'K');

			final ContainmentToAvgViolationDayWise avgViolationDayWise = new ContainmentToAvgViolationDayWise(
					entry.getKey(), averages, distance);

			containmentToAvgViolationDayWises.add(avgViolationDayWise);

		}

		return containmentToAvgViolationDayWises;

	}

	public static List<AreaWiseZoneCategroization> getAreaWiseZoneCategorization(Double inputLat, Double inputLong) {

		final Map<String, SocialDistancingCSV> latLongMappedLOcalities = QuaranUtil
				.getLatLongMappedSocialDistancingCsvs();
		final SocialDistancingCSV socialDistancingCSV = latLongMappedLOcalities.get(inputLat + "/" + inputLong);
		final String areaName = socialDistancingCSV.getAreaName();
		final Map<String, List<SocialDistancingCSV>> areaMappedLOcalities = QuaranUtil
				.getAreaNameMappedSocialDistancingCsvs();
		final List<SocialDistancingCSV> groupedByAreaCSV = areaMappedLOcalities.get(areaName);

		final TreeMap<String, Double> averages = groupedByAreaCSV.stream()
				.collect(Collectors.groupingBy(e -> e.getZoneCategory(), TreeMap::new,
						Collectors.averagingDouble(SocialDistancingCSV::getNoOfViolations)));
		final List<AreaWiseZoneCategroization> areaWiseZoneCategroizations = new ArrayList<>();
		final Map<String, List<SocialDistancingCSV>> map = QuaranUtil.getLocalityNameMappedSocialDistancingCsvs();
		for (final Entry<String, Double> entry : averages.entrySet()) {
			final AreaWiseZoneCategroization categroization = new AreaWiseZoneCategroization(areaName, entry.getKey(),
					entry.getValue(), map.get(entry.getKey()).get(0).getZoneCategory());
			areaWiseZoneCategroizations.add(categroization);
		}
		return areaWiseZoneCategroizations;

	}

	// Utility function
	public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
		final Map<Object, Boolean> map = new ConcurrentHashMap<>();
		return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

	/*
	 * final List<SocialDistancingCSV> distinctElements = distancingCSVs.stream()
	 * .filter(distinctByKey(p -> p.getLocality())).collect(Collectors.toList());
	 *
	 * for (final SocialDistancingCSV distinctElement : distinctElements) { final
	 * Double distance = DistanceCalculator.distance(distinctElement.getLatitude(),
	 * distinctElement.getLongitude(), inputLat, inputLong, 'K');
	 * distinctElement.setDistanceFromInput(distance);
	 *
	 * final Map<String, SocialDistancingCSV> map3 = distancingCSVs.stream()
	 * .collect(Collectors.toMap(x -> x.getLocality(), x -> x)); } final Map<String,
	 * Set<SocialDistancingCSV>> map2 = distancingCSVs.stream()
	 * .collect(Collectors.toMap(SocialDistancingCSV::getLatitude,
	 * Collectors.toSet()));
	 *
	 * distancingCSVs.stream().collect(Collectors.toMap(SocialDistancingCSV::
	 * getLocality, DistanceCalculator.distance(SocialDistancingCSV::getLatitude,
	 * null, inputLat, inputLong, 'K')));
	 *
	 */

	// groupedByLocalityCSV.stream().collect(Collectors.groupingBy(SocialDistancingCSV::getHourFromTimestamp,
	// Collectors.averagingDouble(SocialDistancingCSV::getNoOfViolations)));

	/*
	 * final Map<String, String> contToAvgViolationsDayWise = new HashMap<>(); for
	 * (final SocialDistancingCSV csv : groupedByLocalityCSV) { Double violations =
	 * 0D; int count = 0; if
	 * (contToAvgViolationsDayWise.containsKey(csv.getHourFromTimestamp())) { final
	 * String temp = contToAvgViolationsDayWise.get(csv.getHourFromTimestamp());
	 * violations = Double.parseDouble(temp.substring(0, temp.indexOf('_'))) +
	 * csv.getNoOfViolations(); count =
	 * Integer.parseInt(temp.substring(temp.indexOf('_') + 1, temp.length())) + 1; }
	 * else { violations = violations + csv.getNoOfViolations(); count = count + 1;
	 * } contToAvgViolationsDayWise.remove(csv.getHourFromTimestamp());
	 * contToAvgViolationsDayWise.put(csv.getHourFromTimestamp(), violations + "_" +
	 * count); }
	 */
}
