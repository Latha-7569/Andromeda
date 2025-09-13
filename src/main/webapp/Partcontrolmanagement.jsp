<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String userAccess = (String) session.getAttribute("userAccess");
    if (userAccess == null) {
        userAccess = "Admin";
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8" />
<title>PartControl Management</title>
<style>
  body {
    font-family: Arial, sans-serif;
    margin: 0; padding: 0;
    background: #fff;
    color: #333;
  }

  /* Top Bar Container */
  .topbar {
    display: flex;
    background: #f5f7fa;
    border-bottom: 1px solid #cfd3db;
    padding: 6px 12px;
    font-size: 13px;
    color: #333;
  }

  /* Box styling */
  .topbar > div {
    display: flex;
    align-items: center;
    padding: 6px 12px;
    background: #f9fbfd;
    border: 1px solid #cfd3db;
    border-right: none;
    white-space: nowrap;
  }

  /* Last box has right border */
  .topbar > div:last-child {
    border-right: 1px solid #cfd3db;
  }

  /* Folder Icon Box */
  .folder-box {
    background: #e3e7eb;
    border: 1px solid #d1d6dc;
    width: 28px;
    height: 28px;
    display: flex;
    justify-content: center;
    align-items: center;
    margin-right: 8px;
    flex-shrink: 0;
  }

  .folder-box img {
    width: 16px;
    height: 16px;
  }

  /* Part Number box */
  .part-number {
    font-weight: 700;
    font-size: 14px;
    padding-right: 13px;
    padding-left:13px;
    border-right: 1px solid #cfd3db;
    margin-right: 12px;
  }

  /* Description box */
  .description {
    font-weight: 600;
    font-size: 13px;
    color: #555;
    padding-right: 12px;
    border-right: 1px solid #cfd3db;
    margin-right: 12px;
  }

  /* State box */
  .state-box {
    font-weight: 600;
    font-size: 13px;
    color: #333;
    display: flex;
    align-items: center;
    gap: 8px;
    padding-right: 12px;
    border-right: 1px solid #cfd3db;
  }

  .state-label {
    margin-right: 4px;
  }

  /* Buttons styling */
  .btn-submit {
    background-color: #5c8bff;
    border: 1px solid #3f70ff;
    color: white;
    font-size: 12px;
    padding: 4px 14px;
    border-radius: 3px;
    cursor: pointer;
    transition: background-color 0.3s ease;
  }

  .btn-submit:hover {
    background-color: #3f70ff;
  }

  .btn-evaluate {
    background-color: #e5e7ea;
    border: 1px solid #c6cad2;
    color: #555;
    font-size: 12px;
    padding: 4px 14px;
    border-radius: 3px;
    cursor: pointer;
    transition: background-color 0.3s ease;
  }

  .btn-evaluate:hover {
    background-color: #c6cad2;
  }

  /* Info box */
  .info-box {
    font-size: 11px;
    color: #666;
    padding-left: 4px;
    line-height: 1.3;
  }

  .info-box strong {
    color: #444;
  }

  /* Adjust spacing between boxes */
  .topbar > div:not(:last-child) {
    margin-right: -1px; /* To collapse adjacent borders */
  }
	
	.vertical-line img {
  height: 20px;  /* Adjust height to make it appear like a line */
  width: 1px;    /* Make it thin like a vertical line */
  margin: 0 10px; /* Space around the line */
}

  .container {
    display: flex;
    height: calc(100vh - 56px);
    font-size: 13px;
  }
.sidebar {
  width: 16%;
  background-color: #f8f9fa;
  border-right: 1px solid #ddd;
  padding: 20px;
  font-size: 14px;
  box-sizing: border-box;
   resize: horizontal;
  overflow-y: auto;
  overflow-x: hidden; 
}

.sidebar a {
  display: block;
  padding: 8px;
  color: #333;
  text-decoration: none;
  margin-bottom: 10px;
  border-radius: 4px;
}

.sidebar a:hover {
  background-color: #e3e7ea; 
}

.sidebar a.active {
  background-color:#808080;
  color: white;
   font-weight: bold;
}

/* Main Panel */
.main-panel {
  flex-grow: 1;
  padding: 20px;
  overflow-y: auto;
  font-size: 13px;
  box-sizing: border-box;
}


.container {
  display: flex;
  height: calc(100vh - 56px); 
}

.topbar {
  display: flex;
  background: #f5f7fa;
  border-bottom: 1px solid #cfd3db;
  padding: 6px 12px;
  font-size: 13px;
  color: #333;
}

  .toolbar {
    margin-bottom: 5px;
    padding-left: 2px;
  }
  .toolbar button {
    background: none;
    border: none;
    cursor: pointer;
    margin-right: 6px;
    vertical-align: middle;
    padding: 2px 4px;
  }
  .toolbar button img {
    vertical-align: middle;
    width: 18px;
    height: 18px;
  }
  .toolbar button:hover {
    background-color: #e3f2fd;
    border-radius: 2px;
  }

table.properties {
  width: 100%; /* full width */
  border-collapse: collapse;
  border: 1px solid #ddd;
  font-size: 16px;
  font-family: Arial, sans-serif;
  margin: 0 auto;
}

table.properties th,
table.properties td {
  padding: 12px 16px;
  border: 1px solid #ddd; /* add borders on all cells */
  vertical-align: middle;
}

table.properties th {
  background: #fafafa;
  font-weight: bold;
  width: 200px; /* label column width */
  text-align: left;
}

  .folder-icon {
    width: 16px;
    height: 16px;
    vertical-align: middle;
    margin-right: 6px;
  }

.properties-container {
  max-height: 600px; 
  overflow-y: auto;
  border: 1px solid #ddd;
  margin-top: 0;
}

  /* Loading Spinner */
  #loadingSpinner {
    display: none;
    position: fixed;
    top: 10px;
    right: 10px;
    font-size: 14px;
    color: #666;
  }

  
  #errorMessage {
    display: none;
    color: red;
    margin: 10px 0;
    font-weight: bold;
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
   font-wrap-mode:nowrap;
}
th {
background-color: #f8f9fa;
width: 200px;
forn-wrap-mode:nowrap;
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
            gap: 10px;
            border-radius: 4px;
            margin-top: 10px;
        }
         #createPanel {
            position: fixed;
            top: 0;
            right: -400px;
            width: 400px;
            height: 100%;
            background: #fff;
            box-shadow: -2px 0 5px rgba(0,0,0,0.3);
            overflow-y: auto;
            transition: right 0.3s ease;
            z-index: 1051;
            padding: 0;
        }
        #createPanel.active {
            right: 0;
        }
        #createPanel iframe {
            border: none;
            width: 100%;
            height: calc(100% - 56px);
        }
