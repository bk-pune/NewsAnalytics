package news.analytics.pipeline.config;

/**
 * Represents a tag which contains information on RawNews.<br>
 * In actual raw html content, HTML tag matching 'this' TagLocator will be found out.<br>
 *
 * For example: if the given meta tag from raw html is as follows-<br>
 * <b>&lt;meta name="keywords" content="Keyword1, Keyword2"/&gt;</b><br>
 * Following TagLocator will represent above html tag<br>
 * tagIdentifierTagName = meta<br>
 * tagIdentifierAttributeName = name<br>
 * tagIdentifierAttributeValue = keywords<br>
 * valueAttributeName = content<br>
 *<br><br>
 * Similarly for html tag <b>&lt;meta charset="utf-8"&gt; </b>, corresponding tag locator will look like:<br>
 * tagIdentifierTagName = meta<br>
 * tagIdentifierAttributeName = charset<br>
 * valueAttributeName = charset<br>
 */

public class TagLocator {
    /**
     * Name of an html tag
     */
    private String tagIdentifierTagName;

    /**
     * TagLocator with this Attribute will contain the valueAttributeName
     */
    private String tagIdentifierAttributeName;

    /**
     * TagLocator having attribute valueAttributeName = this valueAttributeName
     */
    private String tagIdentifierAttributeValue;

    /**
     * Value of this attribute is what we expect
     */
    private String valueAttributeName;


    public String getTagIdentifierTagName() {
        return tagIdentifierTagName;
    }

    public void setTagIdentifierTagName(String tagIdentifierTagName) {
        this.tagIdentifierTagName = tagIdentifierTagName;
    }

    public String getTagIdentifierAttributeName() {
        return tagIdentifierAttributeName;
    }

    public void setTagIdentifierAttributeName(String tagIdentifierAttributeName) {
        this.tagIdentifierAttributeName = tagIdentifierAttributeName;
    }

    public String getTagIdentifierAttributeValue() {
        return tagIdentifierAttributeValue;
    }

    public void setTagIdentifierAttributeValue(String tagIdentifierAttributeValue) {
        this.tagIdentifierAttributeValue = tagIdentifierAttributeValue;
    }

    public String getValueAttributeName() {
        return valueAttributeName;
    }

    public void setValueAttributeName(String valueAttributeName) {
        this.valueAttributeName = valueAttributeName;
    }
}

