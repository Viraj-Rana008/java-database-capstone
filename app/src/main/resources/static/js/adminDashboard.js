/*
  adminDashboard.js
  Handles the Admin Dashboard functionality:
  - Loads all doctors on page load
  - Filters/searches doctors by name, specialty, or time
  - Adds new doctors through modal form submission
*/

import { openModal } from "../components/modals.js";
import { getDoctors, filterDoctors, saveDoctor } from "../services/doctorServices.js";
import { createDoctorCard } from "../components/doctorCard.js";

/* =====================================================
   EVENT BINDINGS
===================================================== */

// Handle "Add Doctor" button click
document.addEventListener("DOMContentLoaded", () => {
  const addDoctorBtn = document.getElementById("addDocBtn");
  if (addDoctorBtn) {
    addDoctorBtn.addEventListener("click", () => openModal("addDoctor"));
  }

  // Load all doctors on page load
  loadDoctorCards();

  // Attach filter/search listeners
  const searchBar = document.getElementById("searchBar");
  const filterTime = document.getElementById("filterTime");
  const filterSpecialty = document.getElementById("filterSpecialty");

  if (searchBar) searchBar.addEventListener("input", filterDoctorsOnChange);
  if (filterTime) filterTime.addEventListener("change", filterDoctorsOnChange);
  if (filterSpecialty) filterSpecialty.addEventListener("change", filterDoctorsOnChange);
});

/* =====================================================
   FUNCTION: loadDoctorCards
   Purpose: Fetch and display all doctors
===================================================== */
async function loadDoctorCards() {
  try {
    const contentDiv = document.getElementById("content");
    if (!contentDiv) return;

    contentDiv.innerHTML = "<p>Loading doctors...</p>";

    const doctors = await getDoctors();
    if (!doctors || doctors.length === 0) {
      contentDiv.innerHTML = "<p>No doctors available.</p>";
      return;
    }

    renderDoctorCards(doctors);
  } catch (error) {
    console.error("Error loading doctors:", error);
    alert("Failed to load doctors. Please try again later.");
  }
}

/* =====================================================
   FUNCTION: filterDoctorsOnChange
   Purpose: Handle doctor filtering logic
===================================================== */
async function filterDoctorsOnChange() {
  try {
    const name = document.getElementById("searchBar")?.value?.trim() || "";
    const time = document.getElementById("filterTime")?.value || "";
    const specialty = document.getElementById("filterSpecialty")?.value || "";

    const result = await filterDoctors(name, time, specialty);
    const doctors = result?.doctors || [];

    if (doctors.length > 0) {
      renderDoctorCards(doctors);
    } else {
      const contentDiv = document.getElementById("content");
      contentDiv.innerHTML = "<p>No doctors found with the given filters.</p>";
    }
  } catch (error) {
    console.error("Error filtering doctors:", error);
    alert("Error applying filters. Please try again later.");
  }
}

/* =====================================================
   FUNCTION: renderDoctorCards
   Purpose: Render a list of doctor cards in the content area
===================================================== */
function renderDoctorCards(doctors) {
  const contentDiv = document.getElementById("content");
  if (!contentDiv) return;

  contentDiv.innerHTML = ""; // clear previous cards
  doctors.forEach((doctor) => {
    const card = createDoctorCard(doctor);
    contentDiv.appendChild(card);
  });
}

/* =====================================================
   FUNCTION: adminAddDoctor
   Purpose: Collect form data and add a new doctor
===================================================== */
window.adminAddDoctor = async function () {
  try {
    // Collect data from modal form
    const name = document.getElementById("doctorName")?.value?.trim();
    const email = document.getElementById("doctorEmail")?.value?.trim();
    const phone = document.getElementById("doctorPhone")?.value?.trim();
    const password = document.getElementById("doctorPassword")?.value?.trim();
    const specialization = document.getElementById("doctorSpecialty")?.value?.trim();

    // Collect checkbox availability
    const availability = Array.from(
      document.querySelectorAll('input[name="availability"]:checked')
    ).map((cb) => cb.value);

    // Basic validation
    if (!name || !email || !password || !specialization) {
      alert("Please fill all required fields.");
      return;
    }

    const token = localStorage.getItem("token");
    if (!token) {
      alert("Session expired. Please log in again.");
      window.location.href = "/";
      return;
    }

    const doctor = { name, email, phone, password, specialization, availability };

    // Send API request to save new doctor
    const response = await saveDoctor(doctor, token);

    if (response.success) {
      alert("Doctor added successfully!");
      // Close modal if open
      const modal = document.getElementById("modal");
      if (modal) modal.style.display = "none";
      // Reload the doctor list
      await loadDoctorCards();
    } else {
      alert(response.message || "Failed to add doctor.");
    }
  } catch (error) {
    console.error("Error adding doctor:", error);
    alert("An error occurred while adding doctor. Please try again later.");
  }
};
