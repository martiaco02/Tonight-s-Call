    package it.unipi.tonightscall.DTO;

    import com.fasterxml.jackson.annotation.JsonProperty;
    import io.swagger.v3.oas.annotations.media.Schema;
    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    /**
     * DTO representing a brief summary of an event managed by an organization.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Summary of an event linked to an organization")
    public class EventOrganizationDTO {

        /**
         * Unique identifier of the organization.
         */
        @JsonProperty("id")
        @Schema(description = "Unique identifier an event linked to an organization")
        private String id;

        /**
         * Name of the event.
         */
        @JsonProperty
        @Schema(description = "Name of the event", example = "New Year's Eve Party")
        private String name;
    }
