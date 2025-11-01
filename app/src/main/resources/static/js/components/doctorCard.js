/*
  doctorCard.js
  Dynamically creates reusable doctor cards for Admin and Patient dashboards.
  Supports role-based actions: Delete (Admin) or Book Appointment (Patients).
*/

import { deleteDoctor } from "../services/doctorServices.js";
import { showBookingOverlay } from "../services/loggedPatient.js";
import { getPatientData } from "../services/patientServices.js";

/**
 * Create a reusable Doctor Card component.
 * @param {Object} doctor - Doctor object containing name, specialty, email, availability, etc.
 * @returns {HTMLElement} doctorCard element.
 */
export function createDoctorCard(doctor) {
  // --- 1. Create main container ---
  const card = document.createElement("div");
  card.classList.add("doctor-card");

  // --- 2. Fetch current role ---
  const role = localStorage.getItem("userRole");

  // --- 3. Doctor Info Section ---
  const infoDiv = document.createElement("div");
  infoDiv.classList.add("doctor-info");

  const name = document.createElement("h3");
  name.textContent = doctor.name || "Unnamed Doctor";

  const specialization = document.createElement("p");
  specialization.textContent = `Specialization: ${doctor.specialization || "N/A"}`;

  const email = document.createElement("p");
  email.textContent = `Email: ${doctor.email || "N/A"}`;

  const availability = document.createElement("p");
  const times = Array.isArray(doctor.availability)
    ? doctor.availability.join(", ")
    : doctor.availability || "Not specified";
  availability.textContent = `Available: ${times}`;

  // Append info fields
  infoDiv.appendChild(name);
  infoDiv.appendChild(specialization);
  infoDiv.appendChild(email);
  infoDiv.appendChild(availability);

  // --- 4. Action Buttons Section ---
  const actionsDiv = document.createElement("div");
  actionsDiv.classList.add("card-actions");

  // --- 5. Role-specific actions ---

  // ADMIN ROLE
  if (role === "admin") {
    const removeBtn = document.createElement("button");
    removeBtn.textContent = "Delete";
    removeBtn.classList.add("delete-btn");

    removeBtn.addEventListener("click", async () => {
      const confirmDelete = confirm(`Are you sure you want to delete Dr. ${doctor.name}?`);
      if (!confirmDelete) return;

      const token = localStorage.getItem("token");
      if (!token) {
        alert("Unauthorized. Please log in again.");
        return;
      }

      try {
        const response = await deleteDoctor(doctor.id, token);
        if (response.success) {
          alert(`Doctor ${doctor.name} deleted successfully.`);
          card.remove();
        } else {
          alert("Failed to delete doctor. Please try again.");
        }
      } catch (error) {
        console.error("Error deleting doctor:", error);
        alert("An error occurred while deleting the doctor.");
      }
    });

    actionsDiv.appendChild(removeBtn);
  }

  // PATIENT (NOT LOGGED IN)
  else if (role === "patient") {
    const bookBtn = document.createElement("button");
    bookBtn.textContent = "Book Now";
    bookBtn.classList.add("book-btn");

    bookBtn.addEventListener("click", () => {
      alert("Please log in to book an appointment.");
    });

    actionsDiv.appendChild(bookBtn);
  }

  // LOGGED-IN PATIENT
  else if (role === "loggedPatient") {
    const bookBtn = document.createElement("button");
    bookBtn.textContent = "Book Now";
    bookBtn.classList.add("book-btn");

    bookBtn.addEventListener("click", async (e) => {
      const token = localStorage.getItem("token");
      if (!token) {
        alert("Session expired. Please log in again.");
        window.location.href = "/";
        return;
      }

      try {
        const patientData = await getPatientData(token);
        showBookingOverlay(e, doctor, patientData);
      } catch (error) {
        console.error("Error fetching patient data:", error);
        alert("Could not retrieve patient data. Please try again.");
      }
    });

    actionsDiv.appendChild(bookBtn);
  }

  // DEFAULT (No role or unknown)
  else {
    const infoMsg = document.createElement("p");
    infoMsg.textContent = "Please select a role to continue.";
    actionsDiv.appendChild(infoMsg);
  }

  // --- 6. Assemble and return the card ---
  card.appendChild(infoDiv);
  card.appendChild(actionsDiv);

  return card;
}