</style>
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://cdn.datatables.net/1.13.6/js/jquery.dataTables.min.js"></script>
<link href="https://cdn.jsdelivr.net/npm/bootstrap-icons/font/bootstrap-icons.css" rel="stylesheet">
</head>
<body>

<div class="topbar">
    <div class="left-section">
        <div class="image-box">
      <img src="https://img.icons8.com/?size=50&id=WECphWgmeM0g&format=png&color=000000" alt="Folder Icon" />
        </div>
        <div class="part-info">
            <div class="part-number" style="font-weight: 700; font-size: 14px;"></div>
            <div class="part-type" style="font-size: 12px; color: #666; margin-top: 2px; padding-left:13px"></div>
        </div>
        <div class="vertical-line"></div>
    </div>
    <div class="right-section">
        <div class="state-box">
            <span class="state-label">State:</span>
            <button id="submitBtn" class="btn-submit">InWork</button>
            <button id="evaluateBtn" class="btn-evaluate">InApproval</button>
        </div>
        <div class="vertical-line"></div>
        <div class="info-box"></div>
        <div class="vertical-line"></div>
    </div>
</div>
<div class="container">
    <div class="sidebar">
        <a href="Partcontroldetails.jsp?name=<%= request.getParameter("name") %>" class="nav-link" data-page="Partcontroldetails.jsp">PC-Properties</a>
        <a class="nav-link" href="Partcontrolhistory.jsp?name=<%= request.getParameter("name") %>">History</a>
        <a href="Partlifecycle.jsp?name=<%= request.getParameter("name") %>" class="nav-link" data-page="Partlifecycle.jsp">LifeCycle</a>
        <a href="Partcontrolmanagement.jsp?name=<%= request.getParameter("name") %>" class="nav-link active" data-page="Partcontrolmanagement.jsp">Part Management</a>
    </div>
    <div class="main-panel">
        <div class="toolbar mt-2">
            <button class="btn btn-light" data-bs-toggle="tooltip" title="Create Part Control" id="openCreatePanelBtn">
               <i class="bi bi-asterisk fs-6" style="color: #9370DB; font-size: 16px;"></i>
            </button>
            <button class="btn btn-light" data-bs-toggle="tooltip" title="Add Existing Part" id="addExistingpart">
                <img src="https://img.icons8.com/?size=100&id=K0l4dwcsMaJa&format=png&color=000000" alt="Add" style="width: 20px; height: 20px;">
            </button>
        </div>
        <div id="loadingSpinner"></div>
        <div id="errorMessage" class="error"></div>
        <table id="detailsTable" class="properties"></table>
    </div>
    <table class="table table-bordered mt-2" id="partControlTable">
        <thead></thead>
        <tbody></tbody>
    </table>

    <div id="createPanel">
        <iframe id="createIframe" src=""></iframe>
    </div>
