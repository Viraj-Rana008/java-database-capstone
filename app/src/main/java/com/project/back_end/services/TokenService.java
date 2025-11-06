package com.project.back_end.services;

import com.project.back_end.models.Admin;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class TokenService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Autowired
    public TokenService(AdminRepository adminRepository,
                       DoctorRepository doctorRepository,
                       PatientRepository patientRepository) {
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateToken(String identifier) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + 7 * 24 * 60 * 60 * 1000); // 7 days

        return Jwts.builder()
                .subject(identifier)
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(getSigningKey())
                .compact();
    }

    public String extractEmail(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateToken(String token, String user) {
        try {
            String identifier = extractEmail(token);

            if ("admin".equalsIgnoreCase(user)) {
                Admin admin = adminRepository.findByUsername(identifier);
                return admin != null;
            } else if ("doctor".equalsIgnoreCase(user)) {
                Doctor doctor = doctorRepository.findByEmail(identifier);
                return doctor != null;
            } else if ("patient".equalsIgnoreCase(user)) {
                Patient patient = patientRepository.findByEmail(identifier);
                return patient != null;
            }

            return false;
        } catch (Exception e) {
            return false;
        }
    }
}