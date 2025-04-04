import React from "react";
import Navbar from "../components/Navbar";
import Footer from "../components/Footer";
import "bootstrap/dist/css/bootstrap.min.css";
import { FaPhone, FaEnvelope, FaMapMarkerAlt } from "react-icons/fa";
import "./Contact.css";
import team1 from "../team1.jpg";
import team2 from "../team2.jpg";
import team3 from "../team3.jpg";

const Contact = () => {
  // Team member data with your actual images
  const teamMembers = [
    {
      name: "Puja Khadka",
      role: "Frontend Developer",
      description: "Expert in React and UI/UX design",
      position: "left",
      image: team1
    },
    {
      name: "Sneha Baniya",
      role: "Backend Developer",
      description: "Experienced in Node.js and database management",
      position: "center",
      image: team2
    },
    {
      name: "Abhishek khadka",
      role: "Full stack developer",
      description: "Skilled in both frontend and backend technologies",
      position: "right",
      image: team3
    }
  ];

  // Single store location
  const store = {
    name: "Adventure Aware",
    hours: "Mon-Fri: 10:00am - 5:00pm",
    address: "2340 Dundas St W Suite 200, Toronto, ON M6P 4A9"
  };

  return (
    <div>
      <Navbar user={null} />
      
      {/* Services Section */}
      <div className="container py-4 text-center">
        <h3 className="fw-bold mb-4">Adventure Aware</h3>
        <h4 className="mb-4">Our Team Members</h4>
      </div>

      {/* Team Section - Updated layout */}
      <div className="container py-5 position-relative" style={{ height: "550px" }}>
        {/* Left Team Member */}
        <div className="position-absolute start-0" style={{ top: "0", width: "350px" }}>
          <div className="card border-0 bg-transparent">
            <img 
              src={team1} 
              alt={teamMembers[0].name}
              className="img-fluid rounded"
              style={{ height: '400px', width: '350px', objectFit: 'cover' }}
            />
            <div className="card-body px-0 text-center">
              <h5 className="fw-bold">{teamMembers[0].name}</h5>
              <p className="text-primary">{teamMembers[0].role}</p>
              <p>{teamMembers[0].description}</p>
            </div>
          </div>
        </div>

        {/* Right Team Member */}
        <div className="position-absolute end-0" style={{ top: "0", width: "350px" }}>
          <div className="card border-0 bg-transparent">
            <img 
              src={team3} 
              alt={teamMembers[1].name}
              className="img-fluid rounded"
              style={{ height: '400px', width: '350px', objectFit: 'cover' }}
            />
            <div className="card-body px-0 text-center">
              <h5 className="fw-bold">{teamMembers[1].name}</h5>
              <p className="text-primary">{teamMembers[1].role}</p>
              <p>{teamMembers[1].description}</p>
            </div>
          </div>
        </div>

        {/* Center Team Member (positioned slightly lower) */}
        <div className="position-absolute start-50 translate-middle-x" style={{ top: "70px", width: "350px" }}>
          <div className="card border-0 bg-transparent">
            <img 
              src={team2} 
              alt={teamMembers[2].name}
              className="img-fluid rounded"
              style={{ height: '400px', width: '350px', objectFit: 'cover' }}
            />
            <div className="card-body px-0 text-center">
              <h5 className="fw-bold">{teamMembers[2].name}</h5>
              <p className="text-primary">{teamMembers[2].role}</p>
              <p>{teamMembers[2].description}</p>
            </div>
          </div>
        </div>
      </div>

      {/* Store Locator Section - Updated layout */}
      <div className="container-fluid bg-light py-5">
        <div className="container">
          <h2 className="text-center fw-bold mb-4">Find a Store</h2>
          
          <div className="row g-0"> {/* Removed gutter between columns */}
            <div className="col-md-6 pe-md-4"> {/* Added right padding only */}
              <div className="card mb-3 shadow-sm h-100">
                <div className="card-body">
                  <h5 className="fw-bold">{store.name}</h5>
                  <p>{store.hours}</p>
                  <p className="text-muted">{store.address}</p>
                </div>
              </div>
            </div>
            
            <div className="col-md-6 ps-md-0"> {/* Removed left padding */}
              <iframe 
                src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2886.5794787263144!2d-79.45478262382295!3d43.65691677110191!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x882b351266c2c95f%3A0xd3c1ce5712a3b234!2sSault%20College%20-%20Toronto%20Campus!5e0!3m2!1sen!2sca!4v1743739654637!5m2!1sen!2sca" 
                width="100%" 
                height="100%" 
                style={{ border: 0, minHeight: '300px' }}
                allowFullScreen 
                loading="lazy" 
                referrerPolicy="no-referrer-when-downgrade"
                title="Store Location"
              ></iframe>
            </div>
          </div>
        </div>
      </div>

      {/* Contact Form Section - Split layout */}
      <div className="container py-5">
        <h1 className="text-center mb-4">Contact us</h1>
        
        <div className="row">
          {/* Left Section - Description */}
          <div className="col-md-6 pe-md-5 d-flex align-items-center justify-content-center">
           <p className="text-center mb-0">
             Need to get in touch with us? Either fill out the form with your inquiry or find the department email you'd like to contact below.
           </p>
          </div>
          
          {/* Right Section - Form */}
          <div className="col-md-6">
            <form>
              <div className="mb-4">
                <hr className="mb-4 border-2 border-top" />
                <div className="row">
                  <div className="col-md-6 mb-3">
                    <label className="form-label fw-bold">First name*</label>
                    <input type="text" className="form-control border-0 border-bottom rounded-0" required />
                  </div>
                  <div className="col-md-6 mb-3">
                    <label className="form-label fw-bold">Last name*</label>
                    <input type="text" className="form-control border-0 border-bottom rounded-0" />
                  </div>
                </div>
                <hr className="mb-4 border-2 border-top" />
              </div>
              
              <div className="mb-4">
                <label className="form-label fw-bold">Email*</label>
                <input type="email" className="form-control mb-3 border-0 border-bottom rounded-0" required />
                <hr className="mb-4 border-2 border-top" />
              </div>
              
              <div className="mb-4">
                <label className="form-label">What can we help you with?</label>
                <textarea className="form-control border-0 border-bottom rounded-0" rows="4"></textarea>
                <hr className="mb-4 border-2 border-top" />
              </div>
              
              <div className="text-center">
                <button type="submit" className="btn btn-primary px-5 py-2 rounded-0">Submit</button>
              </div>
            </form>
          </div>
        </div>
      </div>
      
      <Footer />
    </div>
  );
};

export default Contact;