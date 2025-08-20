<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <title>Part History</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            font-family: Arial, sans-serif;
            padding: 20px;
            background-color: white;
        }
        h2 {
            text-align: center;
            margin-bottom: 20px;
        }
        #detailsTable {
            width: 100%;
            border-collapse: collapse;
            text-wrap-mode: nowrap;
            margin-top: 10px;
        }
        th, td {
            border: 1px solid #dee2e6;
            padding: 12px;
            text-align: left;
            vertical-align: top;
        }
        th {
            background-color: #f8f9fa;
            width: 200px;
        }
        .error {
            color: red;
            margin-top: 20px;
            text-align: center;
        }
        #loadingSpinner {
            border: 4px solid #f3f3f3;
            border-top: 4px solid #3498db;
            border-radius: 50%;
            width: 40px;
            height: 40px;
            animation: spin 1s linear infinite;
            margin: 40px auto 10px auto;
            display: none;
        }
        @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
        }
        .nav-tabs {
            margin-bottom: 20px;
        }
        .nav-tabs .nav-link.active {
            background-color: #e9ecef;
            font-weight: bold;
        }
    </style>
</head>
<body class="container mt-4">
    <ul class="nav nav-tabs">
        <li class="nav-item">
            <a class="nav-link" href="Properties.jsp?name=<%= request.getParameter("name") %>">Part Properties</a>
        </li>
        <li class="nav-item">
            <a class="nav-link active" href="searchHistory.jsp?name=<%= request.getParameter("name") %>">History</a>
        </li>
        <li class="nav-item">
            <a class="nav-link" href="Lifecycle.jsp?name=<%= request.getParameter("name") %>">Lifecycle</a>
        </li>
    </ul>

    <div id="loadingSpinner"></div>
    <div id="errorMessage" style="display:none;"></div>

    <table id="historyTable" class="table table-bordered" style="display:none;">
        <thead>
            <tr></tr>
        </thead>
        <tbody>
            <tr>
                <td id="historyText"></td>
            </tr>
        </tbody>
    </table>

    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script>

        function getQueryParam(param) {
            const urlParams = new URLSearchParams(window.location.search);
            return urlParams.get(param);
        }

        function showLoading(show) {
            $('#loadingSpinner').css('display', show ? 'block' : 'none');
        }

        function showError(msg) {
            $('#errorMessage').text(msg).show();
            $('#historyTable').hide();
            showLoading(false);
        }

        function displayHistory(historyText) {
            if (typeof historyText === 'string') {
                $('#historyText').text(historyText); 
                $('#historyTable').show(); 
            } else {
                showError("Invalid history format.");
            }
        }

        $(document).ready(function () {
      
            const objectId = getQueryParam('name');
            if (!objectId) {
                showError("No 'name' (ObjectId) parameter found in the URL.");
                return;
            }

            showLoading(true);
            $.ajax({
                url: 'http://localhost:8080/navigator/api/datafetchservice/gethistory',
                method: 'GET',
                data: { objectId: objectId },
                dataType: 'json',
                success: function (data) {
                    if (data && Array.isArray(data) && data.length > 0 && data[0].History) {
                        const historyText = data[0].History;
                        displayHistory(historyText); 
                    } else {
                        showError("No history found for the given Object ID.");
                    }
                },
                error: function (xhr) {
                    let msg = "Error fetching history data.";
                    try {
                        const errResp = JSON.parse(xhr.responseText);
                        if (errResp.error) msg = errResp.error;
                    } catch (e) {
                    
                    }
                    showError(msg);
                },
                complete: function () {
                    showLoading(false);
                }
            });
        });
    </script>
</body>
</html>
