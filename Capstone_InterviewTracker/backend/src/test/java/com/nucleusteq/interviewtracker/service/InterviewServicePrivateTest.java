package com.nucleusteq.interviewtracker.service;

import com.nucleusteq.interviewtracker.enums.InterviewStage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class InterviewServicePrivateTest {

    @Mock
    private com.nucleusteq.interviewtracker.repository.InterviewRepository interviewRepository;
    @Mock
    private com.nucleusteq.interviewtracker.repository.CandidateRepository candidateRepository;
    @Mock
    private com.nucleusteq.interviewtracker.repository.PanelMemberRepository panelMemberRepository;
    @Mock
    private com.nucleusteq.interviewtracker.repository.InterviewPanelRepository interviewPanelRepository;
    @Mock
    private com.nucleusteq.interviewtracker.repository.UserRepository userRepository;
    @Mock
    private com.nucleusteq.interviewtracker.mapper.InterviewMapper interviewMapper;
    @Mock
    private com.nucleusteq.interviewtracker.repository.FeedbackRepository feedbackRepository;
    @Mock
    private org.springframework.mail.javamail.JavaMailSender mailSender;

    @InjectMocks
    private InterviewService interviewService;

    @Test
    void formatStageName_shouldReturnHumanReadableNames() throws Exception {
        Method m = InterviewService.class.getDeclaredMethod("formatStageName", InterviewStage.class);
        m.setAccessible(true);

        String l1 = (String) m.invoke(interviewService, InterviewStage.L1_TECHNICAL);
        String l2 = (String) m.invoke(interviewService, InterviewStage.L2_TECHNICAL);
        String hr = (String) m.invoke(interviewService, InterviewStage.HR_ROUND);
        String def = (String) m.invoke(interviewService, InterviewStage.SELECTED);

        assertEquals("L1 Technical", l1);
        assertEquals("L2 Technical", l2);
        assertEquals("HR Round", hr);
        assertEquals("SELECTED", def);
    }
}
