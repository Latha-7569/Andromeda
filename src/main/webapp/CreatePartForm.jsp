<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <title>Create Part</title>
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <!-- Bootstrap CSS -->
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet" />
  <style>
  body {
    margin: 0;
    background-color: #f9f9f9;
    height: 100vh;
    display: flex;
    justify-content: center;
    align-items: center;
  }

  #createPartForm {
    width: 100%;
    height:100%;
    padding: 30px;
    background-color: white;
    border-radius: 10px;
    box-shadow: 0 0 15px rgba(0, 0, 0, 0.1);
  }
  h2 {
    margin-bottom: 20px;
  }
  label {
    font-weight: 600;
  }
  textarea,
  select,
  input {
    margin-bottom: 15px;
  }
  .form-control:focus,
  .form-select:focus {
    border-color: #00afc4;
    box-shadow: 0 0 0 0.2rem rgba(0, 175, 196, 0.25);
  }
</style>
</head>
<body>
  <form id="createPartForm">
    <h2>Create Part</h2>
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
      <textarea id="inputDescription" class="form-control" rows="4" placeholder="Enter description" required></textarea>
    </div>
    <div class="mb-3">
      <label for="inputResponsibleEngineer" class="form-label">Responsible Engineer</label>
      <textarea id="inputResponsibleEngineer" class="form-control" rows="1" readonly></textarea>
    </div>
    <div class="d-flex justify-content-end gap-2">
      <button type="button" class="btn btn-secondary" onclick="window.close()">Cancel</button>
      <button type="submit" class="btn btn-primary">Submit</button>
    </div>
  </form>

  <script>
    window.addEventListener('DOMContentLoaded', async () => {
      const supertypeSelect = document.getElementById('supertype');
      const typeSelect = document.getElementById('type');
      const apnSelect = document.getElementById('APN');
      const descriptionInput = document.getElementById('inputDescription');
      const engineerInput = document.getElementById('inputResponsibleEngineer');
      const form = document.getElementById('createPartForm');

      let dropdownData = {};

      // Load dropdown data
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
              const label = apnWithLabel.trim();
              const option = new Option(label, label);
              apnSelect.add(option);
            });
          }
        });

      } catch (err) {
        console.error('Error loading dropdown data:', err);
        alert('Failed to load dropdown data.');
      }

      // Fill engineer field
      const user = JSON.parse(sessionStorage.getItem('loggedInUser'));
      if (user) {
        engineerInput.value = user.username || '';
      } else {
        alert('No logged-in user. Please log in.');
        window.close();
      }

      // Form submission
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
          const successMessage = "The following object was created successfully!\n"
        	  + "SuperType: " + formData.SuperType + "\n"+ "Type: " + formData.Type + "\n"+ "Name: " + formData.APN;
        	alert(successMessage);
        	window.close();
        	}
        	catch (error) {
          alert('Submission failed: ' + error.message);
        }
      });
    });
    

  </script>
</body>
</html>
