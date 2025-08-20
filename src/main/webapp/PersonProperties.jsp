<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <title>Person Properties</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet" />
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons/font/bootstrap-icons.css" rel="stylesheet">

    <style>
         body {
            font-family: Arial, sans-serif;
            padding: 20px;
            background-color: white;
        }
        #detailsTable {
            width: 100%;
            border-collapse: collapse;
            margin-top: 10px;
        }
        th, td {
            border: 1px solid #dee2e6;
            padding: 12px;
            text-align: left;
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
        .toolbar {
            background-color: #f8f9fa;
            padding: 6px 10px;
            border: 1px solid #dee2e6;
            border-bottom: none;
            display: flex;
            gap: 12px;
            margin-top: 10px;
        }
    </style>
</head>
<body class="container mt-4">

    <ul class="nav nav-tabs">
        <li class="nav-item">
            <a class="nav-link active">Person Details</a>
        </li>
    </ul>
    <div class="toolbar">
        <button type="button" class="btn btn-sm btn-light" id="editBtn" data-bs-toggle="tooltip" title="Edit">
            <i class="bi bi-pencil-fill fs-4" style="background: linear-gradient(45deg, #ff6347, #4682b4); -webkit-background-clip: text; color: transparent;"></i>
        </button>
        <button type="button" class="btn btn-sm btn-light" id="refreshBtn" data-bs-toggle="tooltip" title="Refresh">
            <i class="bi bi-arrow-clockwise text-secondary fs-4"></i>
        </button>
    </div>

    <div id="loadingSpinner"></div>
    <div id="errorMessage" class="error"></div>
    <table id="detailsTable" class="table table-bordered"></table>
    <div class="action-buttons" id="actionButtons" style="display: none;">
        <button type="button" class="btn btn-sm btn-success" id="saveBtn">Save</button>
        <button type="button" class="btn btn-sm btn-secondary" id="cancelBtn">Cancel</button>
    </div>

    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>

    <script>
        $(document).ready(function () {
            const objectId = getQueryParam('name');
            if (!objectId) {
                showError("No 'name' (ObjectId) parameter found in the URL.");
                return;
            }

            let originalPersonData = {};
            let accessOptions = [];

            showLoading(true);

            $.ajax({
                url: 'http://localhost:8080/navigator/api/datafetchservice/persons',
                method: 'GET',
                dataType: 'json',
                success: function (data) {
                    if (!data || $.isEmptyObject(data)) {
                        showError("No data available.");
                        return;
                    }
                    const person = data.find(p => p.ObjectId === objectId);
                    if (!person) {
                        showError("No person found with ObjectId: " + objectId);
                        return;
                    }
                    originalPersonData = { ...person };
                    populateTable(person);
                },
                error: function () {
                    showError("Error fetching person details.");
                },
                complete: function () {
                    showLoading(false);
                }
            });
            function fetchAccessOptions(callback) {
                $.ajax({
                    url: 'http://localhost:8080/navigator/api/datafetchservice/personaccess',
                    method: 'GET',
                    dataType: 'json',
                    success: function (data) {
                        accessOptions = data || [];
                        if (callback) callback();
                    },
                    error: function () {
                        alert('Error fetching access options');
                    }
                });
            }

            function populateTable(person) {
                const table = $('#detailsTable');
                table.empty();
                $('#errorMessage').hide();
                table.show();

                for (const key in person) {
                    if (key === 'ObjectId') continue;
                    const row = $('<tr>');
                    const th = $('<th>').text(key.charAt(0).toUpperCase() + key.slice(1));
                    const td = $('<td>').attr('data-key', key);
                    td.text(person[key] ?? 'N/A');
                    row.append(th).append(td);
                    table.append(row);
                }
                if (person.Access !== 'Admin') {
                    $('#editBtn').hide();
                } else {
                    $('#editBtn').show();
                }
            }

            function enableEditing() {
                fetchAccessOptions(function() {
                    $('#detailsTable td').each(function () {
                        const currentValue = $(this).text();
                        const key = $(this).attr('data-key');

                        if (key === 'Username') {
                            // Do not allow editing
                            return;
                        } else if (key === 'Access') {
                            const select = $('<select>', {
                                class: 'form-control',
                                'data-key': key
                            });
                            accessOptions.forEach(function (access) {
                                const option = $('<option>', { value: access, text: access });
                                if (access === currentValue) {
                                    option.attr('selected', 'selected');
                                }
                                select.append(option);
                            });
                            $(this).html(select);
                        } else {
                            const input = $('<input>', {
                                type: 'text',
                                value: currentValue,
                                class: 'form-control',
                                'data-key': key
                            });
                            $(this).html(input);
                        }
                    });

                    $('#actionButtons').show();
                    $('#editBtn').hide();
                });
            }     
            function saveEdits() {
                const updatedData = {};

                $('#detailsTable td').each(function () {
                    const key = $(this).attr('data-key');
                    const newValue = $(this).find('input, select').val();
                    updatedData[key] = newValue;
                });

                $.ajax({
                    url: 'http://localhost:8080/navigator/api/datafetchservice/updatePerson/' + objectId,
                    method: 'PUT',
                    contentType: 'application/json',
                    data: JSON.stringify(updatedData),
                    success: function () {
                        alert('Data updated successfully!');
                        populateTable(updatedData);
                        $('#actionButtons').hide();
                        $('#editBtn').show();
                    },
                    error: function (xhr) {
                        alert('Error updating data: ' + xhr.responseText);
                    }
                });
            }

            function cancelEdits() {
                populateTable(originalPersonData);
                $('#editBtn').show();
                $('#actionButtons').hide();
            }

            $('#editBtn').on('click', enableEditing);
            $(document).on('click', '#saveBtn', saveEdits);
            $(document).on('click', '#cancelBtn', cancelEdits);
            $('#refreshBtn').on('click', function () {
                location.reload();
            });
        });

        function getQueryParam(param) {
            return new URLSearchParams(window.location.search).get(param);
        }

        function showLoading(show) {
            $('#loadingSpinner').css('display', show ? 'block' : 'none');
        }

        function showError(msg) {
            $('#errorMessage').text(msg).show();
            $('#detailsTable').hide();
            showLoading(false);
        }
    </script>
</body>
</html>
