package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.javainuse.domain.Locality;
import com.javainuse.domain.SocialDistancingCSV;

public class QuaranUtil {

	private static String bucketName = "deepikahackathon";
	private static String key = "social_distancing_data.csv";

	private static final AWSCredentials credentials;

	private static String CSV_KEY = "social_distancing_dataset.csv";

	static {
		// put your accesskey and secretkey here
		credentials = new BasicAWSCredentials("YourAccessKey", "YourSecretKey");
	}

	/**
	 * Complete data set for the application, created by reading csv file from S3
	 *
	 * @return
	 */
	public static List<SocialDistancingCSV> getSocialDistancingData() {
		String line = "";
		final String splitBy = ",";
		BufferedReader reader = null;
		int id = 0;
		final List<SocialDistancingCSV> socialDistancingCSVs = new ArrayList<>();

		final AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(Regions.AP_SOUTHEAST_1)
				.build();
		final S3Object obj = s3Client.getObject(new GetObjectRequest(bucketName, CSV_KEY));
		final S3ObjectInputStream stream = obj.getObjectContent();
		try {
			reader = new BufferedReader(new InputStreamReader(stream));
			while ((line = reader.readLine()) != null) {
				id++;
				if (id > 1) {
					final String[] data = line.split(splitBy);
					getSocialDistancingCSVObject(data, id, socialDistancingCSVs);
				} else {
					id = 1;
				}
			}
			System.out.println();
		} catch (final IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
		}
		return socialDistancingCSVs;
	}

	/**
	 * Get social distancing data according to the location provided as latitude and
	 * longitude
	 *
	 * @param inputLat
	 * @param inputLong
	 * @return
	 */
	public static SocialDistancingCSV getSocialDistancingDataBasedOnLatLong(Double inputLat, Double inputLong) {
		final Map<String, SocialDistancingCSV> latLongMappedLOcalities = getLatLongMappedSocialDistancingCsvs();
		final SocialDistancingCSV socialDistancingCSV = latLongMappedLOcalities.get(inputLat + "/" + inputLong);
		return socialDistancingCSV;

	}

	/**
	 * Get All area names available
	 *
	 * @return
	 */
	public static Set<String> getAllAreaNames() {
		final Map<String, List<SocialDistancingCSV>> map = QuaranUtil.getAreaNameMappedSocialDistancingCsvs();
		return map.keySet();

	}

	/**
	 * Get All localities along with latitude and longitude
	 *
	 * @param areaName
	 * @return
	 */
	public static List<Locality> getAllLocalities() {
		final List<Locality> localities = new ArrayList<>();
		final Map<String, List<SocialDistancingCSV>> map = QuaranUtil.getLocalityNameMappedSocialDistancingCsvs();
		for (final String localityName : map.keySet()) {
			final SocialDistancingCSV csv = map.get(localityName).get(0);
			localities.add(new Locality(csv.getLocality(), csv.getLatitude() + "/" + csv.getLongitude()));
		}

		return localities;
	}

	/**
	 * Get All localities in an area along with latitude and longitude
	 *
	 * @param areaName
	 * @return
	 */
	public static Map<String, String> getLocalitiesMappedLatLong() {
		final Map<String, String> localityToLatitudeLongitudeData = new TreeMap<>();
		final Map<String, List<SocialDistancingCSV>> map = QuaranUtil.getLocalityNameMappedSocialDistancingCsvs();
		for (final String localityName : map.keySet()) {
			final SocialDistancingCSV csv = map.get(localityName).get(0);
			localityToLatitudeLongitudeData.put(csv.getLocality(), csv.getLatitude() + "/" + csv.getLongitude());
		}

		return localityToLatitudeLongitudeData;
	}

	public static void displayTextInputStream(InputStream input) throws IOException {
		// Read the text input stream one line at a time and display each line.
		final BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		String line = null;
		while ((line = reader.readLine()) != null) {
			System.out.println(line);
		}
		System.out.println();
	}

	public static Map<String, List<SocialDistancingCSV>> getLocalityNameMappedSocialDistancingCsvs() {
		final List<SocialDistancingCSV> distancingCSVs = getSocialDistancingData();
		/**
		 * Grouping all the same locality data
		 */
		final Map<String, List<SocialDistancingCSV>> map = new HashMap<>();

		for (final SocialDistancingCSV csv : distancingCSVs) {
			if (map.containsKey(csv.getLocality())) {
				map.get(csv.getLocality()).add(csv);
			} else {
				final List<SocialDistancingCSV> list = new ArrayList<>();
				list.add(csv);
				map.put(csv.getLocality(), list);
			}
		}

		return map;
	}

