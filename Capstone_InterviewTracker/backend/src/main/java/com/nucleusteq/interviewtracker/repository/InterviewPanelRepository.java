package com.nucleusteq.interviewtracker.repository;

import com.nucleusteq.interviewtracker.entity.Interview;
import com.nucleusteq.interviewtracker.entity.InterviewPanel;
import com.nucleusteq.interviewtracker.entity.PanelMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for InterviewPanel entity.
 * Handles the mapping between interviews and assigned panel members.
 */
@Repository
public interface InterviewPanelRepository extends JpaRepository<InterviewPanel, Long> {

    /**
     * Finds all interview assignments for a specific panel member.
     * Used to show a panel member their assigned interviews.
     *
     * @param panelMember the panel member entity
     * @return list of all interview assignments for this panel member
     */
    List<InterviewPanel> findByPanelMember(PanelMember panelMember);

    /**
     * Counts how many panel members are assigned to an interview.
     * Used to enforce the maximum of 2 panel members per interview.
     *
     * @param interview the interview entity
     * @return count of panel members assigned to this interview
     */
    long countByInterview(Interview interview);
}