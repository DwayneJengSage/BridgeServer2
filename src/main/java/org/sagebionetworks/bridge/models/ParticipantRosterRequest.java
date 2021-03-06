package org.sagebionetworks.bridge.models;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request for participant roster download.
 */

public class ParticipantRosterRequest implements BridgeEntity{
    private final String password;
    private final String studyId;

    /** Private constructor. See builder. */
    private ParticipantRosterRequest(@JsonProperty("password") String password, @JsonProperty("studyId") String studyId) {
        this.password = password;
        this.studyId = studyId;
    }

    /** Get the password. */
    public String getPassword() {
        return password;
    }

    /** Get the studyId, if supplied. */
    public String getStudyId() {
        return studyId;
    }

    /** Participant roster request builder. */
    public static class Builder {
        private String password;
        private String studyId;

        /** @see ParticipantRosterRequest#getPassword() */
        public ParticipantRosterRequest.Builder withPassword(String password) {
            this.password = password;
            return this;
        }

        /** @see ParticipantRosterRequest#getStudyId() */
        public ParticipantRosterRequest.Builder withStudyId(String studyId) {
            this.studyId = studyId;
            return this;
        }

        /** Builds a participant roster request. */
        public ParticipantRosterRequest build() {
            return new ParticipantRosterRequest(password, studyId);
        }
    }
}
