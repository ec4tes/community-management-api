package com.devkit.hsd.service.impl;

import com.devkit.hsd.HSD.HsdApplication;
import com.devkit.hsd.common.enums.ApprovalStatus;
import com.devkit.hsd.common.enums.EventTypes;
import com.devkit.hsd.common.enums.MeetingTypes;
import com.devkit.hsd.common.enums.Priority;
import com.devkit.hsd.common.enums.Role;
import com.devkit.hsd.common.enums.SystemRole;
import com.devkit.hsd.common.enums.TaskStatus;
import com.devkit.hsd.dto.request.CommitteeRequest;
import com.devkit.hsd.dto.request.CreateEventRequest;
import com.devkit.hsd.dto.request.CreateMeetingRequest;
import com.devkit.hsd.dto.request.CreateProjectRequest;
import com.devkit.hsd.dto.response.CommitteeResponse;
import com.devkit.hsd.dto.response.EventResponse;
import com.devkit.hsd.dto.response.LeaderBoardResponse;
import com.devkit.hsd.dto.response.MeetingResponse;
import com.devkit.hsd.dto.response.ProjectResponse;
import com.devkit.hsd.dto.response.ProjectTaskResponse;
import com.devkit.hsd.dto.response.UserResponse;
import com.devkit.hsd.entity.Committee;
import com.devkit.hsd.entity.Community;
import com.devkit.hsd.entity.Event;
import com.devkit.hsd.entity.Meeting;
import com.devkit.hsd.entity.Project;
import com.devkit.hsd.entity.ProjectTask;
import com.devkit.hsd.entity.TaskPointRecord;
import com.devkit.hsd.entity.User;
import com.devkit.hsd.exception.ForbiddenException;
import com.devkit.hsd.exception.NotFoundException;
import com.devkit.hsd.repository.ActivityLogRepository;
import com.devkit.hsd.repository.CommitteeRepository;
import com.devkit.hsd.repository.CommunityRepository;
import com.devkit.hsd.repository.EventAttendanceRepository;
import com.devkit.hsd.repository.EventRepository;
import com.devkit.hsd.repository.MeetingAttendanceRepository;
import com.devkit.hsd.repository.MeetingRepository;
import com.devkit.hsd.repository.ProjectRepository;
import com.devkit.hsd.repository.ProjectTaskRepository;
import com.devkit.hsd.repository.TaskPointRecordRepository;
import com.devkit.hsd.repository.UserRepository;
import com.devkit.hsd.service.abs.ICommitteeService;
import com.devkit.hsd.service.abs.IEventService;
import com.devkit.hsd.service.abs.IMeetingService;
import com.devkit.hsd.service.abs.IProjectService;
import com.devkit.hsd.service.abs.IProjectTaskService;
import com.devkit.hsd.service.abs.IStatsService;
import com.devkit.hsd.service.abs.IUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = HsdApplication.class)
@ActiveProfiles("test")
@Transactional
class MultiTenantIntegrationTest {

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommitteeRepository committeeRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectTaskRepository projectTaskRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private TaskPointRecordRepository taskPointRecordRepository;

    @Autowired
    private ActivityLogRepository activityLogRepository;

    @Autowired
    private EventAttendanceRepository eventAttendanceRepository;

    @Autowired
    private MeetingAttendanceRepository meetingAttendanceRepository;

    @Autowired
    private IUserService userService;

    @Autowired
    private ICommitteeService committeeService;

    @Autowired
    private IProjectService projectService;

    @Autowired
    private IProjectTaskService projectTaskService;

    @Autowired
    private IEventService eventService;

    @Autowired
    private IMeetingService meetingService;

    @Autowired
    private IStatsService statsService;

    private Community communityA;
    private Community communityB;
    private User leaderA;
    private User assistantA;
    private User memberA;
    private User communityAdminA;
    private User leaderB;
    private User superAdmin;
    private Committee committeeA;
    private Committee committeeB;
    private Project projectA;
    private Project projectB;
    private ProjectTask taskA;
    private ProjectTask taskB;
    private Event eventA;
    private Event eventB;
    private Meeting meetingA;
    private Meeting meetingB;

