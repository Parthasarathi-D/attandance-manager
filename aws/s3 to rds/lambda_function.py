import json
from datetime import datetime
import pymysql

hostname = "att2.c5gcm40minnf.eu-north-1.rds.amazonaws.com"
user = "dev"
password = "devnath123"
db = "att2"
conn = pymysql.connect(host=hostname, user=user, passwd=password, db=db, connect_timeout=60)

claimdict = {0: "rollno", 1: "name", 2: "attendance_timestamp", 3: "location", 4: "staffId", 5: "staffName", 6: "subjectCode"}

def lambda_handler(event, context):
    with conn.cursor() as cur:
        cur.execute("SELECT * FROM attend")
        print("Query executed")
        claim_details = []
        try:
            for claim in cur.fetchall():
                claim_detail = {}
                for row in range(0, len(claim)):
                    if isinstance(claim[row], datetime):
                        claim_detail[claimdict[row]] = claim[row].strftime("%Y-%m-%d %H:%M:%S")
                    else:
                        claim_detail[claimdict[row]] = claim[row]

                claim_details.append(claim_detail)
        except Exception as e:
            print(e)
    return {
        'statusCode': 200,
        'headers': {
            'Access-Control-Allow-Origin': '*',
            'Access-Control-Allow-Headers': 'Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token',
            'Access-Control-Allow-Credentials': 'true',
            'Content-Type': 'application/json'
        },
        'body': json.dumps(claim_details)
    }
