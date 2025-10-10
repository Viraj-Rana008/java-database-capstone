## MySQL Database Design
Entities:
### Table: admin
  - id: INT, Primary Key, Auto increment
### Table: user
  - id: INT, Primary Key, Auto increment
  - username: VARCHAR, Not Null
  - password: VARCHAR, Not Null
### Table: patient
  - id: INT, Primary Key, Auto increment
  - name: VARCHAR
  - phone_number: VARCHAR
### Table: doctor
  - id: INT, Primary Key, Auto increment
  - name: VARCHAR
  - consultation_fee: INT
  - qualification: VARCHAR
### Table: doctor_working_hour
  - id: INT, Primary Key, Auto increment
  - doctor_id: INT, Foreign Key -> doctor(id)
  - start_time: DATETIME
  - end_time: DATETIME
### Table: appointment
  - id: INT, Primary Key, Auto increment
  - patient_id: INT, Foreign Key -> patient(id)  
  - doctor_id: INT, Foreign Key -> doctor(id)
  - appointment_time: DATETIME
  - status: INT (0=cancelled, 1=done, 2-scheduled)
  - prescription_id: INT


## MongoDB Collection Design
### Document: prescription
assumption: prescription doesn't exists outside a appointment
```
{
  "_id": ObjectId("..."),
  "appointment_id": 434,
  "medicine": [
    {
      "mdecine_name": "dolo",
      "dosage": "10gm",
      "frquency": "3 times a day",
      "duration": "4 days"
    }
  ]
}
```

### Document: patients_health_history
```
{
  "_id": Object("..."),
  "patient_id": 1231,
  "illness": [
    {
      "illness_name": "hernia",
      "treatment_start": Object("..."),
      "treatment_end": Object("..."),
      "medicine": [
        {
          "medicine_name": "PCM",
          "dosage": "1kg",
          "frequency": "once a day",
          "duration": "10 years"
        },
        {
          "medicine_name": "Morphine",
          "dosage": "2 crystals",
          "frequency": null
        }
      ]
    },
    {
      "illness_name": "cancer",
      "treatment_start": Object("...")
    }
  ]
}
```

### Document: doctors_qualification
```
{
  "_id": Object("..."),
  "degree": "MBBS",
  "year": ISODate("..."),
  "university": "Lucknow School of Arts",
  "credentials": "WERG23GGT11009"
}
```

### Document: patients_feedback
```
{
  "_id": Object("..."),
  "feedback": "blah blah blah, not a doctor",
  "appointment_id": null,
  "doctor_id": 1356,
  "patient_id": 563
}
```