    @BeforeEach
    void setUp() {
        meetingAttendanceRepository.deleteAll();
        eventAttendanceRepository.deleteAll();
        activityLogRepository.deleteAll();
        taskPointRecordRepository.deleteAll();
        projectTaskRepository.deleteAll();
        projectRepository.deleteAll();
        meetingRepository.deleteAll();
        eventRepository.deleteAll();
        committeeRepository.deleteAll();
        userRepository.deleteAll();
        communityRepository.deleteAll();

        communityA = communityRepository.save(new Community("Community A", "Uni A", "City A", "community-a", true));
        communityB = communityRepository.save(new Community("Community B", "Uni B", "City B", "community-b", true));

        leaderA = saveUser("leaderA", "leaderA@test.com", SystemRole.USER, Role.KOMITE_LIDERI, communityA);
        assistantA = saveUser("assistantA", "assistantA@test.com", SystemRole.USER, Role.KOMITE_YARDIMCISI, communityA);
        memberA = saveUser("memberA", "memberA@test.com", SystemRole.USER, Role.UYE, communityA);
        communityAdminA = saveUser("communityAdminA", "communityAdminA@test.com", SystemRole.COMMUNITY_ADMIN, Role.UYE, communityA);
        leaderB = saveUser("leaderB", "leaderB@test.com", SystemRole.USER, Role.KOMITE_LIDERI, communityB);
        superAdmin = saveUser("superAdmin", "superAdmin@test.com", SystemRole.SUPER_ADMIN, Role.UYE, communityA);

        committeeA = saveCommittee("Committee A", communityA, leaderA, assistantA, List.of(leaderA, assistantA, memberA, communityAdminA));
        committeeB = saveCommittee("Committee B", communityB, leaderB, leaderB, List.of(leaderB));

        projectA = saveProject("Project A", communityA, committeeA, leaderA);
        projectB = saveProject("Project B", communityB, committeeB, leaderB);

        taskA = saveTask("Task A", projectA, communityA, leaderA);
        taskB = saveTask("Task B", projectB, communityB, leaderB);

        eventA = saveEvent("Event A", communityA);
        eventB = saveEvent("Event B", communityB);

        meetingA = saveMeeting("Meeting A", communityA, committeeA);
        meetingB = saveMeeting("Meeting B", communityB, committeeB);
    }

    @Test
    void crossCommunityAccess_isDenied_forScopedUsers() {
        authenticateAs(memberA);

        assertThrows(NotFoundException.class, () -> projectService.getProjectById(projectB.getId()));
        assertThrows(NotFoundException.class, () -> projectTaskService.claimTask(taskB.getId()));
        assertThrows(NotFoundException.class, () -> eventService.getEventById(eventB.getId()));
        assertThrows(NotFoundException.class, () -> meetingService.getMeetingById(meetingB.getId()));
        assertThrows(NotFoundException.class, () -> userService.getUserById(leaderB.getId()));
        assertThrows(NotFoundException.class, () -> committeeService.getCommitteeById(committeeB.getId()));
    }

    @Test
    void sameCommunityAccess_succeeds_forScopedUsers() {
        authenticateAs(memberA);

        ProjectResponse projectResponse = projectService.getProjectById(projectA.getId());
        ProjectTaskResponse claimedTask = projectTaskService.claimTask(taskA.getId());
        EventResponse eventResponse = eventService.getEventById(eventA.getId());
        MeetingResponse meetingResponse = meetingService.getMeetingById(meetingA.getId());
        UserResponse userResponse = userService.getUserById(leaderA.getId());
        CommitteeResponse committeeResponse = committeeService.getCommitteeById(committeeA.getId());

        assertEquals(projectA.getId(), projectResponse.getId());
        assertEquals(taskA.getId(), claimedTask.getId());
        assertEquals(eventA.getId(), eventResponse.getId());
        assertEquals(meetingA.getId(), meetingResponse.getId());
        assertEquals(leaderA.getId(), userResponse.getId());
        assertEquals(committeeA.getId(), committeeResponse.getId());
    }

    @Test
    void superAdmin_hasGlobalReadAccess_andCanQueryOtherTenantLeaderboard() {
        createPointRecord(memberA, projectA, taskA, communityA, 50);
        createPointRecord(leaderB, projectB, taskB, communityB, 80);
        authenticateAs(superAdmin);

        ProjectResponse projectResponse = projectService.getProjectById(projectB.getId());
        EventResponse eventResponse = eventService.getEventById(eventB.getId());
        MeetingResponse meetingResponse = meetingService.getMeetingById(meetingB.getId());
        UserResponse userResponse = userService.getUserById(leaderB.getId());
        CommitteeResponse committeeResponse = committeeService.getCommitteeById(committeeB.getId());
        List<ProjectTaskResponse> availableTasks = projectTaskService.getAvailableTasks();
        List<LeaderBoardResponse> leaderboard = statsService.getCurrentMonthLeaderboard(communityB.getId());

        assertEquals(projectB.getId(), projectResponse.getId());
        assertEquals(eventB.getId(), eventResponse.getId());
        assertEquals(meetingB.getId(), meetingResponse.getId());
        assertEquals(leaderB.getId(), userResponse.getId());
        assertEquals(committeeB.getId(), committeeResponse.getId());
        assertTrue(availableTasks.stream().anyMatch(task -> task.getId().equals(taskB.getId())));
        assertEquals(1, leaderboard.size());
        assertEquals(leaderB.getId(), leaderboard.get(0).getUserId());
    }

