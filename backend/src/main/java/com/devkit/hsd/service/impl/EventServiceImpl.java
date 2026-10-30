package com.devkit.hsd.service.impl;

import com.devkit.hsd.common.enums.ErrorCode;
import com.devkit.hsd.common.enums.Role;
import com.devkit.hsd.common.enums.SystemRole;
import com.devkit.hsd.dto.request.CreateEventRequest;
import com.devkit.hsd.dto.request.EventAttendanceScanRequest;
import com.devkit.hsd.dto.request.UpdateEventRequest;
import com.devkit.hsd.dto.response.AttendanceListItemResponse;
import com.devkit.hsd.dto.response.EventAttendanceResponse;
import com.devkit.hsd.dto.response.EventResponse;
import com.devkit.hsd.entity.Community;
import com.devkit.hsd.entity.Event;
import com.devkit.hsd.entity.EventAttendance;
import com.devkit.hsd.entity.User;
import com.devkit.hsd.exception.BadRequestException;
import com.devkit.hsd.exception.ConflictException;
import com.devkit.hsd.exception.ForbiddenException;
import com.devkit.hsd.exception.NotFoundException;
import com.devkit.hsd.mapper.EventAttendanceMapper;
import com.devkit.hsd.mapper.EventMapper;
import com.devkit.hsd.repository.CommunityRepository;
import com.devkit.hsd.repository.EventAttendanceRepository;
import com.devkit.hsd.repository.EventRepository;
import com.devkit.hsd.repository.UserRepository;
import com.devkit.hsd.service.abs.IAuthService;
import com.devkit.hsd.service.abs.IEventService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


@Service
public class EventServiceImpl implements IEventService {

    private final EventRepository eventRepository;

    private final EventMapper eventMapper;

    private final IAuthService authService;

    private final EventAttendanceRepository eventAttendanceRepository;

    private final EventAttendanceMapper eventAttendanceMapper;

    private final UserRepository userRepository;

    private final CommunityRepository communityRepository;

