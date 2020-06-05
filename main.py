import cv2
import csv
import os
import sys
import argparse
from network_model import model
from aux_functions import *
from convertmp4 import *
import tensorflow.compat.v1 as tf
tf.disable_v2_behavior()
import datetime



import boto3
from botocore.exceptions import NoCredentialsError

ACCESS_KEY = ''
SECRET_KEY = ''
BUCKET_NAME = ''
DATA_FILE_NAME = ''

# Suppress TF warnings
os.environ["TF_CPP_MIN_LOG_LEVEL"] = "3"

mouse_pts = []


def get_mouse_points(event, x, y, flags, param):
    # Used to mark 4 points on the frame zero of the video that will be warped
    # Used to mark 2 points on the frame zero of the video that are 6 feet away
    global mouseX, mouseY, mouse_pts
    if event == cv2.EVENT_LBUTTONDOWN:
        mouseX, mouseY = x, y
        cv2.circle(image, (x, y), 10, (0, 255, 255), 10)
        if "mouse_pts" not in globals():
            mouse_pts = []
        mouse_pts.append((x, y))
        print("Point detected")
        print(mouse_pts)
		
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


# Command-line input setup
parser = argparse.ArgumentParser(description="SocialDistancing")
parser.add_argument( "--videopath", type=str, help="Path to the video file")
parser.add_argument( "--region", type=str, help="Region" )

args = parser.parse_args()

input_video = args.videopath
input_region = args.region

video_name = input_video.split(".")
video_name = video_name[0]
video_name = video_name + '.avi'

# Define a DNN model
DNN = model()
# Get video handle
cap = cv2.VideoCapture(input_video)
height = int(cap.get(cv2.CAP_PROP_FRAME_HEIGHT))
width = int(cap.get(cv2.CAP_PROP_FRAME_WIDTH))
fps = int(cap.get(cv2.CAP_PROP_FPS))


scale_w = 1.2 / 2
scale_h = 4 / 2

Pedestrian_detect_video_writer =  "Output_" + video_name  
Pedestrian_bird_video_writer = "Output_bird" + video_name  
SOLID_BACK_COLOR = (41, 41, 41)
# Setuo video writer
#fourcc = cv2.VideoWriter_fourcc(*"XVID")
fourcc = cv2.VideoWriter_fourcc(*"mp4v")
output_movie = cv2.VideoWriter(Pedestrian_detect_video_writer, fourcc, fps, (width, height))
bird_movie = cv2.VideoWriter(
    Pedestrian_bird_video_writer, fourcc, fps, (int(width * scale_w), int(height * scale_h))
)
# Initialize necessary variables
frame_num = 0
total_pedestrians_detected = 0
total_six_feet_violations = 0
total_pairs = 0
abs_six_feet_violations = 0
pedestrian_per_sec = 0
sh_index = 1
sc_index = 1

# current date and time

timestamp = datetime.datetime.now()

if os.path.isfile(DATA_FILE_NAME):
    print ("File exist")
else:
    print ("File not exist")
    with open(DATA_FILE_NAME, mode='w', newline='' ) as socialdistancing_file:
	    social_writer = csv.writer(socialdistancing_file, delimiter=',',quotechar='"', quoting=csv.QUOTE_MINIMAL )
	    social_writer.writerow(['S_NO', 'Region', 'violation', 'pedestrian_detect', 'stayathome', 'social_distance','Total_Pedestrian_Detected','Start_TimeStamp', 'End_TimeStamp', 'Current_TimeStamp' ])
	
	
    
cv2.namedWindow("image")
cv2.setMouseCallback("image", get_mouse_points)
num_mouse_points = 0
first_frame_display = True