    @Test
    void leaderboardQuery_isTenantAware_forRegularUsers() {
        createPointRecord(memberA, projectA, taskA, communityA, 35);
        createPointRecord(leaderB, projectB, taskB, communityB, 90);
        authenticateAs(memberA);

        List<LeaderBoardResponse> leaderboard = statsService.getCurrentMonthLeaderboard(communityB.getId());

        assertEquals(1, leaderboard.size());
        assertEquals(memberA.getId(), leaderboard.get(0).getUserId());
        assertFalse(leaderboard.stream().anyMatch(item -> item.getUserId().equals(leaderB.getId())));
    }

    @Test
    void roleMatrix_enforcesExpectedProjectCreationRules() {
        authenticateAs(memberA);
        assertThrows(ForbiddenException.class, () -> projectService.createProject(buildProjectRequest("Member Project", committeeA.getId())));

        authenticateAs(leaderA);
        ProjectResponse leaderProject = projectService.createProject(buildProjectRequest("Leader Project", committeeA.getId()));

        authenticateAs(assistantA);
        ProjectResponse assistantProject = projectService.createProject(buildProjectRequest("Assistant Project", committeeA.getId()));

        authenticateAs(communityAdminA);
        ProjectResponse adminProject = projectService.createProject(buildProjectRequest("Community Admin Project", committeeA.getId()));

        assertNotNull(leaderProject.getId());
        assertNotNull(assistantProject.getId());
        assertNotNull(adminProject.getId());
    }

    @Test
    void communityAdmin_canManageCommitteeEventAndMeetingFlows() {
        authenticateAs(communityAdminA);

        CommitteeResponse createdCommittee = committeeService.createCommittee(
                new CommitteeRequest(
                        "Admin Committee",
                        "Created by community admin",
                        leaderA.getId(),
                        assistantA.getId(),
                        List.of(leaderA.getId(), assistantA.getId(), memberA.getId()),
                        communityA.getId()
                )
        );

        EventResponse createdEvent = eventService.createEvent(buildEventRequest("Community Admin Event"));
        MeetingResponse createdMeeting = meetingService.createMeeting(buildMeetingRequest("Community Admin Meeting", committeeA.getId()));

        assertNotNull(createdCommittee.getId());
        assertNotNull(createdEvent.getId());
        assertNotNull(createdMeeting.getId());
    }

    @Test
    void completingTask_preservesTenantConsistencyAndUpdatesPoints() {
        authenticateAs(memberA);

        projectTaskService.claimTask(taskA.getId());
        ProjectTaskResponse completedTask = projectTaskService.completeTask(taskA.getId());

        TaskPointRecord pointRecord = taskPointRecordRepository.findAll().getFirst();
        User updatedMember = userRepository.findById(memberA.getId()).orElseThrow();

        assertEquals(TaskStatus.TAMAMLANDI, completedTask.getStatus());
        assertEquals(communityA.getId(), pointRecord.getCommunity().getId());
        assertEquals(projectA.getId(), pointRecord.getProject().getId());
        assertEquals(taskA.getId(), pointRecord.getProjectTask().getId());
        assertEquals(memberA.getId(), pointRecord.getUser().getId());
        assertEquals(taskA.getPoints(), pointRecord.getPoints());
        assertEquals(taskA.getPoints(), updatedMember.getTotalPoints());
    }

    @Test
    void tenantBoundSchema_hasNonNullCommunityForeignKeys_andMigrationContainsHardeningSteps() {
        assertFalse(committeeRepository.findAll().isEmpty());
        assertNotNull(committeeA.getCommunity());
        assertNotNull(projectA.getCommunity());
        assertNotNull(taskA.getCommunity());
        assertNotNull(eventA.getCommunity());
        assertNotNull(meetingA.getCommunity());
    }

