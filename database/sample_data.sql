
-- OPTIONAL TEST FIXTURE: adds sample company rows without deleting existing data.
-- Do not load this file as part of normal setup or against production databases.
USE job_tracker;

INSERT IGNORE INTO companies (company_id, name, location, role_offered, contact_email) VALUES
(1, 'ABC Technologies', 'Chennai, India', 'Software Developer Intern', 'hr@abctech.com'),
(2, 'XYZ Solutions', 'Bangalore, India', 'Graduate Engineer Trainee', 'careers@xyz.com'),
(3, 'BrightSoft', 'Hyderabad, India', 'Java Developer Intern', 'hr@brightsoft.com'),
(4, 'Nexa Systems', 'Chennai, India', 'Associate Engineer', 'jobs@nexa.com'),
(5, 'TechCorp', 'Pune, India', 'Data Analyst', 'hr@techcorp.com'),
(6, 'CodeLabs', 'Delhi, India', 'Web Development Intern', 'careers@codelabs.com'),
(7, 'Zoho Corporation', 'Chennai, India', 'Software Developer Intern', 'careers@zoho.com'),
(8, 'Freshworks', 'Chennai, India', 'Product Engineer Intern', 'careers@freshworks.com'),
(9, 'Microsoft India', 'Hyderabad, India', 'Software Engineer Intern', 'careers@microsoft.com'),
(10, 'Google India', 'Bangalore, India', 'Software Engineer Intern', 'careers@google.com'),
(11, 'Amazon India', 'Bangalore, India', 'SDE Intern', 'careers@amazon.in'),
(12, 'Adobe India', 'Noida, India', 'Computer Scientist Intern', 'careers@adobe.com'),
(13, 'Flipkart', 'Bangalore, India', 'Backend Developer Intern', 'careers@flipkart.com'),
(14, 'Paytm', 'Noida, India', 'Software Developer Intern', 'careers@paytm.com'),
(15, 'Swiggy', 'Bangalore, India', 'Data Analyst Intern', 'careers@swiggy.in'),
(16, 'Oracle India', 'Hyderabad, India', 'Associate Software Engineer', 'careers@oracle.com'),
(17, 'Infosys', 'Chennai, India', 'Systems Engineer', 'askus@infosys.com'),
(18, 'Tata Consultancy Services', 'Chennai, India', 'Assistant System Engineer', 'careers@tcs.com'),
(19, 'Wipro', 'Bangalore, India', 'Project Engineer', 'careers@wipro.com'),
(20, 'HCLTech', 'Noida, India', 'Graduate Engineer Trainee', 'careers@hcltech.com'),
(21, 'Tech Mahindra', 'Pune, India', 'Associate Software Engineer', 'careers@techmahindra.com'),
(22, 'Cognizant', 'Chennai, India', 'Programmer Analyst Trainee', 'careers@cognizant.com'),
(23, 'Capgemini', 'Mumbai, India', 'Analyst', 'careers@capgemini.com'),
(24, 'Accenture', 'Bangalore, India', 'Associate Software Engineer', 'careers@accenture.com'),
(25, 'LTIMindtree', 'Mumbai, India', 'Graduate Engineer Trainee', 'careers@ltimindtree.com'),
(26, 'Mphasis', 'Bangalore, India', 'Software Engineer Trainee', 'careers@mphasis.com');