# Process each frame, until end of video
while cap.isOpened():
    count = 1
    frame_num += 1
    ret, frame = cap.read()

    if not ret:
        print("end of the video file...")
        break

    frame_h = frame.shape[0]
    frame_w = frame.shape[1]

    if frame_num == 1:
        # Ask user to mark parallel points and two points 6 feet apart. Order bl, br, tr, tl, p1, p2
        while True:
            image = frame
            cv2.imshow("image", image)
            cv2.waitKey(1)
            if len(mouse_pts) == 7:
                cv2.destroyWindow("image")
                break
            first_frame_display = False
        four_points = mouse_pts

        # Get perspective
        M, Minv = get_camera_perspective(frame, four_points[0:4])
        pts = src = np.float32(np.array([four_points[4:]]))
        warped_pt = cv2.perspectiveTransform(pts, M)[0]
        d_thresh = np.sqrt(
            (warped_pt[0][0] - warped_pt[1][0]) ** 2
            + (warped_pt[0][1] - warped_pt[1][1]) ** 2
        )
        bird_image = np.zeros(
            (int(frame_h * scale_h), int(frame_w * scale_w), 3), np.uint8
        )

        bird_image[:] = SOLID_BACK_COLOR
        pedestrian_detect = frame

    print("Processing frame: ", frame_num)

    # draw polygon of ROI
    pts = np.array(
        [four_points[0], four_points[1], four_points[3], four_points[2]], np.int32
    )
    cv2.polylines(frame, [pts], True, (0, 255, 255), thickness=4)

    # Detect person and bounding boxes using DNN
    pedestrian_boxes, num_pedestrians = DNN.detect_pedestrians(frame)
        
    if len(pedestrian_boxes) > 0:
        distance = 0
        pedestrian_detect = plot_pedestrian_boxes_on_image(frame, pedestrian_boxes, distance )
        warped_pts, bird_image = plot_points_on_bird_eye_view(
            frame, pedestrian_boxes, M, scale_w, scale_h
        )
        six_feet_violations, ten_feet_violations, pairs = plot_lines_between_nodes(
            warped_pts, bird_image, d_thresh       
        )

        #if six_feet_violations >=1 and num_pedestrians >=2:
        #   voilation_distance = 1
        #    pedestrian_detect = plot_pedestrian_boxes_on_image(frame, pedestrian_boxes, voilation_distance ) 
        #else:
        #    voilation_distance = 0
        #    pedestrian_detect = plot_pedestrian_boxes_on_image(frame, pedestrian_boxes, voilation_distance )    		
		
                
        total_pedestrians_detected += num_pedestrians
        total_pairs += pairs

        total_six_feet_violations += six_feet_violations / fps
        abs_six_feet_violations += six_feet_violations
        pedestrian_per_sec, sh_index = calculate_stay_at_home_index(
            total_pedestrians_detected, frame_num, fps
        )
	
    
    current_timestamp = datetime.datetime.now()   

    last_h = 50	

    text = "violations: " + str(int(total_six_feet_violations))
    pedestrian_detect, last_h = put_text(pedestrian_detect, text, text_offset_y=last_h)

   
    cv2.imshow("Street Cam", pedestrian_detect)
    cv2.waitKey(1)
    output_movie.write(pedestrian_detect)
    bird_movie.write(bird_image)
	
    end_timestamp = datetime.datetime.now()   
	
    with open(r'social_distancing_data.csv', mode='a', newline='') as socialdistancing_file_w:
        social_writer = csv.writer(socialdistancing_file_w, delimiter=',',quotechar='"', quoting=csv.QUOTE_MINIMAL)
        social_writer.writerow([frame_num,input_region,str(int(total_six_feet_violations)), pedestrian_detect, str(np.round(100 * sh_index, 1)) + "%", str(np.round(100 * sc_index, 1)) + "%", total_pedestrians_detected, timestamp, end_timestamp, current_timestamp ])
   
	
upload_fileName = convertFile(Pedestrian_detect_video_writer, TargetFormat.MP4)
	
uploaded = upload_to_aws(DATA_FILE_NAME, BUCKET_NAME, DATA_FILE_NAME)
uploaded = upload_to_aws(upload_fileName, BUCKET_NAME, upload_fileName)
	

    