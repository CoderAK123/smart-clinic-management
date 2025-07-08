/*
  This script handles the admin dashboard functionality for managing doctors:
  - Loads all doctor cards
  - Filters doctors by name, time, or specialty
  - Adds a new doctor via modal form


  Attach a click listener to the "Add Doctor" button
  When clicked, it opens a modal form using openModal('addDoctor')


  When the DOM is fully loaded:
    - Call loadDoctorCards() to fetch and display all doctors


  Function: loadDoctorCards
  Purpose: Fetch all doctors and display them as cards

    Call getDoctors() from the service layer
    Clear the current content area
    For each doctor returned:
    - Create a doctor card using createDoctorCard()
    - Append it to the content div

    Handle any fetch errors by logging them


  Attach 'input' and 'change' event listeners to the search bar and filter dropdowns
  On any input change, call filterDoctorsOnChange()


  Function: filterDoctorsOnChange
  Purpose: Filter doctors based on name, available time, and specialty

    Read values from the search bar and filters
    Normalize empty values to null
    Call filterDoctors(name, time, specialty) from the service

    If doctors are found:
    - Render them using createDoctorCard()
    If no doctors match the filter:
    - Show a message: "No doctors found with the given filters."

    Catch and display any errors with an alert


  Function: renderDoctorCards
  Purpose: A helper function to render a list of doctors passed to it

    Clear the content area
    Loop through the doctors and append each card to the content area


  Function: adminAddDoctor
  Purpose: Collect form data and add a new doctor to the system

    Collect input values from the modal form
    - Includes name, email, phone, password, specialty, and available times

    Retrieve the authentication token from localStorage
    - If no token is found, show an alert and stop execution

    Build a doctor object with the form values

    Call saveDoctor(doctor, token) from the service

    If save is successful:
    - Show a success message
    - Close the modal and reload the page

    If saving fails, show an error message
*/
// adminDashboard.js

import { openModal, closeModal } from "./components/modals.js";
import { getDoctors, saveDoctor, filterDoctors } from "./services/doctorServices.js";
import { createDoctorCard } from "./components/doctorCard.js";

// Attach modal handler to "Add Doctor" button
document.addEventListener("DOMContentLoaded", () => {
  const addDocBtn = document.getElementById("addDocBtn");
  if (addDocBtn) {
    addDocBtn.addEventListener("click", () => openModal("addDoctor"));
  }

  loadDoctorCards();

  // Event listeners for search and filter
  const searchInput = document.getElementById("searchInput");
  const timeFilter = document.getElementById("timeFilter");
  const specialtyFilter = document.getElementById("specialtyFilter");

  [searchInput, timeFilter, specialtyFilter].forEach(input =>
    input?.addEventListener("input", filterDoctorsOnChange)
  );
});

// Load and render all doctor cards
async function loadDoctorCards() {
  try {
    const doctors = await getDoctors();
    renderDoctorCards(doctors);
  } catch (error) {
    console.error("Failed to load doctors:", error);
  }
}

// Filter doctors on input change
async function filterDoctorsOnChange() {
  const name = document.getElementById("searchInput")?.value.trim() || null;
  const time = document.getElementById("timeFilter")?.value || null;
  const specialty = document.getElementById("specialtyFilter")?.value || null;

  try {
    const filtered = await filterDoctors(name, time, specialty);
    if (filtered.length > 0) {
      renderDoctorCards(filtered);
    } else {
      document.getElementById("content").innerHTML = "<p>No doctors found with the given filters.</p>";
    }
  } catch (error) {
    console.error("Error filtering doctors:", error);
    alert("Something went wrong while filtering doctors.");
  }
}

// Render list of doctor cards
function renderDoctorCards(doctors) {
  const contentDiv = document.getElementById("content");
  contentDiv.innerHTML = "";
  doctors.forEach(doctor => {
    const card = createDoctorCard(doctor);
    contentDiv.appendChild(card);
  });
}

// Handle adding a new doctor
window.adminAddDoctor = async function () {
  const name = document.getElementById("docName").value.trim();
  const email = document.getElementById("docEmail").value.trim();
  const phone = document.getElementById("docPhone").value.trim();
  const password = document.getElementById("docPassword").value.trim();
  const specialty = document.getElementById("docSpecialty").value.trim();
  const availableTime = document.getElementById("docAvailableTime").value.trim();

  const token = localStorage.getItem("token");
  if (!token) {
    alert("Session expired. Please log in again.");
    return;
  }

  const doctor = { name, email, phone, password, specialty, availableTime };

  try {
    const result = await saveDoctor(doctor, token);
    if (result.success) {
      alert("Doctor added successfully!");
      closeModal("addDoctor");
      window.location.reload(); // reload dashboard
    } else {
      alert(result.message || "Failed to add doctor.");
    }
  } catch (error) {
    console.error("Add doctor error:", error);
    alert("An error occurred while saving the doctor.");
  }
};
