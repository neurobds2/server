package org.ohmage.domain.survey.prompt;

import java.net.URI;
import java.util.Map;

import name.jenkins.paul.john.concordia.Concordia;
import name.jenkins.paul.john.concordia.exception.ConcordiaException;
import name.jenkins.paul.john.concordia.schema.ReferenceSchema;
import name.jenkins.paul.john.concordia.schema.Schema;

import org.ohmage.domain.exception.InvalidArgumentException;
import org.ohmage.domain.survey.Media;
import org.ohmage.domain.survey.condition.Condition;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * <p>
 * A prompt for the user to launch a remote activity.
 * </p>
 *
 * @author John Jenkins
 */
public class RemoteActivityPrompt extends Prompt<JsonNode> {
    /**
     * The string type of this survey item.
     */
    public static final String SURVEY_ITEM_TYPE = "remote_activity_prompt";

    /**
     * The JSON key for the URI.
     */
    public static final String JSON_KEY_URI = "uri";

    /**
     * The JSON key for the response's definition.
     */
    public static final String JSON_KEY_DEFINITION = "definition";

    /**
     * The URI to use to launch the remote activity. This may include query
     * parameters and/or fragments.
     */
    @JsonProperty(JSON_KEY_URI)
    private final URI uri;

    /**
     * The definition that a valid response must follow.
     */
    private final Concordia definition;

    /**
     * Creates a new remote activity prompt.
     *
     * @param displayType
     *        The display type to use to visualize the prompt.
     *
     * @param surveyItemId
     *        The survey-unique identifier for this prompt.
     *
     * @param condition
     *        The condition on whether or not to show this prompt.
     *
     * @param text
     *        The text to display to the user.
     *
     * @param displayLabel
     *        The text to use as a short name in visualizations.
     *
     * @param skippable
     *        Whether or not this prompt may be skipped.
     *
     * @param defaultResponse
     *        The default response for this prompt or null if a default is not
     *        allowed.
     *
     * @param minRuns
     *        The minimum number of times the user must launch the remote
     *        activity. This must be greater than zero and, if null, will
     *        default to {@link DEFAULT_MIN_RUNS}.
     *
     * @param maxRuns
     *        The maximum number of times the user may launch the remote
     *        activity. This must be greater than 'minRuns' and, if null, will
     *        default to {@link DEFAULT_MAX_RUNS}.
     *
     * @throws InvalidArgumentException
     *         A parameter was invalid.
     */
    @JsonCreator
    public RemoteActivityPrompt(
        @JsonProperty(JSON_KEY_DISPLAY_TYPE) final String displayType,
        @JsonProperty(JSON_KEY_SURVEY_ITEM_ID) final String surveyItemId,
        @JsonProperty(JSON_KEY_CONDITION) final Condition condition,
        @JsonProperty(JSON_KEY_TEXT) final String text,
        @JsonProperty(JSON_KEY_DISPLAY_LABEL) final String displayLabel,
        @JsonProperty(JSON_KEY_SKIPPABLE) final boolean skippable,
        @JsonProperty(JSON_KEY_DEFAULT_RESPONSE)
            final JsonNode defaultResponse,
        @JsonProperty(JSON_KEY_URI) final URI uri,
        @JsonProperty(JSON_KEY_DEFINITION) final Concordia definition)
        throws InvalidArgumentException {

        super(
            displayType,
            surveyItemId,
            condition,
            text,
            displayLabel,
            skippable,
            defaultResponse);

        // Default values are not allowed.
        if(defaultResponse != null) {
            throw
                new InvalidArgumentException(
                    "Default values are not allowed for remote activities.");
        }

        // Validate the URI.
        if(uri == null) {
            throw new InvalidArgumentException("The URI is missing.");
        }
        else {
            this.uri = uri;
        }

        // Validate the definition.
        if(definition == null) {
            throw new InvalidArgumentException("The definition is missing.");
        }
        else {
            this.definition = definition;
        }
    }

    /*
     * (non-Javadoc)
     * @see org.ohmage.domain.survey.Respondable#getResponseSchema()
     */
    @Override
    public Schema getResponseSchema() {
        try {
            return
                new ReferenceSchema(
                    getText(),
                    (skippable() || (getCondition() != null)),
                    getSurveyItemId(),
                    definition.getSchema());
        }
        catch(ConcordiaException e) {
            throw
                new IllegalStateException(
                    "There was a problem creating a an empty object schema.",
                    e);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.ohmage.domain.survey.prompt.Prompt#validateResponse(java.lang.Object, java.util.Map)
     */
    @Override
    public JsonNode validateResponse(
        final JsonNode response,
        final Map<String, Media> media)
        throws InvalidArgumentException {

        try {
            definition.validateData(response);
        }
        catch(ConcordiaException e) {
            throw
                new InvalidArgumentException(
                    "The data was invalid, \"" +
                        e.getLocalizedMessage() +
                        "\": " +
                        getSurveyItemId());
        }

        return response;
    }
}