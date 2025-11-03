/*
  doctorDashboard.js
  Handles Doctor Dashboard functionality:
  - Loads appointments for the selected date
  - Filters by patient name
  - Provides “Today” quick filter
*/

import { getAllAppointments } from "../services/appointmentRecordService.js";
import { createPatientRow } from "../components/patientRows.js";

/* =====================================================
   GLOBAL VARIABLES
===================================================== */
const tableBody = document.getElementById("patientTableBody");
let selectedDate = new Date().toISOString().split("T")[0]; // today's date (YYYY-MM-DD)
const token = localStorage.getItem("token");
let patientName = "null";

/* =====================================================
   EVENT BINDINGS
===================================================== */
document.addEventListener("DOMContentLoaded", async () => {
  // Load default appointments
  await loadAppointments();

  // Search bar filter
  const searchBar = document.getElementById("searchBar");
  if (searchBar) {
    searchBar.addEventListener("input", async (e) => {
      patientName = e.target.value.trim() || "null";
      await loadAppointments();
    });
  }

  // "Today's Appointments" button
  const todayButton = document.getElementById("todayButton");
  if (todayButton) {
    todayButton.addEventListener("click", async () => {
      selectedDate = new Date().toISOString().split("T")[0];
      const datePicker = document.getElementById("datePicker");
      if (datePicker) datePicker.value = selectedDate;
      await loadAppointments();
    });
  }

  // Date picker filter
  const datePicker = document.getElementById("datePicker");
  if (datePicker) {
    datePicker.value = selectedDate;
    datePicker.addEventListener("change", async (e) => {
      selectedDate = e.target.value;
      await loadAppointments();
    });
  }
});

/* =====================================================
   FUNCTION: loadAppointments
   Purpose: Fetch and render appointments for the doctor
===================================================== */
async function loadAppointments() {
  try {
    if (!tableBody) return;
    tableBody.innerHTML = `
      <tr>
        <td colspan="5">Loading appointments...</td>
      </tr>
    `;

    const appointments = await getAllAppointments(selectedDate, patientName, token);

    // Clear table before rendering
    tableBody.innerHTML = "";

    if (!appointments || appointments.length === 0) {
      tableBody.innerHTML = `
        <tr>
          <td colspan="5">No Appointments found for the selected date.</td>
        </tr>
      `;
      return;
    }

    // Render each appointment row
    appointments.forEach((appointment) => {
      const patient = {
        id: appointment?.patientId || "N/A",
        name: appointment?.patientName || "Unknown",
        phone: appointment?.patientPhone || "-",
        email: appointment?.patientEmail || "-",
        prescription: appointment?.prescription || "",
      };

      const row = createPatientRow(patient);
      tableBody.appendChild(row);
    });
  } catch (error) {
    console.error("Error loading appointments:", error);
    tableBody.innerHTML = `
      <tr>
        <td colspan="5">Error loading appointments. Try again later.</td>
      </tr>
    `;
  }
}
