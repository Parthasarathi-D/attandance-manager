function makeApiCall() {
    var xhr = new XMLHttpRequest();
    var url = "https://dotveas0h2.execute-api.eu-north-1.amazonaws.com/prod/";

    xhr.onreadystatechange = function () {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status === 200) {
                try {
                    // Parse the JSON response
                    var response = JSON.parse(xhr.responseText);

                    // Check if response body exists and is a JSON string
                    if (response.body) {
                        var responseData = JSON.parse(response.body);
                        
                        // Log the response to inspect the structure
                        console.log(responseData);

                        // Display the data in a table on the webpage
                        displayDataInTable(responseData);
                    } else {
                        console.error("Response body is missing or invalid");
                    }
                } catch (e) {
                    console.error("Error parsing JSON response:", e);
                }
            } else {
                console.error("API request failed with status:", xhr.status);
            }
        }
    };

    // Open and send the request
    xhr.open("GET", url, true);
    xhr.send();
}

// Function to display unique data in a table based on the timestamp
function displayDataInTable(data) {
    var resultDiv = document.getElementById("result");

    // Clear previous content
    resultDiv.innerHTML = "";

    var table = document.createElement("table");
    var tableHeader = table.createTHead();
    var headerRow = tableHeader.insertRow();

    // Headers for the table
    var headers = ["RollNo", "Name", "Timestamp", "Location", "Staff ID", "Staff Name", "Subject Code"];
    for (var i = 0; i < headers.length; i++) {
        var th = document.createElement("th");
        th.innerHTML = headers[i];
        headerRow.appendChild(th);
    }

    var tableBody = table.createTBody();

    // Check if data is defined and is an array
    if (data && Array.isArray(data)) {
        // Filter out duplicate records based on timestamp
        var uniqueRecords = {};
        data.forEach(function (item) {
            var key = item.rollno + "_" + item.attendance_timestamp; // Concatenate RollNo and Timestamp
            if (!uniqueRecords[key]) {
                uniqueRecords[key] = item;
            }
        });
        var uniqueData = Object.values(uniqueRecords);

        // Iterate through the unique data and create table rows
        uniqueData.forEach(function (item) {
            var row = tableBody.insertRow();

            var cell1 = row.insertCell(0);
            var cell2 = row.insertCell(1);
            var cell3 = row.insertCell(2);
            var cell4 = row.insertCell(3);
            var cell5 = row.insertCell(4);
            var cell6 = row.insertCell(5);
            var cell7 = row.insertCell(6);

            cell1.innerHTML = item.rollno || '';
            cell2.innerHTML = item.name || '';
            cell3.innerHTML = item.attendance_timestamp || '';
            cell4.innerHTML = item.location || '';
            cell5.innerHTML = item.staffId || '';
            cell6.innerHTML = item.staffName || '';
            cell7.innerHTML = item.subjectCode || '';
        });
    } else {
        console.error("Invalid or missing data format:", data);
    }

    resultDiv.appendChild(table);

    // Filter/Search function based on date, subjectCode, and staffId
    function filterTable() {
        var dateInput = document.getElementById("datePicker").value;
        var subjectCodeInput = document.getElementById("subjectCodeInput").value.toLowerCase();
        var staffIdInput = document.getElementById("staffIdInput").value.toLowerCase();
        var rows = tableBody.getElementsByTagName("tr");

        for (var i = 0; i < rows.length; i++) {
            var row = rows[i];
            var dateCell = row.cells[2]; // Date cell
            var staffIdCell = row.cells[4]; // Staff ID cell
            var subjectCodeCell = row.cells[6]; // Subject Code cell

            if (
                (!dateInput || (dateCell && new Date(dateCell.innerHTML).toDateString() === new Date(dateInput).toDateString())) &&
                (!subjectCodeInput || (subjectCodeCell && subjectCodeCell.innerHTML.toLowerCase().includes(subjectCodeInput))) &&
                (!staffIdInput || (staffIdCell && staffIdCell.innerHTML.toLowerCase().includes(staffIdInput)))
            ) {
                row.style.display = "";
            } else {
                row.style.display = "none";
            }
        }
    }
    
    // Add event listeners for the search inputs
    document.getElementById("datePicker").addEventListener("change", filterTable);
    document.getElementById("subjectCodeInput").addEventListener("keyup", filterTable);
    document.getElementById("staffIdInput").addEventListener("keyup", filterTable);
}

// Call the function to initiate the API call and display data
makeApiCall();

