import json
import pymysql
import base64
import boto3
from io import BytesIO
from facepplib import FacePP, exceptions
from datetime import datetime

# Database and API credentials
hostname = "att2.c5gcm40minnf.eu-north-1.rds.amazonaws.com"
user = "dev"
password = "devnath123"
db = "att2"
hostname1 = "photos.c5gcm40minnf.eu-north-1.rds.amazonaws.com"
user1 = "dev"
password1 = "Devnath123456"
db1 = "photos"
api_key = 'xQLsTmMyqp1L2MIt7M3l0h-cQiy0Dwhl'
api_secret = 'TyBSGw8NBEP9Tbhv_JbQM18mIlorY6-D'

def connect_to_db():
    print("Connecting to DB...")
    return pymysql.connect(host=hostname, user=user, passwd=password, db=db, connect_timeout=60)

def connect_to_db1():
    print("Connecting to DB1...")
    return pymysql.connect(host=hostname1, user=user1, passwd=password1, db=db1, connect_timeout=60)

def face_comparing(app, Image1, Image2): 
    print("Comparing faces...")
    cmp_ = app.compare.get(image_url1=Image1, image_url2=Image2)
    return cmp_.confidence 

def upload_image_to_s3(base64_string, bucket):
    print("Uploading image to S3...")
    image_data = base64.b64decode(base64_string)
    unique_filename = f"uploaded_image_{datetime.now().strftime('%Y%m%d%H%M%S')}.jpg"
    
    # Create an S3 client
    s3_client = boto3.client('s3')
    print("Connecting to S3 using boto3...")
    print("\n The error will come in the line below as timeout...")
    # Upload the image data to S3
    try:
        s3_client.put_object(Body=image_data,Bucket=bucket,Key=unique_filename,ContentType='image/jpg')
        # Generate the URL for the uploaded image
        image_url = f"https://{bucket}.s3.amazonaws.com/{unique_filename}"
        print(f"Image uploaded to S3 at {image_url}")
        return image_url
    except Exception as e:
        print(f"Error uploading image: {str(e)}")
        return None
def lambda_handler(event, context):
    print("Lambda function invoked.")
    app_ = FacePP(api_key=api_key, api_secret=api_secret)
    req_body = json.loads(event['body'])
    base64_image = req_body["photo"]
    roll = req_body["rollno"]
    s3_bucket = "mitstudents"
    
    print("Starting image upload to S3.")
    image_url = upload_image_to_s3(base64_image, s3_bucket)
    
    conn1 = connect_to_db1()
    with conn1.cursor() as curs:
        print(f"Fetching image for roll number {roll}")
        curs.execute("SELECT image1 FROM studentimages WHERE rollno = %s", (roll,))
        result = curs.fetchone()
        if result:
            target = result[0]
            print(f"Fetched image URL from DB: {target}")
        else:
            print("Student image not found.")
            return {
                'statusCode': 404,
                'body': json.dumps({'error': 'Student image not found'})
            }
    conn1.close()

    print("Starting face comparison.")
    confidence_score = face_comparing(app_, image_url, target)
    print(f"Face comparison confidence score: {confidence_score}")
    
    if confidence_score < 50.0:
        print("Face not recognized.")
        return {
            'statusCode': 200,
            'body': json.dumps({'message': 'Face not recognized', 'confidence': confidence_score})
        }
    else:
        conn = connect_to_db()
        names = req_body["name"]
        timestamps = req_body["currentDate"]
        staffids = req_body["staffId"]
        staffnames = req_body["staffName"]
        loc = req_body["location"]
        subjectcodes = req_body["subjectCode"]
        with conn.cursor() as cur:
            print(f"Inserting attendance record for roll number {roll}")
            cur.execute("INSERT INTO attend (rollno, name, attendance_timestamp, location, staffId, staffName, subjectCode) VALUES (%s, %s, %s, %s, %s, %s, %s)", 
                        (roll, names, timestamps, loc, staffids, staffnames, subjectcodes))
            conn.commit()
            print("Attendance record inserted.")
        conn.close()

        return {
            'statusCode': 200,
            'body': json.dumps({'message': 'Attendance recorded', 'confidence': confidence_score})
        }
