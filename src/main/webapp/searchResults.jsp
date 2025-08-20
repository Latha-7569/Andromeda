<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Search Results</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.datatables.net/1.13.6/css/jquery.dataTables.min.css" rel="stylesheet" />

<style>

.spinner-container {
  position: relative;
  width: 80px;
  height: 80px;
  margin: 40px auto 10px auto;
}

.spinner-ring {
  box-sizing: border-box;
  position: absolute;
  width: 80px;
  height: 80px;
  border: 6px solid #f3f3f3;
  border-top: 6px solid #3498db;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  top: 0;
  left: 0;
}
  .spinner-overlay {
  position: fixed;      
  top: 0; left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(255, 255, 255, 0.7); 
  display: flex;
  flex-direction: column;   
  justify-content: center;  
  align-items: center;      
  gap: 10px;
  z-index: 9999;
}

.spinner-image {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  transform: translate(-50%, -50%);
  pointer-events: none;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loadingText {
  font-weight: bold;
  font-size: 1.1rem;
  color: #3498db;
}

a.apn-link {
cursor: pointer;
color: blue;
text-decoration: underline;
}
.noResults {
display: none;                    
text-align: center;               
position: absolute;              
bottom: 70px;                     
left: 50%;                         
transform: translateX(-50%);     
font-size: 18px;                 
color: #555;                      
background-color: transparent;    
border: none;                     
box-shadow: none;                
padding: 0;                       
margin: 0;                       
width: auto;                      
max-width: 90%;                   
}

.noResults ul {
list-style: none;
padding: 0;
 }
.noResults li {
font-size: 16px;
color: #555;
}
.table-wrapper {
text-wrap-mode : nowrap;
position: relative;
min-height: 200px;
}
.resultsTable thead th {
background-color: #f8f9fa;
}
.inline-bullets {
  padding: 0;
  margin: 0;
  list-style-type: none; 
}

.inline-bullets li {
  display: flex;
  align-items: center;
  margin-bottom: 4px;
  font-size: 16px; 
}

.inline-bullets li::before {
  content: '•';  
  font-size: 20px;  
  margin-right: 3px;  
}
</style>
</head>
<body class="container mt-4">
    <!-- Loading Spinner -->
<div id="spinnerOverlay" class="spinner-overlay" style="display: flex; flex-direction: column; gap: 10px; align-items: center;">
  <div class="spinner-container" style="position: relative; width: 80px; height: 80px;">
    <div class="spinner-ring"></div>
    <img src="logo.png" alt="loading image" class="spinner-image" /></div>
  <div id="loadingText" class="loadingText" style="font-weight: bold;">Loading...</div></div>
  
    <div id="errorMessage" class="alert alert-danger" style="display:none;"></div>
    <div id="contextMenu" style="display:none; position:absolute; background:#fff; border:1px solid #ccc; box-shadow:0 2px 6px rgba(0,0,0,0.2); z-index:1000;">
        <ul style="list-style:none; margin:0; padding:5px 0; width:100px;">
            <li id="openMenuItem" style="padding:8px 15px; cursor:pointer;">Open</li>
        </ul>
    </div>

    <div id="noResults" class="noResults">
    <h2 style="font-weight: bold; font-size:32px;">No Result Found</h2>
    <h4>Suggestions:</h4>
    <ul class="inline-bullets">
        <li>Make sure all words are spelled correctly</li>
        <li>Try different keywords.</li>
        <li>Try more general keywords.</li>
    </ul>
</div>
    <div class="table-wrapper">
        <table id="resultsTable" class="display table table-striped" style="width:100%; display:none;">
            <thead>
                <tr id="tableHeaderRow">
                    
                </tr>
            </thead>
            <tbody id="resultsBody">
                
            </tbody>
        </table>
    </div>

    <script src="https://code.jquery.com/jquery-3.7.0.min.js"></script>
    <script src="https://cdn.datatables.net/1.13.6/js/jquery.dataTables.min.js"></script>
    <script>
        document.addEventListener('DOMContentLoaded', async function () {
            const urlParams = new URLSearchParams(window.location.search);
            const query = urlParams.get('query');
            
            const spinner = document.getElementById('loadingSpinner');
            const loadingText = document.getElementById('loadingText');
            setTimeout(() => {
                spinner.style.display = 'none'; 
                loadingText.style.display = 'none'; 
            }, 15000); 
            const errorMessage = document.getElementById('errorMessage');
            const resultsTable = $('#resultsTable');
            const resultsBody = document.getElementById('resultsBody');
            const tableHeaderRow = document.getElementById('tableHeaderRow');

            const contextMenu = document.getElementById('contextMenu');
            let selectedObjectId = null;

            function showLoading(show) {
                const spinnerOverlay = document.getElementById('spinnerOverlay');
                if (show) {
                    spinnerOverlay.style.display = 'flex';  
                    loadingText.style.display = 'block';
                } else {
                    spinnerOverlay.style.display = 'none';
                    loadingText.style.display = 'none';
                }
            }
            function showError(msg) {
                errorMessage.textContent = msg;
                errorMessage.style.display = 'block';
                showLoading(false);
                resultsTable.hide();
            }

            function isValidPartNumber(query) {
                const regex = /^[0-9]{3}(-\d{3})?(-APN)?$/;
                return regex.test(query);
            }

            if (!query || !isValidPartNumber(query)) {
                alert("Enter a valid number (3 digits, optionally followed by -001 or -001-APN).");
                showLoading(false);
                return;
            }

            try {
                showLoading(true);
                errorMessage.style.display = 'none';
                resultsTable.hide();

                const response = await fetch("http://localhost:8080/navigator/api/datafetchservice/amxFullSearch?name=" + encodeURIComponent(query), {
                    method: 'GET',
                    headers: { 'Content-Type': 'application/json' }
                });

                if (!response.ok) {
                    if (response.status === 404) {
                        alert("Entered part is not available.");
                        showLoading(false);
                        return;
                    } else {
                        const errText = await response.text();
                        throw new Error(errText);
                    }
                }

                const data = await response.json();
                if (Array.isArray(data) && data.length > 0) {
                    renderResults(data);
                } else {
                    renderNoResults();
                }
            } catch (error) {
                console.error("Error fetching results:", error);
                showError("Entered part is not available.");
            } finally {
                showLoading(false);
            }

            function renderResults(results) {
                resultsTable.show();
                document.getElementById('noResults').style.display = 'none';
                errorMessage.style.display = 'none';
                resultsBody.innerHTML = '';
                tableHeaderRow.innerHTML = '';

                const allowedKeys = ["APN", "Name", "SuperType", "Type", "Description", "CreatedDate", "Owner", "Email"];

                allowedKeys.forEach(key => {
                    const th = document.createElement('th');
                    th.textContent = key;
                    tableHeaderRow.appendChild(th);
                });

                results.forEach((item, index) => {
                    const tr = document.createElement('tr');
                    const rowClass = index % 2 === 0 ? 'even' : 'odd';
                    tr.classList.add(rowClass);

                    if (item.ObjectId) {
                        const safeObjectIdClass = 'id-' + item.ObjectId.replace(/[^a-zA-Z0-9\-_]/g, '-');
                        tr.classList.add(safeObjectIdClass);
                        tr.setAttribute('ObjectId', item.ObjectId);
                    }

                    allowedKeys.forEach(key => {
                        const td = document.createElement('td');
                        if (key === 'APN') {
                            const a = document.createElement('a');
                            a.href = '#';
                            a.classList.add('apn-link');
                            a.textContent = item[key] != null ? item[key] : '';
                            a.setAttribute('ObjectId', item.ObjectId);
                            td.appendChild(a);
                        } else {
                            td.textContent = item[key] != null ? item[key] : '';
                        }
                        tr.appendChild(td);
                    });
                    resultsBody.appendChild(tr);
                });

                resultsTable.DataTable({
                    paging: false,
                    info: false,
                    lengthChange: false,
                    ordering: false
                });
            }

            function renderNoResults() {
                resultsTable.show();
                document.getElementById('noResults').style.display = 'block';
                resultsBody.innerHTML = '';
                tableHeaderRow.innerHTML = '';

                const allowedKeys = ["APN", "Name", "SuperType", "Type", "Description", "CreatedDate", "Owner", "Email"];
                allowedKeys.forEach(key => {
                    const th = document.createElement('th');
                    th.textContent = key;
                    tableHeaderRow.appendChild(th);
                });
            }

            document.body.addEventListener('click', function(e) {
                if (e.target && e.target.classList.contains('apn-link')) {
                    e.preventDefault();
                    e.stopPropagation();
                    selectedObjectId = e.target.getAttribute('ObjectId');
                    if (!selectedObjectId) return;
                    contextMenu.style.top = e.pageY + 'px';
                    contextMenu.style.left = e.pageX + 'px';
                    contextMenu.style.display = 'block';
                } else if (!contextMenu.contains(e.target)) {
                    contextMenu.style.display = 'none';
                }
            });

            document.getElementById('openMenuItem').addEventListener('click', function() {
                contextMenu.style.display = 'none';
                if (selectedObjectId) {
                    const propertiesUrl = '/navigator/Properties.jsp?name=' + encodeURIComponent(selectedObjectId);
                    window.location.href = propertiesUrl;
                }
            });
        });
    </script>
</body>
</html>