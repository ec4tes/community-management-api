ALTER TABLE committees
    ALTER COLUMN community_id SET NOT NULL;

ALTER TABLE projects
    ALTER COLUMN community_id SET NOT NULL;

ALTER TABLE project_task
    ALTER COLUMN community_id SET NOT NULL;

ALTER TABLE event
    ALTER COLUMN community_id SET NOT NULL;

ALTER TABLE meetings
    ALTER COLUMN community_id SET NOT NULL;

ALTER TABLE event_attendances
    ALTER COLUMN community_id SET NOT NULL;

ALTER TABLE meeting_attendance
    ALTER COLUMN community_id SET NOT NULL;

ALTER TABLE task_point_record
    ALTER COLUMN community_id SET NOT NULL;

ALTER TABLE activity_log
    ALTER COLUMN community_id SET NOT NULL;

ALTER TABLE committees
    ADD CONSTRAINT fk_committees_community
        FOREIGN KEY (community_id) REFERENCES communities (id);

ALTER TABLE projects
    ADD CONSTRAINT fk_projects_community
        FOREIGN KEY (community_id) REFERENCES communities (id);

ALTER TABLE project_task
    ADD CONSTRAINT fk_project_task_community
        FOREIGN KEY (community_id) REFERENCES communities (id);

ALTER TABLE event
    ADD CONSTRAINT fk_event_community
        FOREIGN KEY (community_id) REFERENCES communities (id);

ALTER TABLE meetings
    ADD CONSTRAINT fk_meetings_community
        FOREIGN KEY (community_id) REFERENCES communities (id);

ALTER TABLE event_attendances
    ADD CONSTRAINT fk_event_attendances_community
        FOREIGN KEY (community_id) REFERENCES communities (id);

ALTER TABLE meeting_attendance
    ADD CONSTRAINT fk_meeting_attendance_community
        FOREIGN KEY (community_id) REFERENCES communities (id);

ALTER TABLE task_point_record
    ADD CONSTRAINT fk_task_point_record_community
        FOREIGN KEY (community_id) REFERENCES communities (id);

ALTER TABLE activity_log
    ADD CONSTRAINT fk_activity_log_community
        FOREIGN KEY (community_id) REFERENCES communities (id);

ALTER TABLE event
    ADD CONSTRAINT uk_event_identity_per_community
        UNIQUE (community_id, title, date_time, location);

ALTER TABLE meetings
    ADD CONSTRAINT uk_meeting_identity_per_community
        UNIQUE (community_id, title, date_time, location);

ALTER TABLE projects
    ADD CONSTRAINT uk_project_name_per_committee_tenant
        UNIQUE (community_id, committee_id, name);

ALTER TABLE project_task
    ADD CONSTRAINT uk_project_task_title_per_project_tenant
        UNIQUE (community_id, project_id, title);

CREATE INDEX idx_committees_community_id
    ON committees (community_id);

CREATE INDEX idx_projects_community_id
    ON projects (community_id);

CREATE INDEX idx_projects_committee_community
    ON projects (committee_id, community_id);

CREATE INDEX idx_project_task_community_id
    ON project_task (community_id);

CREATE INDEX idx_project_task_project_community
    ON project_task (project_id, community_id);

CREATE INDEX idx_project_task_community_status
    ON project_task (community_id, status);

CREATE INDEX idx_event_community_id
    ON event (community_id);

CREATE INDEX idx_event_community_date_time
    ON event (community_id, date_time);

CREATE INDEX idx_meetings_community_id
    ON meetings (community_id);

CREATE INDEX idx_meetings_community_date_time
    ON meetings (community_id, date_time);

CREATE INDEX idx_event_attendances_community_id
    ON event_attendances (community_id);

CREATE INDEX idx_meeting_attendance_community_id
    ON meeting_attendance (community_id);

CREATE INDEX idx_task_point_record_community_created_at
    ON task_point_record (community_id, created_at);

CREATE INDEX idx_task_point_record_user_created_at
    ON task_point_record (user_id, created_at);

CREATE INDEX idx_activity_log_community_created_at
    ON activity_log (community_id, created_at);

CREATE INDEX idx_activity_log_user_community_created_at
    ON activity_log (user_id, community_id, created_at);