</div>
<script>
function receiveSelectedParts(selectedParts) {
    if (!selectedParts || selectedParts.length === 0) {
        console.log("No parts selected.");
        return; 
    }
    console.log("Received selected parts:", selectedParts);

    const table = $('#partControlTable').DataTable();

    if (!table) {
        console.log("Table not initialized. Initializing...");
        loadPartControlTable();
        return;
    }

    selectedParts.forEach(part => {
        const columns = table.settings().init().columns;
        const newRowData = columns.map(col => part[col.data] || '');
        table.row.add(newRowData).draw(false);
    });

    $('#errorMessage').text('New parts added.');
}
function loadPartControlTable() {
    $('#errorMessage').text('Loading part controls...');

    const urlParams = new URLSearchParams(window.location.search);
    const objectid = urlParams.get('name');

    if (!objectid) {
        $('#errorMessage').text('Missing object ID.');
        return;
    }

    $.ajax({
        url: 'http://localhost:8080/andromeda/api/searchdata/getpartcontrol',
        data: { objectid: objectid },
        dataType: 'json',
        cache: false,
        success: function(data) {
            $('#errorMessage').text('');

            if (!data || data.length === 0 || data.message) {
                $('#errorMessage').text(data.message || 'No part controls found.');
                if ($.fn.DataTable.isDataTable('#partControlTable')) {
                    $('#partControlTable').DataTable().clear().draw();
                }
                return;
            }

            let keys = Object.keys(data[0]);
            const excludeKeys = ['fts_document','objectid','linkedobjectid','connectionid'];
            keys = keys.filter(k => !excludeKeys.includes(k));

            const columns = keys.map(key => ({
                data: key,
                title: key.charAt(0).toUpperCase() + key.slice(1)
               
            }));

            if ($.fn.DataTable.isDataTable('#partControlTable')) {
                $('#partControlTable').DataTable().destroy();
            }

            $('#partControlTable').DataTable({
                data: data,
                columns: columns,
                order: [[ columns.findIndex(c => c.data === 'createddate') || 0, 'desc' ]],
                responsive: false,
                paging: false,
                searching: false,
                scrollX: true,
                info: false,
                destroy: true
            });
        },
        error: function() {
            $('#errorMessage').text('Failed to load part controls.');
        }
    });
}

    $(document).ready(function() {
        loadPartControlTable();
	const partInfo = JSON.parse(sessionStorage.getItem('partInfo'));
        
        if (partInfo) {
          $('.part-number').text(partInfo.name || 'N/A');
          $('.part-type').text(partInfo.type || 'N/A');
        } else {
          $('.part-number').text('N/A');
          $('.part-type').text('N/A');
        }
        document.getElementById('openCreatePanelBtn').addEventListener('click', function () {
            const urlParams = new URLSearchParams(window.location.search);
            const objectid = urlParams.get('name');

            if (objectid) {
                const panel = document.getElementById('createPanel');
                panel.classList.add('active');
                document.getElementById('createIframe').src = 'CreatePartControl.jsp?name=' + encodeURIComponent(objectid);
            } else {
                alert('No object ID found!');
            }
        });

    });
    
    $('#addExistingpart').on('click', function () {
        const urlParams = new URLSearchParams(window.location.search);
        const objectid = urlParams.get('name');

        if (objectid) {
            const targetUrl = 'search.jsp?name=' + encodeURIComponent(objectid);
            window.open(targetUrl,'AddExistingPartPopup','width=900,height=800, position=center,left=100,top=100,resizable=yes' );
        } else {
            alert('No object ID found!');
        }
    });
    function closeCreatePanel() {
        const panel = document.getElementById('createPanel');
        panel.classList.remove('active');
        document.getElementById('createIframe').src = ''; 
    }

    window.addEventListener('message', function (event) {
  	  const action = event.data?.action;
  	  const createPanel = document.getElementById('createPanel');

  	  if (!createPanel) return;

  	  if (action === 'closeOnly') {
  	    createPanel.classList.remove('active');
  	  }

  	  if (action === 'closeAndRefresh') {
  	    createPanel.classList.remove('active');

  	    refreshPartControlList(); 
  	  }
  	});
</script>
</body>
</html>
