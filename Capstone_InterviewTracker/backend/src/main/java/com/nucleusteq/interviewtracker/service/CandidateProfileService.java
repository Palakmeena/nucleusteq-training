package com.nucleusteq.interviewtracker.service;

import com.nucleusteq.interviewtracker.entity.CandidateProfile;
import com.nucleusteq.interviewtracker.entity.User;
import com.nucleusteq.interviewtracker.repository.CandidateProfileRepository;
import com.nucleusteq.interviewtracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CandidateProfileService {

    private final CandidateProfileRepository profileRepository;
    private final UserRepository userRepository;

    @Autowired
    public CandidateProfileService(CandidateProfileRepository profileRepository, UserRepository userRepository) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
    }

    public CandidateProfile getProfileByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("User not found"));
        
        return profileRepository.findByUser(user)
                .orElseGet(() -> {
                    CandidateProfile newProfile = new CandidateProfile(user);
                    return profileRepository.save(newProfile);
                });
    }

    @Transactional
    public CandidateProfile updateProfile(String email, CandidateProfile updatedInfo) {
        CandidateProfile profile = getProfileByEmail(email);

        profile.setFullName(updatedInfo.getFullName());
        profile.setMobileCode(updatedInfo.getMobileCode());
        profile.setMobileNumber(updatedInfo.getMobileNumber());
        profile.setDateOfBirth(updatedInfo.getDateOfBirth());
        profile.setCurrentOrganization(updatedInfo.getCurrentOrganization());
        profile.setTotalExperience(updatedInfo.getTotalExperience());
        profile.setRelevantExperience(updatedInfo.getRelevantExperience());
        profile.setCurrentCtc(updatedInfo.getCurrentCtc());
        profile.setExpectedCtc(updatedInfo.getExpectedCtc());
        profile.setNoticePeriod(updatedInfo.getNoticePeriod());
        profile.setPreferredLocation(updatedInfo.getPreferredLocation());
        profile.setUpdatedAt(LocalDateTime.now());

        return profileRepository.save(profile);
    }

    @Transactional
    public void updateResume(String email, String resumePath) {
        CandidateProfile profile = getProfileByEmail(email);
        profile.setResumePath(resumePath);
        profile.setUpdatedAt(LocalDateTime.now());
        profileRepository.save(profile);
    }
}
