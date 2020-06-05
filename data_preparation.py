
from random import randrange
import time
import csv
import random
import boto3
import pandas as pd
from botocore.exceptions import NoCredentialsError


#1.  to return random timestamp within a range
def randomize_time(start_timestamp,end_timestamp):
    return time.strftime('%b %d %Y %H:%M:%S', time.localtime(randrange(start_timestamp,end_timestamp, step=1)))

#2. to return a random string out of given list
def list_random(ran):
    return ran[random.randint(0,len(ran)-1)]

def upload_to_aws(local_file, bucket, s3_file):
    s3 = boto3.client('s3', aws_access_key_id=ACCESS_KEY,
                      aws_secret_access_key=SECRET_KEY)
    try:
        s3.upload_file(local_file, bucket, s3_file)
        print("Upload Successful")
        return True
    except FileNotFoundError:
        print("The file was not found")
        return False
    except NoCredentialsError:
        print("Credentials not available")
        return False 

ACCESS_KEY = 'AKIAYFXRICJOSDCHJ34R'
SECRET_KEY = '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' 

#3. to return a random value for within a given range of latitude
dict_locality_details={'Millenium village-Alpha1':[28.402,77.498,10,'Residential','orange','20'],
          'Edana-Alpha1':[28.472,77.488,0,'Residential','red','75'], 
          'Arsh Complex-Alpha1':[28.463,77.489,1,'Commercial','red','58'],
          'Alpha commercial belt- Alpha1':[28.455,77.499,12,'Commercial','orange','35'],
          'Central park-Alpha1':[28.442,77.501,7,'Park','green','15'],
          'St. Josephs school-Alpha1':[28.432,77.483,9,'School','green','2'],
          'City Park-Alpha2':[28.504,77.502,15,'Park','green','3'],
          'Sommerville school-Alpha2':[28.496,77.507,9.5,'School','orange','32'],
          'Golf Vista-Alpha2':[28.501,77.510,3,'Residential','red','55'],
          'Oasis homes-Alpha2':[28.486,77.513,13,'Residential','green','0'],
          'Elder Homes-Alpha2':[28.491,77.508,19,'Old-Age Home','orange','23']}

start_timestamp = time.mktime(time.strptime('May 1 2020  07:00:00', '%b %d %Y %H:%M:%S'))
end_timestamp = time.mktime(time.strptime('Jun 1 2020  19:33:00', '%b %d %Y %H:%M:%S'))

start_timestamp_3 = time.mktime(time.strptime('Jun 3 2020  17:00:00', '%b %d %Y %H:%M:%S'))
end_timestamp_3 = time.mktime(time.strptime('Jun 3 2020  21:33:00', '%b %d %Y %H:%M:%S'))

start_timestamp_4 = time.mktime(time.strptime('Jun 4 2020  07:00:00', '%b %d %Y %H:%M:%S'))
end_timestamp_4 = time.mktime(time.strptime('Jun 4 2020  19:33:00', '%b %d %Y %H:%M:%S'))

start_timestamp_5 = time.mktime(time.strptime('Jun 5 2020  07:00:00', '%b %d %Y %H:%M:%S'))
end_timestamp_5 = time.mktime(time.strptime('Jun 5 2020  19:33:00', '%b %d %Y %H:%M:%S'))