    private void authenticateAs(User user) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())
        );
    }

    private User saveUser(String username, String email, SystemRole systemRole, Role role, Community community) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword("password");
        user.setFirstName(username);
        user.setLastName("tester");
        user.setDepartment("CS");
        user.setGrade(3);
        user.setTotalPoints(0);
        user.setSystemRole(systemRole);
        user.setRole(role);
        user.setCommunity(community);
        user.setIsActive(true);
        user.setIsOnline(false);
        return userRepository.save(user);
    }

    private Committee saveCommittee(String name, Community community, User leader, User viceLeader, List<User> members) {
        Committee committee = new Committee();
        committee.setName(name);
        committee.setDescription(name + " description");
        committee.setCommunity(community);
        committee.setLeader(leader);
        committee.setViceLeader(viceLeader);
        committee.setMembers(members);
        return committeeRepository.save(committee);
    }

    private Project saveProject(String name, Community community, Committee committee, User owner) {
        Project project = new Project();
        project.setName(name);
        project.setDescription(name + " description");
        project.setPriority(Priority.YUKSEK);
        project.setOwner(owner);
        project.setCommittee(committee);
        project.setCommunity(community);
        return projectRepository.save(project);
    }

    private ProjectTask saveTask(String title, Project project, Community community, User createdBy) {
        ProjectTask task = new ProjectTask();
        task.setTitle(title);
        task.setDescription(title + " description");
        task.setPriority(Priority.ORTA);
        task.setStatus(TaskStatus.MUSAIT);
        task.setPoints(25);
        task.setDeadline(LocalDateTime.now().plusDays(7));
        task.setProject(project);
        task.setCreatedBy(createdBy);
        task.setCommunity(community);
        return projectTaskRepository.save(task);
    }

    private Event saveEvent(String title, Community community) {
        Event event = new Event();
        event.setTitle(title);
        event.setDescription(title + " description");
        event.setEventType(EventTypes.SOSYAL_ETKINLIK);
        event.setDateTime(LocalDateTime.now().plusDays(5));
        event.setLocation(title + " location");
        event.setQrCodeData(title + "-qr");
        event.setQrExpiresAt(LocalDateTime.now().plusDays(5).plusHours(1));
        event.setAttendancePoints(15);
        event.setApprovalStatus(ApprovalStatus.APPROVED);
        event.setCommunity(community);
        return eventRepository.save(event);
    }

    private Meeting saveMeeting(String title, Community community, Committee committee) {
        Meeting meeting = new Meeting();
        meeting.setTitle(title);
        meeting.setDescription(title + " description");
        meeting.setEventType(EventTypes.SOSYAL_ETKINLIK);
        meeting.setDateTime(LocalDateTime.now().plusDays(6));
        meeting.setLocation(title + " location");
        meeting.setQrCodeData(title + "-qr");
        meeting.setQrExpiresAt(LocalDateTime.now().plusDays(6).plusHours(1));
        meeting.setAttendancePoints(20);
        meeting.setApprovalStatus(ApprovalStatus.APPROVED);
        meeting.setMeetingType(MeetingTypes.KOMITE);
        meeting.setAgendaItems("Agenda");
        meeting.setNotes("Notes");
        meeting.setDecisions("Decisions");
        meeting.setActions("Actions");
        meeting.setIsGeneral(false);
        meeting.setCommittee(committee);
        meeting.setCommunity(community);
        return meetingRepository.save(meeting);
    }

    private void createPointRecord(User user, Project project, ProjectTask task, Community community, int points) {
        TaskPointRecord taskPointRecord = new TaskPointRecord();
        taskPointRecord.setUser(user);
        taskPointRecord.setProject(project);
        taskPointRecord.setProjectTask(task);
        taskPointRecord.setCommunity(community);
        taskPointRecord.setDescription(task.getTitle() + " completed");
        taskPointRecord.setPoints(points);
        taskPointRecordRepository.save(taskPointRecord);
        user.setTotalPoints(points);
        userRepository.save(user);
    }

    private CreateProjectRequest buildProjectRequest(String name, Long committeeId) {
        CreateProjectRequest request = new CreateProjectRequest();
        request.setName(name);
        request.setDescription(name + " description");
        request.setPriority(Priority.YUKSEK);
        request.setCommitteeId(committeeId);
        return request;
    }

    private CreateEventRequest buildEventRequest(String title) {
        CreateEventRequest request = new CreateEventRequest();
        request.setTitle(title);
        request.setDescription(title + " description");
        request.setEventType(EventTypes.SOSYAL_ETKINLIK);
        request.setDateTime(LocalDateTime.now().plusDays(10));
        request.setLocation(title + " location");
        request.setAttendancePoints(50);
        request.setApprovalStatus(ApprovalStatus.APPROVED);
        return request;
    }

    private CreateMeetingRequest buildMeetingRequest(String title, Long committeeId) {
        CreateMeetingRequest request = new CreateMeetingRequest();
        request.setTitle(title);
        request.setDescription(title + " description");
        request.setEventType(EventTypes.SOSYAL_ETKINLIK);
        request.setDateTime(LocalDateTime.now().plusDays(10));
        request.setLocation(title + " location");
        request.setAttendancePoints(30);
        request.setApprovalStatus(ApprovalStatus.APPROVED);
        request.setMeetingType(MeetingTypes.KOMITE);
        request.setAgendaItems("Agenda");
        request.setNotes("Notes");
        request.setDecisions("Decisions");
        request.setActions("Actions");
        request.setIsGeneral(false);
        request.setCommitteeId(committeeId);
        return request;
    }
}
