/*
  header.js
  Dynamically renders the header section based on user role and session state.
*/

function renderHeader() {
  const headerDiv = document.getElementById("header");
  if (!headerDiv) return;

  // --- 1. Handle root (index.html) page ---
  if (window.location.pathname.endsWith("/") || window.location.pathname.endsWith("/index.html")) {
    localStorage.removeItem("userRole");
    localStorage.removeItem("token");

    headerDiv.innerHTML = `
      <header class="header">
        <div class="logo-section">
          <img src="./assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
          <span class="logo-title">Hospital CMS</span>
        </div>
      </header>
    `;
    return;
  }

  // --- 2. Retrieve session info ---
  const role = localStorage.getItem("userRole");
  const token = localStorage.getItem("token");

  // --- 3. Basic header structure ---
  let headerContent = `
    <header class="header">
      <div class="logo-section">
        <img src="../assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
        <span class="logo-title">Hospital CMS</span>
      </div>
      <nav>
  `;

  // --- 4. Session validation ---
  if ((role === "loggedPatient" || role === "admin" || role === "doctor") && !token) {
    localStorage.removeItem("userRole");
    alert("Session expired or invalid login. Please log in again.");
    window.location.href = "/";
    return;
  }

  // --- 5. Role-based header buttons ---
  if (role === "admin") {
    headerContent += `
      <button id="addDocBtn" class="adminBtn" onclick="openModal('addDoctor')">Add Doctor</button>
      <a href="#" class="logout-link" onclick="logout()">Logout</a>
    `;
  } else if (role === "doctor") {
    headerContent += `
      <button class="adminBtn" onclick="window.location.href='/pages/doctorDashboard.html'">Home</button>
      <a href="#" class="logout-link" onclick="logout()">Logout</a>
    `;
  } else if (role === "patient") {
    headerContent += `
      <button id="patientLogin" class="adminBtn">Login</button>
      <button id="patientSignup" class="adminBtn">Sign Up</button>
    `;
  } else if (role === "loggedPatient") {
    headerContent += `
      <button id="home" class="adminBtn" onclick="window.location.href='/pages/loggedPatientDashboard.html'">Home</button>
      <button id="patientAppointments" class="adminBtn" onclick="window.location.href='/pages/patientAppointments.html'">Appointments</button>
      <a href="#" class="logout-link" onclick="logoutPatient()">Logout</a>
    `;
  } else {
    // Fallback for users with no role
    headerContent += `
      <button id="patientLogin" class="adminBtn">Login</button>
      <button id="patientSignup" class="adminBtn">Sign Up</button>
    `;
  }

  // --- 6. Close header and render ---
  headerContent += `
      </nav>
    </header>
  `;

  headerDiv.innerHTML = headerContent;

  // --- 7. Attach event listeners ---
  attachHeaderButtonListeners();
}

/* ===============================
   Helper Functions
=============================== */

// Attach login modal listeners for patients
function attachHeaderButtonListeners() {
  const loginBtn = document.getElementById("patientLogin");
  const signupBtn = document.getElementById("patientSignup");

  if (loginBtn) {
    loginBtn.addEventListener("click", () => openModal("patientLogin"));
  }

  if (signupBtn) {
    signupBtn.addEventListener("click", () => openModal("patientSignup"));
  }
}

// Generic logout (admin/doctor)
function logout() {
  localStorage.removeItem("token");
  localStorage.removeItem("userRole");
  window.location.href = "/";
}

// Patient logout (keep role as patient)
function logoutPatient() {
  localStorage.removeItem("token");
  localStorage.setItem("userRole", "patient");
  window.location.href = "/";
}

// Auto render header when script loads
document.addEventListener("DOMContentLoaded", renderHeader);
