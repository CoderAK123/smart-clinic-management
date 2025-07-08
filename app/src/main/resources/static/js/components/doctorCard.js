/*
Import the overlay function for booking appointments from loggedPatient.js

  Import the deleteDoctor API function to remove doctors (admin role) from docotrServices.js

  Import function to fetch patient details (used during booking) from patientServices.js

  Function to create and return a DOM element for a single doctor card
    Create the main container for the doctor card
    Retrieve the current user role from localStorage
    Create a div to hold doctor information
    Create and set the doctor’s name
    Create and set the doctor's specialization
    Create and set the doctor's email
    Create and list available appointment times
    Append all info elements to the doctor info container
    Create a container for card action buttons
    === ADMIN ROLE ACTIONS ===
      Create a delete button
      Add click handler for delete button
     Get the admin token from localStorage
        Call API to delete the doctor
        Show result and remove card if successful
      Add delete button to actions container
   
    === PATIENT (NOT LOGGED-IN) ROLE ACTIONS ===
      Create a book now button
      Alert patient to log in before booking
      Add button to actions container
  
    === LOGGED-IN PATIENT ROLE ACTIONS === 
      Create a book now button
      Handle booking logic for logged-in patient   
        Redirect if token not available
        Fetch patient data with token
        Show booking overlay UI with doctor and patient info
      Add button to actions container
   
  Append doctor info and action buttons to the car
  Return the complete doctor card element
*/
// doctorCard.js
import { showBookingOverlay } from '../loggedPatient.js';
import { deleteDoctor } from '../services/doctorServices.js';
import { getLoggedPatientDetails } from '../services/patientServices.js';

export function createDoctorCard(doctor) {
  const card = document.createElement('div');
  card.className = 'doctor-card';

  const role = localStorage.getItem('role');

  const infoContainer = document.createElement('div');
  infoContainer.className = 'doctor-info';

  const name = document.createElement('h3');
  name.textContent = doctor.name;

  const specialty = document.createElement('p');
  specialty.textContent = `Specialization: ${doctor.specialty}`;

  const email = document.createElement('p');
  email.textContent = `Email: ${doctor.email}`;

  const times = document.createElement('ul');
  times.textContent = 'Available Times:';
  doctor.availableTimes.forEach(time => {
    const li = document.createElement('li');
    li.textContent = time;
    times.appendChild(li);
  });

  infoContainer.append(name, specialty, email, times);

  const actionsContainer = document.createElement('div');
  actionsContainer.className = 'doctor-actions';

  if (role === 'ADMIN') {
    const deleteBtn = document.createElement('button');
    deleteBtn.textContent = 'Delete';
    deleteBtn.className = 'btn-delete';
    deleteBtn.onclick = async () => {
      const token = localStorage.getItem('token');
      const confirmed = confirm('Are you sure you want to delete this doctor?');
      if (!confirmed) return;

      try {
        const res = await deleteDoctor(doctor.id, token);
        alert('Doctor deleted successfully.');
        card.remove();
      } catch (err) {
        alert('Failed to delete doctor.');
        console.error(err);
      }
    };
    actionsContainer.appendChild(deleteBtn);

  } else if (!role || role === 'GUEST') {
    const bookBtn = document.createElement('button');
    bookBtn.textContent = 'Book Now';
    bookBtn.className = 'btn-book';
    bookBtn.onclick = () => alert('Please log in as a patient to book an appointment.');
    actionsContainer.appendChild(bookBtn);

  } else if (role === 'PATIENT') {
    const bookBtn = document.createElement('button');
    bookBtn.textContent = 'Book Now';
    bookBtn.className = 'btn-book';
    bookBtn.onclick = async () => {
      const token = localStorage.getItem('token');
      if (!token) return alert('Please log in to book.');

      try {
        const patient = await getLoggedPatientDetails(token);
        showBookingOverlay(doctor, patient);
      } catch (err) {
        console.error('Failed to fetch patient details:', err);
        alert('Booking failed. Try again.');
      }
    };
    actionsContainer.appendChild(bookBtn);
  }

  card.append(infoContainer, actionsContainer);
  return card;
}
