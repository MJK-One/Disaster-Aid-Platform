CREATE EXTENSION IF NOT EXISTS postgis;

-- 기존 테이블 삭제 (의존성 역순으로)
DROP TABLE IF EXISTS user_report_alert CASCADE;
DROP TABLE IF EXISTS report_response CASCADE;
DROP TABLE IF EXISTS report CASCADE;
DROP TABLE IF EXISTS report_alert CASCADE;
DROP TABLE IF EXISTS attendance_policy CASCADE;
DROP TABLE IF EXISTS volunteer_participant CASCADE;
DROP TABLE IF EXISTS volunteer_team CASCADE;
DROP TABLE IF EXISTS volunteer_location CASCADE;
DROP TABLE IF EXISTS post CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- 1. 사용자 테이블 (예시)
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       nickname VARCHAR(255) UNIQUE,
                       phone_number VARCHAR(20),
                       email VARCHAR(100) NOT NULL UNIQUE,
                       password VARCHAR(255),
                       login_type VARCHAR(50),
                       provider VARCHAR(255),
                       business_number VARCHAR(255),
                       organization_name VARCHAR(255),
                       user_role VARCHAR(50),
                       last_login_at TIMESTAMP,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2. post
CREATE TABLE post (
                      id BIGSERIAL PRIMARY KEY,
                      user_id BIGINT NOT NULL,
                      category VARCHAR(50),
                      title VARCHAR(255),
                      content TEXT,
                      total_capacity INT,
                      team_size INT,
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      CONSTRAINT fk_post_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 3. volunteer_location (1:1 with post)
CREATE TABLE volunteer_location (
                                    id BIGSERIAL PRIMARY KEY,
                                    place_name VARCHAR(255) NOT NULL,
                                    location_lat DOUBLE PRECISION,
                                    location_lng DOUBLE PRECISION,
                                    post_id BIGINT NOT NULL UNIQUE,
                                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                    CONSTRAINT fk_location_post FOREIGN KEY (post_id) REFERENCES post(id)
);

-- 4. volunteer_team (N:1 with post)
CREATE TABLE volunteer_team (
                                id BIGSERIAL PRIMARY KEY,
                                post_id BIGINT NOT NULL,
                                team_number INT NOT NULL,
                                max_capacity INT NOT NULL,
                                CONSTRAINT fk_team_post FOREIGN KEY (post_id) REFERENCES post(id)
);

-- 5. volunteer_participant
CREATE TABLE volunteer_participant (
                                       id BIGSERIAL PRIMARY KEY,
                                       user_id BIGINT,
                                       team_id BIGINT,
                                       joined_at TIMESTAMP,
                                       checkin_status VARCHAR(50) NOT NULL,
                                       CONSTRAINT fk_participant_user FOREIGN KEY (user_id) REFERENCES users(id),
                                       CONSTRAINT fk_participant_team FOREIGN KEY (team_id) REFERENCES volunteer_team(id)
);

-- 6. attendance_policy
CREATE TABLE attendance_policy (
                                   id BIGSERIAL PRIMARY KEY,
                                   checkin_start TIMESTAMP,
                                   checkin_end TIMESTAMP,
                                   attendance_radius_meters INT,
                                   min_checkin_minutes INT,
                                   post_id BIGINT NOT NULL UNIQUE,
                                   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                   updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                   CONSTRAINT fk_attendance_post FOREIGN KEY (post_id) REFERENCES post(id)
);

-- 7. report_alert
CREATE TABLE report_alert (
                              id BIGSERIAL PRIMARY KEY,
                              si VARCHAR(255) NOT NULL,
                              gu VARCHAR(255) NOT NULL,
                              disaster_type VARCHAR(50) NOT NULL,
                              count BIGINT NOT NULL,
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 8. user_report_alert
CREATE TABLE user_report_alert (
                                   id BIGSERIAL PRIMARY KEY,
                                   alert_id BIGINT NOT NULL,
                                   user_id BIGINT NOT NULL,
                                   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                   updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                   CONSTRAINT fk_user_report_alert_alert FOREIGN KEY (alert_id) REFERENCES report_alert(id),
                                   CONSTRAINT fk_user_report_alert_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 9. report
CREATE TABLE report (
                        id BIGSERIAL PRIMARY KEY,
                        reporter_id BIGINT NOT NULL,
                        disaster_type VARCHAR(50) NOT NULL,
                        description TEXT NOT NULL,
                        image_url VARCHAR(255),
                        video_url VARCHAR(255),
                        status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
                        si VARCHAR(255) NOT NULL,
                        gu VARCHAR(255) NOT NULL,
                        location geometry(Point, 4326),
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        CONSTRAINT fk_report_user FOREIGN KEY (reporter_id) REFERENCES users(id)
);

-- 10. report_response
CREATE TABLE report_response (
                                 id BIGSERIAL PRIMARY KEY,
                                 is_notified BOOLEAN NOT NULL DEFAULT FALSE,
                                 response_status VARCHAR(50) NOT NULL,
                                 notified_at TIMESTAMP,
                                 report_id BIGINT NOT NULL,
                                 responder_id BIGINT NOT NULL,
                                 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                 updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                 CONSTRAINT fk_response_report FOREIGN KEY (report_id) REFERENCES report(id),
                                 CONSTRAINT fk_response_user FOREIGN KEY (responder_id) REFERENCES users(id)
);