start_timestamp_6 = time.mktime(time.strptime('May 1 2020  07:00:00', '%b %d %Y %H:%M:%S'))
end_timestamp_6 = time.mktime(time.strptime('Jun 1 2020  19:33:00', '%b %d %Y %H:%M:%S'))
counter=range(200);
counter_current=range(200);
fields =['Timestamp', 'People in frame', 'No. of violations', 'Locality','Latitude','Longitude','Locality Type','Zone Category','Distance from containment area', 'Current Active Covid-19 Cases']   
with open('social_distancing_dataset.csv', 'w', newline='') as file:
   sowriter = csv.writer(file, dialect='excel')
   sowriter.writerow(fields)
   for x1 in counter:
     locality=list_random(['Millenium village-Alpha1','Edana-Alpha1', 
                          'Arsh Complex-Alpha1', 'Alpha commercial belt- Alpha1','Central park-Alpha1',
                          'St. Josephs school-Alpha1', 'City Park-Alpha2','Sommerville school-Alpha2',
                          'Golf Vista-Alpha2','Oasis homes-Alpha2','Elder Homes-Alpha2'])
     latitude=dict_locality_details[locality][0]
     longitude=dict_locality_details[locality][1]
     distance=dict_locality_details[locality][2]
     locality_type=dict_locality_details[locality][3]
     zone_category=dict_locality_details[locality][4]
     active_cases=dict_locality_details[locality][5]
     sowriter.writerow([randomize_time(start_timestamp,end_timestamp),
                   random.randint(0,50),
                   random.randint(0,25),
                   locality,latitude,longitude,locality_type,
                   zone_category,distance,active_cases])
   for x2 in counter_current:
     locality=list_random(['Millenium village-Alpha1','Edana-Alpha1', 
                          'Arsh Complex-Alpha1', 'Alpha commercial belt- Alpha1','Central park-Alpha1',
                          'St. Josephs school-Alpha1', 'City Park-Alpha2','Sommerville school-Alpha2',
                          'Golf Vista-Alpha2','Oasis homes-Alpha2','Elder Homes-Alpha2'])
     latitude=dict_locality_details[locality][0]
     longitude=dict_locality_details[locality][1]
     distance=dict_locality_details[locality][2]
     locality_type=dict_locality_details[locality][3]
     zone_category=dict_locality_details[locality][4]
     active_cases=dict_locality_details[locality][5]
     sowriter.writerow([randomize_time(start_timestamp_3,end_timestamp_3),
                   random.randint(0,50),
                   random.randint(0,25),
                   locality,latitude,longitude,locality_type,
                   zone_category,distance,active_cases])
   for x2 in counter_current:
     locality=list_random(['Millenium village-Alpha1','Edana-Alpha1', 
                          'Arsh Complex-Alpha1', 'Alpha commercial belt- Alpha1','Central park-Alpha1',
                          'St. Josephs school-Alpha1', 'City Park-Alpha2','Sommerville school-Alpha2',
                          'Golf Vista-Alpha2','Oasis homes-Alpha2','Elder Homes-Alpha2'])
     latitude=dict_locality_details[locality][0]
     longitude=dict_locality_details[locality][1]
     distance=dict_locality_details[locality][2]
     locality_type=dict_locality_details[locality][3]
     zone_category=dict_locality_details[locality][4]
     active_cases=dict_locality_details[locality][5]
     sowriter.writerow([randomize_time(start_timestamp_4,end_timestamp_4),
                   random.randint(0,50),
                   random.randint(0,25),
                   locality,latitude,longitude,locality_type,
                   zone_category,distance,active_cases])
   for x2 in counter_current:
     locality=list_random(['Millenium village-Alpha1','Edana-Alpha1', 
                          'Arsh Complex-Alpha1', 'Alpha commercial belt- Alpha1','Central park-Alpha1',
                          'St. Josephs school-Alpha1', 'City Park-Alpha2','Sommerville school-Alpha2',
                          'Golf Vista-Alpha2','Oasis homes-Alpha2','Elder Homes-Alpha2'])
     latitude=dict_locality_details[locality][0]
     longitude=dict_locality_details[locality][1]
     distance=dict_locality_details[locality][2]
     locality_type=dict_locality_details[locality][3]
     zone_category=dict_locality_details[locality][4]
     active_cases=dict_locality_details[locality][5]
     sowriter.writerow([randomize_time(start_timestamp_5,end_timestamp_5),
                   random.randint(0,50),
                   random.randint(0,25),
                   locality,latitude,longitude,locality_type,
                   zone_category,distance,active_cases])
   for x2 in counter_current:
     locality=list_random(['Millenium village-Alpha1','Edana-Alpha1', 
                          'Arsh Complex-Alpha1', 'Alpha commercial belt- Alpha1','Central park-Alpha1',
                          'St. Josephs school-Alpha1', 'City Park-Alpha2','Sommerville school-Alpha2',
                          'Golf Vista-Alpha2','Oasis homes-Alpha2','Elder Homes-Alpha2'])
     latitude=dict_locality_details[locality][0]
     longitude=dict_locality_details[locality][1]
     distance=dict_locality_details[locality][2]
     locality_type=dict_locality_details[locality][3]
     zone_category=dict_locality_details[locality][4]
     active_cases=dict_locality_details[locality][5]
     sowriter.writerow([randomize_time(start_timestamp_6,end_timestamp_6),
                   random.randint(0,50),
                   random.randint(0,25),
                   locality,latitude,longitude,locality_type,
                   zone_category,distance,active_cases])
uploaded = upload_to_aws('social_distancing_dataset.csv', 'deepikahackathon', 'social_distancing_dataset.csv') 