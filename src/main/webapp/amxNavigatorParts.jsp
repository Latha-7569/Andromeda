<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <title>AndromedaParts</title>
    <!-- DataTables CSS -->
    <link rel="stylesheet" href="https://cdn.datatables.net/1.13.6/css/jquery.dataTables.min.css" />
    <!-- jQuery -->
    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    <!-- DataTables JS -->
    <script src="https://cdn.datatables.net/1.13.6/js/jquery.dataTables.min.js"></script>
    <style>
        body {
            font-family: Arial, sans-serif;
            padding: 20px;
            background-color: white;
        }
        h1 {
            text-align: center;
            margin-bottom: 30px;
        }
        .container {
            width: 100%;
            margin: auto;
        }
        .error {
            text-align: center;
            margin-top: 20px;
            color: red;
        }
    </style>
</head>
<body>
    <h1>Andromeda Parts</h1>
    <div class="container">
        <table id="partsTable" class="display" style="width:100%">
            <thead>
                <tr>
                    <th>Name</th>
                    <th>APN</th>
                    <th>SuperType</th>
                    <th>Type</th>
                    <th>Description</th>
                    <th>CreatedDate</th>
                    <th>Owner</th>
                    
                </tr>
            </thead>
            <tbody></tbody>
        </table>
        <div class="error" id="errorMessage"></div>
    </div>

    <script>
    $(document).ready(function () {
        $.ajax({
            url: 'http://localhost:8080/navigator/api/datafetchservice/latestparts',
            method: 'GET',
            dataType: 'json',
            success: function (data) {
                if (!Array.isArray(data) || data.length === 0) {
                    $('#errorMessage').text('No parts data found.');
                    return;
                }
                $('#partsTable').DataTable({
                    data: data,
                    columns: [
                    	  {
                    	    data: 'Name',
                    	    render: function(data, type, row) {
                    	   return '<a href="Properties.jsp?name=' + encodeURIComponent(data) + '" class="part-link">' + data + '</a>';
                    	    }
                    	   },
                        { data: 'APN' },
                        { data: 'SuperType' },
                        { data: 'Type' },
                        { data: 'Description' },
                        { data: 'CreatedDate' },
                        { data: 'Owner'}
                    ],
                    paging: false,
                    searching: false,
                    info: false,
                    ordering: true,
                    lengthChange: false,
                    destroy: true
                });
            },
            error: function (xhr, status, error) {
                console.error('Error fetching parts:', error);
                $('#errorMessage').text('Failed to fetch parts data.');
            }
        });
    });
    $('a.part-link').on('click', function(e) {
        e.preventDefault();
        var url = $(this).attr('href');
        window.parent.document.querySelector('iframe[name="contentFrame"]').src = url;
    });

    </script>
</body>
</html>
