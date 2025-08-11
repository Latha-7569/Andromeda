<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <title>AndromedaHome</title>
  <!-- Bootstrap CSS -->
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet" />
  <!-- FontAwesome for icons -->
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" crossorigin="anonymous" />

  <style>
    /* Navbar styles unchanged */
    .blue-toolbar {
      background-color: #00AFC4;
      color: white;
      padding: 0.25rem 1rem;
      min-height: 50px;
    }
    .navbar-brand {
      color: white;
      font-size: 1.1rem;
    }
    .navbar-brand img {
      border-radius: 60px;
      width: 40px;
      height: 40px;
      color: white;
    }
    #songSearch {
      border-radius: 30px !important;
    }
    .blue-toolbar .nav-link,
    .blue-toolbar .btn-link {
      color: white;
    }
    .blue-toolbar .nav-link:hover,
    .blue-toolbar .btn-link:hover {
      color: #cce5ff;
      text-decoration: none;
    }
    .blue-toolbar .nav-link i {
      font-size: 1.3rem;
    }

    /* Container holding both panels */
   .panels-container {
  display: flex;
  gap: 20px;
  padding: 15px;
  height: calc(105vh - 90px); /* adjust if navbar is taller */
  box-sizing: border-box;
}
    /* Left panel - scrollable and resizable horizontally */
    .left-panel {
  width: 200px;
  min-width: 120px;
  max-width: 600px;
  background-color: #f8f9fa;
  border: 1px solid #ddd;
  border-radius: 8px;
  overflow-y: auto;
  overflow-x: hidden;        /* prevent horizontal scroll */
  padding: 1rem;
  box-sizing: border-box;
  resize: horizontal;
  height: 100%;              /* Fill the available height */
}
    /* Right panel - fixed width and height, with background image */
    .right-panel {
      flex-grow: 1;            /* take remaining space */
      background-image: url('andromeda.png');
      background-repeat: no-repeat;
      background-position: center center;
      background-size: contain;
      border: 1px solid #ddd;
      border-radius: 8px;
      box-sizing: border-box;
      height: 100%;            /* fill container height */
    }

    /* Profile dropdown, modal and other styles unchanged (copied from original) */
    .profile-dropdown {
      position: absolute;
      top: 60px;
      right: 10px;
      width: 200px;
      background-color: white;
      color: black;
      border: 1px solid #ccc;
      border-radius: 5px;
      box-shadow: 0px 2px 5px rgba(0, 0, 0, 0.3);
      display: none;
      z-index: 999;
    }
    .profile-dropdown p {
      margin: 0;
      padding: 10px;
      font-size: 0.9rem;
      border-bottom: 1px solid #eee;
    }
    .profile-dropdown button {
      width: 100%;
      padding: 8px 10px;
      border: none;
      background-color: #00AFC4;
      color: white;
      border-radius: 0 0 5px 5px;
      cursor: pointer;
    }
    .profile-dropdown button:hover {
      background-color: #008ba3;
    }
    .position-relative {
      position: relative;
    }
    .modal {
      display: none;
      position: fixed;
      z-index: 1050;
      left: 0;
      top: 0;
      width: 100vw;
      height: 100vh;
      background-color: rgba(0, 0, 0, 0.3);
      overflow: hidden; 
    }
    .modal-content {
      background-color: white;
      padding: 40px 40px 30px 40px;
      border-radius: 12px;
      width: 500px;
      max-height: 90vh;
      box-shadow: 0 8px 25px rgba(0, 0, 0, 0.35);
      position: fixed;
      top: 55%;
      left: 60%;
      transform: translate(-50%, -50%);
      overflow-y: auto;
    }
    form label {
      font-weight: 600;
    }
    form textarea,
    form select,
    form input {
      margin-bottom: 15px;
      padding: 8px 12px;
      font-size: 1rem;
      border-radius: 6px;
      border: 1px solid #ccc;
      transition: border-color 0.3s ease;
    }
    form textarea:focus,
    form select:focus,
    form input:focus {
      border-color: #00afc4;
      outline: none;
    }
    .close-button {
      position: absolute;
      right: 10px;
      top: 5px;
      font-size: 24px;
      font-weight: bold;
      cursor: pointer;
      color: #333;
    } 
    .right-panel iframe {
  width: 100%;
  height: 100%;
  border: none;
  background-color: white; 
}
  </style>
</head>