	public static Map<String, List<SocialDistancingCSV>> getAreaNameMappedSocialDistancingCsvs() {
		final List<SocialDistancingCSV> distancingCSVs = getSocialDistancingData();
		/**
		 * Grouping all the same area data
		 */
		final Map<String, List<SocialDistancingCSV>> map = new HashMap<>();

		for (final SocialDistancingCSV csv : distancingCSVs) {
			final String area = csv.getAreaName();
			if (map.containsKey(area)) {
				map.get(area).add(csv);
			} else {
				final List<SocialDistancingCSV> list = new ArrayList<>();
				list.add(csv);
				map.put(area, list);
			}
		}

		return map;
	}

	public static Map<String, SocialDistancingCSV> getLatLongMappedSocialDistancingCsvs() {
		final List<SocialDistancingCSV> distancingCSVs = getSocialDistancingData();
		/**
		 * Grouping all the same area data
		 */
		final Map<String, SocialDistancingCSV> map = new HashMap<>();

		for (final SocialDistancingCSV csv : distancingCSVs) {
			final String latLong = csv.getLatitude() + "/" + csv.getLongitude();
			map.put(latLong, csv);
		}
		return map;
	}

	/**
	 * Helper method to convert data read from csv to SocialDistancingCSV object
	 *
	 * @param data
	 * @param id
	 * @param socialDistancingCSVs
	 * @return
	 */
	private static SocialDistancingCSV getSocialDistancingCSVObject(String[] data, int id,
			List<SocialDistancingCSV> socialDistancingCSVs) {
		SocialDistancingCSV csv = null;
		if (data != null && data.length > 0) {
			csv = new SocialDistancingCSV();
			csv.setId(id + "");
			csv.setTimestamp(data[0]);
			if (data[0] != null) {
				final String timestampTillhours = data[0].substring(0, 14);
				final String minutes = data[0].substring(15, 17);
				final String date = data[0].substring(0, 11);
				csv.setHourFromTimestamp(timestampTillhours);
				csv.setMinsFromTimestamp(minutes);
				csv.setDateFromTimestamp(date);
			}
			csv.setPeopleInFrame(data[1]);
			csv.setNoOfViolations(data[2] != null ? Double.parseDouble(data[2]) : 0);
			csv.setLocality(data[3]);
			if (data[3] != null) {
				final String areaName = data[3].split("-")[1];
				csv.setAreaName(areaName.trim());
			}
			csv.setLatitude(data[4] != null ? Double.parseDouble(data[4]) : 0);
			csv.setLongitude(data[5] != null ? Double.parseDouble(data[5]) : 0);
			csv.setLocalityType(data[6]);
			csv.setZoneCategory(data[7]);
			csv.setContainmentZoneDistance(data[8]);
			csv.setActiveCovidCases(data[9]);
			socialDistancingCSVs.add(csv);
		}
		return csv;
	}

	public static void readFromS3() {
		S3Object fullObject = null;
		final S3Object objectPortion = null, headerOverrideObject = null;
		try {
			final AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
					.withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(Regions.AP_SOUTHEAST_1)
					.build();

			// Get an object and print its contents.
			System.out.println("Downloading an object");
			fullObject = s3Client.getObject(new GetObjectRequest(bucketName, key));
			System.out.println("Content-Type: " + fullObject.getObjectMetadata().getContentType());
			System.out.println("Content: ");
			displayTextInputStream(fullObject.getObjectContent());

		} catch (final AmazonServiceException e) {
			// The call was transmitted successfully, but Amazon S3 couldn't
			// process
			// it, so it returned an error response.
			e.printStackTrace();
		} catch (final SdkClientException e) {
			// Amazon S3 couldn't be contacted for a response, or the client
			// couldn't parse the response from Amazon S3.
			e.printStackTrace();
		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
			// To ensure that the network connection doesn't remain open, close
			// any open
			// input streams.
			try {
				if (fullObject != null) {
					fullObject.close();
				}
				if (objectPortion != null) {
					objectPortion.close();
				}
				if (headerOverrideObject != null) {
					headerOverrideObject.close();
				}
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static byte[] getFile(Double inputLat, Double inputLong, String fileName) {
		// S3Object obj = amazonS3Client.getObject(defaultBucketName,
		// defaultBaseFolder+"/"+key);
		final AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(Regions.AP_SOUTHEAST_1)
				.build();
		if (fileName == null || fileName.equals("")) {
			fileName = key;
		}
		final S3Object obj = s3Client.getObject(new GetObjectRequest(bucketName, fileName));
		final S3ObjectInputStream stream = obj.getObjectContent();
		try {
			final byte[] content = IOUtils.toByteArray(stream);
			obj.close();
			return content;
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
