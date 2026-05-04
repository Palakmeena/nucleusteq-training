package com.nucleusteq.interviewtracker.repository;

import com.nucleusteq.interviewtracker.entity.Feedback;
import com.nucleusteq.interviewtracker.entity.Interview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByInterview(Interview interview);
    List<Feedback> findByPanelMember(com.nucleusteq.interviewtracker.entity.PanelMember panelMember);
    boolean existsByInterviewAndPanelMember(com.nucleusteq.interviewtracker.entity.Interview interview, com.nucleusteq.interviewtracker.entity.PanelMember panelMember);
    boolean existsByPanelMember(com.nucleusteq.interviewtracker.entity.PanelMember panelMember);
}
