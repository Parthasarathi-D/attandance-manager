from flask import Flask, render_template, jsonify, request
import pandas as pd

app = Flask(__name__)

# Read CSV file
df = pd.read_csv(r'D:\Advanced Networks\records.csv')  # Use raw string literal for Windows path

# Route for home page
@app.route('/')
def index():
    # Get unique subject codes and staff names
    subject_codes = df['subjectCode'].unique().tolist()
    staff_names = df['staffName'].unique().tolist()
    return render_template('index.html', subject_codes=subject_codes, staff_names=staff_names)

# Route to fetch data
@app.route('/data')
def data():
    # Filter data based on user selections
    subject_code = request.args.get('subject_code')
    staff_name = request.args.get('staff_name')

    filtered_df = df.copy()

    if subject_code:
        filtered_df = filtered_df[filtered_df['subjectCode'] == subject_code]

    if staff_name:
        filtered_df = filtered_df[filtered_df['staffName'] == staff_name]

    # Debug statements
    print("Filtered DataFrame:")
    print(filtered_df)
    print("Length of Filtered DataFrame:", len(filtered_df))

    # Drop duplicates based on both attendance_timestamp and roll_no
    unique_records = filtered_df.drop_duplicates(subset=['attendance_timestamp', 'rollno'])

    # Group by date and count attendance records
    grouped_data = unique_records.groupby('attendance_timestamp').size().reset_index(name='attendance_count')

    data = grouped_data.to_dict(orient='records')
    return jsonify(data)

if __name__ == '__main__':
    app.run(debug=True)
