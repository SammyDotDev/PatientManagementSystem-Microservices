package com.devnaza.patientservice.service;

import com.devnaza.patientservice.dto.PatientRequestDTO;
import com.devnaza.patientservice.dto.PatientResponseDTO;
import com.devnaza.patientservice.exception.EmailAlreadyExistsException;
import com.devnaza.patientservice.exception.PatientNotFoundException;
import com.devnaza.patientservice.grpc.BillingServiceGrpcClient;
import com.devnaza.patientservice.mapper.PatientMapper;
import com.devnaza.patientservice.model.Patient;
import com.devnaza.patientservice.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PatientService {
    private final PatientRepository patientRepository;
    private final BillingServiceGrpcClient  billingServiceGrpcClient;

    public PatientService(PatientRepository patientRepository,  BillingServiceGrpcClient billingServiceGrpcClient) {
        this.patientRepository = patientRepository;
        this.billingServiceGrpcClient = billingServiceGrpcClient;
    }

    public List<PatientResponseDTO> getPatients(){
        List<Patient> patients = patientRepository.findAll();
        return patients.stream().map(PatientMapper::toDto).toList();
    }

    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO){
        if(patientRepository.existsByEmail(patientRequestDTO.getEmail())){
            throw new EmailAlreadyExistsException("A patient with this email already exists " + patientRequestDTO.getEmail());
        }
        Patient newPatient = patientRepository.save(PatientMapper.toModel(patientRequestDTO));
        billingServiceGrpcClient.createBillingAccount(newPatient.getId().toString(), newPatient.getName(), newPatient.getEmail());
        return PatientMapper.toDto(newPatient);
    }

    public PatientResponseDTO updatePatient(UUID id, PatientRequestDTO patientRequestDTO){
        Patient patient = patientRepository.findById(id).orElseThrow(()-> new PatientNotFoundException("Patient not found with ID: "+ id));

        if(patientRepository.existsByEmailAndIdNot(patientRequestDTO.getEmail(), id)){
            throw new EmailAlreadyExistsException("A patient with this email already exists " + patientRequestDTO.getEmail());
        }
        if(patientRequestDTO.getName() != null){
            patient.setName(patientRequestDTO.getName());
        }
        if(patientRequestDTO.getAddress() != null){
            patient.setAddress(patientRequestDTO.getAddress());
        }
        if(patientRequestDTO.getEmail() != null){
            patient.setEmail(patientRequestDTO.getEmail());
        }
        if(patientRequestDTO.getDateOfBirth() != null){
            patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));
        }

        Patient updatePatient = patientRepository.save(patient);
        return PatientMapper.toDto(updatePatient);
    }

    public void deletePatient(UUID id){
        Patient patient = patientRepository.findById(id).orElseThrow(()->new PatientNotFoundException("Patient not found with ID: "+ id));
        patientRepository.deleteById(patient.getId());
    }


}