<body>
  <nav class="navbar navbar-expand-lg blue-toolbar position-relative">
    <div class="container-fluid">
      <div class="navbar-brand d-flex align-items-center">
        <img src="rr.png" alt="Logo" />
        <b class="ms-2">ANDROMEDA</b>
      </div>
      <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#headerComponents" aria-controls="headerComponents" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
      </button>
      <div class="collapse navbar-collapse" id="headerComponents">
        <ul class="navbar-nav me-auto mb-2 mb-lg-0 align-items-center">
          <li class="nav-item d-flex align-items-center">
            <form class="d-flex ms-lg-3" role="search">
              <input class="form-control" id="songSearch" type="search" placeholder="Search" aria-label="Search" />
            </form>
          </li>
        </ul>
        <ul class="navbar-nav mb-2 mb-lg-0 align-items-center">
          <li class="nav-item ms-3">
            <a class="nav-link" href="amxNavigatorHome.jsp"><i class="fas fa-home"></i></a>
          </li>
          <li class="nav-item ms-3">
            <a class="nav-link" href="#" id="openModalBtn"><i class="fas fa-plus-circle me-2"></i></a>
          </li>
          <li class="nav-item ms-3 position-relative">
            <a class="nav-link" href="#" onclick="toggleProfileDropdown(event)">
              <i class="fas fa-user"></i>
            </a>
            <div class="profile-dropdown" id="profileDropdown">
              <p><strong>Username:</strong> <span id="usernameDisplay"></span></p>
              <p><strong>Email:</strong> <span id="emailDisplay"></span></p>
              <button onclick="logout()">Logout</button>
            </div>
          </li>
        </ul>
      </div>
    </div>
  </nav>

  <div class="panels-container">
    <div class="left-panel">
      <!-- Put your left content here -->
      <ul class="nav flex-column">
  <li class="nav-item">
    <a class="nav-link" href="#" onclick="loadRightPanel('amxNavigatorParts.jsp')">Parts</a>
  </li>
</ul>
    </div>
   <iframe class="right-panel" id="contentFrame"  name="contentFrame" src="" frameborder="0"></iframe>
  </div>
  <!-- Modal code (unchanged) -->
  <div id="myModal" class="modal">
    <div class="modal-content">
      <span class="close-button">&times;</span>
      <form id="createPartForm">
  <h2 class="mb-4">Create Part</h2>
  <div class="mb-3">
    <label for="supertype" class="form-label">SuperType</label>
    <select id="supertype" name="supertype" class="form-select" required>
      <option value="">Select</option>
    </select>
  </div>
  <div class="mb-3">
    <label for="type" class="form-label">Type</label>
    <select id="type" name="type" class="form-select" required>
      <option value="">Select</option>
    </select>
  </div>
  <div class="mb-3">
    <label for="APN" class="form-label">APN</label>
    <select id="APN" name="APN" class="form-select" required>
      <option value="">Select</option>
    </select>
  </div>
  <div class="mb-3">
    <label for="inputDescription" class="form-label">Description</label>
    <textarea id="inputDescription" class="form-control" rows="4" placeholder="Enter description"></textarea>
  </div>
  <div class="mb-3">
    <label for="inputResponsibleEngineer" class="form-label">Responsible Engineer</label>
    <textarea id="inputResponsibleEngineer" class="form-control" rows="1" placeholder="username" readonly></textarea>
  </div>
  <div class="d-flex justify-content-end gap-2 mt-3">
    <button type="button" class="btn btn-secondary" id="cancelBtn">Cancel</button>
    <button type="submit" class="btn btn-primary">Submit</button>
  </div>