    public EventServiceImpl(EventRepository eventRepository, EventMapper eventMapper, IAuthService authService, EventAttendanceRepository eventAttendanceRepository, EventAttendanceMapper eventAttendanceMapper, UserRepository userRepository, CommunityRepository communityRepository) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
        this.authService = authService;
        this.eventAttendanceRepository = eventAttendanceRepository;
        this.eventAttendanceMapper = eventAttendanceMapper;
        this.userRepository = userRepository;
        this.communityRepository = communityRepository;
    }


    private void checkEventAuthority(){
        User authenticatedUser = authService.getAuthenticatedUser();

        if (authenticatedUser.getSystemRole() == SystemRole.SUPER_ADMIN
                || authenticatedUser.getSystemRole() == SystemRole.COMMUNITY_ADMIN) {
            return;
        }

        if (!(authenticatedUser.getRole() == Role.KOMITE_LIDERI || authenticatedUser.getRole() == Role.KOMITE_YARDIMCISI)){
            throw new ForbiddenException(ErrorCode.USER_NOT_AUTHORIZED_TO_CREATE_EVENT,"Yetkiniz yok");
        }
    }

    private void validateEventRequestRules(LocalDateTime dateTime, Integer attendancePoints){
        if(dateTime != null){
            if (dateTime.isBefore(LocalDateTime.now())){
                throw new BadRequestException(ErrorCode.EVENT_DATE_INVALID,"Bu tarihte etkinlik oluşturulamaz!");
            }
            if (dateTime.isBefore(LocalDateTime.now().plusMinutes(30))) {
                throw new BadRequestException(ErrorCode.EVENT_DATE_TOO_SOON,"Etkinlik tarihi şu andan en az 30 dakika sonra olmalıdır!");
            }
        }
        if(attendancePoints != null){
            if (attendancePoints < 0){
                throw new BadRequestException(ErrorCode.INVALID_ATTENDANCE_POINT,"Puan negatif olamaz!");
            }
            if (attendancePoints > 1000){
                throw new BadRequestException(ErrorCode.ATTENDANCE_POINT_OUT_OF_RANGE,"Puan çok yüksek!");
            }
        }
    }

    private void checkEventDuplicate(CreateEventRequest request, Long communityId){
        if (eventRepository.existsByTitleAndDateTimeAndLocationAndCommunityId(request.getTitle(),request.getDateTime(), request.getLocation().trim(),communityId)){
            throw new ConflictException(ErrorCode.EVENT_ALREADY_EXISTS,"Bu etkinlik kaydı zaten var!");
        }
    }

    private Community resolveTargetCommunity(User user, CreateEventRequest request) {
        if (user.getSystemRole() == SystemRole.SUPER_ADMIN && request.getCommunityId() != null) {
            return communityRepository.findById(request.getCommunityId())
                    .orElseThrow(() -> new NotFoundException(
                            ErrorCode.COMMUNITY_NOT_FOUND,
                            "Topluluk bulunamadı!"
                    ));
        }

        return user.getCommunity();
    }



    @Override
    public EventResponse createEvent(CreateEventRequest request) {

        User user = authService.getAuthenticatedUser();

        checkEventAuthority();
        validateEventRequestRules(request.getDateTime(), request.getAttendancePoints());
        Community targetCommunity = resolveTargetCommunity(user, request);
        checkEventDuplicate(request, targetCommunity.getId());
        Event entity = eventMapper.toEntity(request);

        entity.setCommunity(targetCommunity);

        entity.setQrCodeData(UUID.randomUUID().toString());
        entity.setQrExpiresAt(request.getDateTime().plusHours(1));

        Event savedEvent = eventRepository.save(entity);
        return eventMapper.toResponse(savedEvent);

    }

    private Event getEventIfExists(Long id){

        User user = authService.getAuthenticatedUser();

        if (user.getSystemRole() == SystemRole.SUPER_ADMIN) {
            return eventRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException(ErrorCode.EVENT_NOT_FOUND, "Etkinlik kaydı bulunamadı!"));
        }

        return eventRepository.findByIdAndCommunityId(id, user.getCommunity().getId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.EVENT_NOT_FOUND, "Etkinlik kaydı bulunamadı!"));
    }

    private Event getEventByQrCodeIfExists(String qrCodeData, User user) {
        if (user.getSystemRole() == SystemRole.SUPER_ADMIN) {
            return eventRepository.findAll().stream()
                    .filter(event -> Objects.equals(event.getQrCodeData(), qrCodeData))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException(ErrorCode.EVENT_NOT_FOUND, "Etkinlik kaydı bulunamadı!"));
        }

        return eventRepository.findByQrCodeDataAndCommunityId(qrCodeData, user.getCommunity().getId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.EVENT_NOT_FOUND, "Etkinlik kaydı bulunamadı!"));
    }

    private void validateEventQrCode(EventAttendanceScanRequest request,Event event){

        if (!Objects.equals(request.getQrCodeData(), event.getQrCodeData())) {

            throw new BadRequestException(ErrorCode.INVALID_EVENT_QR_CODE,"QR kod uyuşmuyor!");

        }
    }

    private void validateEventQrNotExpired(Event event){

        if (event.getQrExpiresAt().isBefore(LocalDateTime.now())){
            throw new BadRequestException(ErrorCode.EVENT_QR_CODE_EXPIRED,"QR kodun süresi dolmuş!");
        }
    }

    private void checkUserAlreadyAttendedEvent(Long eventId, Long userId, Long communityId){
        if (eventAttendanceRepository.existsByEventIdAndUserIdAndCommunityId(eventId, userId, communityId)){
            throw new ConflictException(ErrorCode.EVENT_ATTENDANCE_ALREADY_EXISTS,"Bu etkinlik için katılım kaydınız zaten mevcut!");
        }
    }

    private EventAttendance buildEventAttendance(Event event,User user){

        EventAttendance eventAttendance = new EventAttendance();
        eventAttendance.setEvent(event);
        eventAttendance.setScannedAt(LocalDateTime.now());
        eventAttendance.setUser(user);
        eventAttendance.setPointsEarned(event.getAttendancePoints());
        eventAttendance.setCommunity(event.getCommunity());

        Integer eventAttendancePoints = event.getAttendancePoints();
        Integer totalPoints = user.getTotalPoints();

        user.setTotalPoints(eventAttendancePoints + totalPoints);
        return eventAttendance;

    }

    @Transactional
    @Override
    public EventAttendanceResponse scanEventAttendance(EventAttendanceScanRequest request) {
        User user = authService.getAuthenticatedUser();
        Event event = getEventIfExists(request.getEventId());
        return scanAttendanceForEvent(user, event, request.getQrCodeData());
    }

    @Transactional
    @Override
    public EventAttendanceResponse scanEventAttendanceByQrCode(String qrCodeData) {
        User user = authService.getAuthenticatedUser();
        Event event = getEventByQrCodeIfExists(qrCodeData, user);
        return scanAttendanceForEvent(user, event, qrCodeData);
    }

    @Override
    public EventResponse getEventById(Long eventId){
        Event event = getEventIfExists(eventId);
        return eventMapper.toResponse(event);
    }

    @Transactional
    @Override
    public void deleteEventById(Long eventId){
        checkEventAuthority();

        Event event = getEventIfExists(eventId);

        List<EventAttendance> attendances = eventAttendanceRepository.findAllByEventIdAndCommunityId(eventId, event.getCommunity().getId());
        for (EventAttendance attendance : attendances) {
            User user = attendance.getUser();
            if (user != null) {
                int currentPoints = user.getTotalPoints() != null ? user.getTotalPoints() : 0;
                int pointsEarned = attendance.getPointsEarned() != null ? attendance.getPointsEarned() : 0;
                user.setTotalPoints(Math.max(0, currentPoints - pointsEarned));
            }
        }

        eventAttendanceRepository.deleteAll(attendances);
        eventRepository.delete(event);
    }

    @Override
    public List<EventResponse> getAllEvents(){

        User user = authService.getAuthenticatedUser();

        if (user.getSystemRole() == SystemRole.SUPER_ADMIN) {
            return eventMapper.toEventResponseList(eventRepository.findAll());
        }

        List<Event> allEvent = eventRepository.findByCommunityId(user.getCommunity().getId());
        return eventMapper.toEventResponseList(allEvent);
    }

    @Override
    public List<AttendanceListItemResponse> getEventAttendances(Long eventId) {
        Event event = getEventIfExists(eventId);
        return eventAttendanceRepository.findAllByEventIdAndCommunityId(eventId, event.getCommunity().getId())
                .stream()
                .map(this::toAttendanceListItem)
                .toList();
    }

    private void checkEventDuplicateForUpdate(Event event, UpdateEventRequest request) {
        String title = request.getTitle() != null ? request.getTitle().trim() : event.getTitle();
        LocalDateTime dateTime = request.getDateTime() != null ? request.getDateTime() : event.getDateTime();
        String location = request.getLocation() != null ? request.getLocation().trim() : event.getLocation();

        if (eventRepository.existsByTitleAndDateTimeAndLocationAndCommunityIdAndIdNot(
                title, dateTime, location, event.getCommunity().getId(), event.getId())) {
            throw new ConflictException(ErrorCode.EVENT_ALREADY_EXISTS, "Bu etkinlik kaydı zaten var!");
        }
    }


    @Override
    public EventResponse updateEvent(Long eventId, UpdateEventRequest updateEventRequest){
        checkEventAuthority();
        Event event = getEventIfExists(eventId);
        validateEventRequestRules(updateEventRequest.getDateTime(),updateEventRequest.getAttendancePoints());
        checkEventDuplicateForUpdate(event,updateEventRequest);
        updateEventFields(event, updateEventRequest);
        Event updatedEvent = eventRepository.save(event);
        return eventMapper.toResponse(updatedEvent);


    }

    private void updateEventFields(Event event, UpdateEventRequest updateEventRequest){

        if (updateEventRequest.getTitle() != null){
            event.setTitle(updateEventRequest.getTitle().trim());
        }
        if (updateEventRequest.getDescription() != null){
            event.setDescription(updateEventRequest.getDescription().trim());
        }
        if (updateEventRequest.getEventType() != null){
            event.setEventType(updateEventRequest.getEventType());
        }
        
        if (updateEventRequest.getLocation() != null){
            event.setLocation(updateEventRequest.getLocation().trim());
        }
        if (updateEventRequest.getPosterImage() != null){
            event.setPosterImage(updateEventRequest.getPosterImage().trim());
        }
        
        if (updateEventRequest.getApprovalStatus() != null){
            event.setApprovalStatus(updateEventRequest.getApprovalStatus());
        }
        if (updateEventRequest.getDateTime() != null){
            event.setDateTime(updateEventRequest.getDateTime());
            event.setQrExpiresAt(updateEventRequest.getDateTime().plusHours(1));
            event.setQrCodeData(UUID.randomUUID().toString());
        }
        if (updateEventRequest.getAttendancePoints() != null){
            event.setAttendancePoints(updateEventRequest.getAttendancePoints());
        }

    }

    private AttendanceListItemResponse toAttendanceListItem(EventAttendance attendance) {
        User attendanceUser = attendance.getUser();
        String firstName = attendanceUser.getFirstName() != null ? attendanceUser.getFirstName() : "";
        String lastName = attendanceUser.getLastName() != null ? attendanceUser.getLastName() : "";
        String fullName = (firstName + " " + lastName).trim();

        return new AttendanceListItemResponse(
                attendance.getId(),
                attendance.getScannedAt(),
                attendance.getPointsEarned(),
                attendanceUser.getId(),
                attendanceUser.getUsername(),
                fullName.isBlank() ? attendanceUser.getUsername() : fullName,
                attendanceUser.getProfileImage()
        );
    }

    private EventAttendanceResponse scanAttendanceForEvent(User user, Event event, String qrCodeData) {
        validateEventQrCode(buildScanRequest(event.getId(), qrCodeData), event);
        validateEventQrNotExpired(event);
        checkUserAlreadyAttendedEvent(event.getId(), user.getId(), user.getCommunity().getId());

        EventAttendance eventAttendance = buildEventAttendance(event, user);
        EventAttendance savedAttendance = eventAttendanceRepository.save(eventAttendance);
        userRepository.save(user);

        return eventAttendanceMapper.toResponse(savedAttendance);
    }

    private EventAttendanceScanRequest buildScanRequest(Long eventId, String qrCodeData) {
        EventAttendanceScanRequest request = new EventAttendanceScanRequest();
        request.setEventId(eventId);
        request.setQrCodeData(qrCodeData);
        return request;
    }



    
    


    


}
