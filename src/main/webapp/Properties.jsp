<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <title>Part Details</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            padding: 20px;
            background-color: white;
        }
        h1 {
            text-align: center;
            margin-bottom: 20px;
        }
        #detailsTable {
            width: 100%;
            border-collapse: collapse;
            margin-top: 10px;
        }
        th, td {
            border: 1px solid black;
            padding: 8px;
            text-align: left;
            vertical-align: top;
        }
        th {
            background-color: #f2f2f2;
            width: 150px;
        }
        .error {
            color: red;
            margin-top: 20px;
            text-align: center;
        }
    </style>
</head>
<body>
    <h2>Properties</h2>
    <div id="errorMessage" class="error"></div>
    <table id="detailsTable"></table>

    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    <script>
        function getQueryParam(param) {
            const urlParams = new URLSearchParams(window.location.search);
            return urlParams.get(param);
        }

        function fetchAndDisplayPart(name) {
            $.ajax({
                url: 'http://localhost:8080/navigator/api/datafetchservice/latestparts',
                method: 'GET',
                dataType: 'json',
                success: function (data) {
                    if (!Array.isArray(data)) {
                        showError("Invalid data format received.");
                        return;
                    }
                    const part = data.find(p => p.Name === name);
                    if (!part) {
                        showError("Part with Name '" + name + "' not found.");
                        return;
                    }
                    populateTable(part);
                },
                error: function (xhr, status, error) {
                    showError("Error fetching part details: " + error);
                }
            });
        }

        function populateTable(part) {
            const table = document.getElementById('detailsTable');
            table.innerHTML = ''; 
            for (const key in part) {
                const row = document.createElement('tr');

                const th = document.createElement('th');
                th.textContent = key;
                row.appendChild(th);

                const td = document.createElement('td');
                td.textContent = part[key] ?? '';
                row.appendChild(td);

                table.appendChild(row);
            }
        }

        function showError(msg) {
            document.getElementById('errorMessage').textContent = msg;
            document.getElementById('detailsTable').innerHTML = '';
        }

        const name = getQueryParam('name');
        if (name) {
            fetchAndDisplayPart(decodeURIComponent(name));
        } else {
            showError("No 'name' parameter found in the URL.");
        }
    </script>
</body>
</html>