</form>
    </div>
  </div>

  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
  <script>
    // Profile dropdown toggle and logout
    function toggleProfileDropdown(event) {
      event.preventDefault();
      const dropdown = document.getElementById('profileDropdown');
      dropdown.style.display = dropdown.style.display === 'block' ? 'none' : 'block';
    }
    document.addEventListener('click', function(event) {
      const dropdown = document.getElementById('profileDropdown');
      const profileIcon = event.target.closest('.fa-user');
      if (!profileIcon && !dropdown.contains(event.target)) {
        dropdown.style.display = 'none';
      }
    });
    function logout() {
      sessionStorage.removeItem('loggedInUser');
      window.location.href = 'amxNavigatorLogin.jsp';
    }
    function updateProfileDropdown() {
      const user = JSON.parse(sessionStorage.getItem('loggedInUser'));
      if (!user) {
        window.location.href = 'amxNavigatorLogin.jsp';
        return;
      }
      document.getElementById('usernameDisplay').textContent = user.username || '';
      document.getElementById('emailDisplay').textContent = user.email || '';
      fetch('http://localhost:8080/navigator/api/navigatorutilites/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded'
        },
        credentials: 'include',
        body: new URLSearchParams({ username: user.username })
      })
      .then(res => res.json())
      .then(data => {
        console.log('Login success:', data);
      })
      .catch(err => {
        console.error('Login error:', err);
        alert('Backend login failed. Please re-login.');
        window.location.href = 'amxNavigatorLogin.jsp';
      });
    }
    window.addEventListener('DOMContentLoaded', async () => {
    	  updateProfileDropdown();

    	  const supertypeSelect = document.getElementById('supertype');
    	  const typeSelect = document.getElementById('type');
    	  const apnSelect = document.getElementById('APN');
    	  const descriptionInput = document.getElementById('inputDescription');
    	  const engineerInput = document.getElementById('inputResponsibleEngineer');
    	  const form = document.getElementById('createPartForm');
    	  const modal = document.getElementById('myModal');

    	  let dropdownData = {};

    	  try {
    	    const response = await fetch('http://localhost:8080/navigator/api/excel/dropdowns');
    	    dropdownData = await response.json();

    	    dropdownData.superTypes.forEach(supertype => {
    	      const option = new Option(supertype, supertype);
    	      supertypeSelect.add(option);
    	    });

    	    supertypeSelect.addEventListener('change', () => {
    	      const selectedSuper = supertypeSelect.value;
    	      typeSelect.innerHTML = '<option value="">Select</option>';
    	      apnSelect.innerHTML = '<option value="">Select</option>';
    	      descriptionInput.value = '';

    	      if (selectedSuper && dropdownData.types[selectedSuper]) {
    	        dropdownData.types[selectedSuper].forEach(type => {
    	          const option = new Option(type, type);
    	          typeSelect.add(option);
    	        });
    	      }
    	    });

    	    typeSelect.addEventListener('change', () => {
    	    	  const selectedType = typeSelect.value;
    	    	  apnSelect.innerHTML = '<option value="">Select</option>';
    	    	  descriptionInput.value = '';

    	    	  const normalizedKey = selectedType.replace(/\s+/g, '').toLowerCase();
    	    	  const apnList = dropdownData.apn && dropdownData.apn[normalizedKey];

    	    	  if (Array.isArray(apnList)) {
    	    	    apnList.forEach(apnWithLabel => {
    	    	      const [apn, ...descParts] = apnWithLabel.split("-");
    	    	      const label = apnWithLabel.trim();
    	    	      const option = new Option(label, apn.trim());
    	    	      apnSelect.add(option);
    	    	    });
    	    	  }
    	    	});

    	    apnSelect.addEventListener('change', () => {
    	    });


    	  } catch (err) {
    	    console.error('Error loading dropdown data:', err);
    	    alert('Failed to load dropdown data.');
    	  }


    	  const user = JSON.parse(sessionStorage.getItem('loggedInUser'));
    	  if (user) {
    	    engineerInput.value = user.username || '';
    	  }

    	  document.getElementById('openModalBtn').addEventListener('click', e => {
    	    e.preventDefault();
    	    modal.style.display = 'block';
    	  });
    	  document.querySelector('.close-button').addEventListener('click', () => modal.style.display = 'none');
    	  document.getElementById('cancelBtn').addEventListener('click', () => modal.style.display = 'none');
    	  window.addEventListener('click', (event) => {
    	    if (event.target === modal) modal.style.display = 'none';
    	  });

    	  form.addEventListener('submit', async (e) => {
    	    e.preventDefault();
    	    const formData = {
    	      SuperType: supertypeSelect.value.trim(),
    	      Type: typeSelect.value.trim(),
    	      APN: apnSelect.value.trim(),
    	      Description: descriptionInput.value.trim(),
    	      ResponsibleEngineer: engineerInput.value.trim()
    	    };

    	    if (!formData.SuperType || !formData.Type || !formData.APN || !formData.Description) {
    	      alert('Please fill in all required fields.');
    	      return;
    	    }

    	    try {
    	      const res = await fetch('http://localhost:8080/navigator/api/navigatorutilites/create', {
    	        method: 'POST',
    	        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    	        credentials: 'include',
    	        body: new URLSearchParams(formData)
    	      });
    	      const result = await res.json();

    	      if (!res.ok || result.Status !== 'Success') {
    	        alert('Error: ' + (result.Message || 'Something went wrong'));
    	        return;
    	      }

    	      alert('Part created successfully!');
    	      modal.style.display = 'none';
    	      form.reset();

    	    } catch (error) {
    	      alert('Submission failed: ' + error.message);
    	    }
    	  });
    	});

    
    function loadRightPanel(url) {
    	  const iframe = document.getElementById('contentFrame');
    	  const rightPanel = document.getElementById('rightPanelContainer');
    	  iframe.src = url;
    	  if(url === 'amxDataFetch.jsp') {
    	    rightPanel.style.backgroundImage = 'none';
    	    rightPanel.style.backgroundColor = 'white';
    	  } else {
    	    rightPanel.style.backgroundImage = "url('andromeda.png')";
    	    rightPanel.style.backgroundColor = ''; 
    	  }
    	}

  </script>
</body>
</html>